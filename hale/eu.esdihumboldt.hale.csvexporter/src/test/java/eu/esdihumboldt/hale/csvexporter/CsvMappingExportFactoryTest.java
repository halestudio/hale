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
package eu.esdihumboldt.hale.csvexporter;

import java.net.URI;

import org.junit.Test;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;
import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportException;

/**
 * Test class for CSVMappingExportFactory
 * @author Stefan Gessner & Jose Gisbert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 02 / ETRA I+D
 */
public class CsvMappingExportFactoryTest {

	/**
	 *  For testing the export function
	 */
	@Test
	public void test() {
		
		CsvMappingExportFactory csvMappingExportFactory = new CsvMappingExportFactory();
		String testFile = this.getClass().getResource("withm4_xp.goml").toString(); //$NON-NLS-1$
		OmlRdfReader reader = new OmlRdfReader();
		
		SchemaProvider sp = new ApacheSchemaProvider();
		Schema source = null;
		Schema target = null;
		try {
			source = sp.loadSchema(new URI(
					this.getClass().getResource("source_geofla_wfs.xsd").toString()), null); //$NON-NLS-1$
			target = sp.loadSchema(new URI(
					this.getClass().getResource("XSD/AdministrativeUnits.xsd").toString()), null); //$NON-NLS-1$
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		} 
			
		if(testFile!=null){
			Alignment alignment = reader.read(testFile);
			try {
				csvMappingExportFactory.export(alignment, 
						"C:\\test_marian_conventionxp.csv",  //$NON-NLS-1$
						source.getElements().values(), 
						target.getElements().values());
			} catch (MappingExportException e) {
				e.printStackTrace();
			}
		}
		
	}

	
}
