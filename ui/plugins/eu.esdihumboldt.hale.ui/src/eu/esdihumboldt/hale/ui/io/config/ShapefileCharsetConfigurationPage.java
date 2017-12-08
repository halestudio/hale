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

package eu.esdihumboldt.hale.ui.io.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Character set configuration page for shapefile sources that tries to read the
 * character set from a .cpg file in the same directory.
 * 
 * @author Florian Esser
 */
public class ShapefileCharsetConfigurationPage
		extends CharsetConfigurationPage<ImportProvider, IOWizard<ImportProvider>> {

	private boolean readFromCpg = false;

	/**
	 * Default constructor
	 */
	public ShapefileCharsetConfigurationPage() {
		super(Mode.autoDetect);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.CharsetConfigurationPage#successMessage(java.nio.charset.Charset)
	 */
	@Override
	protected String successMessage(Charset cs) {
		if (!readFromCpg) {
			return super.successMessage(cs);
		}
		else {
			return MessageFormat.format("Detected charset (from .CPG file): {0}", cs.displayName());
		}

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.CharsetConfigurationPage#detectCharset(eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier)
	 */
	@Override
	protected void detectCharset(LocatableInputSupplier<? extends InputStream> source)
			throws IOException {
		readFromCpg = false;
		URI sourceLocation = source.getLocation();
		if ("file".equalsIgnoreCase(sourceLocation.getScheme())) {
			Path sourceFile = Paths.get(sourceLocation);
			if (!sourceFile.toFile().isFile()) {
				throw new IllegalArgumentException("Source is not a file");
			}
			Path sourcePath = sourceFile.getParent();
			String sourceFileWithoutExt = Files
					.getNameWithoutExtension(sourceFile.getFileName().toString());
			String cpgFileName = sourceFileWithoutExt + ".cpg";
			Path cpgFile = sourcePath.resolve(cpgFileName);

			if (cpgFile.toFile().exists()) {
				String charset = Files.readFirstLine(cpgFile.toFile(), Charset.forName("UTF-8"));
				super.setCharset(charset);
				super.setCharsetDecoration(
						MessageFormat.format("Character set read from file: {0}", cpgFileName),
						FieldDecorationRegistry.getDefault()
								.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL)
								.getImage(),
						0);
				readFromCpg = true;
				update();
			}
			else {
				super.setCharsetDecoration(
						"No accompanying .CPG file found. Please select charset manually.",
						FieldDecorationRegistry.getDefault()
								.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage(),
						0);
			}
		}
		else {
			super.setCharsetDecoration("Not a local file, please select charset manually.",
					FieldDecorationRegistry.getDefault()
							.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage(),
					0);
		}

	}
}
