package nz.net.dnh.eve.business.impl.dto.blueprint;

import java.math.BigDecimal;
import java.util.Date;

import nz.net.dnh.eve.business.UnresolvedBlueprint;
import nz.net.dnh.eve.model.domain.Blueprint;

public abstract class AbstractBlueprintImpl implements UnresolvedBlueprint {

	protected final Blueprint blueprint;

	public AbstractBlueprintImpl(final Blueprint blueprint) {
		this.blueprint = blueprint;
	}

	public Blueprint toBlueprint() {
		return this.blueprint;
	}

	@Override
	public String getName() {
		return this.blueprint.getTypeName();
	}

	@Override
	public BigDecimal getSaleValue() {
		return this.blueprint.getCostSummary().getSaleValue();
	}

	@Override
	public Date getSaleValueLastUpdated() {
		return this.blueprint.getLastUpdated();
	}

	@Override
	public int getId() {
		return this.blueprint.getBlueprintTypeID();
	}

	@Override
	public int getHours() {
		return (int) Math.ceil(this.blueprint.getCostSummary().getHoursForSingleRun() * getNumberPerRun());
	}

	@Override
	public int getProductionEfficiency() {
		return this.blueprint.getProductionEfficiency();
	}

	@Override
	public int getMaterialEfficiency() {
		return this.blueprint.getMaterialEfficiency();
	}

	@Override
	public int getProducedTypeID() {
		return this.blueprint.getBlueprintType().getProductTypeID();
	}

	@Override
	public int getProducedQuantity() {
		return this.blueprint.getProducedQuantity();
	}

	@Override
	public boolean isAutomaticallyUpdateSalePrice() {
		return this.blueprint.isAutomaticallyUpdateSalePrice();
	}

	@Override
	public String toString() {
		return "Blueprint Summary (" + this.blueprint + ")";
	}

}
