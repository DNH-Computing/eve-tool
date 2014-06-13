package nz.net.dnh.eve.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.servlet.view.tiles3.TilesView;

/**
 * Allows views to call other invoke other spring beans using EL.
 * 
 * @author Daniel Hodder (danielh)
 * @see ContextExposingHttpServletRequest
 */
public class ContextBeanExposingView extends TilesView {
	/**
	 * Wraps the HttpServletRequest in a {@link ContextExposingHttpServletRequest} which enables EL expressions to
	 * access elements in the context.
	 * 
	 * @param originalRequest
	 *            The original request from Spring.
	 * @return The wrapped request
	 */
	protected HttpServletRequest getRequestToExpose(final HttpServletRequest originalRequest) {
		return new ContextExposingHttpServletRequest(originalRequest, getWebApplicationContext(), null);
	}

	/**
	 * Overrides the superclass method to include wrapping the request.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see #getRequestToExpose(HttpServletRequest)
	 */
	@Override
	protected void renderMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		super.renderMergedOutputModel(model, getRequestToExpose(request), response);
	}
}
