package nz.net.dnh.eve.business.impl;

import nz.net.dnh.eve.business.BlueprintReference;
import nz.net.dnh.eve.business.RequiredTypes;

public interface BlueprintRequiredTypesService {
	// TODO caching wrapper
	RequiredTypes getRequiredTypes(BlueprintReference blueprint);
}
