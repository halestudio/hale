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

package eu.esdihumboldt.hale.common.align.io.impl.internal

import eu.esdihumboldt.hale.common.align.io.LoadAlignmentContext;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex


/**
 * Default implementation of {@link LoadAlignmentContext}.
 * 
 * @author Simon Templer
 */
class LoadAlignmentContextImpl implements LoadAlignmentContext {

	/**
	 * The source schema type index.
	 */
	TypeIndex sourceTypes

	/**
	 * The target schema type index.
	 */
	TypeIndex targetTypes
}
