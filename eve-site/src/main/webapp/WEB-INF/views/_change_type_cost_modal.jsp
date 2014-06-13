<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="change-type-cost" class="modal hide fade" data-focus-on-open="true" data-action-prefix="<c:url value="/types/" />">
	<form class="form-horizontal" method="post" action="" autocomplete="off">
		<input type="hidden" name="typeId" value="" />
		
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 id="myModalLabel">Edit the cost of <span class="type-name"></span></h3>
		</div>
		
		<div class="modal-body">
			<div class="control-group">
				<label class="control-label">Price Update Policy</label>
				<div class="controls">
					<select name="autoUpdate" class="input input-xlarge">
						<option value="1">Automatically update the Sale Price</option>
						<option value="0" selected="selected">Manually update the Sale Price</option>
					</select>
				</div>
			</div>
		
			<div class="control-group">
				<label class="control-label" for="cost">Cost:</label>
				<div class="controls">
					<div class="input-append">
						<input type="text" name="cost" value="" required="required"
							pattern="[0-9]+(\.[0-9]{1,2})?" title="Please enter a currency including the cost to two decimal places" />
						<span class="add-on">ISK</span>
					</div>
				</div>
			</div>
		</div>
		<div class="modal-footer">
			<input type="hidden" name="return" value="<c:out value="${pageContext.request.getAttribute('javax.servlet.forward.request_uri')}" />" />
			<input type="hidden" name="missing" />
			<input type="hidden" name="mineral" />
			<button type="reset" class="btn" data-dismiss="modal">Close</button>
			<button type="submit" class="btn btn-primary" data-loading-text="Saving...">Save</button>
		</div>
	</form>
</div>
<script type="text/javascript">
	$('#change-type-cost').on('show', function() {
		var dialog = $(this);
		var options = dialog.data('modal').options;
		// Update the inputs
		$('.type-name', this).text(options.typeName);
		$('[name=cost]', this).val(options.typeCost);
		$('[name=missing]', this).val(options.typeMissing);
		$('[name=mineral]', this).val(options.typeMineral);
		$('[name=autoUpdate]', this).val(options.typeAutoUpdate == true ? 1 : 0);
		$('[name=typeId]', this).val(options.typeId);
		// Set the form action to the data-action-prefix value plus the type id
		$('form', this).attr('action', dialog.data('actionPrefix') + options.typeId);
	}).on('hide', function() {
		$(this).removeData('modal');
	});
	
	$('#change-type-cost [name="autoUpdate"]').change(function () {
		var $saleValueField = $('#change-type-cost [name="cost"]');
		var typeId = $('#change-type-cost [name=typeId]').val();
		
		if ($(this).val() == "0")
			$saleValueField.prop('disabled', false);
		else if ($(this).val() == "1") {
			$saleValueField.prop('disabled', true);
			
			var url = '<c:url value="/price/" />';
			$.get(url+typeId, function (data) {
				if (data.value == -1)
					alert('Error retrieving market information');
				else
					$('#change-type-cost [name="cost"]').val(data.value);
			}, 'json');
		}
	});
</script>