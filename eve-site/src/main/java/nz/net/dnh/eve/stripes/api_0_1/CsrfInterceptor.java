/**
 * 
 */
package nz.net.dnh.eve.stripes.api_0_1;

import java.math.BigInteger;
import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts(LifecycleStage.ActionBeanResolution)
public class CsrfInterceptor implements Interceptor {
	private static final Logger LOG = LoggerFactory.getLogger(CsrfInterceptor.class);
	public static final String CSRF_TOKEN_NAME = "ctoken";

	@Override
	public Resolution intercept(final ExecutionContext context) throws Exception {
		final HttpServletRequest request = context.getActionBeanContext().getRequest();
		final HttpSession session = request.getSession();

		if ("GET".equals(request.getMethod())) {
			if (session.getAttribute(CSRF_TOKEN_NAME) == null){
				final UUID tokenBase = UUID.randomUUID();
				LOG.trace("Creating CSRF token from: {}", tokenBase);
				
				final BigInteger tokenValue = BigInteger.valueOf(tokenBase.getLeastSignificantBits());
				tokenValue.shiftLeft(64);
				tokenValue.or(BigInteger.valueOf(tokenBase.getMostSignificantBits()));
				
				final String encodedToken = Base64.getUrlEncoder().encodeToString(tokenValue.toByteArray());
				session.setAttribute(CSRF_TOKEN_NAME, encodedToken);
				LOG.debug("Set token {} into session", encodedToken);
			} else {
				LOG.trace("Bypassing CSRF protection for GET request");
			}
			
			return context.proceed();
		} else {
			if (session.getAttribute(CSRF_TOKEN_NAME) == null) {
				LOG.warn("There was no CSRF token for this non-GET request");

				throw new CsrfValidationException("Non-GET request was made with no CSRF token in the session");
			}

			if (session.getAttribute(CSRF_TOKEN_NAME).equals(request.getAttribute(CSRF_TOKEN_NAME))) {
				LOG.trace("CSRF token check passed");
				return context.proceed();
			} else {
				LOG.warn("The CSRF Token for the request didn't match what was in the session");

				throw new CsrfValidationException("The provided CSRF token was not correct for this session");
			}
		}
	}

	public static final class CsrfValidationException extends Exception {
		private static final long serialVersionUID = 1L;

		public CsrfValidationException(final String message, final Throwable cause) {
			super(message, cause);
		}

		public CsrfValidationException(final String message) {
			super(message);
		}

	}
}
