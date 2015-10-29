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

package eu.esdihumboldt.hale.common.align.model.functions;

/**
 * Rename function constants.
 * 
 * @author Kai Schwierczek
 */
public interface RenameFunction {

	/**
	 * The rename function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.rename";

	/**
	 * Name of the parameter specifying whether a structural rename should be
	 * performed or not. Default value for the parameter is <code>false</code>.
	 */
	public static final String PARAMETER_STRUCTURAL_RENAME = "structuralRename";

	/**
	 * Name of the parameter specifying whether for the structural rename, when
	 * checking for structure equality, the namespaces of properties may be
	 * ignored. Default value for the parameter is <code>false</code>.
	 */
	public static final String PARAMETER_IGNORE_NAMESPACES = "ignoreNamespaces";

	/**
	 * Name of the parameter specifying whether for the structural rename,
	 * geometry objects should be copied. Default value for the parameter is
	 * <code>true</code>.
	 */
	public static final String PARAMETER_COPY_GEOMETRIES = "copyGeometries";

}
