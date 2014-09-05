package nz.net.dnh.eve.legacy.web.view;

import java.util.Calendar;

import nz.net.dnh.eve.business.AbstractType;

import org.springframework.stereotype.Service;

@Service
public final class DashboardViewHelper {
	public boolean isTypeDataOld(final AbstractType type) {
		if (type.getCostLastUpdated() == null)
			return false;

		final Calendar timeWhenDataIsStale = Calendar.getInstance();
		timeWhenDataIsStale.add(Calendar.DAY_OF_MONTH, -7);

		return timeWhenDataIsStale.getTime().after(type.getCostLastUpdated());
	}
}
