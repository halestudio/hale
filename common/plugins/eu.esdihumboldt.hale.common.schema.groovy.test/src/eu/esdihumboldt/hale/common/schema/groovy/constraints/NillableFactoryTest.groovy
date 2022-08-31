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

package eu.esdihumboldt.hale.common.schema.groovy.constraints

import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import groovy.test.GroovyTestCase
import groovy.transform.CompileStatic

/**
 * Tests creation of {@link NillableFlag} constraints w/ {@link NillableFactory}.
 * 
 * @author Simon Templer
 */
@CompileStatic
class NillableFactoryTest extends GroovyTestCase {

	/*
	 * NOTE: In Eclipse in the editor there might be errors shown here,
	 * even if the code actually compiles.
	 */

	void testTrueFalse() {
		assertEquals NillableFlag.ENABLED, NillableFactory.instance.createConstraint(true)
		assertEquals NillableFlag.DISABLED, NillableFactory.instance.createConstraint(false)
	}
}
