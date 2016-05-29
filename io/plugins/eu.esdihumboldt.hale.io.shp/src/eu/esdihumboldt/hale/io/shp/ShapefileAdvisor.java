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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.eclipse.core.runtime.content.IContentType;
import org.geotools.data.shapefile.files.ShpFileType;

import com.google.common.io.ByteStreams;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Resource advisor for Shapefiles. When copying a Shapefile it also copies the
 * auxiliary files.
 * 
 * @author Simon Templer
 */
public class ShapefileAdvisor extends DefaultResourceAdvisor {

	private static final ALogger log = ALoggerFactory.getLogger(ShapefileAdvisor.class);

	@Override
	public void copyResource(LocatableInputSupplier<? extends InputStream> resource,
			final Path target, IContentType resourceType, boolean includeRemote,
			IOReporter reporter) throws IOException {
		URI orgUri = resource.getLocation();
		if (orgUri == null) {
			throw new IOException("URI for original resource must be known");
		}

		// copy if files can be resolved as a Path
		Path orgPath = null;
		try {
			orgPath = Paths.get(orgUri);
		} catch (Exception e) {
			// ignore
		}

		if (orgPath != null) {
			// determine the filename w/o extension
			String filemain = orgPath.getFileName().toString();
			int extPos = filemain.lastIndexOf('.');
			if (extPos > 0) {
				filemain = filemain.substring(0, extPos);
			}
			// matcher for associated files
			final PathMatcher auxfiles = orgPath.getFileSystem()
					.getPathMatcher("glob:" + filemain + ".???");

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
		else {
			// copy if not accessible through file system

			// copy the main file
			try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(target));
					InputStream in = new DefaultInputSupplier(orgUri).getInput()) {
				ByteStreams.copy(in, out);
			}

			// determine base URI w/o dot and extension
			String base = orgUri.toASCIIString();
			int extPos = base.lastIndexOf('.');
			if (extPos > 0) {
				base = base.substring(0, extPos);
			}

			// determine file base name w/o dot and extension
			String filemain = target.getFileName().toString();
			extPos = filemain.lastIndexOf('.');
			if (extPos > 0) {
				filemain = filemain.substring(0, extPos);
			}

			for (ShpFileType type : ShpFileType.values()) {
				if (!type.equals(ShpFileType.SHP)) {
					try {
						URI source = URI.create(base + type.extensionWithPeriod);
						if (HaleIO.testStream(source, true)) {
							Path targetFile = target
									.resolveSibling(filemain + type.extensionWithPeriod);
							// copy the auxiliary file
							try (OutputStream out = new BufferedOutputStream(
									Files.newOutputStream(targetFile));
									InputStream in = new DefaultInputSupplier(source).getInput()) {
								ByteStreams.copy(in, out);
							}
						}
					} catch (Exception e) {
						log.debug("Failed to copy auxiliary file for Shapefile", e);
					}
				}
			}
		}
	}

}
