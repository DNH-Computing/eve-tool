<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="eve" uri="urn:nz.net.dnh.eve.api_0_1" %>
<%@ taglib prefix="partials" tagdir="/WEB-INF/tags/blueprint" %>

<eve:Template>
	<div class="row-fluid">
		<div class="span12">
			<eve:BlueprintInformation blueprint="${actionBean.blueprint}" />
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span4">
			<h2>
				Components and Minerals
				
				<a href="#update-blueprint-decomposition" class="btn btn-primary pull-right" role="button" data-toggle="modal" data-keyboard="true">Change</a>
			</h2>
			
			<div class="clearfix"></div>
			
			<header>
				Name
				<span class="pull-right">Quantity</span>
			</header>
			<ul class="itemtree">
				<partials:BlueprintTreeLevel items="${actionBean.blueprint.requiredTypes.requiredTypesTree}" />
			</ul>
		</div>
	
		<div class="span8">
			<section role="blueprints">
				<h2>Required Blueprints</h2>
				
				<eve:RequiredBlueprints items="${actionBean.blueprint.requiredTypes.requiredBlueprints}"/>
			</section>
			
			<section role="components">
				<h2>
					Resolved Components and Minerals <i class="icon-question-sign" data-toggle="tooltip" title="This is the list of minerals, components, moon materials, 
					planatery goods and other miscellaneous bits and pieces needed to make this blueprint, taking into account the decomposition of blueprints you 
					specified"></i>
				</h2>
				
				<partials:DetailedTypeList typeName="Name" types="${actionBean.blueprint.requiredTypes.resolvedRequiredTypes}" blueprint="${actionBean.blueprint}" />
			</section>
		</div>
	</div>
	
<%-- 	<tiles:insertTemplate template="_update_blueprint_modal.jsp"> --%>
<%-- 		<tiles:putAttribute name="blueprint" value="${view.blueprint}" /> --%>
<%-- 		<tiles:putAttribute name="form" value="${view.form}" /> --%>
<%-- 	</tiles:insertTemplate> --%>

	<partials:UpdateBlueprintModal blueprint="${actionBean.blueprint}" formModel="${actionBean.blueprintForm}" />
	
<%-- 	<tiles:insertTemplate template="../_change_type_cost_modal.jsp" /> --%>
	
<%-- 	<tiles:insertTemplate template="_blueprint_decomposition_modal.jsp"> --%>
<%-- 		<tiles:putAttribute name="blueprint" value="${view.blueprint}" /> --%>
<%-- 		<tiles:putAttribute name="requiredTypes" value="${view.requiredTypes.requiredTypesTree}" /> --%>
<%-- 	</tiles:insertTemplate> --%>
</eve:Template>