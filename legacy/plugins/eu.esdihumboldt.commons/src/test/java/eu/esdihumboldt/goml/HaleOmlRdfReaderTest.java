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

package eu.esdihumboldt.goml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * @author Anna Pitaev, Logica
 * 
 */
public class HaleOmlRdfReaderTest {

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader#read(java.lang.String)}
	 * .
	 * 
	 * @throws MalformedURLException
	 * @throws MalformedURLException
	 */
	@Test
	public final void testRead() throws MalformedURLException {
		URI uri = null;
		try {
			uri = new URI(HaleOmlRdfReaderTest.class.getResource(
					"testproject.xml").getFile());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alignment alignment = new OmlRdfReader().read(new URL("file", null, uri
				.getPath()));
		// test alignment basic elements
		assertEquals("http://www.opengis.net/gml", alignment.getSchema1()
				.getLocation());
		assertEquals("urn:x-inspire:specification:gmlas-v31:Network:3.1",
				alignment.getSchema2().getLocation());
		assertEquals("", alignment.getLevel());
		assertEquals(2, alignment.getMap().size());

		// test the cell defining renaming
		// 1.test the mapping for the attribute renaming mapping3
		ICell renaming = alignment.getMap().get(0);
		// test entity1 contexts
		// check that entity1 is not empty
		assertNotNull(renaming.getEntity1());
		FeatureClass fc1 = (FeatureClass) renaming.getEntity1();
		assertNotNull(fc1);
		// test fc1 labels
		List<String> labels = fc1.getLabel();
		assertEquals(2, labels.size());
		assertEquals("http://www.esdi-humboldt.org/waterVA", labels.get(0));
		assertEquals("Watercourses_VA", labels.get(1));
		// test fc1 transformer
		assertNotNull(fc1.getTransformation());
		assertEquals(
				"eu.esdihumboldt.cst.transformer.impl.RenameFeatureTransformer",
				fc1.getTransformation().getService().getLocation());
		// test entity2 contexts
		assertNotNull(renaming.getEntity2());
		FeatureClass fc2 = (FeatureClass) renaming.getEntity2();
		assertNotNull(fc2);
		// test fc1 labels
		labels = fc2.getLabel();
		assertEquals(2, labels.size());
		assertEquals("urn:x-inspire:specification:gmlas-v31:Hydrography:2.0",
				labels.get(0));
		assertEquals("Watercourse", labels.get(1));
		// test measure
		assertEquals(0.0, renaming.getMeasure(), 0);
		// test relation is null
		assertNull(renaming.getRelation());

		// test the cell defining networkexpansion
		ICell netExpansion = alignment.getMap().get(1);
		// test entity1 contexts
		// check that entity1 is not empty
		assertNotNull(netExpansion.getEntity1());
		Property pr1 = (Property) netExpansion.getEntity1();
		assertNotNull(pr1);
		// test pr1 labels
		labels = pr1.getLabel();
		assertEquals(3, labels.size());
		assertEquals("http://www.esdi-humboldt.org/waterVA", labels.get(0));
		assertEquals("Watercourses_VA", labels.get(1));
		assertEquals("the_geom", labels.get(2));
		// test pr1 transformer
		assertNotNull(pr1.getTransformation());
		assertEquals(
				"eu.esdihumboldt.cst.transformer.impl.NetworkExpansionTransformer",
				pr1.getTransformation().getService().getLocation());
		// transformer parameter list should contain 1 parameter
		assertEquals(1, pr1.getTransformation().getParameters().size());
		IParameter parameter = pr1.getTransformation().getParameters().get(0);
		assertEquals("Expansion", parameter.getName());
		assertEquals("50", parameter.getValue());
		// test entity2 contexts
		assertNotNull(renaming.getEntity2());
		Property pr2 = (Property) netExpansion.getEntity2();
		assertNotNull(pr2);
		// test fc1 labels
		labels = pr2.getLabel();
		assertEquals(3, labels.size());
		assertEquals("urn:x-inspire:specification:gmlas-v31:Hydrography:2.0",
				labels.get(0));
		assertEquals("SurfaceWater", labels.get(1));
		assertEquals("geometry", labels.get(2));
		// test measure
		assertEquals(0.0, netExpansion.getMeasure(), 0);
		// test relation is null
		assertNull(netExpansion.getRelation());

	}

}
