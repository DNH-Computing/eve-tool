package nz.net.dnh.eve.config;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import nz.net.dnh.eve.legacy.web.view.DateFormatDelegate;
import nz.net.dnh.eve.legacy.web.view.DurationFormatter;
import nz.net.dnh.eve.legacy.web.view.ImageURILocater;
import nz.net.dnh.eve.legacy.web.view.NumberFormatter;
import nz.net.dnh.eve.legacy.web.view.VersionReader;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan(basePackages = { "nz.net.dnh.eve" }, excludeFilters = { @Filter(value = AvoidComponentScan.class) })
public class RootConfig {
	private static final Resource EMPTY_RESOURCE = new ByteArrayResource(new byte[0]);

	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		final PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();

		final Properties environmentProperties = new Properties();
		environmentProperties.putAll(System.getenv());
		ppc.setProperties(environmentProperties);

		ppc.setLocations(new Resource[] {
				new ClassPathResource("/persistence.properties"),
				new ClassPathResource("/static.properties"),
				RootConfig.getOverrideResource() });
		return ppc;
	}

	@Bean
	public static ImageURILocater imageURILocator() {
		return new ImageURILocater();
	}

	@Bean
	public static NumberFormatter currencyFormatter() {
		final DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();

		formatter.setPositivePrefix("");
		formatter.setPositiveSuffix(" ISK");
		formatter.setNegativePrefix("-");
		formatter.setNegativeSuffix(" ISK");

		return new NumberFormatter(formatter, "??? ISK");
	}

	@Bean
	public static NumberFormatter percentageFormatter() {
		final DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance();

		formatter.setMaximumIntegerDigits(3);
		formatter.setMaximumFractionDigits(2);
		formatter.setGroupingUsed(false);
		formatter.setPositiveSuffix("%");
		formatter.setNegativeSuffix("%");

		return new NumberFormatter(formatter, "???");
	}

	@Bean
	public static DateFormatDelegate dateFormatter() {
		return new DateFormatDelegate(new SimpleDateFormat("d/M/yyyy"));
	}

	@Bean
	public static DateFormatDelegate dateTimeFormatter() {
		return new DateFormatDelegate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a"));
	}

	@Bean
	public static DurationFormatter durationFormatter() {
		return new DurationFormatter();
	}

	@Bean
	public static VersionReader versionReader() {
		return new VersionReader();
	}

	private static Resource getOverrideResource() {
		final Resource overrideResource = new ClassPathResource("/local.properties");
		if (overrideResource.exists())
			return overrideResource;
		return RootConfig.EMPTY_RESOURCE;
	}
}