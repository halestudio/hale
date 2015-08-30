/*
 * Copyright (c) 2013 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.application.splash;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.splash.EclipseSplashHandler;
import org.osgi.framework.Version;

/**
 * Splash screen that shows current version information at the bottom of the
 * splash screen.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class HaleSplash extends EclipseSplashHandler {

	/**
	 * Name of the product property containing the revision.
	 */
	private static final String PRODUCT_PROP_REVISION = "revision";

	/**
	 * Name of the product property containing the copyright notice.
	 */
	private static final String PRODUCT_PROP_COPYRIGHT = "splashCopyrightNotice";

	/**
	 * Name of the product property containing the version tag.
	 */
	private static final String PRODUCT_PROP_VERSION_TAG = "versionTag";

	/**
	 * Name of the product property containing the font color.
	 */
	private static final String PRODUCT_PROP_SPLASH_FONT_COLOR = "splashFontColor";

	/**
	 * Minimum font size in points for the version information.
	 */
	private static final int MIN_SIZE = 5;

	/**
	 * Margin in pixels at the bottom of the splash screen for the version
	 * information string.
	 */
	private static final int BOTTOM_MARGIN = 6;

	/**
	 * The maximum height in pixels of the version information.
	 */
	protected static final int MAX_HEIGHT = 36;

	/**
	 * The custom font for the version information, may be <code>null</code>.
	 */
	private Font versionStringFont;

	/**
	 * Our custom font color.
	 */
	private RGB customFontColor;

	@Override
	public void init(Shell splash) {
		IProduct product = Platform.getProduct();
		if (product != null) {
			/*
			 * Using a custom property for the foreground color, because when
			 * using the one defined in IProductConstants, it is overriden when
			 * recreating a run configuration from the product.
			 */
			String foregroundColorString = product.getProperty(PRODUCT_PROP_SPLASH_FONT_COLOR);
			int foregroundColorInteger;
			try {
				foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
			} catch (Exception ex) {
				foregroundColorInteger = 0xD2D7FF; // off white
			}

			/*
			 * Foreground color will be overriden in super.init(...), that's why
			 * we have to set it in getContent() - store it here for later
			 * access.
			 */
			customFontColor = new RGB((foregroundColorInteger & 0xFF0000) >> 16,
					(foregroundColorInteger & 0xFF00) >> 8, foregroundColorInteger & 0xFF);
		}

		super.init(splash);

		if (product != null) {
			// get application version
			Version haleVersion = Version.parseVersion(Display.getAppVersion());

			// classified as development version if a qualifier other than
			// RELEASE is given
			boolean developmentVersion = haleVersion.getQualifier() != null
					&& !haleVersion.getQualifier().isEmpty()
					&& !haleVersion.getQualifier().equalsIgnoreCase("RELEASE");

			if (!developmentVersion) {
				// strip qualifier for RELEASE
				haleVersion = new Version(haleVersion.getMajor(), haleVersion.getMinor(),
						haleVersion.getMicro());
			}

			StringBuilder versionStringBuilder = new StringBuilder();
			versionStringBuilder.append(haleVersion.toString());

			if (developmentVersion) {
				// add revision information
				String revisionString = product.getProperty(PRODUCT_PROP_REVISION);
				if (revisionString != null && !revisionString.isEmpty()) {
					versionStringBuilder.insert(0, '\n');
					versionStringBuilder.insert(0, revisionString);
					versionStringBuilder.insert(0, "Revision ");
				}
			}

			// add version tag
			String versionTagString = product.getProperty(PRODUCT_PROP_VERSION_TAG);
			if (versionTagString != null && !versionTagString.isEmpty()) {
				versionStringBuilder.append('-');
				versionStringBuilder.append(versionTagString);
			}

			// add copyright notice
			String copyrightString = product.getProperty(PRODUCT_PROP_COPYRIGHT);
			if (copyrightString != null && !copyrightString.isEmpty()) {
				versionStringBuilder.append(' ');
				versionStringBuilder.append(copyrightString);
			}

			final String versionString = versionStringBuilder.toString();

			getContent().addPaintListener(new PaintListener() {

				@Override
				public void paintControl(PaintEvent e) {
					// computed version string location
					Point extent = e.gc.textExtent(versionString);
					if (versionStringFont == null) {
						// find fitting font
						Font customFont = null;

						while (extent.x >= e.width || extent.y > MAX_HEIGHT) {
							FontData[] orgFont = e.gc.getFont().getFontData();

							// minimum font size
							if (orgFont[0].getHeight() <= MIN_SIZE) {
								break;
							}

							FontData fd = new FontData(orgFont[0].toString());
							fd.setHeight(orgFont[0].getHeight() - 1);
							if (customFont != null) {
								// dispose previous custom font
								customFont.dispose();
							}
							customFont = new Font(e.display, fd);
							e.gc.setFont(customFont);
							extent = e.gc.textExtent(versionString);
						}

						if (customFont != null) {
							versionStringFont = customFont;
						}
					}

					e.gc.setForeground(getForeground());
					e.gc.drawText(versionString, (e.width - extent.x) / 2, e.height - extent.y
							- BOTTOM_MARGIN, true);
				}
			});
		}
	}

	@Override
	protected Composite getContent() {
		if (customFontColor != null) {
			setForeground(customFontColor);
		}

		return super.getContent();
	}

	@Override
	public void dispose() {
		super.dispose();

		if (versionStringFont != null) {
			versionStringFont.dispose();
		}
	}

}
