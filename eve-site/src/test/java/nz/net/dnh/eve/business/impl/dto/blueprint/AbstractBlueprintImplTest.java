package nz.net.dnh.eve.business.impl.dto.blueprint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;

import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.domain.BlueprintCostSummary;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.raw.InventoryType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public abstract class AbstractBlueprintImplTest<T extends AbstractBlueprintImpl> {

	@Mock
	protected Timestamp lastUpdated;

	protected Blueprint blueprint = new Blueprint();
	protected BlueprintCostSummary costSummary = new BlueprintCostSummary();
	protected InventoryType productType = new InventoryType();
	protected InventoryBlueprintType blueprintType = new InventoryBlueprintType();

	protected T impl;

	@Before
	public void abstractSetup() {
		this.blueprint.setNumberPerRun(70);
		this.blueprint.setLastUpdated(this.lastUpdated);
		this.blueprint.setBlueprintTypeID(111);
		this.blueprint.setProductionEfficiency(-9);
		this.blueprint.setMaterialEfficiency(17);
		this.blueprint.setAutomaticallyUpdateSalePrice(true);

		this.blueprint.setCostSummary(this.costSummary);

		this.blueprintType.setProductType(this.productType);

		this.blueprint.setBlueprintType(this.blueprintType);
	}

	@Test
	public void simpleGetters() {
		assertEquals(this.lastUpdated, this.impl.getSaleValueLastUpdated());
		assertEquals(111, this.impl.getId());
		assertEquals(-9, this.impl.getProductionEfficiency());
		assertEquals(17, this.impl.getMaterialEfficiency());
		assertTrue(this.impl.isAutomaticallyUpdateSalePrice());
	}

	@Test
	public void blueprintTypeGetters() {
		this.productType.setTypeName("typeName");
		this.productType.setPortionSize(79);
		this.blueprintType.setProductTypeID(555);
	
		assertEquals("typeName", this.impl.getName());
		assertEquals(555, this.impl.getProducedTypeID());
		assertEquals(79, this.impl.getProducedQuantity());
	}

	@Test
	public void costSummaryGetters() {
		this.costSummary.setSaleValue(new BigDecimal("5.20"));
	
		assertEquals(new BigDecimal("5.20"), this.impl.getSaleValue());
	}

}