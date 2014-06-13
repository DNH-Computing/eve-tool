package nz.net.dnh.eve.business.impl.dependencies;

import nz.net.dnh.eve.model.domain.Blueprint;

public class RootBlueprintNode extends BlueprintNode {

	public RootBlueprintNode(final Blueprint blueprint) {
		super(blueprint, null);
	}

	@Override
	protected int getRequiredBlueprints(final BlueprintDependencyState state) {
		// For the root blueprint we always "require" the number which will be produced - that is, the produced quantity multiplied by the
		// number per run
		return this.blueprint.getProducedQuantity() * this.blueprint.getNumberPerRun();
	}
}
