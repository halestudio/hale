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

package eu.esdihumboldt.hale.io.shp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.impl.DefaultResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Resource advisor for Shapefiles. When copying a Shapefile it also copies the
 * auxiliary files.
 * 
 * @author Simon Templer
 */
public class ShapefileAdvisor extends DefaultResourceAdvisor {

	@Override
	public void copyResource(LocatableInputSupplier<? extends InputStream> resource,
			final Path target, IContentType resourceType, boolean includeRemote, IOReporter reporter)
			throws IOException {
		URI orgUri = resource.getLocation();
		if (orgUri == null) {
			throw new IOException("URI for original resource must be known");
		}

		// determine the filename w/o extension
		Path orgPath = Paths.get(orgUri);
		String filemain = orgPath.getFileName().toString();
		int extPos = filemain.lastIndexOf('.');
		if (extPos > 0) {
			filemain = filemain.substring(0, extPos);
		}
		// matcher for associated files
		final PathMatcher auxfiles = orgPath.getFileSystem().getPathMatcher(
				"glob:" + filemain + ".???");

		// find all associated files
		Path orgDir = orgPath.getParent();
		try (DirectoryStream<Path> files = Files.newDirectoryStream(orgDir,
				new DirectoryStream.Filter<Path>() {

					@Override
					public boolean accept(Path entry) throws IOException {
						return auxfiles.matches(entry.getFileName());
					}
				})) {
			// copy the files
			for (Path orgFile : files) {
				Path targetFile = target.resolveSibling(orgFile.getFileName());
				Files.copy(orgFile, targetFile);
			}
		}
	}

}
