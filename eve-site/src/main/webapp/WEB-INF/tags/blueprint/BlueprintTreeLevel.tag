<%@ tag body-content="empty" %>
<%@ attribute name="items" type="java.util.List" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="eve" uri="urn:nz.net.dnh.eve.api_0_1" %>
<%@ taglib prefix="blueprint" tagdir="/WEB-INF/tags/blueprint" %>

<c:forEach items="${items}" var="requiredType">
	<li>
		<img src="<c:out value="${eve:getImageUriForType(requiredType.type, 32)}" />" />
		${requiredType.type.name}
		<span class="pull-right">${requiredType.units}</span>
		
		<span class="decomposition-state">
			<c:if test="${requiredType.decompositionState.toString() == 'DECOMPOSED'}">
				ME: ${requiredType.typeBlueprint.materialEfficiency},
				PE: ${requiredType.typeBlueprint.productionEfficiency},
				${requiredType.typeBlueprint.producedQuantity} per run,
			</c:if>
		</span>
		
		<c:if test="${requiredType.typeBlueprintRequiredTypes != null && requiredType.decompositionState.toString() == 'DECOMPOSED'}">
			<ul>
				<blueprint:BlueprintTreeLevel items="${requiredType.typeBlueprintRequiredTypes}" />
			</ul>
		</c:if>
</c:forEach>