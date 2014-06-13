package nz.net.dnh.eve.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import nz.net.dnh.eve.model.raw.InventoryBlueprintType;

@Entity
@NamedQueries({ @NamedQuery(name="Blueprint.findAutomaticlyUpdating", query=Blueprint.AUTOMATICLY_UPDATING_QUERY) })
public class Blueprint extends AbstractLastUpdatedBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String AUTOMATICLY_UPDATING_QUERY = "SELECT b FROM Blueprint b WHERE b.automaticallyUpdateSalePrice = TRUE";

	@NotNull
	@Id
	private int blueprintTypeID;

	@OneToOne
	@JoinColumn(name = "blueprintTypeID", updatable = false, insertable = false)
	private InventoryBlueprintType blueprintType;

	@NotNull
	private int numberPerRun;

	@NotNull
	private int productionEfficiency;

	@NotNull
	private BigDecimal saleValue;

	@NotNull
	private int materialEfficiency;

	@NotNull
	private boolean automaticallyUpdateSalePrice;

	@OneToOne
	@JoinColumn(name = "blueprintTypeID", updatable = false, insertable = false)
	private BlueprintCostSummary costSummary;

	@OneToMany(mappedBy = "id.blueprint", fetch = FetchType.LAZY)
	private Collection<BlueprintRequiredType> requiredTypes;

	public Blueprint() {
	}

	public Blueprint(final int blueprintTypeID, final int numberPerRun, final int productionEfficiency, final BigDecimal saleValue,
			final int materialEfficiency, final boolean automaticallyUpdateSalePrice) {
		this.blueprintTypeID = blueprintTypeID;
		this.numberPerRun = numberPerRun;
		this.productionEfficiency = productionEfficiency;
		this.saleValue = saleValue;
		touchLastUpdated();
		this.materialEfficiency = materialEfficiency;
		this.automaticallyUpdateSalePrice = automaticallyUpdateSalePrice;
	}

	public void setBlueprintTypeID(final int blueprintTypeID) {
		this.blueprintTypeID = blueprintTypeID;
	}

	public int getBlueprintTypeID() {
		return this.blueprintTypeID;
	}

	public void setNumberPerRun(final int numberPerRun) {
		this.numberPerRun = numberPerRun;
	}

	public int getNumberPerRun() {
		return this.numberPerRun;
	}

	public void setProductionEfficiency(final int productionEfficiency) {
		this.productionEfficiency = productionEfficiency;
	}

	public int getProductionEfficiency() {
		return this.productionEfficiency;
	}

	public void setSaleValue(final BigDecimal saleValue) {
		this.saleValue = saleValue;
	}

	public BigDecimal getSaleValue() {
		return this.saleValue;
	}

	public void setMaterialEfficiency(final int materialEfficiency) {
		this.materialEfficiency = materialEfficiency;
	}

	public int getMaterialEfficiency() {
		return this.materialEfficiency;
	}

	public Collection<BlueprintRequiredType> getRequiredTypes() {
		return this.requiredTypes;
	}

	public void setRequiredTypes(final Collection<BlueprintRequiredType> requiredTypes) {
		this.requiredTypes = requiredTypes;
	}

	public BlueprintCostSummary getCostSummary() {
		return this.costSummary;
	}

	public void setCostSummary(final BlueprintCostSummary costSummary) {
		this.costSummary = costSummary;
	}

	/**
	 * Convenience method which returns the name of the
	 * {@link #getBlueprintType() Blueprint}'s
	 * {@link InventoryBlueprintType#getProductType() Product Type}.
	 * 
	 * @return The blueprint's type name
	 */
	public String getTypeName() {
		return getBlueprintType().getProductType().getTypeName();
	}

	public InventoryBlueprintType getBlueprintType() {
		if (this.blueprintType == null) {
			throw new IllegalStateException("Missing raw record for blueprint " + this.blueprintTypeID
					+ ". You may need to import an updated EVE dump.");
		}
		return this.blueprintType;
	}

	public void setBlueprintType(final InventoryBlueprintType blueprintType) {
		this.blueprintType = blueprintType;
	}

	@Override
	public String toString() {
		return "Blueprint [blueprintTypeID=" + this.blueprintTypeID + ", blueprintType=" + this.blueprintType + ", numberPerRun="
				+ this.numberPerRun + ", productionEfficiency=" + this.productionEfficiency + ", saleValue=" + this.saleValue
				+ ", materialEfficiency=" + this.materialEfficiency + ", automaticallyUpdateSalePrice=" + this.automaticallyUpdateSalePrice
				+ ", costSummary=" + this.costSummary + ", requiredTypes=" + this.requiredTypes + "]";
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Blueprint
				&& this.blueprintTypeID == ((Blueprint) o).getBlueprintTypeID();
	}

	@Override
	public int hashCode() {
		return this.blueprintTypeID;
	}

	public int getProducedQuantity() {
		return getBlueprintType().getProducedQuantity();
	}

	public boolean isAutomaticallyUpdateSalePrice() {
		return this.automaticallyUpdateSalePrice;
	}

	public void setAutomaticallyUpdateSalePrice(final boolean automaticallyUpdateSalePrice) {
		this.automaticallyUpdateSalePrice = automaticallyUpdateSalePrice;
	}
}
