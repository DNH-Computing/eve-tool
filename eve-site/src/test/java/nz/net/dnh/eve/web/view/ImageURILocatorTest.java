/**
 * 
 */
package nz.net.dnh.eve.web.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nz.net.dnh.eve.business.AbstractType;
import nz.net.dnh.eve.business.BlueprintReference;
import nz.net.dnh.eve.web.view.ImageURILocater.Size;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author Daniel Hodder (danielh)
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ImageURILocatorTest {
	private final ImageURILocater imageURILocater = new ImageURILocater();
	
	@Before
	public void injectBaseURI() {
		this.imageURILocater.baseLocatorURI = "foobar";
	}

	@Test
	public void fromSizeReturnTheirSizeEnum() {
		assertSame(Size._32x32, Size.fromSize(32));
		assertSame(Size._64x64, Size.fromSize(64));
		assertSame(Size._128x128, Size.fromSize(128));
		assertSame(Size._256x256, Size.fromSize(256));
	}

	@Test(expected = IllegalArgumentException.class)
	public void fromSizeThrowsAnExceptionForUnknownSizes() {
		Size.fromSize(100);
	}

	@Test
	public void getUriForTypeID() {
		assertEquals("foobar/100_32.png", this.imageURILocater.getUriForTypeID(100, 32));
	}

	@Test
	public void getUrlForBlueprint() {
		final BlueprintReference blueprintReference = mock(BlueprintReference.class);
		when(blueprintReference.getId()).thenReturn(100);

		assertEquals("foobar/100_32.png", this.imageURILocater.getUriForBlueprint(blueprintReference, 32));
	}

	@Test
	public void getUrlForType() {
		final AbstractType abstractType = mock(AbstractType.class);
		when(abstractType.getId()).thenReturn(100);

		assertEquals("foobar/100_32.png", this.imageURILocater.getUriForType(abstractType, 32));
	}
}
