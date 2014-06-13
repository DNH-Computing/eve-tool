package nz.net.dnh.eve.business.impl.dto.type;

import java.math.BigDecimal;
import java.util.Date;

import nz.net.dnh.eve.business.Component;
import nz.net.dnh.eve.business.Mineral;
import nz.net.dnh.eve.model.raw.InventoryType;

public abstract class AbstractMissingTypeImpl extends AbstractSortableTypeImpl {
	public static class MissingMineralImpl extends AbstractMissingTypeImpl
			implements Mineral {
		public MissingMineralImpl(final InventoryType inventoryType) {
			super(inventoryType);
		}

		@Override
		public String toString() {
			return "Mineral " + super.toString();
		}

		@Override
		public boolean isMineral() {
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof MissingMineralImpl && super.equals(obj);
		}
	}

	public static class MissingComponentImpl extends AbstractMissingTypeImpl
			implements Component {
		public MissingComponentImpl(final InventoryType inventoryType) {
			super(inventoryType);
		}

		@Override
		public String toString() {
			return "Component " + super.toString();
		}

		@Override
		public boolean isMineral() {
			return false;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof MissingComponentImpl && super.equals(obj);
		}
	}

	private final InventoryType inventoryType;

	public AbstractMissingTypeImpl(final InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}

	public InventoryType toInventoryType() {
		return this.inventoryType;
	}

	@Override
	public int getId() {
		return this.inventoryType.getTypeID();
	}

	@Override
	public String getName() {
		return this.inventoryType.getTypeName();
	}

	@Override
	public BigDecimal getCost() {
		return null;
	}

	@Override
	public Date getCostLastUpdated() {
		return null;
	}

	@Override
	public boolean isMissing() {
		return true;
	}

	@Override
	public String toString() {
		return "(Missing) [name=" + getName() + "]";
	}

	@Override
	public int hashCode() {
		return this.inventoryType.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AbstractMissingTypeImpl
				&& this.inventoryType
						.equals(((AbstractMissingTypeImpl) obj).inventoryType);
	}

	@Override
	public boolean isAutoUpdate() {
		return false;
	}
}
