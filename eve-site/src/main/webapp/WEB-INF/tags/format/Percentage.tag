<%@ tag body-content="scriptless" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="value">
	<jsp:doBody />
</c:set>

<c:choose>
	<c:when test="${value != null && value != ''}">
		<fmt:formatNumber type="percent" groupingUsed="false" maxIntegerDigits="3" maxFractionDigits="2" value="${value}" />
	</c:when>
	
	<c:otherwise>
		???
	</c:otherwise>
</c:choose>