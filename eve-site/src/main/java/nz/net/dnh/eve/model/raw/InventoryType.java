package nz.net.dnh.eve.model.raw;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "invTypes")
@NamedQueries({
		@NamedQuery(name = "InventoryType.findUnknownMineralsForBlueprint", query = InventoryType.UNKNOWN_TYPES_SQL
				+ InventoryType.MINERAL_SQL + " AND brt.id.blueprint = :blueprint" + InventoryType.ORDER_BY_NAME),
		@NamedQuery(name = "InventoryType.findUnknownMinerals", query = InventoryType.UNKNOWN_TYPES_SQL + InventoryType.MINERAL_SQL
				+ InventoryType.ORDER_BY_NAME),
		@NamedQuery(name = "InventoryType.findUnknownComponentsForBlueprint", query = InventoryType.UNKNOWN_TYPES_SQL
				+ InventoryType.COMPONENT_SQL + " AND brt.id.blueprint = :blueprint" + InventoryType.ORDER_BY_NAME),
		@NamedQuery(name = "InventoryType.findUnknownComponents", query = InventoryType.UNKNOWN_TYPES_SQL + InventoryType.COMPONENT_SQL
				+ InventoryType.ORDER_BY_NAME) })
public class InventoryType implements Serializable {
	// t.cost is not a nullable column, so this really checks whether t is null
	public static final String UNKNOWN_TYPES_SQL = "SELECT DISTINCT brt.id.inventoryType FROM BlueprintRequiredType brt LEFT OUTER JOIN brt.type t WHERE t.cost IS NULL";
	public static final String MINERAL_SQL = " AND brt.id.inventoryType.group.groupName = '"
			+ InventoryGroup.MINERAL_GROUP + "'";
	public static final String COMPONENT_SQL = " AND brt.id.inventoryType.group.groupName != '" + InventoryGroup.MINERAL_GROUP + "'";
	public static final String ORDER_BY_NAME = " ORDER BY brt.id.inventoryType.typeName";

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	private int typeID;

	@ManyToOne
	@JoinColumn(name = "groupID", updatable = false, insertable = false)
	private InventoryGroup group;

	@Max(100)
	private String typeName;

	@Max(3000)
	private String description;

	private Double mass;

	private Double volume;

	private Double capacity;

	private Integer portionSize;

	private BigDecimal basePrice;

	private Integer marketGroupID;

	private Double chanceOfDuplicating;

	public int getTypeID() {
		return this.typeID;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}

	public InventoryGroup getGroup() {
		return this.group;
	}

	public boolean isMineral() {
		return this.group.isMineral();
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Double getMass() {
		return this.mass;
	}

	public void setMass(final Double mass) {
		this.mass = mass;
	}

	public Double getVolume() {
		return this.volume;
	}

	public void setVolume(final Double volume) {
		this.volume = volume;
	}

	public Double getCapacity() {
		return this.capacity;
	}

	public void setCapacity(final Double capacity) {
		this.capacity = capacity;
	}

	/**
	 * From wiki.eve-id.net:
	 * 
	 * Portion size of item, if applicable. Portion size is size of group for
	 * reprocessing purposes, for example. It also represents the number of
	 * units produced by a production run of the item.
	 */
	public Integer getPortionSize() {
		return this.portionSize;
	}

	public void setPortionSize(final Integer portionSize) {
		this.portionSize = portionSize;
	}

	public BigDecimal getBasePrice() {
		return this.basePrice;
	}

	public void setBasePrice(final BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public Integer getMarketGroupID() {
		return this.marketGroupID;
	}

	public void setMarketGroupID(final Integer marketGroupID) {
		this.marketGroupID = marketGroupID;
	}

	public Double getChanceOfDuplicating() {
		return this.chanceOfDuplicating;
	}

	public void setChanceOfDuplicating(final Double chanceOfDuplicating) {
		this.chanceOfDuplicating = chanceOfDuplicating;
	}

	@Override
	public String toString() {
		return "InventoryType [typeID=" + this.typeID + ", group=" + this.group + ", typeName=" + this.typeName + ", description="
				+ this.description + ", mass=" + this.mass + ", volume=" + this.volume + ", capacity=" + this.capacity + ", portionSize="
				+ this.portionSize + ", basePrice=" + this.basePrice + ", marketGroupID=" + this.marketGroupID + ", chanceOfDuplicating="
				+ this.chanceOfDuplicating + "]";
	}

	@Override
	public int hashCode() {
		return this.typeID;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof InventoryType
				&& this.typeID == ((InventoryType) obj).getTypeID();
	}

}
