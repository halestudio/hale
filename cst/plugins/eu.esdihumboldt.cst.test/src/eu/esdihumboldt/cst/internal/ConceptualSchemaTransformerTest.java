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

package eu.esdihumboldt.cst.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.cst.ConceptualSchemaTransformer;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.NullProgressIndicator;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceUtil;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * Tests for the CST's alignment processor implementation
 * @author Simon Templer
 */
public class ConceptualSchemaTransformerTest {

	/**
	 * Wait for needed services.
	 */
	@BeforeClass
	public static void waitForService() {
		TestUtil.startConversionService();
		TestUtil.startInstanceFactory();
	}
	
	/**
	 * Test based on a very simple mapping with a retype and renames. 
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleRename() throws Exception {
		test("/testdata/simplerename/t1.xsd",
				"/testdata/simplerename/t2.xsd",
				"/testdata/simplerename/t1t2.halex.alignment.xml",
				"/testdata/simplerename/instance1.xml",
				"/testdata/simplerename/instance2.xml");
	}

	

	/**
	 * Test based on a simple mapping with a retype and renames, where high
	 * cardinalities are allowed. 
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityRename() throws Exception {
		test("/testdata/cardrename/t1.xsd",
				"/testdata/cardrename/t2.xsd",
				"/testdata/cardrename/t1t2.halex.alignment.xml",
				"/testdata/cardrename/instance1.xml",
				"/testdata/cardrename/instance2.xml");
	}

	/**
	 * Test based on a simple mapping with a retype, rename and assign, duplicated
	 * targets should also get the assigned values.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testDupeAssign() throws Exception {
		test("/testdata/dupeassign/t1.xsd",
				"/testdata/dupeassign/t2.xsd",
				"/testdata/dupeassign/t1t2.halex.alignment.xml",
				"/testdata/dupeassign/instance1.xml",
				"/testdata/dupeassign/instance2.xml");
	}

	/**
	 * Test based on a join and some renames.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyJoin() throws Exception {
		test("/testdata/propjoin/t1.xsd",
				"/testdata/propjoin/t2.xsd",
				"/testdata/propjoin/t1t2.halex.alignment.xml",
				"/testdata/propjoin/instance1.xml",
				"/testdata/propjoin/instance2.xml");
	}
	
	/**
	 * Test where multiple properties from the source type are mapped to
	 * a sub property of a single property of the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertyMerge() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmerge/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmerge/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmerge/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(5, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure including a choice in the target type.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testChoice() throws Exception {
		fail("What should happen?");
		// TODO what is expected here? That any of the two choice properties is used?
		// Currently expected result contains both choice properties, which is the result of the transform, too.
		test("/testdata/choice/t1.xsd",
				"/testdata/choice/t2.xsd",
				"/testdata/choice/t1t2.halex.alignment.xml",
				"/testdata/choice/instance1.xml",
				"/testdata/choice/instance2.xml");
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure in the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertiesMix() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where a complex property structure from a source type is mapped to
	 * multiple properties in a simple target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertiesUnMix() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t2t1.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure in the target type. In this case there
	 * are no facts that the decision which way to group the source properties
	 * to an address can be based on.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertiesMix2() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix2/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix2/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix2/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(9, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure including a repeatable group in the target 
	 * type. In this case there are no facts that the decision which way to 
	 * group the source properties to an address can be based on.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testGroup() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/group/t1.xsd").toURI(), 
				getClass().getResource("/testdata/group/t2.xsd").toURI(), 
				getClass().getResource("/testdata/group/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(9, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where properties from a simple source type are mapped to
	 * to complex properties in that target type, with some of the needed 
	 * information being given only implicit through the corresponding
	 * source property.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testImplicitAssign() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/impassign/t1.xsd").toURI(), 
				getClass().getResource("/testdata/impassign/t2.xsd").toURI(), 
				getClass().getResource("/testdata/impassign/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		/*
		 * FIXME the alignment is still missing the assignments for the language 
		 * values, which must be in correspondence with the related source property
		 */
		assertEquals(5, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where properties from a simple source type are mapped to
	 * to a complex property with simple content and attributes.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testSimpleContentAttribute() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simpleatt/t1.xsd").toURI(), 
				getClass().getResource("/testdata/simpleatt/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simpleatt/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where a type with complex properties is mapped to itself, switching
	 * certain attributes.
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testSimpleComplex() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simplecomplex/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simplecomplex/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simplecomplex/t2t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	private Alignment loadAlignment(URI sourceSchemaLocation, 
			URI targetSchemaLocation, final URI alignmentLocation) throws IOProviderConfigurationException, IOException, MarshalException, ValidationException, MappingException {
		// load source schema
		Schema source = TestUtil.loadSchema(sourceSchemaLocation);
		
		// load target schema
		Schema target = TestUtil.loadSchema(targetSchemaLocation);

		// load alignment
		Alignment result = TestUtil.loadAlignment(alignmentLocation, source, target);
		
		return result;
	}

	/**
	 * Tests that the ConceptualSchemaTransformer transforms the source instances
	 * to the given target instances.
	 * 
	 * @param sourceSchemaLocation location of the source schema
	 * @param targetSchemaLocation location of the target schema
	 * @param alignmentLocation location of the alignment
	 * @param sourceDataLocation location of the source data
	 * @param targetDataLocation location of the target data
	 * @throws Exception if any exception (mostly IO) occurs
	 */
	private void test(String sourceSchemaLocation, String targetSchemaLocation,
			String alignmentLocation, String sourceDataLocation,
			String targetDataLocation) throws Exception {
		Schema sourceSchema = TestUtil.loadSchema(toLocalURI(sourceSchemaLocation));
		Schema targetSchema = TestUtil.loadSchema(toLocalURI(targetSchemaLocation));
		Alignment alignment = TestUtil.loadAlignment(toLocalURI(alignmentLocation), sourceSchema, targetSchema);
		InstanceCollection sourceData = TestUtil.loadInstances(toLocalURI(sourceDataLocation), sourceSchema);
		InstanceCollection targetData = TestUtil.loadInstances(toLocalURI(targetDataLocation), targetSchema);

		List<Instance> transformedData = transformData(alignment, sourceData);

		test(targetData, transformedData);
	}

	/**
	 * Compares the two given collections for equality.
	 * 
	 * @param targetData the expected data
	 * @param transformedData the transformed data to test
	 */
	private void test(InstanceCollection targetData, List<Instance> transformedData) {
		ResourceIterator<Instance> targetIter = targetData.iterator();
		// make sure we can remove instances from the list...
		transformedData = new LinkedList<Instance>(transformedData);
		Iterator<Instance> transformedIter2 = transformedData.iterator();
		while (transformedIter2.hasNext()) {
			System.err.println(InstanceUtil.instanceToString(transformedIter2.next()));
		}

		int targetInstanceCount = 0;
		int transformedInstanceCount = transformedData.size();
		try {
			while (targetIter.hasNext()) {
				Instance targetInstance = targetIter.next();
				targetInstanceCount++;

				// if transformed data is empty simply continue
				// will fail equals at the end
				if (transformedData.isEmpty())
					continue;

				Iterator<Instance> transformedIter = transformedData.iterator();
				boolean found = false;
				while (!found && transformedIter.hasNext()) {
					if (InstanceUtil.instanceEqual(targetInstance, transformedIter.next(), false)) {
						transformedIter.remove();
						found = true;
					}
				}
				assertTrue("Could not find matching instance for: \n" +
						InstanceUtil.instanceToString(targetInstance), found);
			}
		} finally {
			targetIter.close();
		}
		assertEquals("Instance count does not match", targetInstanceCount, transformedInstanceCount);
	}

	/**
	 * Returns an URI for the given location: <br>
	 * <code>getClass().getResource(location).toURI()</code>
	 *
	 * @param location the location
	 * @return an uri for the location
	 * @throws URISyntaxException if toURI throws an exception
	 */
	private URI toLocalURI(String location) throws URISyntaxException {
		return getClass().getResource(location).toURI();
	}

	/**
	 * Transforms the given source data on the given alignment.
	 * 
	 * @param alignment the alignment
	 * @param sourceData the source data
	 * @return the transformed data
	 */
	private List<Instance> transformData(Alignment alignment, InstanceCollection sourceData) {
		ConceptualSchemaTransformer transformer = new ConceptualSchemaTransformer();
		DefaultInstanceSink sink = new DefaultInstanceSink();
		transformer.transform(alignment, sourceData, sink, new NullProgressIndicator());

		return sink.getInstances();
	}
}
