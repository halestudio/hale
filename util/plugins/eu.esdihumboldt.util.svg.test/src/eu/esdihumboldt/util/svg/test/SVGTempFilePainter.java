/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.svg.test;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SVG painter with an associated temporary file.
 * 
 * @author Simon Templer
 */
public class SVGTempFilePainter extends SVGPainter {

	private Path tempFile;
	private final String filePrefix;

	/**
	 * Create a new painter.
	 * 
	 * @param settings the paint settings
	 */
	public SVGTempFilePainter(PaintSettings settings) {
		this(settings, null);
	}

	/**
	 * Create a new painter.
	 * 
	 * @param settings the paint settings
	 * @param filePrefix the prefix for the temporary file
	 */
	public SVGTempFilePainter(PaintSettings settings, String filePrefix) {
		super(settings);

		this.filePrefix = (filePrefix != null) ? (filePrefix) : ("testdrawing");
	}

	/**
	 * Write the graphic to a temporary file.
	 * 
	 * @throws IOException if writing the file fails
	 */
	public void writeToFile() throws IOException {
		if (tempFile == null) {
			tempFile = Files.createTempFile(filePrefix, ".svg");
		}
		writeToFile(tempFile);
		System.out.println("Test drawing written to " + tempFile);
	}

	/**
	 * Write the graphic to a temporary file. Open the file is possible.
	 * 
	 * @throws IOException if writing the file fails
	 */
	public void writeAndOpenFile() throws IOException {
		writeToFile();
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(tempFile.toFile());
		}
	}

	/**
	 * Remove the created temporary file.
	 * 
	 * @throws IOException if deleting the file fails
	 */
	public void cleanup() throws IOException {
		if (tempFile != null) {
			Files.delete(tempFile);
			tempFile = null;
		}
	}
}
