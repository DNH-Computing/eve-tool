-- NB: Replace `eve-dump` with the database of your eve dump

-- Clear out the schema first (this is the reverse order that these objects are defined in this file)
DROP VIEW IF EXISTS BlueprintSummary, invBlueprintTypes, invCategories, invGroups, invTypes, BlueprintCosts, BlueprintTypeCosts;
DROP TABLE IF EXISTS Type;
DROP VIEW IF EXISTS BlueprintTypes, BlueprintTypesRaw, BlueprintSubTypeRequirements;
DROP TABLE IF EXISTS Blueprint, BlueprintTypeDecomposition;
DROP FUNCTION IF EXISTS calculate_materials;
DROP FUNCTION IF EXISTS calculate_production_time_hours;

-- Helper functions from http://wiki.eve-id.net/Equations
delimiter $$
CREATE FUNCTION calculate_materials (materialAmount INT(11), baseWasteFactor INT(11), materialEfficiency INT(11))
  RETURNS INT(11) DETERMINISTIC
BEGIN
  # Can't use a double here as we get annoying rounding errors...
  DECLARE wasteEffect INT;
  IF materialEfficiency < 0 THEN
    SET wasteEffect = ROUND(materialAmount * baseWasteFactor / 100 * (1 - materialEfficiency));
  ELSE
    SET wasteEffect = ROUND(materialAmount * baseWasteFactor / 100 * (1 / (materialEfficiency + 1)));
  END IF;
  RETURN materialAmount + wasteEffect;
END$$

-- Calculates the production time, in hours, unrounded, for a single run of a blueprint.
-- To calculate the exact number of hours, multiply this by the number of runs then take the next-highest integer.
CREATE FUNCTION calculate_production_time_hours (baseProductionTime INT(11), productivityModifier INT(11), 
    productionEfficiency INT(11))
  RETURNS DOUBLE DETERMINISTIC
BEGIN
  # ImplantModifier=1.0 (ignore for now)
  # ProductionSlotModifier=1.0 (NPC stations are always 0)
  # IndustrySkill=5 (maxed out at 5)
  # ProductionTimeModifier=0.8 (derived from above)
  DECLARE efficiencyEffect DOUBLE;
  IF productionEfficiency < 0 THEN
    SET efficiencyEffect = productionEfficiency - 1;
  ELSE
    SET efficiencyEffect = productionEfficiency / (1 + productionEfficiency);
  END IF;
  RETURN baseProductionTime * (1 - (productivityModifier / baseProductionTime) * efficiencyEffect) * 0.8 / 3600;
END$$

delimiter ;

CREATE TABLE Blueprint (
  blueprintTypeID int(11) NOT NULL,
  numberPerRun int(11) NOT NULL,
  productionEfficiency int(11) NOT NULL,
  saleValue decimal(65,2) NOT NULL,
  materialEfficiency int(11) NOT NULL,
  lastUpdated TIMESTAMP NOT NULL,
  automaticallyUpdateSalePrice bit(1) not null default 0,
  PRIMARY KEY (blueprintTypeID)
) ENGINE=InnoDB;

CREATE TABLE BlueprintTypeDecomposition (
  blueprintTypeID int(11) NOT NULL,
  materialTypeID int(11) NOT NULL,
  PRIMARY KEY (blueprintTypeID,materialTypeID)
) ENGINE=InnoDB;

# Requirements of sub-types are already added, they need to be subtracted so we collect them here
CREATE VIEW BlueprintSubTypeRequirements AS
  SELECT
    bp.blueprintTypeID as blueprintTypeID,
    itm.materialTypeID as materialTypeID,
    sum(itm.quantity) as rawQuantity
  FROM Blueprint bp
    JOIN `eve-dump`.invBlueprintTypes ibt ON ibt.blueprintTypeID = bp.blueprintTypeID
    JOIN `eve-dump`.invTypes it ON it.typeID = ibt.blueprintTypeID
    JOIN `eve-dump`.ramTypeRequirements ram on ram.typeID = it.typeID
    JOIN `eve-dump`.invTypes ramT ON ram.requiredTypeID = ramT.typeID
    JOIN `eve-dump`.invGroups ramG ON ramG.groupID = ramT.groupID
    JOIN `eve-dump`.ramActivities ramA on ramA.activityID = ram.activityID
    JOIN `eve-dump`.invTypeMaterials itm on itm.typeID = ramT.typeID
  WHERE
    ramA.activityName = 'Manufacturing'
    AND ramG.groupName != 'Tool'
  GROUP BY bp.blueprintTypeID, itm.materialTypeID;

CREATE VIEW BlueprintTypesRaw AS
  SELECT
    bp.blueprintTypeID as blueprintTypeID,
    itm.materialTypeID as materialTypeID,
    # Remove any materials that are only used to create sub-types (bstr.rawQuantity)
    calculate_materials(itm.quantity - ifnull(bstr.rawQuantity, 0), ibt.wasteFactor, bp.materialEfficiency) as units,
    mat.typeName,
    materialBlueprintType.blueprintTypeID as materialBlueprintTypeID,
    btd.blueprintTypeID IS NOT NULL as decomposed
  FROM Blueprint bp
    JOIN `eve-dump`.invBlueprintTypes ibt ON ibt.blueprintTypeID = bp.blueprintTypeID
    JOIN `eve-dump`.invTypes it ON it.typeID = ibt.productTypeID
    JOIN `eve-dump`.invTypeMaterials itm on itm.typeID = it.typeID
    JOIN `eve-dump`.invTypes mat ON mat.typeID = itm.materialTypeID
    LEFT OUTER JOIN BlueprintSubTypeRequirements bstr 
      ON bstr.blueprintTypeId = bp.blueprintTypeID
      AND bstr.materialTypeID = itm.materialTypeID
    LEFT OUTER JOIN `eve-dump`.invBlueprintTypes materialBlueprintType ON materialBlueprintType.productTypeID = itm.materialTypeID
    LEFT OUTER JOIN BlueprintTypeDecomposition btd ON btd.blueprintTypeID = bp.blueprintTypeID AND btd.materialTypeID = itm.materialTypeID
  WHERE
    bstr.rawQuantity IS NULL OR itm.quantity - bstr.rawQuantity > 0
  UNION
  SELECT 
    bp.blueprintTypeID as blueprintTypeID,
    ram.requiredTypeID as materialTypeID,
    ram.quantity as units,
    ramT.typeName,
    materialBlueprintType.blueprintTypeID as materialBlueprintTypeID,
    btd.blueprintTypeID IS NOT NULL as decomposed
  FROM Blueprint bp
    JOIN `eve-dump`.invBlueprintTypes ibt ON ibt.blueprintTypeID = bp.blueprintTypeID
    JOIN `eve-dump`.invTypes it ON it.typeID = ibt.blueprintTypeID
    JOIN `eve-dump`.ramTypeRequirements ram on ram.typeID = it.typeID
    JOIN `eve-dump`.invTypes ramT ON ram.requiredTypeID = ramT.typeID
    JOIN `eve-dump`.invGroups ramG ON ramG.groupID = ramT.groupID
    JOIN `eve-dump`.invCategories ramC ON ramC.categoryID = ramG.categoryID
    JOIN `eve-dump`.ramActivities ramA on ramA.activityID = ram.activityID
    LEFT OUTER JOIN `eve-dump`.invBlueprintTypes materialBlueprintType ON materialBlueprintType.productTypeID = ram.requiredTypeID
    LEFT OUTER JOIN BlueprintTypeDecomposition btd ON btd.blueprintTypeID = bp.blueprintTypeID AND btd.materialTypeID = ram.requiredTypeID
  WHERE
    ramA.activityName = 'Manufacturing'
    AND ramC.categoryName != 'Skill';

# Materials can appear in both sides of the union in BlueprintTypesRaw, so sum them and group them so they appear as 1 row
CREATE VIEW BlueprintTypes AS
SELECT
  blueprintTypeID,
  materialTypeID,
  cast(sum(units) as SIGNED) as units,
  typeName,
  materialBlueprintTypeID,
  decomposed
FROM BlueprintTypesRaw
GROUP BY blueprintTypeID, materialTypeID;

CREATE TABLE Type (
  typeID int(11) NOT NULL,
  cost decimal(65,2) NOT NULL,
  lastUpdated TIMESTAMP NOT NULL,
  autoUpdate bit(1) not null default 0,
  PRIMARY KEY (typeID)
) ENGINE=InnoDB;


CREATE VIEW BlueprintTypeCosts AS
  select 
    bp.blueprintTypeID AS blueprintTypeID,
    it.typeName AS blueprintName,
    mt.typeName AS typeName,
    bt.units AS units,
    (bt.units * t.cost) AS cost,
    bt.decomposed AS decomposed
  from Blueprint bp
    join BlueprintTypes bt on bt.blueprintTypeID = bp.blueprintTypeID
    left outer join Type t on t.typeID = bt.materialTypeID
    JOIN `eve-dump`.invBlueprintTypes ibt ON ibt.blueprintTypeID = bp.blueprintTypeID
    JOIN `eve-dump`.invTypes it ON it.typeID = ibt.productTypeID
    JOIN `eve-dump`.invTypes mt ON mt.typeID = bt.materialTypeID;

CREATE VIEW BlueprintCosts AS
  select
    bp.blueprintTypeID AS blueprintTypeID,
    # This makes my head hurt, MySQL doesn't return null if there are null values present, so we need to do it ourselves
    # Also, btc might return 0 rows, and MySQL returns null if there are 0 rows, so we convert that to 0 with coalesce
    if(sum(btc.cost is null),null,coalesce(sum(btc.cost),0)) AS materialCost,
    calculate_production_time_hours(ibt.productionTime, ibt.productivityModifier, bp.productionEfficiency) as hoursForSingleRun,
    cast(ral.costPerHour AS decimal(19,2)) AS costPerHour,
    cast(ral.costInstall AS decimal(19,2)) AS installCost,
    bp.saleValue AS saleValue,
    EXISTS(select blueprintTypeID from BlueprintTypeDecomposition btd where btd.blueprintTypeID=bp.blueprintTypeID) AS containsDecomposed
  from Blueprint bp
    join `eve-dump`.invBlueprintTypes ibt on ibt.blueprintTypeID = bp.blueprintTypeID
    # Ignore the cost of materials if they're decomposed - java will sort that out for us
    left outer join BlueprintTypeCosts btc on btc.blueprintTypeID = bp.blueprintTypeID and btc.decomposed = 0
    JOIN `eve-dump`.ramAssemblyLines ral ON ral.assemblyLineID = 1 # They're all the same!
  group by bp.blueprintTypeID;


CREATE VIEW invTypes AS
  SELECT * FROM `eve-dump`.invTypes;

CREATE VIEW invGroups AS
  SELECT * FROM `eve-dump`.invGroups;

CREATE VIEW invCategories AS
  SELECT * FROM `eve-dump`.invCategories;

CREATE VIEW invBlueprintTypes AS
  SELECT invBlueprintTypes.* FROM `eve-dump`.invBlueprintTypes;
