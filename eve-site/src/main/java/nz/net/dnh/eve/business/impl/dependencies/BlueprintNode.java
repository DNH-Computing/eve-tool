package nz.net.dnh.eve.business.impl.dependencies;

import java.util.List;
import java.util.Map;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.RequiredBlueprint;
import nz.net.dnh.eve.business.RequiredType;
import nz.net.dnh.eve.business.impl.dto.blueprint.RequiredBlueprintSummaryImpl;
import nz.net.dnh.eve.model.domain.Blueprint;

public class BlueprintNode extends AbstractNode<Blueprint, BlueprintDependencyState> {

	protected final Blueprint blueprint;
	private final AbstractType blueprintType;
	private List<RequiredType<? extends AbstractType>> requiredTypes;

	public BlueprintNode(final Blueprint blueprint, final AbstractType blueprintType) {
		this.blueprint = blueprint;
		this.blueprintType = blueprintType;
	}

	public void setRequiredTypes(final List<RequiredType<? extends AbstractType>> requiredTypes) {
		if (this.requiredTypes != null)
			throw new IllegalStateException("Required types already set for blueprint " + this.blueprint);
		this.requiredTypes = requiredTypes;
	}

	@Override
	public Blueprint getKey() {
		return this.blueprint;
	}

	@Override
	protected void doApply(final BlueprintDependencyState state) {
		if (this.requiredTypes == null)
			throw new IllegalStateException("Required types not set for blueprint " + this.blueprint);
		final int runs = getBlueprintCountAndUpdateState(state);
		for (final RequiredType<? extends AbstractType> requiredType : this.requiredTypes) {
			incrementMapValue(state.getCounts(), requiredType.getType(), runs * requiredType.getUnits());
		}
	}

	private int getBlueprintCountAndUpdateState(final BlueprintDependencyState state) {
		final int requiredBlueprints = getRequiredBlueprints(state);

		final int producedQuantity = this.blueprint.getProducedQuantity();
		// divide requiredBlueprints by producedQuantity, rounding up
		final int runs = (requiredBlueprints + producedQuantity - 1) / producedQuantity;

		state.getRequiredBlueprints().add(new RequiredBlueprint(new RequiredBlueprintSummaryImpl(this.blueprint, runs), requiredBlueprints, runs * producedQuantity, runs));
		return runs;
	}

	protected int getRequiredBlueprints(final BlueprintDependencyState state) {
		final Integer requiredBlueprints = state.getCounts().remove(this.blueprintType);
		if (requiredBlueprints == null)
			throw new IllegalStateException("Blueprint " + this.blueprintType + " not found in counts " + state.getCounts());
		return requiredBlueprints;
	}

	@Override
	public int hashCode() {
		return this.blueprint.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BlueprintNode && this.blueprint.equals(((BlueprintNode) obj).blueprint);
	}

	@Override
	public String toString() {
		return "Blueprint Node [blueprint=" + this.blueprint + ", blueprintType=" + this.blueprintType + ", requiredTypes="
				+ this.requiredTypes + ", dependencies=" + getDependencies() + "]";
	}

	private static <K> void incrementMapValue(final Map<K, Integer> map, final K key, final int incrementBy) {
		final Integer existingValue = map.get(key);
		final int newValue = existingValue == null ? incrementBy : existingValue + incrementBy;
		map.put(key, newValue);
	}
}
