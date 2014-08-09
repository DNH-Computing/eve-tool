<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="eve" uri="urn:nz.net.dnh.eve.api_0_1" %>

<eve:Template>
	<h1>Overview</h1>
	
	<div class="row-fluid">
		<div class="span12">
			<eve:BlueprintList blueprints="${actionBean.blueprints}" />
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span6">
			<h2>Minerals</h2>
			
			<eve:TypeList typeName="Minerals" types="${actionBean.minerals}" />
		</div>
		
		<div class="span6">
			<h2>Components</h2>
			
			<eve:TypeList typeName="Components" types="${actionBean.components}" />
		</div>
	</div>
	
	<eve:ChangeTypeCostModal />
</eve:Template>