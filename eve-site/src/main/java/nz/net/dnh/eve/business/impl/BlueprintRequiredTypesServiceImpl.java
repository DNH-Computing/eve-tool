package nz.net.dnh.eve.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.BlueprintReference;
import nz.net.dnh.eve.business.RequiredType;
import nz.net.dnh.eve.business.RequiredType.DecompositionState;
import nz.net.dnh.eve.business.RequiredTypes;
import nz.net.dnh.eve.business.UnresolvedBlueprint;
import nz.net.dnh.eve.business.impl.dependencies.BlueprintDependencyState;
import nz.net.dnh.eve.business.impl.dependencies.BlueprintNode;
import nz.net.dnh.eve.business.impl.dependencies.Graph;
import nz.net.dnh.eve.business.impl.dependencies.RootBlueprintNode;
import nz.net.dnh.eve.business.impl.dto.blueprint.RequiredBlueprintSummaryImpl;
import nz.net.dnh.eve.business.impl.dto.type.AbstractMissingTypeImpl.MissingComponentImpl;
import nz.net.dnh.eve.business.impl.dto.type.AbstractMissingTypeImpl.MissingMineralImpl;
import nz.net.dnh.eve.business.impl.dto.type.AbstractTypeImpl.ComponentImpl;
import nz.net.dnh.eve.business.impl.dto.type.AbstractTypeImpl.MineralImpl;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.domain.BlueprintRequiredType;
import nz.net.dnh.eve.model.domain.Type;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.raw.InventoryType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlueprintRequiredTypesServiceImpl implements BlueprintRequiredTypesService {

	@Autowired
	private BlueprintResolverService blueprintResolverService;

	@Override
	public RequiredTypes getRequiredTypes(final BlueprintReference ref) {
		final Blueprint blueprint = this.blueprintResolverService.toBlueprint(ref);
		final BlueprintNode rootNode = new RootBlueprintNode(blueprint);
		final Graph<Blueprint, BlueprintDependencyState, BlueprintNode> dependencyGraph = new Graph<>(rootNode);
		final List<RequiredType<? extends AbstractType>> requiredTypes = addRequiredTypes(blueprint, dependencyGraph);
		rootNode.setRequiredTypes(requiredTypes);
		final BlueprintDependencyState state = new BlueprintDependencyState();
		dependencyGraph.apply(state);

		return new RequiredTypes(requiredTypes, state.getCounts(), state.getRequiredBlueprints());
	}

	private List<RequiredType<? extends AbstractType>> addRequiredTypes(final Blueprint blueprint,
			final Graph<Blueprint, BlueprintDependencyState, BlueprintNode> dependencyGraph) {
		final List<RequiredType<? extends AbstractType>> requiredTypes = new ArrayList<>();
		for (final BlueprintRequiredType requiredType : blueprint.getRequiredTypes()) {
			final int units = requiredType.getUnits();
			final Type typeDto = requiredType.getType();
			final InventoryType inventoryType = requiredType.getInventoryType();
			final Blueprint materialBlueprint = requiredType.getMaterialBlueprint();
			final InventoryBlueprintType materialBlueprintType = requiredType.getMaterialBlueprintType();

			final AbstractType type = toBusinessType(typeDto, inventoryType);

			final UnresolvedBlueprint typeBlueprint;
			final DecompositionState decompositionState;
			final List<RequiredType<? extends AbstractType>> typeRequiredTypes;
			if (materialBlueprint != null) {
				typeBlueprint = new RequiredBlueprintSummaryImpl(materialBlueprint, 1);
				BlueprintNode graphNode = null;
				if (requiredType.isDecomposed()) {
					decompositionState = DecompositionState.DECOMPOSED;
					// Add a dependency on the parent blueprint to this type in the dependency graph, if we are tracking dependencies
					if (dependencyGraph != null) {
						graphNode = new BlueprintNode(materialBlueprint, type);
						dependencyGraph.addDependency(graphNode, blueprint);
					}
				} else {
					decompositionState = DecompositionState.NOT_DECOMPOSED;
				}
				// Track dependencies iff the current node is marked as being decomposed
				typeRequiredTypes = addRequiredTypes(materialBlueprint, graphNode == null ? null : dependencyGraph);
				if (graphNode != null) {
					graphNode.setRequiredTypes(typeRequiredTypes);
				}
			} else {
				typeBlueprint = null;
				typeRequiredTypes = null;
				if (materialBlueprintType != null)
					decompositionState = DecompositionState.NOT_CONFIGURED;
				else
					decompositionState = DecompositionState.NEVER_DECOMPOSED;
			}
			requiredTypes.add(new RequiredType<AbstractType>(type, units, typeBlueprint, decompositionState, typeRequiredTypes));
		}
		Collections.sort(requiredTypes);
		return requiredTypes;
	}

	private AbstractType toBusinessType(final Type typeDto, final InventoryType inventoryType) {
		final AbstractType type;
		if (inventoryType.isMineral()) {
			if (typeDto != null) {
				type = new MineralImpl(typeDto);
			} else {
				type = new MissingMineralImpl(inventoryType);
			}
		} else {
			if (typeDto != null) {
				type = new ComponentImpl(typeDto);
			} else {
				type = new MissingComponentImpl(inventoryType);
			}
		}
		return type;
	}
}
