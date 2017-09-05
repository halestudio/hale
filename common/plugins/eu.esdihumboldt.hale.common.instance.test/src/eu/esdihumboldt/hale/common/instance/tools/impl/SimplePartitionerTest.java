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

package eu.esdihumboldt.hale.common.instance.tools.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;

/**
 * Tests for {@link SimplePartitioner}.
 * 
 * @author Simon Templer
 */
public class SimplePartitionerTest {

	static InstanceCollection createCollection(int num) {
		Collection<Instance> instances = new ArrayList<>();

		for (int i = 0; i < num; i++) {
			instances.add(new DefaultInstance(null, null));
		}

		InstanceCollection res = new DefaultInstanceCollection(instances);
		return res;
	}

	@Test
	public void testSize() {
		InstanceCollection c1 = createCollection(1000);
		assertEquals(1000, c1.size());

		try (ResourceIterator<InstanceCollection> it = new SimplePartitioner().partition(c1, 10)) {
			int count = 0;
			while (it.hasNext()) {
				InstanceCollection part = it.next();
				count += part.size();
			}
			assertEquals(1000, count);
		}
	}

}
