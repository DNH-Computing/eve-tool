package nz.net.dnh.eve.business;

import java.util.List;

/** Represents a single type required by a blueprint for its construction */
public class RequiredType<T extends AbstractType> implements Comparable<RequiredType<?>> {
	/** Whether this required type is currently decomposed, or whether it could be decomposed in the future */
	public static enum DecompositionState {
		/**
		 * The blueprint exists, and this required type should be replaced with it. The blueprint will exist in
		 * {@link RequiredTypes#getRequiredBlueprints()}
		 */
		DECOMPOSED,
		/** The blueprint exists, but this required type should <b>not</b> be replaced with it */
		NOT_DECOMPOSED,
		/**
		 * A blueprint for this required type exists in EVE Online, but has not been added to the list of blueprints this tool knows about.
		 * <p>
		 * If the blueprint was added to the system, it would be possible to replace this required type with a blueprint
		 */
		NOT_CONFIGURED,
		/** It is never possible to replace this required type with a blueprint, because no blueprint for this type exists in EVE Online */
		NEVER_DECOMPOSED
	}

	private final T type;
	private final int units;
	private final UnresolvedBlueprint typeBlueprint;
	private final DecompositionState decompositionState;
	private final List<RequiredType<? extends AbstractType>> typeBlueprintRequiredTypes;

	public RequiredType(final T type, final int units, final UnresolvedBlueprint typeBlueprint,
			final DecompositionState decompositionState,
			final List<RequiredType<? extends AbstractType>> typeBlueprintRequiredTypes) {
		this.type = type;
		this.units = units;
		this.typeBlueprint = typeBlueprint;
		this.decompositionState = decompositionState;
		this.typeBlueprintRequiredTypes = typeBlueprintRequiredTypes;
	}

	/**
	 * @return The type the blueprint requires for its construction
	 */
	public T getType() {
		return this.type;
	}

	/**
	 * Get the number of this {@link #getType() type} required to construct the blueprint. These units are not multiplied by the number of
	 * blueprints required.
	 * 
	 * @return The number of this type required to construct the blueprint
	 */
	public int getUnits() {
		return this.units;
	}

	/**
	 * @return whether this required type is currently decomposed, or whether it could be decomposed in the future
	 */
	public DecompositionState getDecompositionState() {
		return this.decompositionState;
	}

	/**
	 * Get the types this type's {@link #getTypeBlueprint() blueprint} requires to create it if it is being {@link #getDecompositionState()
	 * decomposed}. Will be valued if {@link #getDecompositionState()} is {@link DecompositionState#DECOMPOSED} or
	 * {@link DecompositionState#NOT_DECOMPOSED}, and null otherwise.
	 * 
	 * @return The types this type requires to create it if it is being decomposed, or null if no blueprint is found
	 */
	public List<RequiredType<? extends AbstractType>> getTypeBlueprintRequiredTypes() {
		return this.typeBlueprintRequiredTypes;
	}

	/**
	 * Get the blueprint which should be used to create this required type if {@link #isDecomposed()} is true.
	 * 
	 * @return The blueprint which can create this required type, or null if no blueprint creates the required type
	 */
	public UnresolvedBlueprint getTypeBlueprint() {
		return this.typeBlueprint;
	}

	@Override
	public int compareTo(final RequiredType<?> o) {
		return this.type.compareTo(o.type);
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RequiredType))
			return false;
		final RequiredType<?> other = (RequiredType<?>) obj;
		return this.type.equals(other.type) && this.units == other.units;
	}

	@Override
	public String toString() {
		return "RequiredType [type=" + this.type + ", units=" + this.units + ", typeBlueprint=" + this.typeBlueprint
				+ ", decompositionState=" + this.decompositionState + ", typeBlueprintRequiredTypes=" + this.typeBlueprintRequiredTypes
				+ "]";
	}

}