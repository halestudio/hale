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

import groovy.test.GroovyTestCase
import groovy.transform.CompileStatic

/**
 * Tests creation of {@link DisplayName} constraints w/ {@link DisplayNameFactory}.
 * 
 * @author Simon Templer
 */
@CompileStatic
class DisplayNameFactoryTest extends GroovyTestCase {

	/*
	 * NOTE: In Eclipse in the editor there might be errors shown here,
	 * even if the code actually compiles.
	 */

	void testName() {
		assertEquals 'test', DisplayNameFactory.instance.createConstraint('test').customName
	}
}
