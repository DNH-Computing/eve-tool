package nz.net.dnh.eve.legacy.web.view;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatDelegate {
	private final SimpleDateFormat dateFormat;

	public DateFormatDelegate(final SimpleDateFormat simpleDateFormat) {
		this.dateFormat = simpleDateFormat;
		
	}

	public String format(final Date date) {
		if (date == null)
			return null;
		else
			return this.dateFormat.format(date);
	}
}