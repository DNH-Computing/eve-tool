<%@ page import="java.util.Calendar"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<table class="table table-striped table-hover types image-table">
	<thead>
		<tr>
			<th colspan="2"><tiles:getAsString name="typeName" /></th>
			<th class="text-right">Last Cost</th>
			<th class="text-right">Last Updated</th>
		</tr>
	</thead>

	<tbody>
		<tiles:importAttribute name="types" />
		<c:forEach var="type" items="${types}">
			<c:choose>
				<c:when test="${type.cost == null}">
					<c:set var="class_name">error</c:set>
					<c:set var="tooltip">There is no pricing information available</c:set>
				</c:when>
				
				<c:when test="${dashboardViewHelper.isTypeDataOld(type)}">
					<c:set var="class_name">warning</c:set>
					<c:set var="tooltip">This data is old and may not be reliable</c:set>
				</c:when>
				
				<c:otherwise>
					<c:set var="tooltip" />
					<c:set var="class_name" />
				</c:otherwise>
			</c:choose>
			
			<tr class="${class_name}" title="${tooltip}" data-toggle="tooltip" data-container="body">
				<td><img src="<c:out value="${imageURILocator.getUriForType(type, 32)}" />" /></td>
				<td><c:out value="${type.name}" /></td>
				<td class="text-right">
					<a class="btn btn-link reveal-icon no-padding" data-toggle="modal" data-target="#change-type-cost"
							data-type-cost="${type.cost}" data-type-name="${type.name}" data-type-id="${type.id}" data-type-missing="${type.missing}" data-type-mineral="${type.mineral}" data-type-auto-update="${type.autoUpdate}">
						${currencyFormatter.format(type.cost)}<i class="icon-pencil"></i>
					</a>
				</td>
				<td class="text-right">
					${dateFormatter.format(type.costLastUpdated)}
					<c:if test="${type.costLastUpdated != null}">
						<i class="icon-info-sign" title="${dateTimeFormatter.format(type.costLastUpdated)}"></i>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>