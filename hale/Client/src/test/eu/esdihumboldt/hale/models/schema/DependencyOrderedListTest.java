/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package test.eu.esdihumboldt.hale.models.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import eu.esdihumboldt.hale.models.schema.DependencyOrderedList;

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
		cdep.add("A");
		cdep.add("B");
		dependencies.put("C", cdep);
		
		dependencies.put("A", new HashSet<String>());
		
		dependencies.put("B", Collections.singleton("A"));
		
		dependencies.put("X", new HashSet<String>());
		
		Set<String> ydep = new HashSet<String>();
		ydep.add("B");
		ydep.add("X");
		dependencies.put("Y", ydep);
		
		DependencyOrderedList<String> dol = new DependencyOrderedList<String>(dependencies);
		
		List<String> items = dol.getInternalList();
		
		System.out.println(items.toString());
		
		int a, b, c, x, y;
		
		Assert.assertTrue("Item A missing", (a = items.indexOf("A")) >= 0);
		Assert.assertTrue("Item B missing", (b = items.indexOf("B")) >= 0);
		Assert.assertTrue("Item C missing", (c = items.indexOf("C")) >= 0);
		Assert.assertTrue("Item X missing", (x = items.indexOf("X")) >= 0);
		Assert.assertTrue("Item Y missing", (y = items.indexOf("Y")) >= 0);
		
		Assert.assertEquals("Wrong list size", 5, items.size());
		
		Assert.assertTrue("A must be before C", a < c);
		Assert.assertTrue("B must be before C", b < c);
		
		Assert.assertTrue("A must be before B", a < b);
		
		Assert.assertTrue("B must be before Y", b < y);
		Assert.assertTrue("X must be before Y", x < y);
	}

}
