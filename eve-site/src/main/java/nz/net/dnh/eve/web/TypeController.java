package nz.net.dnh.eve.web;

import java.math.BigDecimal;

import nz.net.dnh.eve.business.TypeIdReference;
import nz.net.dnh.eve.business.TypeReference;
import nz.net.dnh.eve.business.TypeService;

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
	@Autowired
	private TypeService typeService;

	@RequestMapping(value = "/types/{id}", method = RequestMethod.POST)
	public RedirectView updateType(@PathVariable("id") final int typeId, @RequestParam("return") final String returnUri,
			@ModelAttribute final TypeForm typeInfo) {
		final TypeReference type = new TypeIdReference(typeId);
		if (typeInfo.isMissing()) {
			// Create a missing mineral or component
			if (typeInfo.isMineral())
				this.typeService.createMissingMineral(type, typeInfo.getCost());
			else
				this.typeService.createMissingComponent(type, typeInfo.getCost());
		} else {
			// Update the existing mineral or component
			if (typeInfo.isMineral())
				this.typeService.updateMineral(type, typeInfo.getCost());
			else
				this.typeService.updateComponent(type, typeInfo.getCost());
		}

		return new RedirectView(returnUri);
	}

	public static final class TypeForm {
		private BigDecimal cost;
		private boolean missing;
		private boolean mineral;

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
	}
}
