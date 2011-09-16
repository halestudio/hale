/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.Resources;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.service.ContentTypeService;

/**
 * TODO Type description
 * 
 * @author Patrick Lieb
 */
public class ContentTypeTrackerTest {

	/**
	 * Test loading a gml file
	 */
	@Test
	public void testloadGml() {
		List<ContentType> result = new ArrayList<ContentType>();
		result.add(ContentType.getContentType("GML"));
		
		ContentTypeService tracker = OsgiUtils.getService(ContentTypeService.class);
		assertNotNull(tracker);
		
		assertEquals(result ,tracker.findContentTypesFor(tracker.getContentTypes(),
				Resources.newInputStreamSupplier(getClass()
						.getResource("/data/wfs_va_sample.gml")), null));
	}
	
	/**
	 * Test loading a shape file
	 */
	@Test
	@Ignore  //no tester for shape files implemented yet
	public void testloadShape() {
		List<ContentType> result = new ArrayList<ContentType>();
		result.add(ContentType.getContentType("Shapefile"));
		
		ContentTypeService tracker = OsgiUtils.getService(ContentTypeService.class);
		assertNotNull(tracker);
		
		assertEquals(result ,tracker.findContentTypesFor(tracker.getContentTypes(),
				Resources.newInputStreamSupplier(getClass()
						.getResource("/data/cc7910_8710_mergedJ.shp")), null));
	}
	
	/**
	 * Test loading a xml file
	 */
	@Test
	public void testloadXml() {
		List<ContentType> result = new ArrayList<ContentType>();
		result.add(ContentType.getContentType("XML"));
		
		ContentTypeService tracker = OsgiUtils.getService(ContentTypeService.class);
		assertNotNull(tracker);
		
		assertEquals(result ,tracker.findContentTypesFor(tracker.getContentTypes(),
				Resources.newInputStreamSupplier(getClass()
						.getResource("/data/testxml.xml")), null));
	}
	
	/**
	 * Test loading a xsd file
	 */
	@Test
	public void testloadXsd() {
		List<ContentType> result = new ArrayList<ContentType>();
		result.add(ContentType.getContentType("XSD"));
		
		ContentTypeService tracker = OsgiUtils.getService(ContentTypeService.class);
		assertNotNull(tracker);
		
		assertEquals(result ,tracker.findContentTypesFor(tracker.getContentTypes(),
				Resources.newInputStreamSupplier(getClass()
						.getResource("/data/attributegroup.xsd")), null));
	}
	
	/**
	 * Test loading a gml file, but only with XML-ContentTester
	 */
	@Test
	public void testloadGmlwithXmltype(){
		List<ContentType> result = new ArrayList<ContentType>();
		result.add(ContentType.getContentType("XML"));
		
		ContentTypeService tracker = OsgiUtils.getService(ContentTypeService.class);
		List<ContentType> list = new ArrayList<ContentType>();
		list.add(ContentType.getContentType("XML"));

		assertNotNull(tracker);
		
		assertEquals(result ,tracker.findContentTypesFor(list,
				Resources.newInputStreamSupplier(getClass()
						.getResource("/data/wfs_va_sample.gml")), null));
		
		assertEquals(result ,tracker.findContentTypesFor(list,
				Resources.newInputStreamSupplier(getClass()
						.getResource("/data/wfs_va_sample.gml")), "xyz.gml"));
	}
}
