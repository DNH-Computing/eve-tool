<%@ attribute name="blueprint" type="nz.net.dnh.eve.business.BlueprintSummary" required="true" %>
<%@ attribute name="formModel" type="nz.net.dnh.eve.web.blueprint.BlueprintDetailsFormModel" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes-dynattr.tld" %>

<div id="update-blueprint-details" class="modal hide fade" data-reset-on-close="true" data-focus-on-open="true">
	<stripes:form class="form-horizontal" beanclass="nz.net.dnh.eve.web.blueprint.BlueprintDetailsActionBean" method="post" autocomplete="off" 
			data-automatic-update="${blueprint.automaticallyUpdateSalePrice ? 'yes' : 'no'}" >
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			<h3>Edit the sale price of the ${blueprint.name}</h3>
		</div>
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label">Price Update Policy</label>
				<div class="controls">
					<stripes:select name="formModel.automaticPriceUpdaManuallyte" class="input input-xlarge">
						<stripes:option value="1">Automatically update the Sale Price</stripes:option>
						<stripes:option value="0" selected="selected">Manually update the Sale Price</stripes:option>
					</stripes:select>
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
			<button type="submit" name="update-blueprint" class="btn btn-primary" data-loading-text="Saving...">Save</button>
		</div>
	</stripes:form>
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
