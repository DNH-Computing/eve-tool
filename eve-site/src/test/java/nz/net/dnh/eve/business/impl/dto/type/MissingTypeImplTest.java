package nz.net.dnh.eve.business.impl.dto.type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import nz.net.dnh.eve.business.impl.dto.type.AbstractMissingTypeImpl.MissingComponentImpl;
import nz.net.dnh.eve.business.impl.dto.type.AbstractMissingTypeImpl.MissingMineralImpl;
import nz.net.dnh.eve.model.raw.InventoryType;

import org.junit.Test;

public class MissingTypeImplTest extends AbstractSortableTypeImplTest<InventoryType, AbstractMissingTypeImpl> {

	public MissingTypeImplTest(final boolean shouldBeMineral) {
		super(shouldBeMineral, true);
	}

	@Override
	protected InventoryType constructWrappedClass(final int id, final String name) {
		final InventoryType inventoryType = new InventoryType();
		inventoryType.setTypeID(id);
		inventoryType.setTypeName(name);
		return inventoryType;
	}

	@Override
	protected AbstractMissingTypeImpl constructComponent(final InventoryType wrapped) {
		return new MissingComponentImpl(wrapped);
	}

	@Override
	protected AbstractMissingTypeImpl constructMineral(final InventoryType wrapped) {
		return new MissingMineralImpl(wrapped);
	}

	@Test
	public void toTypeReturnsWrappedType() {
		assertSame(this.wrappedObject, this.impl.toInventoryType());
	}

	@Test
	public void gettersForMissingDataReturnNoData() {
		assertNull(this.impl.getCost());
		assertNull(this.impl.getCostLastUpdated());
		assertFalse(this.impl.isAutoUpdate());
	}
}
