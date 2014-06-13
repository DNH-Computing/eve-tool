package nz.net.dnh.eve.web;

import static java.util.stream.Collectors.toList;

import java.util.List;

import nz.net.dnh.eve.business.BlueprintService;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.Component;
import nz.net.dnh.eve.business.Mineral;
import nz.net.dnh.eve.business.TypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public final class DashboardController {
	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);
	private final BlueprintService blueprintService;
	private final TypeService typeService;

	@Autowired
	public DashboardController(final BlueprintService blueprintService, final TypeService typeService) {
		this.blueprintService = blueprintService;
		this.typeService = typeService;
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView dashboard() {
		LOG.trace("Loading dashboard view");

		return new ModelAndView("dashboard", "view", new DashboardView(
				this.blueprintService.listSummaries(),
				this.typeService.listMinerals(true),
				this.typeService.listComponents(true)));
	}

	public class DashboardView {
		private final Logger LOG = LoggerFactory.getLogger(DashboardView.class);

		private final List<BlueprintSummary> blueprints;
		private final List<Mineral> minerals;
		private final List<Component> components;

		public DashboardView(final List<BlueprintSummary> blueprints,
				             final List<Mineral> minerals,
				             final List<Component> components) {
			this.blueprints = blueprints;
			this.minerals = minerals;
			this.components = components;

			if (this.LOG.isTraceEnabled()) {
				this.LOG.trace("Creating dashboard view with \n\tBlueprints: {}\n\tMinerals: {}\n\tComponents: {}\n\t",
				        blueprints.stream().map((blueprint) -> "Blueprint [id = " + blueprint.getId() + ",  name=" + blueprint.getName() + "]").collect(toList()),
				        minerals, components);
			}
		}

		public List<BlueprintSummary> getBlueprints() {
			return this.blueprints;
		}

		/**
		 * @return the minerals
		 */
		public List<Mineral> getMinerals() {
			return this.minerals;
		}

		/**
		 * @return the components
		 */
		public List<Component> getComponents() {
			return this.components;
		}
	}
}
