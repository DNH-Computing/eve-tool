<%@ tag body-content="empty" %>
<%@ attribute name="typeName" required="true" type="java.lang.String" %>
<%@ attribute name="blueprint" required="true" type="nz.net.dnh.eve.business.BlueprintSummary" %>
<%@ attribute name="types" required="true" type="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="urn:nz.net.dnh.eve.api_0_1" prefix="eve" %>

<table class="table table-striped table-hover types image-table">
	<thead>
		<tr>
			<th style="width: 325px;"><c:out value="${typeName}" /></th>
			<th class="text-right">Quantity</th>
			<th class="text-right">Last Cost</th>
			<th class="text-right">Total Cost</th>
			<th class="text-right"><abbr title="Percentage of build cost">Percentage</abbr></th>
			<th class="text-right">Last Updated</th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="requiredComponent"  items="${types}">
			<c:choose>
				<c:when test="${requiredComponent.key.cost == null}">
					<c:set var="class_name">error</c:set>
					<c:set var="tooltip">There is no pricing information available</c:set>
				</c:when>
				
				<c:when test="${eve:isTypeDataOld(requiredComponent.key)}">
					<c:set var="class_name">warning</c:set>
					<c:set var="tooltip">This data is old and may not be reliable</c:set>
				</c:when>
				
				<c:otherwise>
					<c:set var="tooltip" />
					<c:set var="class_name" />
				</c:otherwise>
			</c:choose>
			
			<tr class="${class_name}" title="${tooltip}" data-toggle="tooltip" data-container="body">
				<td>
					<img src="<c:out value="${eve:getImageUriForType(requiredComponent.key, 32)}" />" />
					${requiredComponent.key.name}
				</td>
				<td class="text-right"><c:out value="${requiredComponent.value}" /></td>
				<td class="text-right">
					<a class="btn btn-link reveal-icon no-padding" data-toggle="modal" data-target="#change-type-cost"
							data-type-cost="${requiredComponent.key.cost}" data-type-name="${requiredComponent.key.name}" data-type-id="${requiredComponent.key.id}" data-type-missing="${requiredComponent.key.missing}" data-type-mineral="${requiredComponent.key.mineral}"  data-type-auto-update="${type.autoUpdate}">
						<eve:Currency>${requiredComponent.key.cost}</eve:Currency><i class="icon-pencil"></i>
					</a>
				</td>
				<td class="text-right">
					<strong>
						<eve:Currency>${requiredComponent.key.cost * requiredComponent.value}</eve:Currency>
					</strong>
				</td>
				<td class="text-right">
					<em>
						<eve:Percentage>${blueprint.totalCost == null ? null : ((requiredComponent.key.cost * requiredComponent.value) / blueprint.totalCost) * 100}</eve:Percentage>
					</em>
				</td>
				<td class="text-right">
					${dateFormatter.format(requiredComponent.key.costLastUpdated)}
					<c:if test="${requiredComponent.key.costLastUpdated != null}">
						<i class="icon-info-sign" title="${dateTimeFormatter.format(requiredComponent.key.costLastUpdated)}"></i>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
