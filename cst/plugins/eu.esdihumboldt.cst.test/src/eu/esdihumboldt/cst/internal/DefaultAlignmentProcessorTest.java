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

import java.io.IOException;
import java.net.URI;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.transformation.Transformation;
import eu.esdihumboldt.hale.common.align.transformation.service.AlignmentProcessor;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * Tests for the CST's alignment processor implementation
 * @author Simon Templer
 */
public class DefaultAlignmentProcessorTest {

	@SuppressWarnings("restriction")
	private AlignmentProcessor processor = new DefaultAlignmentProcessor();
	
	/**
	 * Test based on a very simple mapping with a retype and renames. 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleRename() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simplerename/t1.xsd").toURI(), 
				getClass().getResource("/testdata/simplerename/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simplerename/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions (mainly for completeness in this case)
	}
	
	/**
	 * Test based on a simple mapping with a retype and renames, where high
	 * cardinalities are allowed. 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityRename() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/cardrename/t1.xsd").toURI(), 
				getClass().getResource("/testdata/cardrename/t2.xsd").toURI(), 
				getClass().getResource("/testdata/cardrename/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from the source type are mapped to
	 * a sub property of a single property of the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyMerge() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmerge/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmerge/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmerge/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure including a choice in the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testChoice() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/choice/t1.xsd").toURI(), 
				getClass().getResource("/testdata/choice/t2.xsd").toURI(), 
				getClass().getResource("/testdata/choice/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(8, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure in the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertiesMix() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where a complex property structure from a source type is mapped to
	 * multiple properties in a simple target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertiesUnMix() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t2t1.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure in the target type. In this case there
	 * are no facts that the decision which way to group the source properties
	 * to an address can be based on.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertiesMix2() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix2/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix2/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix2/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(9, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure including a repeatable group in the target 
	 * type. In this case there are no facts that the decision which way to 
	 * group the source properties to an address can be based on.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testGroup() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/group/t1.xsd").toURI(), 
				getClass().getResource("/testdata/group/t2.xsd").toURI(), 
				getClass().getResource("/testdata/group/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(9, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where properties from a simple source type are mapped to
	 * to complex properties in that target type, with some of the needed 
	 * information being given only implicit through the corresponding
	 * source property.
	 * @throws Exception if an error occurs executing the test
	 */
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
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where properties from a simple source type are mapped to
	 * to a complex property with simple content and attributes.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleContentAttribute() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simpleatt/t1.xsd").toURI(), 
				getClass().getResource("/testdata/simpleatt/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simpleatt/t1t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where a type with complex properties is mapped to itself, switching
	 * certain attributes.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleComplex() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simplecomplex/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simplecomplex/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simplecomplex/t2t2.halex.alignment.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
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

}
