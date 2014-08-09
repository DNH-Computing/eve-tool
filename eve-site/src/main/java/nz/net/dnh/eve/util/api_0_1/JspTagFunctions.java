/**
 * 
 */
package nz.net.dnh.eve.util.api_0_1;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.web.view.ImageURILocater;
import nz.net.dnh.eve.web.view.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * A class that holds simple class that holds function that are delegated to Spring Beans.
 */
public class JspTagFunctions {
	private static final Logger LOG = LoggerFactory.getLogger(JspTagFunctions.class);

	private static ImageURILocater imageURILocater;
	private static NumberFormatter currencyFormatter;
	private static NumberFormatter percentageFormatter;

	public static String getImageUriForTypeId(final int typeId, final int size) {
		LOG.trace("Getting URI for type id {} at size {}px", typeId, size);

		return imageURILocater.getUriForTypeID(typeId, size);
	}

	public static String getImageUriForType(final AbstractType type, final int size) {
		LOG.trace("Getting URI for type {} at size {}.px", type.getId(), size);

		return imageURILocater.getUriForType(type, size);
	}

	public static String formatAsCurrency(final Object value) {
		LOG.trace("Formatting '{}' as currency", value);

		return currencyFormatter.format(value);
	}

	public static String formatAsPercentage(final Object value) {
		LOG.trace("Formatting '{}' as a percentage", value);

		return percentageFormatter.format(value);
	}

	@Component
	public static final class SpringContextListner implements ApplicationListener<ContextRefreshedEvent> {
		@Override
		public void onApplicationEvent(final ContextRefreshedEvent event) {
			final ApplicationContext context = event.getApplicationContext();

			imageURILocater = context.getBean(ImageURILocater.class);
			currencyFormatter = context.getBean("currencyFormatter", NumberFormatter.class);
			percentageFormatter = context.getBean("percentageFormatter", NumberFormatter.class);
		}

	}
}
