package nz.net.dnh.eve.business.impl.dependencies;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A dependency graph which will {@link AbstractNode#doApply(Object) apply} all nodes in an order that ensures a node is not applied until
 * all its dependencies are applied.
 * 
 */
public class Graph<K, T, Node extends AbstractNode<K, T>> {
	private final Map<K, Node> nodes = new HashMap<>();

	/**
	 * Construct a graph with a single node which does not have any dependencies (generally the first node to be applied, unless a
	 * dependency is added to it later)
	 * 
	 * @param root
	 *            The node to add without any dependencies
	 */
	public Graph(final Node root) {
		addNode(root);
	}

	/**
	 * Add a dependency from child to the node corresponding to dependencyKey.
	 * <p>
	 * The node 'child' is added to this graph if it did not already exist. A node whose {@link AbstractNode#getKey() key} equals
	 * dependencyKey must already exist in the graph, and it will be added as a dependency of 'child' (that is, the node with the key
	 * 'dependencyKey' will be applied before the node 'child')
	 * 
	 * @param child
	 *            The node to add a dependency to. If a node equal to this node already exists in the graph, the existing node is updated
	 * @param dependencyKey
	 *            The key used to look up the dependency
	 * @return the child node in this graph, which may not be the same object as the 'child' parameter if it already existed in this graph
	 */
	public Node addDependency(final Node child, final K dependencyKey) {
		final Node dependencyNode = this.nodes.get(dependencyKey);
		if (dependencyNode == null)
			throw new IllegalArgumentException("No dependency node found for key " + dependencyKey);
		final Node realChild = addNode(child);
		realChild.addDependency(dependencyNode);
		return realChild;
	}

	private Node addNode(final Node node) {
		Node realChild = this.nodes.get(node.getKey());
		if (realChild == null) {
			realChild = node;
			this.nodes.put(node.getKey(), node);
		}
		return realChild;
	}

	public void apply(final T state) {
		final Collection<AbstractNode<?, ?>> done = new HashSet<>();
		while (!done.containsAll(this.nodes.values())) {
			boolean found = false;
			for (final Node node : this.nodes.values()) {
				if (node.shouldApply(done)) {
					node.apply(state, done);
					found = true;
					break;
				}
			}
			if (!found)
				throw new IllegalStateException("Could not evaluate all nodes (" + this.nodes
						+ "), last pass found 0 satisfied nodes. Already evaluated nodes: " + done);
		}
	}

	@Override
	public String toString() {
		return "Graph [nodes=" + this.nodes + "]";
	}

}
