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

package eu.esdihumboldt.hale.io.xsd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.impl.DefaultResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.util.XMLSchemaUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Resource advisor for XML Schemas. Additionally to the main schema file it
 * copies also referenced schema files if applicable.
 * 
 * @author Simon Templer
 */
public class XMLSchemaResourceAdvisor extends DefaultResourceAdvisor {

	@Override
	public void copyResource(LocatableInputSupplier<? extends InputStream> resource, Path target,
			IContentType resourceType, boolean includeRemote, IOReporter reporter)
			throws IOException {
		URI pathUri = resource.getLocation();
		if (pathUri == null) {
			throw new IOException("URI for original resource must be known");
		}

		// first copy the schema file to the new location
		super.copyResource(resource, target, resourceType, includeRemote, reporter);

		// update schema files
		File newFile = target.toFile();
		XMLSchemaUpdater.update(newFile, pathUri, includeRemote, reporter);
	}

}
