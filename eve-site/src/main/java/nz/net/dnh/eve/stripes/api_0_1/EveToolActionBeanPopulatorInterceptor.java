/**
 * 
 */
package nz.net.dnh.eve.stripes.api_0_1;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.integration.spring.SpringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An intercepter that populates Spring Beans into the action bean context
 */
@Intercepts(LifecycleStage.ActionBeanResolution)
public class EveToolActionBeanPopulatorInterceptor implements Interceptor {
	private static final Logger LOG = LoggerFactory.getLogger(EveToolActionBeanPopulatorInterceptor.class);

	@Override
	public Resolution intercept(final ExecutionContext context) throws Exception {
		final Resolution resolution = context.proceed();

		LOG.trace("Injecting the action bean context with spring beans");

		SpringHelper.injectBeans(context.getActionBeanContext(), context.getActionBeanContext());

		return resolution;
	}

}
