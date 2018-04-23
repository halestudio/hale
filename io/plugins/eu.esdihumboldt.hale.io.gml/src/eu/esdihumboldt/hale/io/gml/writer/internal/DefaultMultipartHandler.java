/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.util.Pair;

/**
 * Handler that adds an incrementing number to the target file name.
 * 
 * @author Florian Esser
 */
public class DefaultMultipartHandler implements MultipartHandler {

	private int currentPart = 1;

	@Override
	public String getTargetFilename(InstanceCollection part, URI location) {
		return formatTargetFilename(location);
	}

	/**
	 * Return the file name without extension and the extension as a
	 * {@link Pair}. If the file name ends with <code>.xml.gz</code> or
	 * <code>.gml.gz</code>, this will also be the returned extension (and not
	 * <code>gz</code>).
	 * 
	 * @param filename File name to split
	 * @return Pair of name and extension
	 */
	public static Pair<String, String> getFileNameAndExtension(String filename) {
		String nameWithoutExt;
		String extension;
		if (filename.endsWith(".gml.gz") || filename.endsWith(".xml.gz")) {
			String gz = Files.getFileExtension(filename);
			String nameWithoutGz = Files.getNameWithoutExtension(filename);
			String xgml = Files.getFileExtension(nameWithoutGz);

			nameWithoutExt = Files.getNameWithoutExtension(nameWithoutGz);
			extension = xgml + "." + gz;
		}
		else {
			nameWithoutExt = Files.getFileExtension(filename);
			extension = Files.getFileExtension(filename);
		}

		return new Pair<>(nameWithoutExt, extension);
	}

	private String formatTargetFilename(URI location) {
		Path origPath = Paths.get(location).normalize();
		Pair<String, String> nameAndExt = getFileNameAndExtension(origPath.toString());
		return String.format("%s%s%s.%04d.%s", origPath.getParent(), File.separator,
				nameAndExt.getFirst(), currentPart++, nameAndExt.getSecond());
	}
}
