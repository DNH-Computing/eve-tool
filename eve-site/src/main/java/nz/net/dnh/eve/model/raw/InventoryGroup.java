package nz.net.dnh.eve.model.raw;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "invGroups")
public class InventoryGroup implements Serializable {
	public static final String MINERAL_GROUP = "Mineral";
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	private int groupID;

	@ManyToOne
	@JoinColumn(name = "categoryID", updatable = false, insertable = false)
	private InventoryCategory category;
	@Max(100)
	private String groupName;
	@Max(3000)
	private String description;
	private Integer iconID;

	public int getGroupID() {
		return this.groupID;
	}

	public void setGroupID(final int groupID) {
		this.groupID = groupID;
	}

	public InventoryCategory getCategory() {
		return this.category;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Integer getIconID() {
		return this.iconID;
	}

	public void setIconID(final Integer iconID) {
		this.iconID = iconID;
	}

	public boolean isMineral() {
		return MINERAL_GROUP.equals(this.groupName);
	}

	@Override
	public String toString() {
		return "InventoryGroup [groupID=" + this.groupID + ", category=" + this.category + ", groupName=" + this.groupName
				+ ", description=" + this.description + ", iconID=" + this.iconID + "]";
	}

	@Override
	public int hashCode() {
		return this.groupID;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof InventoryGroup
				&& this.groupID == ((InventoryGroup) obj).getGroupID();
	}
}
