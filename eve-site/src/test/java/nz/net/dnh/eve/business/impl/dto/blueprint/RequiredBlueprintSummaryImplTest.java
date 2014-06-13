package nz.net.dnh.eve.business.impl.dto.blueprint;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RequiredBlueprintSummaryImplTest extends AbstractBlueprintImplTest<RequiredBlueprintSummaryImpl> {

	private static final int RUNS = 177;

	@Before
	public void setup() {
		this.impl = new RequiredBlueprintSummaryImpl(this.blueprint, RUNS);
	}

	@Test
	public void numberPerRunPassedInConstructor() {
		assertEquals(RUNS, this.impl.getNumberPerRun());
	}

	@Test
	public void hoursCalculatedFromCostSummaryAndConstructor() {
		this.costSummary.setHoursForSingleRun(0.1);
		assertEquals(18, this.impl.getHours());

		this.costSummary.setHoursForSingleRun(10.5);
		assertEquals(1859 /* 177 * 10.5 = 1858.5 */, this.impl.getHours());
	}

	@Test
	public void runningCostCalculatedFromCostSummaryAndHours() {
		final BigDecimal costPerHour = new BigDecimal("1.12");
		final BigDecimal installCost = new BigDecimal("5000");
		final int hoursForSingleRun = 20;
		this.costSummary.setHoursForSingleRun(hoursForSingleRun);
		this.costSummary.setCostPerHour(costPerHour);
		this.costSummary.setInstallCost(installCost);

		final BigDecimal totalCostPerRun = costPerHour.multiply(new BigDecimal(hoursForSingleRun)).multiply(new BigDecimal(RUNS))
				.add(installCost);
		final BigDecimal expectedRunningCost = totalCostPerRun.divide(new BigDecimal(RUNS), BigDecimal.ROUND_HALF_UP);

		final BigDecimal actualRunningCost = this.impl.getRunningCost();
		assertEquals(expectedRunningCost, actualRunningCost);
		assertEquals(2, actualRunningCost.scale());
	}
}
