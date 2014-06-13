<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<tiles:importAttribute name="blueprint" />
<tiles:importAttribute name="requiredTypes" />

<div id="update-blueprint-decomposition" class="modal hide fade" data-reset-on-close="true" data-focus-on-open="true">
	<form class="form-horizontal" action="<s:url value="/blueprints/${blueprint.id}/decomposition" />" method="post" autocomplete="off">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			<h3>Edit the decomposition of ${blueprint.name}</h3>
		</div>
		
		<div class="modal-body">
			<c:forEach items="${requiredTypes}" var="requiredType">
				<c:if test="${requiredType.decompositionState.toString() != 'NEVER_DECOMPOSED'}">
					<div class="control-group">
						<label class="control-label">Decompose ${requiredType.type.name}</label>
						<div class="controls">
							<c:choose>
								<c:when test="${requiredType.decompositionState.toString() == 'NOT_CONFIGURED'}">
									Add the blueprint for ${requiredType.type.name} to EVE Tool to be able to decompose them
								</c:when>
								
								<c:otherwise>
									<select name="decompositionStatus['${requiredType.type.id}']">
										<option value="true" ${requiredType.decompositionState.toString() == 'DECOMPOSED' ? 'selected' : '' }>Yes</option>
										<option value="false" ${requiredType.decompositionState.toString() == 'NOT_DECOMPOSED' ? 'selected' : '' }>No</option>
									</select>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</c:if>
			</c:forEach>
		</div>
		
		<div class="modal-footer">
			<button type="reset" class="btn" data-dismiss="modal">Close</button>
			<button type="submit" class="btn btn-primary" data-loading-text="Saving...">Save</button>
		</div>
	</form>
</div>