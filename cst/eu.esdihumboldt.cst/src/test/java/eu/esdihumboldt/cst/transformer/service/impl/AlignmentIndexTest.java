/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service.impl;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class AlignmentIndexTest {
	
	/**
	 * Test method for {@link eu.esdihumboldt.cst.transformer.service.impl.AlignmentIndex#getFeatureTypeKey(String key, String namespace)}
	 */
	@Test
	public void testGetFeatureTypeKey() {
		String key1 = "http://xsdi.org/namespace/FeatureTypeName";
		String key2 = "http://xsdi.org/namespace/FeatureTypeName/AttributeName";
		String namespace1 = "http://xsdi.org/namespace";
		String namespace2 = "http://xsdi.org/namespace/";
		
		String[] results = new String[4];
		
		results[0] = AlignmentIndex.getFeatureTypeKey(key1, namespace1);
		results[1] = AlignmentIndex.getFeatureTypeKey(key1, namespace2);
		
		results[2] = AlignmentIndex.getFeatureTypeKey(key2, namespace1);
		results[3] = AlignmentIndex.getFeatureTypeKey(key2, namespace2);
		
		for (String result : results) {
			assertTrue(result.equals(key1));
		}
	}

}
