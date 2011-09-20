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

package eu.esdihumboldt.hale.io.csv;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;

/**
 * TODO Type description
 * @author sitemple
 */
public class CSVSchemaReaderTest {
	
	/**
	 * TODO
	 */
	@Test
	public void testRead() throws Exception {
		//TODO
		
		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(getClass().getResource("/data/test1.csv").toURI()));
		//TODO configure type name
		schemaReader.setParameter(CSVSchemaReader.PARAM_TYPENAME, "TestTyp");
		
		IOReport report = schemaReader.execute(null);
		assertTrue(report.isSuccess());
		
		Schema schema = schemaReader.getSchema();
		
		//TODO
	}

}
