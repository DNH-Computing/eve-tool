package nz.net.dnh.eve.web;

import java.math.BigDecimal;

import nz.net.dnh.eve.business.TypeIdReference;
import nz.net.dnh.eve.business.TypeReference;
import nz.net.dnh.eve.business.TypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class TypeController {
	private static final Logger LOG = LoggerFactory.getLogger(TypeController.class);

	private final TypeService typeService;

	@Autowired
	public TypeController(final TypeService typeService) {
		this.typeService = typeService;
	}

	@RequestMapping(value = "/types/{id}", method = RequestMethod.POST)
	public RedirectView updateType(@PathVariable("id") final int typeId, @RequestParam("return") final String returnUri,
			@ModelAttribute final TypeForm typeInfo) {
		LOG.trace("Upating type {} with new data: {}", typeId, typeInfo);

		final TypeReference type = new TypeIdReference(typeId);
		if (typeInfo.isMissing()) {
			// Create a missing mineral or component
			if (typeInfo.isMineral()) {
				this.typeService.createMissingMineral(type, typeInfo.getCost(), typeInfo.isAutoUpdate());
			} else {
				this.typeService.createMissingComponent(type, typeInfo.getCost(), typeInfo.isAutoUpdate());
			}
		} else {
			// Update the existing mineral or component
			if (typeInfo.isMineral()) {
				this.typeService.updateMineral(type, typeInfo.getCost(), typeInfo.isAutoUpdate());
			} else {
				this.typeService.updateComponent(type, typeInfo.getCost(), typeInfo.isAutoUpdate());
			}
		}

		LOG.debug("Redirecting to: {}", returnUri);
		return new RedirectView(returnUri);
	}

	public static final class TypeForm {
		private BigDecimal cost;
		private boolean missing;
		private boolean mineral;
		private boolean autoUpdate;

		public BigDecimal getCost() {
			return this.cost;
		}

		public void setCost(final BigDecimal cost) {
			this.cost = cost;
		}

		public boolean isMissing() {
			return this.missing;
		}

		public void setMissing(final boolean missing) {
			this.missing = missing;
		}

		public boolean isMineral() {
			return this.mineral;
		}

		public void setMineral(final boolean mineral) {
			this.mineral = mineral;
		}

		public boolean isAutoUpdate() {
			return this.autoUpdate;
		}

		public void setAutoUpdate(final boolean autoUpdate) {
			this.autoUpdate = autoUpdate;
		}

		@Override
		public String toString() {
			return "TypeForm [cost=" + this.cost + ", missing=" + this.missing + ", mineral=" + this.mineral + ", autoUpdate="
					+ this.autoUpdate + "]";
		}
	}
}
