/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.test.constraints.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.factory.GeometryMetadataFactory;

/**
 * Tests related to the geometry metadata value constraint factory.
 * 
 * @author Simon Templer
 */
public class GeometryMetadataFactoryTest {

	/**
	 * Test if alias "jdbc.geometry" for geometry metadata constraint is
	 * working.
	 */
	@Test
	public void testAlias() {
		ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE
				.get("jdbc.geometry");
		assertNotNull(desc);
		assertEquals(GeometryMetadata.class, desc.getConstraintType());
		assertTrue(desc.getFactory() instanceof GeometryMetadataFactory);
	}

}
