<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<tiles:importAttribute name="blueprint" />

<div class="hero-unit container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<h1>
				<img src="<c:out value="${imageURILocator.getUriForTypeID(blueprint.producedTypeID, 64)}" />" />${blueprint.name}
				<small>
					<abbr title="Material Efficency">ME:</abbr>	${blueprint.materialEfficiency}
					<abbr title="Production Efficency">PE:</abbr> ${blueprint.productionEfficiency}
				</small>
				
				<a href="#update-blueprint-details" role="button" class="btn btn-primary pull-right" data-toggle="modal"
					data-keyboard="true">Update Blueprint</a>
			</h1>
		</div>
	</div>

	<div class="row-fluid">
		<div class="span7">
			<h2>
				Production Cost: ${currencyFormatter.format(blueprint.totalCost)} 
				
				<small class="show">
					Material Cost:	${currencyFormatter.format(blueprint.materialCost)}
					Production Cost: ${currencyFormatter.format(blueprint.runningCost)}
				</small>
			</h2>

			<h2>Sale Value:
				${currencyFormatter.format(blueprint.saleValue)}
			</h2>

			<h2>
				Profit: <span <c:if test="${blueprint.profit <= 0}">class="text-error"</c:if>>${currencyFormatter.format(blueprint.profit)}</span>
			</h2>
		</div>

		<aside class="span5 text-right">
			<h3>
				<small>Sale price last updated:	${dateFormatter.format(blueprint.saleValueLastUpdated)}</small>
			</h3>

			<h3>Number per production run: ${blueprint.numberPerRun}</h3>

			<h3>
				Takes: ${blueprint.hours} <abbr title="This is approximate">hours</abbr>
				(~${durationFormatter.hoursToDays(blueprint.hours)} <abbr	title="This is approximate">Day(s)</abbr>)
			</h3>
		</aside>
	</div>
</div>