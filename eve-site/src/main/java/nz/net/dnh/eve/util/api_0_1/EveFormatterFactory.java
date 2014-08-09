/**
 * 
 */
package nz.net.dnh.eve.util.api_0_1;

import java.math.BigDecimal;
import java.util.Locale;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.format.DefaultFormatterFactory;
import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.integration.spring.SpringHelper;
import nz.net.dnh.eve.web.view.NumberFormatter;

/**
 * 
 */
public class EveFormatterFactory extends DefaultFormatterFactory {
	public static final String FORMAT_ISK = "isk";
	public static final String FORMAT_PERCENTAGE = "isk";
	
	@SpringBean("currencyFormatter")
	private NumberFormatter iskFormatter;
	@SpringBean
	private NumberFormatter percentageFormatter;

	@Override
	public void init(final Configuration configuration) throws Exception {
		super.init(configuration);

		SpringHelper.injectBeans(this, configuration.getServletContext());
	}

	@Override
	public Formatter<?> getFormatter(final Class<?> clazz, final Locale locale, final String formatType, final String formatPattern) {
		if (clazz == BigDecimal.class && FORMAT_ISK.equals(formatType))
			return new IskFormatter();

		if (clazz == BigDecimal.class && FORMAT_PERCENTAGE.equals(formatType))
			return new PercentageFormatter();
		
		return super.getFormatter(clazz, locale, formatType, formatPattern);
	}
	
	public class IskFormatter implements Formatter<BigDecimal> {
		@Override
		public void setFormatType(final String formatType) {
		}

		@Override
		public void setFormatPattern(final String formatPattern) {
		}

		@Override
		public void setLocale(final Locale locale) {
		}

		@Override
		public void init() {
		}

		@Override
		public String format(final BigDecimal input) {
			return EveFormatterFactory.this.iskFormatter.format(input);
		}
	}

	public class PercentageFormatter implements Formatter<BigDecimal> {
		@Override
		public void setFormatType(final String formatType) {
		}

		@Override
		public void setFormatPattern(final String formatPattern) {
		}

		@Override
		public void setLocale(final Locale locale) {
		}

		@Override
		public void init() {
		}

		@Override
		public String format(final BigDecimal input) {
			return EveFormatterFactory.this.percentageFormatter.format(input);
		}
	}
}
