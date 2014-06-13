package nz.net.dnh.eve.business.impl.dto.blueprint;


import java.math.BigDecimal;
import java.util.Map.Entry;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.RequiredBlueprint;
import nz.net.dnh.eve.business.RequiredTypes;
import nz.net.dnh.eve.business.impl.BlueprintRequiredTypesService;
import nz.net.dnh.eve.model.domain.Blueprint;

public class BlueprintSummaryImpl extends AbstractBlueprintImpl implements BlueprintSummary {

	private RequiredTypes requiredTypes;
	private final BlueprintRequiredTypesService requiredTypesService;

	public BlueprintSummaryImpl(final Blueprint blueprint, final BlueprintRequiredTypesService requiredTypesGenerator) {
		super(blueprint);
		this.requiredTypesService = requiredTypesGenerator;
	}

	@Override
	public RequiredTypes getRequiredTypes() {
		// Lazily evaluated for the times when the list of required types is not needed
		if (this.requiredTypes == null) {
			this.requiredTypes = this.requiredTypesService.getRequiredTypes(this);
		}
		return this.requiredTypes;
	}

	@Override
	public BigDecimal getRunningCost() {
		BigDecimal runningCost = BigDecimal.ZERO;
		for (final RequiredBlueprint requiredBlueprint : getRequiredTypes().getRequiredBlueprints()) {
			runningCost = runningCost.add(requiredBlueprint.getProductionCost());
		}
		return runningCost;
	}

	@Override
	public int getNumberPerRun() {
		return this.blueprint.getNumberPerRun();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BlueprintSummaryImpl && this.blueprint.equals(((BlueprintSummaryImpl) obj).blueprint);
	}

	@Override
	public int hashCode() {
		return this.blueprint.hashCode();
	}

	@Override
	public BigDecimal getTotalCost() {
		final BigDecimal materialCost = getMaterialCost();
		if (materialCost == null) {
			return null;
		}
		return getRunningCost().add(materialCost);
	}

	@Override
	public BigDecimal getProfit() {
		final BigDecimal totalCost = getTotalCost();
		if (totalCost == null) {
			return null;
		}
		return getSaleValue().subtract(totalCost);
	}

	@Override
	public BigDecimal getProfitPercentage() {
		final BigDecimal profit = getProfit();
		final BigDecimal saleValue = getSaleValue();
		// Return null if we don't know our profit, or if our sale value is zero
		if (profit == null || saleValue.signum() == 0) {
			return null;
		}
		return profit.divide(saleValue, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public BigDecimal getMaterialCost() {
		BigDecimal materialCost = BigDecimal.ZERO;
		for (final Entry<? extends AbstractType, Integer> requiredType : getRequiredTypes().getResolvedRequiredTypes().entrySet()) {
			final BigDecimal requiredTypeCost = requiredType.getKey().getCost();
			if (requiredTypeCost == null)
				return null;
			final BigDecimal requiredUnits = BigDecimal.valueOf(requiredType.getValue());
			materialCost = materialCost.add(requiredTypeCost.multiply(requiredUnits));
		}
		return materialCost;
	}
}