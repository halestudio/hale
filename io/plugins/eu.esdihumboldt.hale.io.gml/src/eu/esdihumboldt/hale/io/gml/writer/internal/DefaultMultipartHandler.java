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

	private String formatTargetFilename(URI location) {
		Path origPath = Paths.get(location).normalize();
		return String.format("%s%s%s.%04d.%s", origPath.getParent(), File.separator,
				Files.getNameWithoutExtension(origPath.toString()), currentPart++,
				Files.getFileExtension(origPath.toString()));
	}
}
