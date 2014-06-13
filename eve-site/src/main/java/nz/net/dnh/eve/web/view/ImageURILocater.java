package nz.net.dnh.eve.web.view;

import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.BlueprintReference;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.annotations.VisibleForTesting;

public final class ImageURILocater {
	@VisibleForTesting
	@Value("${eve.imageBaseURI}")
	String baseLocatorURI;

	public String getUriForType(final AbstractType type, final long size) {
		return getUriForTypeID(type.getId(), size);
	}

	public String getUriForBlueprint(final BlueprintReference blueprint, final long size) {
		return getUriForTypeID(blueprint.getId(), size);
	}

	public String getUriForTypeID(final int id, final long size) {
		return this.baseLocatorURI + "/" + id + "_" + Size.fromSize(size).SizeString + ".png";
	}

	public static enum Size {
		_32x32("32"),
		_64x64("64"),
		_128x128("128"),
		_256x256("256");

		final String SizeString;

		private Size(final String sizeString) {
			this.SizeString = sizeString;
		}

		public static Size fromSize(final long size) {
			switch ((int) size) {
				case 32:
					return _32x32;

				case 64:
					return _64x64;

				case 128:
					return _128x128;

				case 256:
					return _256x256;

				default:
					throw new IllegalArgumentException(size + " was not a recognized size");
			}
		}
	}
}
