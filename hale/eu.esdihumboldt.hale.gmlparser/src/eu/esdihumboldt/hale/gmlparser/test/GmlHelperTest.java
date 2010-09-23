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

package eu.esdihumboldt.hale.gmlparser.test;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GmlHelperTest {
	
	@Test
	public void testDetermineVersionGML2() {
		Assert.assertEquals(ConfigurationType.GML2, determineGMLVersion("d_ogr.gml"));
	}
	
	@Test
	public void testDetermineVersionGML3() {
		Assert.assertEquals(ConfigurationType.GML3, determineGMLVersion("Italy_SIC_data.xml"));
	}
	
	@Test
	public void testDetermineVersionGML3_2() {
		Assert.assertEquals(ConfigurationType.GML3_2, determineGMLVersion("Bestandsdatenauszug_FC.xml"));
	}
	
	private Object determineGMLVersion(String filename) {
		InputStream in = GmlHelperTest.class.getResourceAsStream(filename);
		return GmlHelper.determineVersion(in, null);
	}

}
