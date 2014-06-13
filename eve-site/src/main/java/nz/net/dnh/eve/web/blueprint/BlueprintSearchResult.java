/**
 * 
 */
package nz.net.dnh.eve.web.blueprint;

import nz.net.dnh.eve.business.CandidateBlueprint;

/**
 * Encapsulates a response from the {@link BlueprintsController#searchBlueprint(String)} method. This is sent back
 * as a JSON repose.
 * 
 * @author Daniel Hodder (danielh)
 * 
 */
public final class BlueprintSearchResult {
	private final String name;
	private final String imageURI;
	private final int id;
	private final int producedQuantity;

	public BlueprintSearchResult(final CandidateBlueprint blueprint, final String imageURI) {
		this.name = blueprint.getName();
		this.id = blueprint.getId();
		this.imageURI = imageURI;
		this.producedQuantity = blueprint.getProducedQuantity();
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public String getImageURI() {
		return this.imageURI;
	}

	public int getProducedQuantity() {
		return this.producedQuantity;
	}
}