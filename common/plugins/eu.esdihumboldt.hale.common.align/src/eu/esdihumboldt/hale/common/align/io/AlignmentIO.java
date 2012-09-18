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

package eu.esdihumboldt.hale.common.align.io;

/**
 * Alignment I/O utilities and constants.
 * 
 * @author Simon Templer
 * @since 2.5
 */
public abstract class AlignmentIO {

	/**
	 * ID of the action to load an alignment. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_ALIGNMENT = "eu.esdihumboldt.hale.io.align.read";

	/**
	 * The name of the project file containing the alignment. Reflects the file
	 * name registered in the extension.
	 */
	public static final String PROJECT_FILE_ALIGNMENT = "alignment.xml";

}
