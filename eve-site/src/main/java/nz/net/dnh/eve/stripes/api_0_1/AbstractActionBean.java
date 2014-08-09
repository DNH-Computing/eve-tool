/**
 * 
 */
package nz.net.dnh.eve.stripes.api_0_1;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

/**
 * An abstract base class for all Action Beans
 */
public class AbstractActionBean implements ActionBean {
	private EveToolActionBeanContext context;

	@Override
	public EveToolActionBeanContext getContext() {
		return this.context;
	}

	@Override
	public void setContext(final ActionBeanContext context) {
		this.context = (EveToolActionBeanContext) context;
	}

}
