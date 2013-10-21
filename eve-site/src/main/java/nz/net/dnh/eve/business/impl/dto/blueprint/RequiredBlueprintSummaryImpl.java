package nz.net.dnh.eve.business.impl.dto.blueprint;

import java.math.BigDecimal;

import nz.net.dnh.eve.business.RequiredBlueprint;
import nz.net.dnh.eve.business.RequiredType;
import nz.net.dnh.eve.business.RequiredTypes;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.domain.BlueprintCostSummary;

/** Blueprint summary used when returning a {@link RequiredBlueprint} or {@link RequiredType} */
public class RequiredBlueprintSummaryImpl extends AbstractBlueprintSummary {

	private final int runs;

	public RequiredBlueprintSummaryImpl(final Blueprint blueprint, final int runs) {
		super(blueprint);
		this.runs = runs;
	}

	@Override
	public int getNumberPerRun() {
		return this.runs;
	}

	@Override
	public RequiredTypes getRequiredTypes() {
		// This also makes getMaterialCost(), getTotalCost(), getProfit() and getProfitPercentage() unavailable
		// TODO have a different interface for this case...
		throw new UnsupportedOperationException("Required types are not available for required blueprints");
	}

	@Override
	public BigDecimal getRunningCost() {
		final BlueprintCostSummary costSummary = this.blueprint.getCostSummary();
		final BigDecimal costPerHour = costSummary.getCostPerHour();
		final BigDecimal installCost = costSummary.getInstallCost();
		final BigDecimal hours = new BigDecimal(getHours());
		final BigDecimal numberPerRun = new BigDecimal(getNumberPerRun());
		return costPerHour.multiply(hours).add(installCost).divide(numberPerRun);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof RequiredBlueprintSummaryImpl && this.blueprint.equals(((RequiredBlueprintSummaryImpl) obj).blueprint);
	}

	@Override
	public int hashCode() {
		return this.blueprint.hashCode();
	}
}
