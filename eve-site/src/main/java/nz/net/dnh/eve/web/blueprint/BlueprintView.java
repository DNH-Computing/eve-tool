/**
 * 
 */
package nz.net.dnh.eve.web.blueprint;

import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.RequiredTypes;

public final class BlueprintView {
	private final BlueprintSummary blueprint;
	private final RequiredTypes requiredTypes;
	private final BlueprintForm form;

	BlueprintView(final BlueprintSummary blueprint, final RequiredTypes requiredTypes, final BlueprintForm form) {
		this.blueprint = blueprint;
		this.requiredTypes = requiredTypes;
		this.form = form;
	}

	public BlueprintSummary getBlueprint() {
		return this.blueprint;
	}

	public RequiredTypes getRequiredTypes() {
		return this.requiredTypes;
	}

	public BlueprintForm getForm() {
		return this.form;
	}
}