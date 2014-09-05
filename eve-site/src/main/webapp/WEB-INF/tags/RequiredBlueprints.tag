<%@ tag body-content="empty" %>
<%@ attribute name="items" type="java.util.List" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="eve" uri="urn:nz.net.dnh.eve.api_0_1" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<table class="table table-striped table-hover types image-table click-row">
	<thead>
		<tr>
			<th style="width: 323px;" colspan="2">Name</th>
			<th class="text-right">Number of runs</th>
			<th class="text-right">Cost to run</th>
			<th class="text-right"><abbr title="Number of items Produced">Produced</abbr></th>
			<th class="text-right"><abbr title="Number of items left after production of ${view.blueprint.name}">Remaining</abbr></th>
		</tr>
	</thead>

	<tbody>
		<c:forEach items="${blueprints}" var="blueprint">
			<tr>
				<td><img src="<c:out value="${eve:getImageUriForTypeId(blueprint.typeBlueprint.id, 32)}" />" /></td>
				<td>
					<stripes:link beanclass="nz.net.dnh.eve.web.blueprint.BlueprintDetailsActionBean">
						<stripes:param name="blueprintID" value="${blueprint.typeBlueprint.id}" />
						<c:out value="${blueprint.typeBlueprint.name}" />
					</stripes:link>
				</td>
				<td class="text-right">${blueprint.runs}</td>
				<td class="text-right">${eve:currency(blueprint.productionCost)}</td>
				<td class="text-right">${blueprint.producedUnits}</td>
				<td class="text-right">${blueprint.producedUnits - blueprint.requiredUnits}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>