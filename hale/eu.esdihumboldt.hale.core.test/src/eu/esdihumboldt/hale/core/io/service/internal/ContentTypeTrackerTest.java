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

package eu.esdihumboldt.hale.core.io.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import com.google.common.io.Resources;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;

/**
 * TODO Type description
 * 
 * @author Patrick Lieb
 */
public class ContentTypeTrackerTest {

	/**
	 * 
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
}
