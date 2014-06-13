package nz.net.dnh.eve.model.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nz.net.dnh.eve.model.raw.InventoryType;

@Entity
public class BlueprintTypeDecomposition implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BlueprintTypePK id;

	@Embeddable
	public static class BlueprintTypePK implements Serializable {
		private static final long serialVersionUID = 1L;

		@ManyToOne
		@JoinColumn(name = "blueprintTypeID")
		private Blueprint blueprint;

		@ManyToOne
		@JoinColumn(name = "materialTypeID")
		private InventoryType inventoryType;

		public BlueprintTypePK() {
		}

		public BlueprintTypePK(final Blueprint blueprint, final InventoryType inventoryType) {
			this.blueprint = blueprint;
			this.inventoryType = inventoryType;
		}

		public Blueprint getBlueprint() {
			return this.blueprint;
		}

		public void setBlueprint(final Blueprint blueprint) {
			this.blueprint = blueprint;
		}

		public InventoryType getInventoryType() {
			return this.inventoryType;
		}

		public void setInventoryType(final InventoryType inventoryType) {
			this.inventoryType = inventoryType;
		}

		@Override
		public String toString() {
			return "Blueprint=" + this.blueprint + ", Type=" + this.inventoryType;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.blueprint) + Objects.hashCode(this.inventoryType);
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof BlueprintTypePK))
				return false;
			final BlueprintTypePK other = (BlueprintTypePK) obj;
			return Objects.equals(this.blueprint, other.blueprint) && Objects.equals(this.inventoryType, other.inventoryType);
		}
	}

	public BlueprintTypeDecomposition() {
	}

	public BlueprintTypeDecomposition(final Blueprint blueprint, final InventoryType inventoryType) {
		this(new BlueprintTypePK(blueprint, inventoryType));
	}

	public BlueprintTypeDecomposition(final BlueprintTypePK id) {
		this.id = id;
	}

	public BlueprintTypePK getId() {
		return this.id;
	}

	public void setId(final BlueprintTypePK id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Blueprint Type Decomposition (" + this.id + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BlueprintTypeDecomposition && Objects.equals(this.id, ((BlueprintTypeDecomposition) obj).id);
	}

}
