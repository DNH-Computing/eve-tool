/**
 * 
 */
package nz.net.dnh.eve.util.api_0_1;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.math.BigDecimal;

import net.sourceforge.stripes.format.Formatter;
import nz.net.dnh.eve.legacy.web.view.NumberFormatter;
import nz.net.dnh.eve.util.api_0_1.EveFormatterFactory.IskFormatter;
import nz.net.dnh.eve.util.api_0_1.EveFormatterFactory.PercentageFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EveFormatterFactoryTest {
	@InjectMocks
	private EveFormatterFactory eveFormatterFactory;

	@Mock(name = "iskFormatter")
	private NumberFormatter iskFormatter;
	@Mock
	private NumberFormatter percentageFormatter;

	@SuppressWarnings("unchecked")
	@Test
	public void getFormatterForABigDecimalAndFORMAT_ISKShouldReturnAnISKFormatter() {
		final Formatter<BigDecimal> formatter = (Formatter<BigDecimal>) this.eveFormatterFactory
				.getFormatter(BigDecimal.class, null, EveFormatterFactory.FORMAT_ISK, null);

		assertThat(formatter, instanceOf(IskFormatter.class));

		formatter.format(BigDecimal.ZERO);

		verify(this.iskFormatter).format(BigDecimal.ZERO);
		verifyZeroInteractions(this.percentageFormatter);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getFormatterForABigDecimalAndFORMAT_PERCENTAGEShouldReturnAnPercentageFormatter() {
		final Formatter<BigDecimal> formatter = (Formatter<BigDecimal>) this.eveFormatterFactory
				.getFormatter(BigDecimal.class, null, EveFormatterFactory.FORMAT_PERCENTAGE, null);

		assertThat(formatter, instanceOf(PercentageFormatter.class));

		formatter.format(BigDecimal.ZERO);

		verify(this.percentageFormatter).format(BigDecimal.ZERO);
		verifyZeroInteractions(this.iskFormatter);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void BigDecimalFormatsThatDoNotSpecifyOneOfTheNamedPattersDelegatesToTheSuperClass() {
		final Formatter<BigDecimal> formatter = (Formatter<BigDecimal>) this.eveFormatterFactory
				.getFormatter(BigDecimal.class, null, null, null);

		assertThat(formatter, not(anyOf(instanceOf(PercentageFormatter.class), instanceOf(IskFormatter.class))));

		formatter.format(BigDecimal.ZERO);

		verifyZeroInteractions(this.percentageFormatter, this.iskFormatter);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void objectsThatAreNotBigDecimalAreJustDelegated() {
		final Formatter<Number> formatter = ((Formatter<Number>) this.eveFormatterFactory
				.getFormatter(Number.class, null, null, null));

		assertThat(formatter, not(anyOf(instanceOf(PercentageFormatter.class), instanceOf(IskFormatter.class))));

		formatter.format(BigDecimal.ZERO);

		verifyZeroInteractions(this.percentageFormatter, this.iskFormatter);
	}
}
