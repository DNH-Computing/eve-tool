package nz.net.dnh.eve.business;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Provides access to Types
 */
@Service
public interface TypeService {
	/**
	 * List the minerals in the system, optionally including minerals which are
	 * referenced by blueprints but missing from the database.
	 * 
	 * @param includeMissing
	 *            Whether to include minerals that are missing from the database
	 * @return The list of minerals, ordered by name
	 * @see #listMissingTypes()
	 */
	public List<Mineral> listMinerals(boolean includeMissing);

	/**
	 * List the components in the system, optionally including components which
	 * are referenced by blueprints but missing from the database.
	 * 
	 * @param includeMissing
	 *            Whether to include components that are missing from the
	 *            database
	 * @return The list of components, ordered by name
	 * @see #listMissingTypes()
	 */
	public List<Component> listComponents(boolean includeMissing);

	/**
	 * List minerals and components which are referenced by blueprints but
	 * missing from the database
	 *
	 * @return The list of minerals and components referenced by blueprints but
	 *         missing from the database
	 */
	public List<? extends AbstractType> listMissingTypes();

	/**
	 * List minerals and components which are referenced by the given blueprint
	 * but missing from the database
	 *
	 * @param The
	 *            blueprint to check for missing references
	 * @return The list of minerals and components referenced by the given
	 *         blueprint but missing from the database
	 */
	public List<? extends AbstractType> listMissingTypes(BlueprintReference blueprint);

	/**
	 * Set whether the given type should be decomposed when building the given blueprint.
	 * 
	 * @param blueprint
	 *            The blueprint which requires the type
	 * @param type
	 *            The required type
	 * @param decompose
	 *            True if the type should be decomposed when building the given blueprint
	 * @throws IllegalArgumentException
	 *             if the blueprint does not require the type
	 */
	public void updateRequiredType(BlueprintReference blueprint, TypeReference type, boolean decompose);

	/**
	 * Get the mineral with the given id (from {@link Mineral#getId()})
	 *
	 * @param type
	 *            A reference to the mineral to retrieve
	 * @return The mineral with the given id, or null if no such mineral exists
	 */
	public Mineral getMineral(TypeReference type);

	/**
	 * Get the component with the given id (from {@link Component#getId()})
	 *
	 * @param id
	 *            A reference to the component to retrieve
	 * @return The component with the given id, or null if no such component
	 *         exists
	 */
	public Component getComponent(TypeReference type);

	/**
	 * Create the mineral with the given id (from {@link Mineral#getId()})
	 * 
	 * @param id
	 *            A reference to the mineral to retrieve
	 * @param cost
	 *            The cost of the mineral
	 * @param autoUpdate
	 *            Is the type to be auto-updated
	 * @return The newly created mineral with the given id
	 * @throws IllegalArgumentException
	 *             if no such missing mineral exists
	 */
	Mineral createMissingMineral(TypeReference type, BigDecimal cost, boolean autoUpdate);

	/**
	 * Create the component with the given id (from {@link Component#getId()})
	 * 
	 * @param id
	 *            A reference to the component to retrieve
	 * @param cost
	 *            The cost of the component
	 * @param autoUpdate
	 *            Is the type to be updated automatically
	 * @return The newly created component with the given id
	 * @throws IllegalArgumentException
	 *             if no such missing component exists
	 */
	Component createMissingComponent(TypeReference type, BigDecimal cost, boolean autoUpdate);

	/**
	 * Update the given mineral with a new cost.
	 * 
	 * @param mineral
	 *            A reference to the mineral to update
	 * @param cost
	 *            The new cost of the mineral
	 * @param autoUpdate
	 *            Is the type to be updated automatically
	 * @return The updated mineral
	 * @throws IllegalArgumentException
	 *             if no such mineral exists
	 */
	Mineral updateMineral(TypeReference mineral, BigDecimal cost, boolean autoUpdate);

	/**
	 * Update the given component with a new cost.
	 * 
	 * @param component
	 *            A reference to the component to update
	 * @param cost
	 *            The new cost of the component
	 * @param autoUpdate
	 *            Is the type to be updated automatically
	 * @return The updated component
	 * @throws IllegalArgumentException
	 *             if no such component exists
	 */
	Component updateComponent(TypeReference component, BigDecimal cost, boolean autoUpdate);

	/**
	 * Get the current market data for the given types.
	 *
	 * @param types
	 *            The types to query for
	 * @return the current market data
	 */
	MarketPrices<? extends AbstractType> getMarketPrices(
			Collection<TypeReference> types);

	/**
	 * @return all the types marked for auto update.
	 */
	public Collection<AbstractType> getTypesForAutomaticUpdate();
}
