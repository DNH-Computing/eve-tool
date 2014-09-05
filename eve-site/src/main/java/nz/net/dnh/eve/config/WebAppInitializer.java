package nz.net.dnh.eve.config;

import static java.util.stream.Collectors.joining;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.integration.spring.SpringInterceptor;
import nz.net.dnh.eve.stripes.api_0_1.CsrfInterceptor;
import nz.net.dnh.eve.stripes.api_0_1.EveToolActionBeanContext;
import nz.net.dnh.eve.stripes.api_0_1.EveToolActionBeanPopulatorInterceptor;
import nz.net.dnh.eve.util.api_0_1.EveFormatterFactory;
import nz.net.dnh.eve.web.blueprint.BlueprintDetailsActionBean;
import nz.net.dnh.eve.web.dashboard.DashboardActionBean;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.google.common.collect.Lists;

public class WebAppInitializer implements WebApplicationInitializer {

	@SuppressWarnings("unchecked")
	@Override
	public void onStartup(final ServletContext servletContext) throws ServletException {
		
		final Set<String> mappingConflicts = new HashSet<>();

		{ // Add Spring
			final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
			context.setConfigLocation("nz.net.dnh.eve.config");

			final FilterRegistration.Dynamic securityFilter = servletContext.addFilter("securityFilter", new DelegatingFilterProxy(
					"springSecurityFilterChain"));
			securityFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

			final FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter",
																								new CharacterEncodingFilter());
			characterEncodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
			characterEncodingFilter.setInitParameter("encoding", "UTF-8");
			characterEncodingFilter.setInitParameter("forceEncoding", "true");

			servletContext.addListener(new ContextLoaderListener(context));
			servletContext.setInitParameter("defaultHtmlEscape", "true");

			final DispatcherServlet servlet = new DispatcherServlet();
			// no explicit configuration reference here: everything is configured in the root container for simplicity
			servlet.setContextConfigLocation("");

			final ServletRegistration.Dynamic appServlet = servletContext.addServlet("appServlet", servlet);
			appServlet.setLoadOnStartup(1);
			appServlet.setAsyncSupported(true);

			// mappingConflicts.addAll(appServlet.addMapping("/"));
		}

		{ // Add Stripes
			final ServletRegistration.Dynamic stripesDispatcher = servletContext
					.addServlet("stripesDispatcher", net.sourceforge.stripes.controller.DispatcherServlet.class);
			stripesDispatcher.setLoadOnStartup(1);
			mappingConflicts.addAll(stripesDispatcher.addMapping("/"));

			final FilterRegistration.Dynamic stripesFilter = servletContext.addFilter("stripesFilter", StripesFilter.class);
			stripesFilter.setInitParameter(	"ActionResolver.Packages",
											Lists.newArrayList(DashboardActionBean.class, BlueprintDetailsActionBean.class)
													.stream()
													.map(Class::getPackage)
													.map(Package::getName)
													.collect(joining(",")));
			stripesFilter.setInitParameter(	"Interceptor.Classes",
											Lists.newArrayList(SpringInterceptor.class, EveToolActionBeanPopulatorInterceptor.class,
																CsrfInterceptor.class)
													.stream()
													.map(Class::getName)
													.collect(joining(",")));
			stripesFilter.setInitParameter("ActionBeanContext.Class", EveToolActionBeanContext.class.getName());
			stripesFilter.setInitParameter("Extension.Packages",
											Lists.newArrayList(EveFormatterFactory.class).stream()
													.map(Class::getPackage)
													.map(Package::getName)
													.collect(joining(",")));
			stripesFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.jsp");
			stripesFilter.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), true, "stripesDispatcher");
		}

		mappingConflicts
				.addAll(servletContext.addServlet("default", "org.apache.catalina.servlets.DefaultServlet").addMapping("/resources/*"));

		if (!mappingConflicts.isEmpty())
			throw new IllegalStateException("'appServlet' cannot be mapped to '/' under Tomcat versions <= 7.0.14");
	}
}