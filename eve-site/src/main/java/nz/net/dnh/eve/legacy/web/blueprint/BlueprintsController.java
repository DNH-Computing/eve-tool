package nz.net.dnh.eve.legacy.web.blueprint;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map.Entry;

import nz.net.dnh.eve.business.BlueprintIdReference;
import nz.net.dnh.eve.business.BlueprintService;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.CandidateBlueprint;
import nz.net.dnh.eve.business.RequiredTypes;
import nz.net.dnh.eve.business.TypeIdReference;
import nz.net.dnh.eve.business.TypeService;
import nz.net.dnh.eve.legacy.web.view.ImageURILocater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public final class BlueprintsController {
	private static final Logger LOG = LoggerFactory.getLogger(BlueprintsController.class);

	private final ImageURILocater imageURILocater;
	private final BlueprintService blueprintService;
	private final TypeService typeService;

	@Autowired
	public BlueprintsController(final ImageURILocater imageURILocater, final BlueprintService blueprintService,
			final TypeService typeService) {
		this.imageURILocater = imageURILocater;
		this.blueprintService = blueprintService;
		this.typeService = typeService;
	}

	@RequestMapping(value = "/blueprints/{id}", method = RequestMethod.GET)
	public ModelAndView showBlueprint(@PathVariable("id") final int id) {
		LOG.trace("Showing blueprint {}", id);

		final BlueprintSummary blueprintInformation = this.blueprintService.getBlueprint(new BlueprintIdReference(id));
		final RequiredTypes requiredTypes = blueprintInformation.getRequiredTypes();

		return new ModelAndView("blueprints/show", "view", new BlueprintView(blueprintInformation, requiredTypes, new BlueprintForm(blueprintInformation)));
	}

	@RequestMapping(value = "/blueprints/{id}", method = RequestMethod.POST)
	public String updateBlueprint(@PathVariable("id") final int id,
			@ModelAttribute final BlueprintForm form) {
		LOG.trace("Updating blueprint {}, with new data: {}", id, form);

		this.blueprintService.editBlueprint(new BlueprintIdReference(id),
											form.getSaleValue(),
											form.getNumberPerRun(),
											form.getProductionEffiecincy(),
											form.getMaterialEfficency(),
											form.getAutomaticPriceUpdate());

		return "redirect:/blueprints/" + id;
	}

	@RequestMapping(value = "/blueprints/search")
	public @ResponseBody List<BlueprintSearchResult> searchBlueprint(@RequestParam("blueprint-name") final String blueprintName) {
		LOG.trace("Searhing for blueprint with name matching '{}'", blueprintName);
		
		final Page<CandidateBlueprint> blueprints = this.blueprintService.findCandidateBlueprints(blueprintName, new PageRequest(0, 10));

		return blueprints.getContent().stream()
				.map(blueprint -> new BlueprintSearchResult(blueprint, this.imageURILocater.getUriForTypeID(blueprint.getProducedTypeID(), 32)))
				.collect(toList());
	}

	@RequestMapping(value = "/blueprints/new", method = RequestMethod.POST)
	public RedirectView addBlueprint(@RequestParam("return") final String returnUri, @RequestParam("blueprint-id") final int blueprintId,
			@ModelAttribute final BlueprintForm form) {
		LOG.trace("Creating new blueprint with ID {} and data {}", blueprintId, form);
		LOG.debug("");
		
		this.blueprintService.createBlueprint(new BlueprintIdReference(blueprintId),
												form.getSaleValue(),
												form.getNumberPerRun(),
												form.getProductionEffiecincy(),
												form.getMaterialEfficency(),
												form.getAutomaticPriceUpdate());

		LOG.debug("Redirecting to: {}", returnUri);
		return new RedirectView(returnUri);
	}

	/*
	 * Saves the decomposition state of the blueprint given the typeID -> boolean mappings
	 */
	@RequestMapping(value = "/blueprints/{id}/decomposition", method = RequestMethod.POST)
	public View saveDecompositionChanges(@PathVariable("id") final int id,
			final BlueprintDecompositionForm decompositionStatus, final Errors errors) {
		for (final Entry<Integer, Boolean> decomposition : decompositionStatus.getDecompositionStatus().entrySet()) {
			this.typeService.updateRequiredType(new BlueprintIdReference(id), new TypeIdReference(decomposition.getKey()), decomposition.getValue());
		}

		return new RedirectView("/blueprints/" + id, true);
	}
}
