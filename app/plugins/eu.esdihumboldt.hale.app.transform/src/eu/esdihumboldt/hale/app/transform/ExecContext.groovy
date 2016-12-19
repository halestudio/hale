/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.app.transform

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.headless.transform.filter.InstanceFilterDefinition
import groovy.transform.CompileStatic

/**
 * Execution context for the {@link ExecApplication}.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ExecContext {

	/**
	 * URI pointing to the HALE project file.
	 */
	URI project

	/**
	 * URI pointing to the source data to transform.
	 */
	List<URI> sources = []

	/**
	 * Specific source I/O provider identifier to use.
	 */
	List<String> sourceProviderIds = []

	/**
	 * Includes in case the source is a directory
	 */
	List<List<String>> sourceIncludes = []

	/**
	 * Excludes in case the source is a directory
	 */
	List<List<String>> sourceExcludes = []

	/**
	 * Settings for reading the source.
	 */
	List<Map<String, Value>> sourcesSettings = []

	/**
	 * The target file to write the transformed data to.
	 */
	URI target

	/**
	 * Name of the export configuration preset.
	 */
	String preset

	/**
	 * Specific target I/O provider identifier to use.
	 */
	String targetProviderId

	/**
	 * Settings for writing the target.
	 */
	Map<String, Value> targetSettings = [:]

	/**
	 * Identifiers of instance validator to use (if any).
	 */
	List<String> validateProviderIds

	/**
	 * Settings for writing the target.
	 */
	Map<String, Value> validateSettings = [:]

	/**
	 * The target file to write any reports to, optional.
	 */
	File reportsOut

	/**
	 * If an exception failing the transformation should be logged.
	 */
	boolean logException = false

	/**
	 * If Groovy based transformation functions should be restricted.
	 */
	boolean restrictGroovy = true

	/**
	 * Filters apply on all sources commonly
	 */
	final InstanceFilterDefinition filters = new InstanceFilterDefinition()
}
