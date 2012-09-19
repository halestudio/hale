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

package eu.esdihumboldt.hale.ui.cst.debug.metadata;

import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;

/**
 * Transformation tree metadata constants.
 * 
 * @author Simon Templer
 */
public interface TransformationTreeMetadata {

	/**
	 * Metadata key for the populated transformation tree of an instance.
	 * 
	 * @see TreeState#SOURCE_POPULATED
	 */
	public static final String KEY_POPULATED_TREE = "cst.ttree.populated";

}
