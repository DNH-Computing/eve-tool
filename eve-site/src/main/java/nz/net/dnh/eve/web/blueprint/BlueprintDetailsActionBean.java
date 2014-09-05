/**
 * 
 */
package nz.net.dnh.eve.web.blueprint;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import nz.net.dnh.eve.business.BlueprintIdReference;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.stripes.api_0_1.AbstractActionBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
@UrlBinding("/blueprint")
public class BlueprintDetailsActionBean extends AbstractActionBean {
	private static final Logger LOG = LoggerFactory.getLogger(BlueprintDetailsActionBean.class);

	@Validate(required = true, minvalue = 0)
	private int blueprintID;

	private transient BlueprintSummary blueprint;
	
	@ValidateNestedProperties({
			@Validate(field = "numberPerRun", minvalue = 0, required = true, on="update-blueprint"),
			@Validate(field = "automaticPriceUpdate", minvalue = 0, maxvalue = 1, required = true, on="update-blueprint"),
			@Validate(field = "saleValue", minvalue = 0, required = true, on="update-blueprint"),
			@Validate(field = "materialEfficency", minvalue = 0, required = true, on="update-blueprint"),
			@Validate(field = "productionEffiecincy", minvalue = 0, required = true, on = "update-blueprint"),
	})
	private BlueprintDetailsFormModel blueprintForm;

	@Before(stages = LifecycleStage.EventHandling)
	public void populateBlueprint() {
		this.blueprint = getContext().getBlueprintService().getBlueprint(new BlueprintIdReference(this.blueprintID));
		this.blueprintForm = new BlueprintDetailsFormModel(this.blueprint);

		LOG.trace("Materialized blueprint from id {} to object: {}", this.blueprintID, this.blueprint);
	}

	@DefaultHandler
	public Resolution showBlueprintDetails() {
		return new ForwardResolution("WEB-INF/views/BlueprintDetails.jsp");
	}

	@HandlesEvent("update-blueprint")
	public Resolution updateBlueprintDetails() {
		return new RedirectResolution(BlueprintDetailsActionBean.class)
				.addParameter("blueprintID", this.blueprintID);
	}

	public int getBlueprintID() {
		return this.blueprintID;
	}

	public void setBlueprintID(final int blueprintID) {
		this.blueprintID = blueprintID;
	}

	public BlueprintSummary getBlueprint() {
		return this.blueprint;
	}

	public BlueprintDetailsFormModel getBlueprintForm() {
		return this.blueprintForm;
	}

	public void setBlueprintForm(final BlueprintDetailsFormModel blueprintForm) {
		this.blueprintForm = blueprintForm;
	}
}