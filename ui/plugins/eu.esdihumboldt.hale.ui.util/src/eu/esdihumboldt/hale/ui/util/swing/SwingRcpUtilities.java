/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.util.swing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Utilities for working with Swing and SWT
 * 
 * @author Simon Templer, Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SwingRcpUtilities {

	private static final ALogger _log = ALoggerFactory.getLogger(SwingRcpUtilities.class);

	/**
	 * is the Look and Feel (laf)initialized?
	 */
	private static boolean lafInitialized = false;

	/**
	 * Is the RcpConfig initialized?
	 */
	private static boolean initialized = false;

	/**
	 * Swing setup, should be called before any AWT/Swing Component is created
	 */
	public static void setup() {
		if (!initialized) {
			// reduce flicker on Windows
			System.setProperty("sun.awt.noerasebackground", "true"); //$NON-NLS-1$ //$NON-NLS-2$

			// setup look and feel
			setupLookAndFeel();

			// initialize MutableGui context
			// FIXME: check requirement on MutableGui or alternative solution
			// (using GeoTools map component!)
			// GuiConfiguration.setCustomConfiguration(new
			// RCPGuiConfiguration());

			initialized = true;
		}
	}

	/**
	 * Setup Look and Feel to match SWT looks
	 */
	public static void setupLookAndFeel() {
		if (!lafInitialized) {
			try {
				if (Platform.WS_GTK.equals(Platform.getWS())) {
					/*
					 * Work-around for Eclipse Bug 341799
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=341799
					 * 
					 * A LookAndFeel other than the GTK look and feel has to be
					 * used when using GTK as window managing system in SWT, as
					 * access to GTK of ATW/Swing and SWT is not synchronized.
					 */
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				}
				else {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			} catch (Exception e) {
				_log.error("Error setting system look and feel", e); //$NON-NLS-1$
			}

			lafInitialized = true;
		}
	}

	/**
	 * Convert a SWT Image to a {@link BufferedImage}
	 * 
	 * {@link "http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java?view=co"}
	 * 
	 * @param data the SWT {@link ImageData}
	 * @return the AWT {@link BufferedImage}
	 */
	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask,
					palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
			return bufferedImage;
		}
		else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue,
						data.transparentPixel);
			}
			else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	/**
	 * Convert a {@link BufferedImage} to a SWT Image.
	 * 
	 * {@link "http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java?view=co"}
	 * 
	 * @param bufferedImage the AWT {@link BufferedImage}
	 * @return the SWT {@link ImageData}
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF,
							rgb & 0xFF));
					data.setPixel(x, y, pixel);
					// also set the alpha value (ST)
					data.setAlpha(x, y, colorModel.getAlpha(rgb));
				}
			}
			return data;
		}
		else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	/**
	 * Create a SWT Image from an {@link ImageIcon}
	 * 
	 * {@link "http://www.eclipseproject.de/modules.php?name=Forums&file=viewtopic&t=5489"}
	 * {@link "http://www.9php.com/FAQ/cxsjl/java/2007/11/5033330101296.html"}
	 * 
	 * @param icon the {@link ImageIcon}
	 * @return the SWT {@link ImageData}
	 */
	public static ImageData convertToSWT(ImageIcon icon) {
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_RGB);

		img.getGraphics().setColor(Color.WHITE);
		img.getGraphics().fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		img.getGraphics().drawImage(icon.getImage(), 0, 0, null);

		return convertToSWT(img);
	}

	/**
	 * Convert a {@link RGB} to an AWT color
	 * 
	 * @param rgb the {@link RGB}
	 * 
	 * @return the color
	 */
	public static Color convertToColor(RGB rgb) {
		return new Color(rgb.red, rgb.green, rgb.blue);
	}

}
