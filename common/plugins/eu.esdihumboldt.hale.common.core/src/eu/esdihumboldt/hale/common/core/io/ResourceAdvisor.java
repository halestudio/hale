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

package eu.esdihumboldt.hale.common.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Advisor for handling specific resources.
 * 
 * @author Simon Templer
 */
public interface ResourceAdvisor {

	/**
	 * Copy a resource to a target location.
	 * 
	 * @param resource the resource to copy
	 * @param target the target location, usually a file
	 * @param resourceType the resource content type, may be <code>null</code>
	 *            if unknown
	 * @param includeRemote specifies if auxiliary resources associated to the
	 *            resource that are available in a remote location should be
	 *            copied too
	 * @param reporter the reporter of the I/O process any errors should be
	 *            reported to
	 * @throws IOException if copying the resource fails or copying the resource
	 *             is not possible
	 */
	public void copyResource(LocatableInputSupplier<? extends InputStream> resource, Path target,
			IContentType resourceType, boolean includeRemote, IOReporter reporter)
			throws IOException;

}
