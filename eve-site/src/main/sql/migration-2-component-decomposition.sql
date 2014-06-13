# We need to recreate the function because we changed its parameters, so drop it first
DROP FUNCTION IF EXISTS calculate_production_time_hours;
# The BlueprintSummary view is no longer used
DROP VIEW BlueprintSummary;

delimiter $$

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

CREATE TABLE BlueprintTypeDecomposition (
  blueprintTypeID int(11) NOT NULL,
  materialTypeID int(11) NOT NULL,
  PRIMARY KEY (blueprintTypeID,materialTypeID)
) ENGINE=InnoDB;

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

ALTER VIEW BlueprintTypes AS
SELECT
  blueprintTypeID,
  materialTypeID,
  cast(sum(units) as SIGNED) as units,
  typeName,
  materialBlueprintTypeID,
  decomposed
FROM BlueprintTypesRaw
GROUP BY blueprintTypeID, materialTypeID;

ALTER VIEW BlueprintTypeCosts AS
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

ALTER VIEW BlueprintCosts AS
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