package nz.net.dnh.eve.business.impl.dependencies;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.RequiredBlueprint;

public class BlueprintDependencyState {
	private final SortedMap<AbstractType, Integer> counts = new TreeMap<>();
	private final List<RequiredBlueprint> requiredBlueprints = new ArrayList<>();

	public SortedMap<AbstractType, Integer> getCounts() {
		return this.counts;
	}

	public List<RequiredBlueprint> getRequiredBlueprints() {
		return this.requiredBlueprints;
	}

	@Override
	public String toString() {
		return "StateWrapper [counts=" + this.counts + ", requiredBlueprints=" + this.requiredBlueprints + "]";
	}

	@Override
	public int hashCode() {
		return this.counts.hashCode() + this.requiredBlueprints.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof BlueprintDependencyState))
			return false;
		final BlueprintDependencyState other = (BlueprintDependencyState) obj;
		return this.counts.equals(other.counts) && this.requiredBlueprints.equals(other.requiredBlueprints);
	}

}
