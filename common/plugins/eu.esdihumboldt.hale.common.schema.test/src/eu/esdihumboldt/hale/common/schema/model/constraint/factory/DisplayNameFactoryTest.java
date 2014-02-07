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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;

/**
 * Tests for {@link DisplayNameFactory}.
 * 
 * @author Simon Templer
 */
public class DisplayNameFactoryTest extends
		AbstractPropertiesCompareConstraintFactoryTest<DisplayName> {

	/**
	 * Test with a custom name.
	 * 
	 * @throws Exception if an error occurs
	 */
	public void testStoreRestoreCustomName() throws Exception {
		storeRestoreTest(new DisplayName("Awesome"));
	}

	/**
	 * Test with the constraint default.
	 * 
	 * @throws Exception if an error occurs
	 */
	public void testStoreRestoreDefault() throws Exception {
		storeRestoreTest(new DisplayName());
	}

	@Override
	protected List<String> getPropertiesToCompare() {
		return Collections.singletonList("customName");
	}

}
