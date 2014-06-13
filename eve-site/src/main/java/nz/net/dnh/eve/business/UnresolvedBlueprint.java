package nz.net.dnh.eve.business;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This interface holds the methods which do not require resolving all the required types of a blueprint.
 */
public interface UnresolvedBlueprint extends BlueprintInformation {

	/**
	 * @return The running cost of building the blueprint, in Isk. Never null
	 */
	public BigDecimal getRunningCost();

	/**
	 * @return The sale value of the blueprint, never null
	 */
	public BigDecimal getSaleValue();

	/**
	 * @return The date and time the sale value was last updated
	 */
	public Date getSaleValueLastUpdated();

	/**
	 * @return The number produced in a single run
	 */
	public int getNumberPerRun();

	/**
	 * @return The time for a single run (i.e. producing
	 *         {@link #getNumberPerRun()} products), in hours
	 */
	public int getHours();

	/**
	 * @return The material efficiency magical value
	 */
	public int getMaterialEfficiency();

	/**
	 * @return The production efficiency magical value
	 */
	public int getProductionEfficiency();

	/**
	 * @return Is the price for this blueprint being updated automatically
	 * @return
	 */
	public boolean isAutomaticallyUpdateSalePrice();

}