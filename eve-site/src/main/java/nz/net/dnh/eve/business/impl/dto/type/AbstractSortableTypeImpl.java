package nz.net.dnh.eve.business.impl.dto.type;

import nz.net.dnh.eve.business.AbstractType;

public abstract class AbstractSortableTypeImpl implements AbstractType {
	@Override
	public int compareTo(final AbstractType o) {
		final int nameComparison = getName().compareTo(o.getName());
		if (nameComparison != 0)
			return nameComparison;
		else
			return Integer.valueOf(getId()).compareTo(o.getId());
	}
}
