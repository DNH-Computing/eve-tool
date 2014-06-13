package nz.net.dnh.eve.business.impl.dto.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.sql.Timestamp;

import nz.net.dnh.eve.business.impl.dto.type.AbstractTypeImpl.ComponentImpl;
import nz.net.dnh.eve.business.impl.dto.type.AbstractTypeImpl.MineralImpl;
import nz.net.dnh.eve.model.domain.Type;
import nz.net.dnh.eve.model.raw.InventoryType;

import org.junit.Test;

public class TypeImplTest extends AbstractSortableTypeImplTest<Type, AbstractTypeImpl> {

	private static final BigDecimal COST = new BigDecimal("9.99");
	private static final boolean AUTO_UPDATE = true;
	private static final Timestamp LAST_UPDATED = new Timestamp(1111111);

	public TypeImplTest(final boolean shouldBeMineral) {
		super(shouldBeMineral, false);
	}

	@Override
	protected Type constructWrappedClass(final int id, final String name) {
		final InventoryType inventoryType = new InventoryType();
		inventoryType.setTypeName(name);

		final Type type = new Type(id, COST, AUTO_UPDATE);
		type.setType(inventoryType);
		type.setLastUpdated(LAST_UPDATED);
		return type;
	}

	@Override
	protected AbstractTypeImpl constructComponent(final Type wrapped) {
		return new ComponentImpl(wrapped);
	}

	@Override
	protected AbstractTypeImpl constructMineral(final Type wrapped) {
		return new MineralImpl(wrapped);
	}

	@Test
	public void toTypeReturnsWrappedType() {
		assertSame(this.wrappedObject, this.impl.toType());
	}

	@Test
	public void getCostPassesThrough() {
		assertEquals(COST, this.impl.getCost());
	}

	@Test
	public void getCostLastUpdatedPassesThrough() {
		assertEquals(LAST_UPDATED, this.impl.getCostLastUpdated());
	}

	@Test
	public void isAutoUpdatePassesThrough() {
		assertEquals(AUTO_UPDATE, this.impl.isAutoUpdate());
	}

}
