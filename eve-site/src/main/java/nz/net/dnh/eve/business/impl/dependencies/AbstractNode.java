package nz.net.dnh.eve.business.impl.dependencies;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractNode<K, T> {
	private final Collection<AbstractNode<?, ?>> dependencies = new ArrayList<>();

	/** @return true if this node has not already been applied, and all of its dependencies have */
	public boolean shouldApply(final Collection<AbstractNode<?, ?>> evaluatedNodes) {
		return !evaluatedNodes.contains(this) && evaluatedNodes.containsAll(this.dependencies);
	}

	/** Apply this node's effects to the state and record that it has been evaluated */
	public void apply(final T state, final Collection<AbstractNode<?, ?>> evaluatedNodes) {
		doApply(state);
		evaluatedNodes.add(this);
	}

	/** @return A key which can be used to uniquely look up this node */
	public abstract K getKey();

	/** Do the actual work to apply this node's effects */
	protected abstract void doApply(T state);

	/** Add a node which must be evaluated before this node is satisfied. NB: This is the opposite of required types. */
	public void addDependency(final AbstractNode<?, ?> dependency) {
		this.dependencies.add(dependency);
	}

	protected Collection<AbstractNode<?, ?>> getDependencies() {
		return this.dependencies;
	}
}
