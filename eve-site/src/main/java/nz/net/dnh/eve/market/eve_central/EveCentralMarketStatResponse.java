package nz.net.dnh.eve.market.eve_central;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "exec_api")
public class EveCentralMarketStatResponse {
	private List<Type> types;

	@XmlElement(name = "type")
	@XmlElementWrapper(name = "marketstat")
	public List<Type> getTypes() {
		return this.types;
	}

	public void setTypes(final List<Type> types) {
		this.types = types;
	}

	public static class MarketStatData {
		// @XmlElement
		// private long volume;

		private BigDecimal average;

		// @XmlElement(name = "max")
		// private BigDecimal maximum;
		//
		// @XmlElement(name = "min")
		// private BigDecimal minimum;
		//
		// @XmlElement(name = "stddev")
		// private BigDecimal standardDeviation;

		private BigDecimal median;

		@XmlElement(name = "avg")
		public BigDecimal getAverage() {
			return this.average;
		}

		public void setAverage(final BigDecimal average) {
			this.average = average;
		}

		@XmlElement
		public BigDecimal getMedian() {
			return this.median;
		}

		public void setMedian(final BigDecimal median) {
			this.median = median;
		}

		// @XmlElement
		// private BigDecimal percentile;
	}

	public static class Type {
		private int id;
		private MarketStatData buy;
		private MarketStatData sell;
		private MarketStatData all;

		@XmlAttribute
		public int getId() {
			return this.id;
		}

		public void setId(final int id) {
			this.id = id;
		}

		@XmlElement
		public MarketStatData getBuy() {
			return this.buy;
		}

		public void setBuy(final MarketStatData buy) {
			this.buy = buy;
		}

		@XmlElement
		public MarketStatData getSell() {
			return this.sell;
		}

		public void setSell(final MarketStatData sell) {
			this.sell = sell;
		}

		@XmlElement
		public MarketStatData getAll() {
			return this.all;
		}

		public void setAll(final MarketStatData all) {
			this.all = all;
		}
	}
}