/**
 * 
 */
package nz.net.dnh.eve.web.blueprint;

import java.math.BigDecimal;

import nz.net.dnh.eve.business.BlueprintSummary;

/**
 * This is the form that is used to bind the results of a create or update query to a blueprint
 * 
 * @author Daniel Hodder (danielh)
 * 
 */
public final class BlueprintForm {
	private BigDecimal saleValue;
	private int numberPerRun;
	private int materialEfficency;
	private int productionEffiecincy;
	private boolean automaticPriceUpdate;

	public BlueprintForm() {
	}

	public BlueprintForm(final BlueprintSummary summary) {
		setSaleValue(summary.getSaleValue());
		setNumberPerRun(summary.getNumberPerRun());
		setMaterialEfficency(summary.getMaterialEfficiency());
		setProductionEffiecincy(summary.getProductionEfficiency());
	}

	public BigDecimal getSaleValue() {
		return this.saleValue;
	}

	public void setSaleValue(final BigDecimal saleValue) {
		this.saleValue = saleValue;
	}

	public int getNumberPerRun() {
		return this.numberPerRun;
	}

	public void setNumberPerRun(final int numberPerRun) {
		this.numberPerRun = numberPerRun;
	}

	public int getMaterialEfficency() {
		return this.materialEfficency;
	}

	public void setMaterialEfficency(final int materialEfficency) {
		this.materialEfficency = materialEfficency;
	}

	public int getProductionEffiecincy() {
		return this.productionEffiecincy;
	}

	public void setProductionEffiecincy(final int productionEffiecincy) {
		this.productionEffiecincy = productionEffiecincy;
	}

	public boolean isAutomaticPriceUpdate() {
		return this.automaticPriceUpdate;
	}

	public boolean getAutomaticPriceUpdate() {
		return this.automaticPriceUpdate;
	}

	public void setAutomaticPriceUpdate(final boolean automaticPriceUpdate) {
		this.automaticPriceUpdate = automaticPriceUpdate;
	}
}