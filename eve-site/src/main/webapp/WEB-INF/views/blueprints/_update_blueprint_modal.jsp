<%@ page import="java.util.Calendar"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<tiles:importAttribute name="blueprint" />
<tiles:importAttribute name="form" />

<div id="update-blueprint-details" class="modal hide fade" data-reset-on-close="true" data-focus-on-open="true">
	<form class="form-horizontal" action="<s:url value="/blueprints/${blueprint.id}" />" method="post" autocomplete="off" data-automatic-update="${blueprint.automaticallyUpdateSalePrice ? "yes" : "no"}">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			<h3>Edit the sale price of the ${blueprint.name}</h3>
		</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label">Price Update Policy</label>
				<div class="controls">
					<select name="automaticPriceUpdate" class="input input-xlarge">
						<option value="1">Automatically update the Sale Price</option>
						<option value="0" selected="selected">Manually update the Sale Price</option>
					</select>
				</div>
			</div>
		
			<div class="control-group">
				<label class="control-label" for="saleValue">Sale Value per ${blueprint.producedQuantity}:</label>
				<div class="controls">
					<div class="input-append">
						<input type="text" name="saleValue" value="${form.saleValue}" required="required" 
						pattern="[0-9]+(\.[0-9]{1,2})?" title="Please enter a currency including the cost to two decimal places" />
						<span class="add-on">ISK</span>
					</div>
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="numberPerRun">Number of runs:</label>
				
				<div class="controls">
					<input type="number" min="1" name="numberPerRun" value="${form.numberPerRun}" required="required" pattern="[1-9][0-9]*" />
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="materialEfficency">Material Efficiency Level:</label>
				
				<div class="controls">
					<input type="number" name="materialEfficency" value="${form.materialEfficency}" required="required" pattern="-?[0-9]+" />
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="productionEffiecincy">Production Efficiency Level:</label>
				
				<div class="controls">
					<input type="number" name="productionEffiecincy" value="${form.productionEffiecincy}" required="required" pattern="-?[0-9]+" />
				</div>
			</div>
		</div>
		
		<div class="modal-footer">
			<input type="hidden" name="blueprint-id" value="${blueprint.id}" />
			<button type="reset" class="btn" data-dismiss="modal">Close</button>
			<button type="submit" class="btn btn-primary" data-loading-text="Saving...">Save</button>
		</div>
	</form>
</div>

<script type="text/javascript">
	$(function () {
		var $form = $('#update-blueprint-details form');
		
		$('[name="automaticPriceUpdate"]', $form).change(function () {
			var $saleValueField = $('[name="saleValue"]', $form);
			
			if ($(this).val() == "0")
				$saleValueField.prop('disabled', false);
			else if ($(this).val() == "1") {
				$saleValueField.prop('disabled', true);
				updateSalePriceField();
			}
		});
		
		function updateSalePriceField() {
			var blueprintId = $('[name="blueprint-id"]', $form).val();
			var producedQuantity = ${blueprint.producedQuantity};
			
			if (blueprintId == '')
				return;
			
			var url = '<c:url value="/price/blueprint/" />';
			$.get(url+blueprintId, function (data) {
				if (data.value == -1)
					alert('Error retrieving marker information');
				else
					$('#new-blueprint [name="saleValue"]').val(data.value * producedQuantity);
			}, 'json');
		}
	});
</script>
