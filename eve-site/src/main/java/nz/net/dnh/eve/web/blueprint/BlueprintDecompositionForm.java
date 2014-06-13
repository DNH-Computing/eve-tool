package nz.net.dnh.eve.web.blueprint;

import java.util.Map;

/**
 * A form backing object that holds the information about the decomposition of a blueprint
 * 
 * @author Daniel Hodder (danielh)
 * 
 */
public class BlueprintDecompositionForm {
	private Map<Integer, Boolean> decompositionStatus;

	public BlueprintDecompositionForm() {
	}

	public Map<Integer, Boolean> getDecompositionStatus() {
		return this.decompositionStatus;
	}

	public void setDecompositionStatus(final Map<Integer, Boolean> decompositionStatus) {
		this.decompositionStatus = decompositionStatus;
	}
}
