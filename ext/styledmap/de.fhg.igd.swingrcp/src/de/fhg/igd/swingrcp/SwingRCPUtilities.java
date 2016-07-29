/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.swingrcp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * SwingRCPUtilities
 * 
 * @author Simon Templer
 */
public class SwingRCPUtilities {

	private static final Log log = LogFactory.getLog(SwingRCPUtilities.class);

	private static boolean lafInitialized = false;

	private static boolean initialized = false;

	/**
	 * Swing setup, should be called before any AWT/Swing Component is created
	 */
	public static void setup() {
		if (!initialized) {
			// set UIManager class loader to allow it to find custom LaFs
			UIManager.put("ClassLoader", SwingRCPUtilities.class.getClassLoader());

			// reduce flicker on Windows
			System.setProperty("sun.awt.noerasebackground", "true");

			// setup look and feel
			setupLookAndFeel();

			initialized = true;
		}
	}

	/**
	 * Setup Look and Feel to match SWT looks
	 */
	public static void setupLookAndFeel() {
		if (!lafInitialized) {
			String laf;
			if (Platform.WS_GTK.equals(Platform.getWS())) {
				/*
				 * Work-around for Eclipse Bug 341799
				 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=341799
				 *
				 * A LookAndFeel other than the GTK look and feel has to be used
				 * when using GTK as window managing system in SWT, as access to
				 * GTK of ATW/Swing and SWT is not synchronized.
				 */
				laf = "javax.swing.plaf.metal.MetalLookAndFeel";
			}
			else {
				laf = SwingRCPPlugin.getCustomLookAndFeel();

				if (laf == null) {
					laf = UIManager.getSystemLookAndFeelClassName();
				}
			}

			try {
				UIManager.setLookAndFeel(laf);
				log.info("Set look and feel to " + laf);
			} catch (Exception e) {
				log.error("Error setting look and feel: " + laf, e);
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
	 * @param applyAlphaMask true if the image data's alpha mask should be
	 *            applied to the result image (if there is any). This method
	 *            calls {@link #applyTransparencyMask(BufferedImage, ImageData)}
	 *            for that purpose.
	 * @return the AWT {@link BufferedImage}
	 */
	public static BufferedImage convertToAWT(ImageData data, boolean applyAlphaMask) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;

		BufferedImage result;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask,
					palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width, data.height), false,
					null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
			result = bufferedImage;
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
					colorModel.createCompatibleWritableRaster(data.width, data.height), false,
					null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			result = bufferedImage;
		}

		if (data.getTransparencyType() == SWT.TRANSPARENCY_MASK && applyAlphaMask) {
			result = applyTransparencyMask(result, data.getTransparencyMask());
		}
		return result;
	}

	/**
	 * Applies the given transparency mask to a buffered image. Always creates a
	 * new buffered image containing an alpha channel. Copies the old image into
	 * the new one and then sets the alpha pixels according to the given mask.
	 * 
	 * @param img the old image
	 * @param mask the alpha mask
	 * @return the new image with alpha channel applied
	 * @throws IllegalArgumentException if the image's size does not match the
	 *             mask's size
	 */
	public static BufferedImage applyTransparencyMask(BufferedImage img, ImageData mask) {
		if (mask.width != img.getWidth() || mask.height != img.getHeight()) {
			throw new IllegalArgumentException("Image size does not match the mask size");
		}

		// copy image and also convert to RGBA
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		g.drawImage(img, 0, 0, null);

		WritableRaster alphaRaster = result.getAlphaRaster();
		int alpha0[] = new int[] { 0 };
		int alpha255[] = new int[] { 255 };
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				alphaRaster.setPixel(x, y, mask.getPixel(x, y) == 0 ? alpha0 : alpha255);
			}
		}

		return result;
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
					int pixel = palette
							.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
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
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(icon.getIconWidth(),
				icon.getIconHeight());

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

	/**
	 * Get the workbench display
	 * 
	 * @return the display
	 */
	public static Display getDisplay() {
		return SwingRCPPlugin.getDefault().getWorkbench().getDisplay();
	}

	/**
	 * Get the active workbench shell
	 * 
	 * @return the shell
	 */
	public static Shell getShell() {
		return getDisplay().getActiveShell();
	}

}
