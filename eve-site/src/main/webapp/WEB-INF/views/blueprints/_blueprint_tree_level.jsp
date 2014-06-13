<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:importAttribute name="items" />

<c:forEach items="${items}" var="requiredType">
	<li>
		<img src="<c:out value="${imageURILocator.getUriForType(requiredType.type, 32)}" />" />
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
				<tiles:insertTemplate template="_blueprint_tree_level.jsp">
					<tiles:putAttribute name="items" value="${requiredType.typeBlueprintRequiredTypes}" />
				</tiles:insertTemplate>
			</ul>
		</c:if>
</c:forEach>