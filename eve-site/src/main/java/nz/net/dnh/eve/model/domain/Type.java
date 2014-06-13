package nz.net.dnh.eve.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import nz.net.dnh.eve.model.raw.InventoryGroup;
import nz.net.dnh.eve.model.raw.InventoryType;

@Entity
@NamedQueries({
 @NamedQuery(name = "Type.findAllMinerals", query = Type.TYPE_ALL_QUERY_PREFIX + '=' + Type.TYPE_ALL_QUERY_SUFFIX),
		@NamedQuery(name = "Type.findAllComponents", query = Type.TYPE_ALL_QUERY_PREFIX + "!=" + Type.TYPE_ALL_QUERY_SUFFIX),
		@NamedQuery(name = "Type.findAllAutoUpdatingTypes", query = Type.TYPE_BASE_QUERY + "t.autoUpdate = true") })
public class Type extends AbstractLastUpdatedBean implements Serializable {
	public static final String TYPE_BASE_QUERY = "select t from Type t where ";
	public static final String TYPE_ALL_QUERY_PREFIX = TYPE_BASE_QUERY + "t.type.group.groupName ";
	public static final String TYPE_ALL_QUERY_SUFFIX = "'" + InventoryGroup.MINERAL_GROUP + "' order by t.type.typeName";

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	private int typeID;

	@OneToOne
	@JoinColumn(name = "typeID", updatable = false, insertable = false)
	private InventoryType type;

	@NotNull
	private BigDecimal cost;

	@OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
	private Collection<BlueprintRequiredType> requiredBy;

	@NotNull
	private boolean autoUpdate;

	public Type() {
	}

	public Type(final int typeID, final BigDecimal cost, final boolean autoUpdate) {
		this.typeID = typeID;
		this.autoUpdate = autoUpdate;
		this.cost = cost;

		if (this.autoUpdate && this.cost == null) {
			this.cost = new BigDecimal(0);
		}

		touchLastUpdated();
	}

	public int getTypeID() {
		return this.typeID;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCost(final BigDecimal cost) {
		this.cost = cost;
	}

	public InventoryType getType() {
		return this.type;
	}

	public void setType(final InventoryType type) {
		this.type = type;
	}

	public void setRequiredBy(final Collection<BlueprintRequiredType> requiredBy) {
		this.requiredBy = requiredBy;
	}

	public Collection<BlueprintRequiredType> getRequiredBy() {
		return this.requiredBy;
	}

	/**
	 * Convenience method which returns the name of this type.
	 * 
	 * @return The blueprint's type name
	 */
	public String getTypeName() {
		return getType().getTypeName();
	}

	@Override
	public String toString() {
		return "Type [typeID=" + this.typeID + ", type=" + this.type + ", cost=" + this.cost + ", lastUpdated=" + getLastUpdated() + "]";
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Type && this.typeID == ((Type) obj).getTypeID();
	}

	@Override
	public int hashCode() {
		return this.typeID;
	}

	public boolean isAutoUpdate() {
		return this.autoUpdate;
	}

	public void setAutoUpdate(final boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
}
