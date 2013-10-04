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

package eu.esdihumboldt.hale.common.core.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.ResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Default resource advisor viable for most resources.
 * 
 * @author Simon Templer
 */
public class DefaultResourceAdvisor implements ResourceAdvisor {

	@Override
	public void copyResource(LocatableInputSupplier<? extends InputStream> resource, Path target,
			IContentType resourceType, boolean includeRemote, IOReporter reporter)
			throws IOException {
		try (InputStream in = resource.getInput()) {
			Files.copy(in, target);
		}
	}

}
