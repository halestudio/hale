/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2008 to 2010.
 */
package eu.esdihumboldt.goml.oml.io;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;

/**
 * @author Anna Pitaev, Logica
 *
 */
public class OmlRdfGeneratorTest {

	@Test
	public final void testWrite() {
		URI uri = null;
		try {
			uri = new URI(OmlRdfReaderTest.class.getResource("test_newFilterOML_TR.xml").getFile());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alignment alignment = new OmlRdfReader().read(uri.getPath());
		OmlRdfGenerator omlGenerator = new OmlRdfGenerator();
		try {
			omlGenerator.write(alignment, "test_newFilterOML_TR_generated.xml");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
