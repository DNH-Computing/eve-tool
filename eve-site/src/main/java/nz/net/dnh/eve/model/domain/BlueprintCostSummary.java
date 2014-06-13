package nz.net.dnh.eve.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Read-only view of the costs of making a blueprint
 */
@Entity
@Table(name = "BlueprintCosts")
public class BlueprintCostSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	private int blueprintTypeID;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blueprintTypeID", updatable = false, insertable = false)
	private Blueprint blueprint;

	private BigDecimal materialCost;

	private BigDecimal costPerHour;

	private BigDecimal installCost;

	private BigDecimal saleValue;

	private double hoursForSingleRun;

	@Column(columnDefinition = "INT")
	private boolean containsDecomposed;

	public int getBlueprintTypeID() {
		return this.blueprintTypeID;
	}

	/**
	 * You probably don't want to call this method - if you are, consider adding
	 * the data you're retrieving to the BlueprintSummary view instead.
	 * 
	 * @return The blueprint this summary is for
	 */
	public Blueprint getBlueprint() {
		return this.blueprint;
	}

	public BigDecimal getMaterialCost() {
		return this.materialCost;
	}

	public void setMaterialCost(final BigDecimal materialCost) {
		this.materialCost = materialCost;
	}

	public BigDecimal getCostPerHour() {
		return this.costPerHour;
	}

	public void setCostPerHour(final BigDecimal costPerHour) {
		this.costPerHour = costPerHour;
	}

	public BigDecimal getInstallCost() {
		return this.installCost;
	}

	public void setInstallCost(final BigDecimal installCost) {
		this.installCost = installCost;
	}

	public BigDecimal getSaleValue() {
		return this.saleValue;
	}

	public void setSaleValue(final BigDecimal saleValue) {
		this.saleValue = saleValue;
	}

	public double getHoursForSingleRun() {
		return this.hoursForSingleRun;
	}

	public void setHoursForSingleRun(final double hoursForSingleRun) {
		this.hoursForSingleRun = hoursForSingleRun;
	}

	public boolean isContainsDecomposed() {
		return this.containsDecomposed;
	}

	public void setContainsDecomposed(final boolean containsDecomposed) {
		this.containsDecomposed = containsDecomposed;
	}

	@Override
	public String toString() {
		return "BlueprintCosts [blueprintTypeID=" + this.blueprintTypeID + ", materialCost=" + this.materialCost + ", installCost="
				+ this.installCost + ", costPerHour=" + this.costPerHour + ", saleValue=" + this.saleValue + ", hoursForSingleRun="
				+ this.hoursForSingleRun
				+ ", containsDecomposed=" + this.containsDecomposed + "]";
	}

	@Override
	public int hashCode() {
		return this.blueprintTypeID;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BlueprintCostSummary
				&& this.blueprintTypeID == ((BlueprintCostSummary) obj).blueprintTypeID;
	}

}
