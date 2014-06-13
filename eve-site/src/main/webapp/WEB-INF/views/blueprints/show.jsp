<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<div class="row-fluid">
	<div class="span12">
		<tiles:insertTemplate template="_blueprint_information.jsp">
			<tiles:putAttribute name="blueprint" value="${view.blueprint}" />
		</tiles:insertTemplate>
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
			<tiles:insertTemplate template="_blueprint_tree_level.jsp">
				<tiles:putAttribute name="items" value="${view.requiredTypes.requiredTypesTree}" />
			</tiles:insertTemplate>
		</ul>
	</div>

	<div class="span8">
		<section role="blueprints">
			<h2>Required Blueprints</h2>
			
			<tiles:insertTemplate template="_required_blueprints.jsp">
				<tiles:putAttribute name="blueprints" value="${view.requiredTypes.requiredBlueprints}" />
			</tiles:insertTemplate>
		</section>
		
		<section role="components">
			<h2>
				Resolved Components and Minerals <i class="icon-question-sign" data-toggle="tooltip" title="This is the list of minerals, components, moon materials, 
				planatery goods and other miscellaneous bits and pieces needed to make this blueprint, taking into account the decomposition of blueprints you 
				specified"></i>
			</h2>
			<tiles:insertTemplate template="_type_list.jsp">
				<tiles:putAttribute name="typeName">Name</tiles:putAttribute>
				<tiles:putAttribute name="types" value="${view.requiredTypes.resolvedRequiredTypes}" />
				<tiles:putAttribute name="blueprint" value="${view.blueprint}" />
			</tiles:insertTemplate>
		</section>
	</div>
</div>

<tiles:insertTemplate template="_update_blueprint_modal.jsp">
	<tiles:putAttribute name="blueprint" value="${view.blueprint}" />
	<tiles:putAttribute name="form" value="${view.form}" />
</tiles:insertTemplate>

<tiles:insertTemplate template="../_change_type_cost_modal.jsp" />

<tiles:insertTemplate template="_blueprint_decomposition_modal.jsp">
	<tiles:putAttribute name="blueprint" value="${view.blueprint}" />
	<tiles:putAttribute name="requiredTypes" value="${view.requiredTypes.requiredTypesTree}" />
</tiles:insertTemplate>