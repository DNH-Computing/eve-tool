/**
 * 
 */
package nz.net.dnh.eve.web.dashboard;

import static java.util.stream.Collectors.toList;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.Component;
import nz.net.dnh.eve.business.Mineral;
import nz.net.dnh.eve.stripes.api_0_1.AbstractActionBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The action bean backing the display of the Eve Tool's Dashboard.
 */
@UrlBinding("/dashboard")
public class DashboardActionBean extends AbstractActionBean {
	private static final Logger LOG = LoggerFactory.getLogger(DashboardActionBean.class);

	@DefaultHandler
	public Resolution dashboard() {
		return new ForwardResolution("WEB-INF/views/dashboard.jsp");
	}

	// Action Bean Properties

	public List<BlueprintSummary> getBlueprints() {
		final List<BlueprintSummary> blueprints = getContext().getBlueprintService().listSummaries();

		if (LOG.isTraceEnabled()) {
			LOG.trace(	"Getting list of blueprints: {}",
						blueprints.stream()
								.map((blueprint) -> "Blueprint [id = " + blueprint.getId() + ",  name=" + blueprint.getName() + "]")
								.collect(toList()));
		}

		return blueprints;
	}

	public List<Mineral> getMinerals() {
		final List<Mineral> minerals = getContext().getTypeService().listMinerals(true);

		LOG.trace("Getting list of minerals: {}", minerals);

		return minerals;
	}

	public List<Component> getComponents() {
		final List<Component> components = getContext().getTypeService().listComponents(true);

		LOG.trace("Getting list of components: {}", components);

		return components;
	}
}
