package nz.net.dnh.eve.business.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nz.net.dnh.eve.business.BlueprintReference;
import nz.net.dnh.eve.business.BlueprintService;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.CandidateBlueprint;
import nz.net.dnh.eve.business.impl.dto.blueprint.BlueprintSummaryImpl;
import nz.net.dnh.eve.business.impl.dto.blueprint.CandidateBlueprintImpl;
import nz.net.dnh.eve.market.eve_central.EveCentralMarketStatRequester;
import nz.net.dnh.eve.market.eve_central.EveCentralMarketStatResponse;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.repository.BlueprintRepository;
import nz.net.dnh.eve.model.repository.InventoryBlueprintTypeRepository;
import nz.net.dnh.eve.model.repository.TypeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlueprintServiceImpl implements BlueprintService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintServiceImpl.class);

	private static final Sort SORT_BY_NAME = new Sort("blueprintType.productType.typeName");

	@Autowired
	private BlueprintRepository blueprintRepository;
	@Autowired
	private TypeRepository typeRepository;
	@Autowired
	private InventoryBlueprintTypeRepository inventoryBlueprintTypeRepository;
	@Autowired
	private EveCentralMarketStatRequester marketDataRepository;
	@Autowired
	private BlueprintResolverService blueprintResolverService;
	@Autowired
	private BlueprintRequiredTypesService blueprintRequiredTypesService;

	@Override
	public List<BlueprintSummary> listSummaries() {
		final List<Blueprint> blueprints = this.blueprintRepository.findAll(SORT_BY_NAME);
		final List<BlueprintSummary> blueprintSummaries = new ArrayList<>(
				blueprints.size());
		for (final Blueprint blueprint : blueprints) {
			blueprintSummaries.add(new BlueprintSummaryImpl(blueprint, this.blueprintRequiredTypesService));
		}
		return blueprintSummaries;
	}

	private boolean isAlreadyKnownToSystem(final BlueprintReference blueprintReference) {
		if (blueprintReference instanceof BlueprintSummaryImpl)
			return true;

		return this.blueprintRepository.findOne(blueprintReference.getId()) != null;
	}

	private static Pageable sanitisePageable(final Pageable page) {
		if (page.getSort() != null)
			throw new IllegalArgumentException("The page parameter must not provide sorting information");
		return page;
	}

	private static Page<CandidateBlueprint> toCandidateBlueprints(final Pageable page, final Page<InventoryBlueprintType> unknownBlueprints) {
		final List<CandidateBlueprint> candidateBlueprints = new ArrayList<>(unknownBlueprints.getNumberOfElements());
		for (final InventoryBlueprintType inventoryBlueprint : unknownBlueprints) {
			candidateBlueprints.add(new CandidateBlueprintImpl(inventoryBlueprint));
		}
		return new PageImpl<>(candidateBlueprints, page, unknownBlueprints.getTotalElements());
	}

	@Override
	public Page<CandidateBlueprint> listCandidateBlueprints(final Pageable page) {
		final Page<InventoryBlueprintType> unknownBlueprints = this.inventoryBlueprintTypeRepository
				.findUnknownBlueprints(sanitisePageable(page));

		return toCandidateBlueprints(page, unknownBlueprints);
	}

	@Override
	public Page<CandidateBlueprint> findCandidateBlueprints(final String search, final Pageable page) {
		final Page<InventoryBlueprintType> unknownBlueprints = this.inventoryBlueprintTypeRepository.findUnknownBlueprintsBySearch(search,
		                                                                                                                           sanitisePageable(page));

		return toCandidateBlueprints(page, unknownBlueprints);
	}

	@Override
	public BlueprintSummary getBlueprint(final BlueprintReference blueprintReference) {
		if (blueprintReference instanceof BlueprintSummary) {
			LOGGER.warn("You already had a blueprint summary, why are you asking for another one?");
			return (BlueprintSummary) blueprintReference;
		}
		return new BlueprintSummaryImpl(this.blueprintResolverService.toBlueprint(blueprintReference), this.blueprintRequiredTypesService);
	}

	@Override
	public BlueprintSummary createBlueprint(final BlueprintReference blueprintReference, BigDecimal saleValue,
			final int numberPerRun, final int productionEfficiency, final int materialEfficiency, final Boolean automaticallyUpdateSalePrice) {
		// Check it doesn't already exist
		if (this.blueprintRepository.exists(blueprintReference.getId()))
			throw new IllegalArgumentException("The blueprint " + blueprintReference + " already exists");
		// Check it matches something in the EVE dump
		if (!this.inventoryBlueprintTypeRepository.exists(blueprintReference.getId()))
			throw new IllegalArgumentException("The blueprint " + blueprintReference + " does not match any InventoryBlueprintType");
		if (saleValue == null && automaticallyUpdateSalePrice != true)
			throw new IllegalArgumentException("Sale value cannot be null if automatic updates are set to off");

		if (automaticallyUpdateSalePrice) {
			saleValue = new BigDecimal(0);
		}

		// Create it
		final Blueprint newBlueprint = new Blueprint(blueprintReference.getId(), numberPerRun, productionEfficiency, saleValue,
				materialEfficiency, automaticallyUpdateSalePrice);
		final Blueprint savedBlueprint = this.blueprintRepository.save(newBlueprint);
		return new BlueprintSummaryImpl(savedBlueprint, this.blueprintRequiredTypesService);
	}

	@Override
	public BlueprintSummary editBlueprint(final BlueprintReference blueprintReference,
			final BigDecimal saleValue,
			final Integer numberPerRun,
			final Integer productionEfficiency,
			final Integer materialEfficiency,
			final Boolean automaticallyUpdateSalePrice) {
		final Blueprint blueprint = this.blueprintResolverService.toBlueprint(blueprintReference);
		if (saleValue != null) {
			// Update the last updated timestamp iff the sale value is different
			if (!saleValue.equals(blueprint.getSaleValue())) {
				blueprint.touchLastUpdated();
			}
			blueprint.setSaleValue(saleValue);
		}
		if (numberPerRun != null) {
			blueprint.setNumberPerRun(numberPerRun);
		}
		if (productionEfficiency != null) {
			blueprint.setProductionEfficiency(productionEfficiency);
		}
		if (materialEfficiency != null) {
			blueprint.setMaterialEfficiency(materialEfficiency);
		}
		if (automaticallyUpdateSalePrice != null) {
			blueprint.setAutomaticallyUpdateSalePrice(automaticallyUpdateSalePrice);
		}

		final Blueprint savedBlueprint = this.blueprintRepository.save(blueprint);
		return new BlueprintSummaryImpl(savedBlueprint, this.blueprintRequiredTypesService);
	}

	@Override
	public BigDecimal getMarketPrice(final BlueprintReference blueprint) {
		if (isAlreadyKnownToSystem(blueprint))
			return this.blueprintResolverService.toBlueprint(blueprint).getSaleValue();
		else {
			final EveCentralMarketStatResponse marketData = this.marketDataRepository.getDataForType(
					Arrays.asList(this.inventoryBlueprintTypeRepository.findOne(blueprint.getId()).getProductTypeID()));
			
			return marketData.getTypes().get(0).getSell().getMedian();
		}
	}

	@Override
	public Collection<BlueprintSummary> getBlueprintsForAutomaticUpdate() {
		final Collection<Blueprint> blueprints = this.blueprintRepository.findAutomaticlyUpdating();
		final ArrayList<BlueprintSummary> blueprintSummaries = new ArrayList<>(blueprints.size());
		
		for (final Blueprint b : blueprints) {
			blueprintSummaries.add(new BlueprintSummaryImpl(b, this.blueprintRequiredTypesService));
		}
		
		return blueprintSummaries;
	}
}
