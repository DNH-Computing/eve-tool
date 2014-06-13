package nz.net.dnh.eve.business;

import java.math.BigDecimal;

import nz.net.dnh.eve.business.RequiredType.DecompositionState;

/**
 * A blueprint which must be made in order to make the current blueprint.
 * 
 * @see DecompositionState#DECOMPOSED
 * @see RequiredType#getDecompositionState()
 * @see RequiredTypes
 */
public class RequiredBlueprint {
	private final UnresolvedBlueprint typeBlueprint;
	private final int requiredUnits;
	private final int producedUnits;
	private final int runs;

	public RequiredBlueprint(final UnresolvedBlueprint typeBlueprint, final int requiredUnits, final int producedUnits, final int runs) {
		this.typeBlueprint = typeBlueprint;
		this.requiredUnits = requiredUnits;
		this.producedUnits = producedUnits;
		this.runs = runs;
	}

	/** @return The blueprint which must be made in order to make the parent blueprint */
	public UnresolvedBlueprint getTypeBlueprint() {
		return this.typeBlueprint;
	}

	/**
	 * Get the total number of this blueprint's produced type required by the parent blueprint.
	 * 
	 * @return The number of this type required to construct the blueprint
	 */
	public int getRequiredUnits() {
		return this.requiredUnits;
	}

	/**
	 * Get the total number of this blueprint's produced type to be created. This may be greater than {@link #getRequiredUnits() the number
	 * required to construct the parent blueprint} if the blueprint {@link BlueprintSummary#getProducedQuantity() produces more than one
	 * item at a time}.
	 * 
	 * @return The number of this type to be created
	 */
	public int getProducedUnits() {
		return this.producedUnits;
	}
	
	public BigDecimal getProductionCost() {
		return this.typeBlueprint.getRunningCost();
	}

	/**
	 * Get the number of runs of this blueprint which must be made in order to satisfy the number of {@link #getRequiredUnits() units
	 * required}
	 * 
	 * @return The number of runs of this blueprint to do
	 * @see #getRequiredUnits()
	 * @see BlueprintInformation#getProducedQuantity()
	 */
	public int getRuns() {
		return this.runs;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RequiredBlueprint)) {
			return false;
		}
		final RequiredBlueprint other = (RequiredBlueprint) obj;
		return this.typeBlueprint.equals(other.typeBlueprint) && this.requiredUnits == other.requiredUnits && this.runs == other.runs;
	}

	@Override
	public int hashCode() {
		return this.typeBlueprint.hashCode();
	}

	@Override
	public String toString() {
		return "Required Blueprint [typeBlueprint=" + this.typeBlueprint + ", units=" + this.requiredUnits + ", runs=" + this.runs + "]";
	}
}
