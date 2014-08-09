/**
 * 
 */
package nz.net.dnh.eve.stripes.api_0_1;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.integration.spring.SpringBean;
import nz.net.dnh.eve.business.BlueprintService;
import nz.net.dnh.eve.business.TypeService;

/**
 * An extension of the {@link ActionBeanContext} that includes getters for the Spring Business services, until we kill
 * spring entirely
 */
public class EveToolActionBeanContext extends ActionBeanContext {
	@SpringBean
	private BlueprintService blueprintService;

	@SpringBean
	private TypeService typeService;

	public EveToolActionBeanContext() {
	}

	public EveToolActionBeanContext(final BlueprintService blueprintService, final TypeService typeService) {
		this.blueprintService = blueprintService;
		this.typeService = typeService;
	}

	/**
	 * @return the Type Service
	 */
	public TypeService getTypeService() {
		return this.typeService;
	}

	/**
	 * @return the Blueprint Service
	 */
	public BlueprintService getBlueprintService() {
		return this.blueprintService;
	}
}