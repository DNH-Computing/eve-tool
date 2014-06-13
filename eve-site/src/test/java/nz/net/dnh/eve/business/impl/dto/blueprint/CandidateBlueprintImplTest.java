package nz.net.dnh.eve.business.impl.dto.blueprint;

import static org.junit.Assert.assertEquals;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.raw.InventoryType;

import org.junit.Before;
import org.junit.Test;

public class CandidateBlueprintImplTest {

	private InventoryBlueprintType inventoryBlueprint;
	private InventoryType inventoryType;

	@Before
	public void setup() {
		this.inventoryType = new InventoryType();
		this.inventoryType.setPortionSize(17);
		this.inventoryType.setTypeName("name");

		this.inventoryBlueprint = new InventoryBlueprintType();
		this.inventoryBlueprint.setBlueprintTypeID(3);
		this.inventoryBlueprint.setProductTypeID(50);
		this.inventoryBlueprint.setProductType(this.inventoryType);
	}

	@Test
	public void testSimpleGetters() {
		final CandidateBlueprintImpl candidateBlueprint = new CandidateBlueprintImpl(this.inventoryBlueprint);
		assertEquals(3, candidateBlueprint.getId());
		assertEquals("name", candidateBlueprint.getName());
		assertEquals(17, candidateBlueprint.getProducedQuantity());
		assertEquals(50, candidateBlueprint.getProducedTypeID());
	}
}
