<%@ tag body-content="empty" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="eve" uri="urn:nz.net.dnh.eve.api_0_1" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container-fluid">
			<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span>
			</a> 
			<stripes:link beanclass="nz.net.dnh.eve.web.dashboard.DashboardActionBean" class="brand">EVE Blueprint Tool</stripes:link>
			<div class="nav-collapse collapse">
				<ul class="nav">
					<li><stripes:link beanclass="nz.net.dnh.eve.web.dashboard.DashboardActionBean">Home</stripes:link></li>
					<li><a href="#new-blueprint" data-toggle="modal">Add a blueprint</a></li>
				</ul>
			</div>
			
			<form class="navbar-form pull-right">
				<button type="submit" id="update-prices" class="btn btn-danger">Update All Prices</button>
			</form>
			<!--/.nav-collapse -->
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$('#update-prices').click(function () {
			var $this = $(this);
			$(this).prop('disabled', true);
			
			var url = '<c:url value="/price/update_all" />';
			$.post(url, function (data) {
				$this.prop('disabled', false);
				
				if (data == true)
					alert('Update Successful');
				else
					alert('Update Failed');
			});
			
			return false;
		})
	});
</script>

<eve:AddBlueprintModal />