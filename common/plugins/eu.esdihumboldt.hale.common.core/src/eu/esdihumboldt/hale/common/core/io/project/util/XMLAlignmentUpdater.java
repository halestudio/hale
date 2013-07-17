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

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectExport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Class for updating alignment in the {@link ArchiveProjectExport}.<br>
 * Resolves the included base alignments, copies them next to the given
 * alignment (or a subdirectory) and adapts the include location in the
 * alignment.
 * 
 * @author Kai Schwierczek
 */
public class XMLAlignmentUpdater {

	private final static String CASTOR = "hale-alignment/base/@location";
	private final static String JAXB = "alignment/base/@location";

	private final static String CASTOR_AND_JAXB = CASTOR + " | " + JAXB;

	/**
	 * Reads the given alignment (resource) and searches for included base
	 * alignment in the file. If these files are local, the function tries to
	 * copy the resources into a new directory next to the given alignment and
	 * adapts the dependencies in it. The oldFile is the path of the alignment
	 * before it was copied to its new directory (eg. temporary directory). The
	 * oldFile is left untouched. Resource has to be a copy of oldFile.
	 * 
	 * @see XMLSchemaUpdater#update(File, URI, boolean, IOReporter)
	 * @see XMLPathUpdater#update(File, URI, String, boolean, IOReporter)
	 * @param resource the file of the new resource (will be adapted)
	 * @param oldFile the file of the old resource (will be untouched), may be
	 *            <code>null</code> in case it didn't exist before
	 * @param includeWebResources true if web resources should be copied and
	 *            updated too otherwise false
	 * @param reporter the reporter of the current I/O process where errors
	 *            should be reported to
	 * @throws IOException if file can not be updated
	 */
	public static void update(File resource, URI oldFile, boolean includeWebResources,
			IOReporter reporter) throws IOException {
		XMLPathUpdater.update(resource, oldFile, CASTOR_AND_JAXB, includeWebResources, reporter);
	}

}
