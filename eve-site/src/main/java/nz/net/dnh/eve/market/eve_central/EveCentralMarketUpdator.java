package nz.net.dnh.eve.market.eve_central;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.BlueprintService;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.TypeService;
import nz.net.dnh.eve.market.eve_central.EveCentralMarketStatResponse.Type;
import nz.net.dnh.eve.market.evemarketer.EveMarketerMarketRequester;

@Service
public class EveCentralMarketUpdator {
	@Autowired
	private BlueprintService blueprintService;

	@Autowired
	private TypeService typeService;

	@Autowired
	private EveMarketerMarketRequester remoteMarketData;

	private static final Logger logger = LoggerFactory.getLogger(EveCentralMarketUpdator.class);

	public void doUpdate() throws InvalidMarketResponseException {
		logger.debug("Beginning update of pricing information");

		updateBlueprints();
		updateTypes();
	}

	private void updateBlueprints() throws InvalidMarketResponseException {
		logger.debug("Updating blueprints");
		final Collection<BlueprintSummary> blueprintsToUpdate = this.blueprintService.getBlueprintsForAutomaticUpdate();
		final Map<Integer, BlueprintSummary> typeIdsToQuery = new HashMap<>(blueprintsToUpdate.size());

		if (blueprintsToUpdate.size() == 0) {
			return;
		}

		for (final BlueprintSummary b : blueprintsToUpdate) {
			typeIdsToQuery.put(b.getProducedTypeID(), b);
		}

		logger.debug("Updating blueprints: {}", typeIdsToQuery.keySet());

		final EveCentralMarketStatResponse response = this.remoteMarketData.getDataForType(typeIdsToQuery.keySet());

		if (response.getTypes().size() == 0) {
			throw new InvalidMarketResponseException("There were no types in the response");
		}

		for (final Type type : response.getTypes()) {
			final BigDecimal producedQuantity = new BigDecimal(typeIdsToQuery.get(type.getId()).getProducedQuantity());
			final BigDecimal newValue = type.getSell().getMedian().multiply(producedQuantity);

			logger.debug("Updating blueprint {}, to new sale value: {}", type.getId(), newValue);

			this.blueprintService.editBlueprint(typeIdsToQuery.get(type.getId()), newValue,
												null, null, null, null);
		}
	}

	private void updateTypes() throws InvalidMarketResponseException {
		logger.debug("Updating types");
		final Collection<AbstractType> typesToUpdate = this.typeService.getTypesForAutomaticUpdate();
		final Map<Integer, AbstractType> typeIdsToQuery = new HashMap<>(typesToUpdate.size());

		if (typesToUpdate.size() == 0) {
			return;
		}

		for (final AbstractType t : typesToUpdate) {
			typeIdsToQuery.put(t.getId(), t);
		}

		final EveCentralMarketStatResponse response = this.remoteMarketData.getDataForType(typeIdsToQuery.keySet());

		if (response.getTypes().size() == 0) {
			throw new InvalidMarketResponseException("There were no types in the response");
		}

		for (final Type type : response.getTypes()) {
			final BigDecimal newValue = type.getSell().getMedian();

			logger.debug("Updating component {}, to new sale value: {}", type.getId(), newValue);

			if (typeIdsToQuery.get(type.getId()).isMineral()) {
				this.typeService.updateMineral(typeIdsToQuery.get(type.getId()), newValue, true);
			} else {
				this.typeService.updateComponent(typeIdsToQuery.get(type.getId()), newValue, true);
			}
		}
	}

	public static final class InvalidMarketResponseException extends Exception {
		private static final long serialVersionUID = -4053548110224327265L;

		public InvalidMarketResponseException(final String string) {
			super(string);
		}
	}
}
