package nz.net.dnh.eve.business.impl.dto.blueprint;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.RequiredBlueprint;
import nz.net.dnh.eve.business.RequiredType;
import nz.net.dnh.eve.business.RequiredTypes;
import nz.net.dnh.eve.business.UnresolvedBlueprint;
import nz.net.dnh.eve.business.impl.BlueprintRequiredTypesService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlueprintSummaryImplTest extends AbstractBlueprintImplTest<BlueprintSummaryImpl> {

	@Mock
	private BlueprintRequiredTypesService requiredTypesGenerator;
	private final List<RequiredType<? extends AbstractType>> requiredTypesTree = new ArrayList<>();
	private final SortedMap<AbstractType, Integer> resolvedRequiredTypes = new TreeMap<>();
	private final List<RequiredBlueprint> requiredBlueprints = new ArrayList<>();
	private final RequiredTypes requiredTypes = new RequiredTypes(this.requiredTypesTree, this.resolvedRequiredTypes,
			this.requiredBlueprints);

	@Before
	public void setup() {
		this.impl = new BlueprintSummaryImpl(this.blueprint, this.requiredTypesGenerator);

		when(this.requiredTypesGenerator.getRequiredTypes(this.impl)).thenReturn(this.requiredTypes);
	}

	private void addRequiredBlueprint(final BigDecimal runningCost) {
		final UnresolvedBlueprint unresolvedBlueprint = mock(UnresolvedBlueprint.class);
		when(unresolvedBlueprint.getRunningCost()).thenReturn(runningCost);
		this.requiredBlueprints.add(new RequiredBlueprint(unresolvedBlueprint, 0, 0, 0));
	}

	private void addResolvedRequiredType(final BigDecimal cost, final int units) {
		final AbstractType type = mock(AbstractType.class);
		when(type.getCost()).thenReturn(cost);

		// Mock compareTo because we're putting them into a TreeMap
		when(type.compareTo(type)).thenReturn(0);
		when(type.compareTo(argThat(not(equalTo(type))))).thenReturn(1);

		this.resolvedRequiredTypes.put(type, units);
	}

	@Test
	public void blueprintSummarySimpleGetters() {
		assertEquals(70, this.impl.getNumberPerRun());
	}

	@Test
	public void hoursCalculatedFromCostSummaryAndNumberPerRun() {
		this.blueprint.setNumberPerRun(1);
		this.costSummary.setHoursForSingleRun(0.1);
		assertEquals(1, this.impl.getHours());

		this.blueprint.setNumberPerRun(50);
		assertEquals(5, this.impl.getHours());

		this.blueprint.setNumberPerRun(3);
		this.costSummary.setHoursForSingleRun(10.5);
		assertEquals(32, this.impl.getHours());
	}

	@Test
	public void requiredTypesRetrievedFromRequiredTypesService() {
		assertEquals(this.requiredTypes, this.impl.getRequiredTypes());
		assertEquals(this.requiredTypes, this.impl.getRequiredTypes());

		// Should only be called once
		verify(this.requiredTypesGenerator).getRequiredTypes(this.impl);
	}

	@Test
	public void runningCostCalculatedFromSingleRequiredBlueprint() {
		addRequiredBlueprint(new BigDecimal("17.34"));

		assertEquals(new BigDecimal("17.34"), this.impl.getRunningCost());
	}

	@Test
	public void runningCostCalculatedFromMultipleRequiredBlueprint() {
		addRequiredBlueprint(new BigDecimal("17.34"));
		addRequiredBlueprint(new BigDecimal("10.00"));

		assertEquals(new BigDecimal("27.34"), this.impl.getRunningCost());
	}

	@Test
	public void materialCostCalculatedFromResolvedRequiredTypes() {
		addResolvedRequiredType(new BigDecimal("5.12"), 7);
		addResolvedRequiredType(new BigDecimal("100.00"), 1);
		addResolvedRequiredType(new BigDecimal("3.00"), 2);

		final BigDecimal expectedMaterialCost = new BigDecimal("5.12").multiply(new BigDecimal(7)) //
				.add(new BigDecimal("100.00")) //
				.add(new BigDecimal("3.00").multiply(new BigDecimal(2)));
		assertEquals(expectedMaterialCost, this.impl.getMaterialCost());
	}

	@Test
	public void materialCostReturnsNullIfRequiredTypeHasNullCost() {
		addResolvedRequiredType(new BigDecimal("5.12"), 7);
		addResolvedRequiredType(null, 1);
		addResolvedRequiredType(new BigDecimal("3.00"), 2);

		assertNull(this.impl.getMaterialCost());
	}

	@Test
	public void totalCostCalculatedFromMaterialCostAndRunningCost() {
		addRequiredBlueprint(new BigDecimal("10.02"));
		addResolvedRequiredType(new BigDecimal("100.10"), 1);

		assertEquals(new BigDecimal("110.12"), this.impl.getTotalCost());
	}

	@Test
	public void totalCostReturnsNullIfMaterialCostIsNull() {
		addRequiredBlueprint(new BigDecimal("10.02"));
		addResolvedRequiredType(null, 1);

		assertNull(this.impl.getTotalCost());
	}

	@Test
	public void profitAndProfitPercentageCalculatedFromSaleValueAndTotalCost() {
		final BigDecimal saleValue = new BigDecimal("500.00");
		final BigDecimal runningCost = new BigDecimal("10.02");
		final BigDecimal materialCost = new BigDecimal("100.10");
		final BigDecimal totalCost = materialCost.add(runningCost);

		this.costSummary.setSaleValue(saleValue);
		addRequiredBlueprint(runningCost);
		addResolvedRequiredType(materialCost, 1);

		final BigDecimal expectedProfit = saleValue.subtract(totalCost);
		assertEquals(expectedProfit, this.impl.getProfit());

		final BigDecimal actualProfitPercentage = this.impl.getProfitPercentage();
		final BigDecimal expectedProfitPercentage = expectedProfit.divide(saleValue, BigDecimal.ROUND_HALF_UP);
		assertEquals(expectedProfitPercentage, actualProfitPercentage);
		assertEquals(2, actualProfitPercentage.scale());
	}

	@Test
	public void profitReturnsNullIfTotalCostIsNull() {
		addRequiredBlueprint(new BigDecimal("10.02"));
		addResolvedRequiredType(null, 1);
		this.costSummary.setSaleValue(new BigDecimal("10.00"));

		assertNull(this.impl.getProfit());
	}

	@Test
	public void profitPercentageReturnsNullIfProfitIsNull() {
		addRequiredBlueprint(new BigDecimal("10.02"));
		addResolvedRequiredType(null, 1);
		this.costSummary.setSaleValue(new BigDecimal("10.00"));

		assertNull(this.impl.getProfitPercentage());
	}

	@Test
	public void profitPercentageReturnsNullIfSaleValueIsZero() {
		addRequiredBlueprint(new BigDecimal("10.02"));
		addResolvedRequiredType(new BigDecimal("22.02"), 1);
		this.costSummary.setSaleValue(BigDecimal.ZERO);

		assertNull(this.impl.getProfitPercentage());
	}
}
