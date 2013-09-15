/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.schema.groovy

import eu.esdihumboldt.hale.common.schema.helper.DefinitionPath
import eu.esdihumboldt.hale.common.schema.helper.internal.DefinitionPathImpl
import eu.esdihumboldt.hale.common.schema.model.Definition
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked



/**
 * Property accessor for {@link Definition}s.
 * 
 * It mutates, so it is only usable once.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
@CompileStatic
class DefinitionAccessor extends DefinitionAccessorBase<DefinitionPath> {

	/**
	 * Create an accessor for a given definition.
	 * 
	 * @param definition the definition
	 */
	DefinitionAccessor(Definition<?> definition) {
		super([
			(DefinitionPath) new DefinitionPathImpl([definition])
		])
	}
}
