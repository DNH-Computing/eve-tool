package nz.net.dnh.eve.business.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.domain.BlueprintRequiredType;
import nz.net.dnh.eve.model.domain.BlueprintRequiredType.BlueprintRequiredTypeId;
import nz.net.dnh.eve.model.domain.Type;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.raw.InventoryType;

import org.junit.Before;

/** Helper methods and fields for dealing with types */
public abstract class AbstractTypesTest {

	protected static BlueprintRequiredType createRequiredType(final Blueprint b, final Type type, final InventoryType inventoryType,
			final int units, final boolean decomposed, final InventoryBlueprintType materialBlueprintType, final Blueprint materialBlueprint) {
		final BlueprintRequiredType requiredType = new BlueprintRequiredType();
		requiredType.setId(new BlueprintRequiredTypeId(b, inventoryType));
		requiredType.setType(type);
		requiredType.setUnits(units);
		requiredType.setDecomposed(decomposed);
		requiredType.setMaterialBlueprintType(materialBlueprintType);
		requiredType.setMaterialBlueprint(materialBlueprint);
		return requiredType;
	}

	protected final Type type1 = mock(Type.class);
	protected final Type type2 = mock(Type.class);
	protected final InventoryType component1 = mock(InventoryType.class);
	protected final InventoryType component2 = mock(InventoryType.class);
	protected final InventoryType mineral1 = mock(InventoryType.class);
	protected final InventoryType mineral2 = mock(InventoryType.class);

	@Before
	public void setup() {
		when(this.component1.isMineral()).thenReturn(false);
		when(this.component2.isMineral()).thenReturn(false);
		when(this.mineral1.isMineral()).thenReturn(true);
		when(this.mineral2.isMineral()).thenReturn(true);
	}

}
