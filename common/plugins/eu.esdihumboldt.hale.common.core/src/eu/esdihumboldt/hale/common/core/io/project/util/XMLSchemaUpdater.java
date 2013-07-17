/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectExport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Class for updating XML schemas in the {@link ArchiveProjectExport}.<br>
 * Resolves the imported/included xml schemas, copies them next to the given
 * schema (or a subdirectory) and adapts the import/include location in the
 * schema.
 * 
 * @author Patrick Lieb
 */
public class XMLSchemaUpdater {

	private final static String IMPORT = "schema/import/@schemaLocation";
	private final static String INCLUDE = "schema/include/@schemaLocation";

	private final static String IMPORT_AND_INCLUDE = IMPORT + " | " + INCLUDE;

	/**
	 * Reads the given xml schema (resource) and searches for included and
	 * imported schemas in the file. If these files are local, the function
	 * tries to copy the resources into a new directory next to the given schema
	 * (resource) and adapts the dependencies in the resource. The oldFile is
	 * the path of the xml schema before it was copied to his new directory (eg.
	 * temporary directory). The oldFile keeps untouched. Resource has to be a
	 * copy of oldFile. <br>
	 * <br>
	 * Example:<br>
	 * resource file is 'C:/Local/Temp/1348138164029-0/watercourse/wfs_va.xsd' <br>
	 * oldFile is 'C:/igd/hale/watercourse/wfs_va.xsd'.<br>
	 * wfs_va.xsd has one schema import with location
	 * 'C:/igd/hale/watercourse/schemas/hydro.xsd'<br>
	 * So hydro.xsd is copied into 'C:/Local/Temp/1348138164029-0/watercourse/'
	 * (or a subdirectory) and the import location in wfs_va.xsd will be
	 * adapted.<br>
	 * Resources only will be copied once. In this case the schema location is
	 * solved relative to the originally schema.
	 * 
	 * @see XMLPathUpdater#update(File, URI, String, boolean, IOReporter)
	 * @param resource the file of the new resource (will be adapted)
	 * @param oldFile the file of the old resource (will be untouched)
	 * @param includeWebResources true if web resources should be copied and
	 *            updated too otherwise false
	 * @param reporter the reporter of the current I/O process where errors
	 *            should be reported to
	 * @throws IOException if file can not be updated
	 */
	public static void update(File resource, URI oldFile, boolean includeWebResources,
			IOReporter reporter) throws IOException {
		XMLPathUpdater.update(resource, oldFile, IMPORT_AND_INCLUDE, includeWebResources, reporter);
	}

}
