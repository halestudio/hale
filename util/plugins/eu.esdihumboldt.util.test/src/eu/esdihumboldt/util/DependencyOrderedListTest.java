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
package eu.esdihumboldt.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link DependencyOrderedList}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class DependencyOrderedListTest {

	/**
	 * Tests if the order of the items in the list is correct
	 */
	@Test
	public void testOrder() {
		Map<String, Set<String>> dependencies = new HashMap<String, Set<String>>();

		Set<String> cdep = new HashSet<String>();
		cdep.add("A"); //$NON-NLS-1$
		cdep.add("B"); //$NON-NLS-1$
		dependencies.put("C", cdep); //$NON-NLS-1$

		dependencies.put("A", new HashSet<String>()); //$NON-NLS-1$

		dependencies.put("B", Collections.singleton("A")); //$NON-NLS-1$ //$NON-NLS-2$

		dependencies.put("X", new HashSet<String>()); //$NON-NLS-1$

		Set<String> ydep = new HashSet<String>();
		ydep.add("B"); //$NON-NLS-1$
		ydep.add("X"); //$NON-NLS-1$
		dependencies.put("Y", ydep); //$NON-NLS-1$

		DependencyOrderedList<String> dol = new DependencyOrderedList<String>(dependencies);

		List<String> items = dol.getInternalList();

		System.out.println(items.toString());

		int a = items.indexOf("A");
		int b = items.indexOf("B");
		int c = items.indexOf("C");
		int x = items.indexOf("X");
		int y = items.indexOf("Y");

		Assert.assertTrue("Item A missing", a >= 0); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertTrue("Item B missing", b >= 0); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertTrue("Item C missing", c >= 0); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertTrue("Item X missing", x >= 0); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertTrue("Item Y missing", y >= 0); //$NON-NLS-1$ //$NON-NLS-2$

		Assert.assertEquals("Wrong list size", 5, items.size()); //$NON-NLS-1$

		Assert.assertTrue("A must be before C", a < c); //$NON-NLS-1$
		Assert.assertTrue("B must be before C", b < c); //$NON-NLS-1$

		Assert.assertTrue("A must be before B", a < b); //$NON-NLS-1$

		Assert.assertTrue("B must be before Y", b < y); //$NON-NLS-1$
		Assert.assertTrue("X must be before Y", x < y); //$NON-NLS-1$
	}

	/**
	 * Tests if the order of the items in the list is correct
	 */
	@Test
	public void testCycle() {
		Map<String, Set<String>> dependencies = new HashMap<String, Set<String>>();

		Set<String> cdep = new HashSet<String>();
		cdep.add("A"); //$NON-NLS-1$
		cdep.add("B"); //$NON-NLS-1$
		dependencies.put("C", cdep); //$NON-NLS-1$

		dependencies.put("A", new HashSet<String>()); //$NON-NLS-1$

		dependencies.put("B", Collections.singleton("C")); //$NON-NLS-1$ //$NON-NLS-2$

		DependencyOrderedList<String> dol = new DependencyOrderedList<String>(dependencies);

		List<String> items = dol.getInternalList();

		System.out.println(items.toString());

		int a = items.indexOf("A");
		int b = items.indexOf("B");
		int c = items.indexOf("C");

		Assert.assertTrue("Item A missing", a >= 0); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertTrue("Item B missing", b >= 0); //$NON-NLS-1$ //$NON-NLS-2$
		Assert.assertTrue("Item C missing", c >= 0); //$NON-NLS-1$ //$NON-NLS-2$

		Assert.assertEquals("Wrong list size", 3, items.size()); //$NON-NLS-1$

		Assert.assertTrue("A must be before B", a < b); //$NON-NLS-1$
		Assert.assertTrue("A must be before C", a < c); //$NON-NLS-1$
	}

}
