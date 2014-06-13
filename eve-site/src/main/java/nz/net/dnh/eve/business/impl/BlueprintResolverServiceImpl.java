package nz.net.dnh.eve.business.impl;

import nz.net.dnh.eve.business.BlueprintNotFoundException;
import nz.net.dnh.eve.business.BlueprintReference;
import nz.net.dnh.eve.business.impl.dto.blueprint.BlueprintSummaryImpl;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.repository.BlueprintRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlueprintResolverServiceImpl implements BlueprintResolverService {

	@Autowired
	private BlueprintRepository blueprintRepository;

	@Override
	public Blueprint toBlueprint(final BlueprintReference blueprintReference) {
		if (blueprintReference instanceof BlueprintSummaryImpl)
			return this.blueprintRepository.refresh(((BlueprintSummaryImpl) blueprintReference).toBlueprint());
		final Blueprint blueprint = this.blueprintRepository.findOne(blueprintReference.getId());
		if (blueprint == null)
			throw new BlueprintNotFoundException(blueprintReference);
		return blueprint;
	}

}
