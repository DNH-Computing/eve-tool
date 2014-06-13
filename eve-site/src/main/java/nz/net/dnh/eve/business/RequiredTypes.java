package nz.net.dnh.eve.business;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

/**
 * The types required for a blueprint
 */
public class RequiredTypes {
	private final List<RequiredType<? extends AbstractType>> requiredTypesTree;
	private final SortedMap<? extends AbstractType, Integer> resolvedRequiredTypes;
	private final List<RequiredBlueprint> requiredBlueprints;

	public RequiredTypes(final List<RequiredType<? extends AbstractType>> requiredTypesTree,
			final SortedMap<? extends AbstractType, Integer> resolvedRequiredTypes, final List<RequiredBlueprint> requiredBlueprints) {
		this.requiredTypesTree = Collections.unmodifiableList(requiredTypesTree);
		this.resolvedRequiredTypes = Collections.unmodifiableSortedMap(resolvedRequiredTypes);
		this.requiredBlueprints = Collections.unmodifiableList(requiredBlueprints);

	}

	/**
	 * Get the types required for this blueprint as a tree. This list contains the first level of the tree, which is the types directly
	 * required by this blueprint. Each type may itself contain a list of types it requires, and so forth.
	 * <p>
	 * This includes types which are marked for {@link RequiredType#getDecompositionState() decomposition}
	 * 
	 * @return The types required for a blueprint as a tree
	 */
	public List<RequiredType<? extends AbstractType>> getRequiredTypesTree() {
		return this.requiredTypesTree;
	}

	/**
	 * Get the blueprints which are configured to be decomposed to create this blueprint, including this blueprint.
	 * 
	 * @return A list of blueprints which must be made in order to make this blueprint. Never null or empty
	 * @see RequiredType#getDecompositionState()
	 */
	public List<RequiredBlueprint> getRequiredBlueprints() {
		return this.requiredBlueprints;
	}

	/**
	 * Get the types required for this blueprint. This list contains the aggregate types required directly by this blueprint and by its
	 * required types which may have been marked for {@link RequiredType#getDecompositionState() decomposition}.
	 * <p>
	 * Any types marked for {@link RequiredType#getDecompositionState() decomposition} will not appear in this map, but will be replaced by
	 * their own required types.
	 * 
	 * @return The resolved types required for a blueprint
	 */
	public SortedMap<? extends AbstractType, Integer> getResolvedRequiredTypes() {
		return this.resolvedRequiredTypes;
	}
}
