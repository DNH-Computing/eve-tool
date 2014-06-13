package nz.net.dnh.eve.business;

import java.math.BigDecimal;

public interface BlueprintSummary extends UnresolvedBlueprint {
	/**
	 * @return The cost of the materials used to build the blueprint. May be null if the cost is unknown
	 */
	public BigDecimal getMaterialCost();

	/**
	 * Convenience method which returns the sum of material and running costs
	 *
	 * @return The total cost of building the blueprint, in Isk. May be null if the cost is unknown
	 */
	public BigDecimal getTotalCost();

	/**
	 * @return The profit from selling the blueprint, in Isk. May be null if the cost is unknown
	 */
	public BigDecimal getProfit();

	/**
	 * @return The profit from selling the blueprint, as a percentage of the sale value. May be null if the cost is unknown
	 */
	public BigDecimal getProfitPercentage();

	/**
	 * Get the minerals and components required to build the given blueprint, and their quantities.
	 * 
	 * @return The required minerals and components
	 */
	public RequiredTypes getRequiredTypes();
}
