package nz.net.dnh.eve.model.raw;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "invBlueprintTypes")
@NamedQueries({ @NamedQuery(name = "InventoryBlueprintType.findUnknownBlueprints", query = InventoryBlueprintType.UNKNOWN_BLUEPRINT_QUERY),
	@NamedQuery(name = "InventoryBlueprintType.findUnknownBlueprintsBySearch", query = InventoryBlueprintType.UNKNOWN_BLUEPRINT_QUERY_SEARCH) })
public class InventoryBlueprintType implements Serializable {
	public static final String UNKNOWN_BLUEPRINT_QUERY = "SELECT ibt FROM InventoryBlueprintType ibt "
			+ "WHERE ibt.blueprintTypeID NOT IN (SELECT b.blueprintTypeID from Blueprint b)";
	// This performs really badly. But with our dataset you're not going to
	// notice...
	public static final String UNKNOWN_BLUEPRINT_QUERY_SEARCH = UNKNOWN_BLUEPRINT_QUERY
			+ " AND LOWER(ibt.productType.typeName) LIKE LOWER(CONCAT('%', :search, '%'))";

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	private int blueprintTypeID;

	@OneToOne
	@JoinColumn(name = "blueprintTypeID", updatable = false, insertable = false)
	private InventoryType blueprintType;

	private Integer parentBlueprintTypeID;

	@OneToOne
	@JoinColumn(name = "parentBlueprintTypeID", updatable = false, insertable = false)
	private InventoryType parentBlueprintType;

	private Integer productTypeID;

	@OneToOne
	@JoinColumn(name = "productTypeID", updatable = false, insertable = false)
	private InventoryType productType;

	private Integer productionTime;

	@Column(columnDefinition = "smallint")
	private Integer techLevel;

	private Integer researchProductivityTime;

	private Integer researchMaterialTime;

	private Integer researchCopyTime;

	private Integer researchTechTime;

	private Integer productivityModifier;

	@Column(columnDefinition = "smallint")
	private Integer materialModifier;

	@Column(columnDefinition = "smallint")
	private Integer wasteFactor;

	private Integer maxProductionLimit;

	public int getBlueprintTypeID() {
		return this.blueprintTypeID;
	}

	public void setBlueprintTypeID(final int blueprintTypeID) {
		this.blueprintTypeID = blueprintTypeID;
	}

	public InventoryType getBlueprintType() {
		return this.blueprintType;
	}

	public Integer getParentBlueprintTypeID() {
		return this.parentBlueprintTypeID;
	}

	public void setParentBlueprintTypeID(final Integer parentBlueprintTypeID) {
		this.parentBlueprintTypeID = parentBlueprintTypeID;
	}

	public InventoryType getParentBlueprintType() {
		return this.parentBlueprintType;
	}

	public Integer getProductTypeID() {
		return this.productTypeID;
	}

	public void setProductTypeID(final Integer productTypeID) {
		this.productTypeID = productTypeID;
	}

	public InventoryType getProductType() {
		return this.productType;
	}

	public void setProductType(final InventoryType productType) {
		this.productType = productType;
	}

	public Integer getProductionTime() {
		return this.productionTime;
	}

	public void setProductionTime(final Integer productionTime) {
		this.productionTime = productionTime;
	}

	public Integer getTechLevel() {
		return this.techLevel;
	}

	public void setTechLevel(final Integer techLevel) {
		this.techLevel = techLevel;
	}

	public Integer getResearchProductivityTime() {
		return this.researchProductivityTime;
	}

	public void setResearchProductivityTime(final Integer researchProductivityTime) {
		this.researchProductivityTime = researchProductivityTime;
	}

	public Integer getResearchMaterialTime() {
		return this.researchMaterialTime;
	}

	public void setResearchMaterialTime(final Integer researchMaterialTime) {
		this.researchMaterialTime = researchMaterialTime;
	}

	public Integer getResearchCopyTime() {
		return this.researchCopyTime;
	}

	public void setResearchCopyTime(final Integer researchCopyTime) {
		this.researchCopyTime = researchCopyTime;
	}

	public Integer getResearchTechTime() {
		return this.researchTechTime;
	}

	public void setResearchTechTime(final Integer researchTechTime) {
		this.researchTechTime = researchTechTime;
	}

	public Integer getProductivityModifier() {
		return this.productivityModifier;
	}

	public void setProductivityModifier(final Integer productivityModifier) {
		this.productivityModifier = productivityModifier;
	}

	public Integer getMaterialModifier() {
		return this.materialModifier;
	}

	public void setMaterialModifier(final Integer materialModifier) {
		this.materialModifier = materialModifier;
	}

	public Integer getWasteFactor() {
		return this.wasteFactor;
	}

	public void setWasteFactor(final Integer wasteFactor) {
		this.wasteFactor = wasteFactor;
	}

	public Integer getMaxProductionLimit() {
		return this.maxProductionLimit;
	}

	public void setMaxProductionLimit(final Integer maxProductionLimit) {
		this.maxProductionLimit = maxProductionLimit;
	}

	@Override
	public String toString() {
		return "InventoryBlueprintType [blueprintTypeID="
				+ this.blueprintTypeID + ", blueprintType="
				+ this.blueprintType + ", parentBlueprintTypeID="
				+ this.parentBlueprintTypeID + ", parentBlueprintType="
				+ this.parentBlueprintType + ", productTypeID="
				+ this.productTypeID + ", productType=" + this.productType
				+ ", productionTime=" + this.productionTime + ", techLevel="
				+ this.techLevel + ", researchProductivityTime="
				+ this.researchProductivityTime + ", researchMaterialTime="
				+ this.researchMaterialTime + ", researchCopyTime="
				+ this.researchCopyTime + ", researchTechTime="
				+ this.researchTechTime + ", productivityModifier="
				+ this.productivityModifier + ", materialModifier="
				+ this.materialModifier + ", wasteFactor=" + this.wasteFactor
				+ ", maxProductionLimit=" + this.maxProductionLimit + "]";
	}

	@Override
	public int hashCode() {
		return this.blueprintTypeID;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof InventoryBlueprintType
				&& this.blueprintTypeID == ((InventoryBlueprintType) obj)
				.getBlueprintTypeID();
	}

	public int getProducedQuantity() {
		return getProductType().getPortionSize();
	}
}