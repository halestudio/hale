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

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.io.InputStream;
import java.io.OutputStream;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * File associated/stored with a project. Implementations must have a default
 * constructor if they shall be used in a project.
 * 
 * @author Simon Templer
 */
public interface ProjectFile {

	/**
	 * Load the file from an input stream when loading a project.
	 * 
	 * @see #apply()
	 * @param in the input stream
	 * @throws Exception if an error occurs loading the file
	 */
	public void load(InputStream in) throws Exception;

	/**
	 * Reset the file when a project is loaded but no data for the file was
	 * present or loading the file failed.
	 * 
	 * @see #apply()
	 */
	public void reset();

	/**
	 * Apply the (loaded) configuration. Is called even if
	 * {@link #load(InputStream)} or {@link #apply()} haven't been called after
	 * the main project file has been loaded.
	 */
	public void apply();

	/**
	 * Store the file to an output stream when saving a project.
	 * 
	 * @param target the target
	 * @throws Exception if an error occurs saving the file
	 */
	public void store(LocatableOutputSupplier<OutputStream> target) throws Exception;

}
