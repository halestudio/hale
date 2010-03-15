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

package eu.esdihumboldt.goml.oml.io;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;


import eu.esdihumboldt.goml.align.Alignment;

/**
 * <p>
 * 
 * This is a simple test for the serialization of the OML PropertyComposition instances. 
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class PropertyCompositionSerializationTest {
	
	/**
	 * an Alignment Instance containing the Property Composistion to be serialized
	 */
	Alignment alignment;
	
	/**
	 * OmlRdfGenerator to serialize PropertyComposition 
	 * 
	 */
	OmlRdfGenerator omlGenerator;
	
	@Before
	public void setUp() throws Exception{
		//creates an Alignment Instance containing the Property Composistion to be serialized
		URI uri = null;
		try {
			uri = new URI(PropertyCompositionSerializationTest.class.getResource("PropertyCompositionTest.xml").getFile());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.alignment = new OmlRdfReader().read(new URL("file", null, uri.getPath()));
		this.omlGenerator = new OmlRdfGenerator();
		
	}
	
	@Test
	public final void testWrite(){
		try {
			omlGenerator.write(alignment, "PropertyCompositionTest_generated.xml");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
