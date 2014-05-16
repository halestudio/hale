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

import groovy.transform.CompileStatic;

import java.net.URI

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
	URI source

	/**
	 * Name of the export configuration preset.
	 */
	String preset
	
	/**
	 * The target file to write the transformed data to.
	 */
	File out
	
	/**
	 * The target file to write any reports to, optional.
	 */
	File reportsOut
	
	//TODO validation?
	
}
