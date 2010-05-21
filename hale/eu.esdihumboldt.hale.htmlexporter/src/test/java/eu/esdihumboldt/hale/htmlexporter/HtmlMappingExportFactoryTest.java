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
package eu.esdihumboldt.hale.htmlexporter;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportException;

/**
 * Test class for HtmlMappingExportFactory
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HtmlMappingExportFactoryTest {

	/**
	 * 
	 */
	@Test
	public void test() {
		
		HtmlMappingExportFactory htmlMappingExportFactory = new HtmlMappingExportFactory();
		String testFile = this.getClass().getResource("TEST.goml").toString();
		OmlRdfReader reader = new OmlRdfReader();
		
		if(testFile!=null){
			Alignment alignment = reader.read(testFile);
			try {
				htmlMappingExportFactory.export(alignment, "bla.html");
			} catch (MappingExportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	
}
