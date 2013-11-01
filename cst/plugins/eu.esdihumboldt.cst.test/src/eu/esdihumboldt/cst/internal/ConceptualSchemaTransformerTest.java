/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.internal;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.cst.ConceptualSchemaTransformer;
import eu.esdihumboldt.cst.test.DefaultTransformationTest;
import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.cst.test.TransformationExamples;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.core.io.impl.NullProgressIndicator;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Tests for the CST's alignment processor implementation
 * 
 * @author Simon Templer
 */
public class ConceptualSchemaTransformerTest extends DefaultTransformationTest {

	/**
	 * Test for the groovy transformation function.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testGroovy1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.GROOVY1));
	}

	/**
	 * Test for the groovy transformation function.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testGroovy2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.GROOVY2));
	}

	/**
	 * Test for the groovy transformation function.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testGroovy3() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.GROOVY3));
	}

	@Ignore
	// XXX not working with the current CST
	@Override
	@Test
	public void testCMUnion1() throws Exception {
		super.testCMUnion1();
	}

	@Ignore
	// XXX not working with the current CST
	@Override
	@Test
	public void testCMUnion2() throws Exception {
		super.testCMUnion2();
	}

	@Ignore
	// XXX not working with the current CST
	@Override
	@Test
	public void testCMNested1() throws Exception {
		super.testCMNested1();
	}

	/**
	 * Test where multiple properties from the source type are mapped to a sub
	 * property of a single property of the target type.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertyMerge() throws Exception {
//		Alignment alignment = loadAlignment(getClass().getResource("/testdata/propmerge/t1.xsd")
//				.toURI(), getClass().getResource("/testdata/propmerge/t2.xsd").toURI(), getClass()
//				.getResource("/testdata/propmerge/t1t2.halex.alignment.xml").toURI());
//
//		assertNotNull(alignment);
//		assertEquals(5, alignment.getCells().size());

//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);

		// TODO check transformation instructions
	}

	/**
	 * Test where multiple properties from a simple source type are mapped to a
	 * complex property structure in the target type.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertiesMix() throws Exception {
//		Alignment alignment = loadAlignment(getClass().getResource("/testdata/propmix/t1.xsd")
//				.toURI(), getClass().getResource("/testdata/propmix/t2.xsd").toURI(), getClass()
//				.getResource("/testdata/propmix/t1t2.halex.alignment.xml").toURI());
//
//		assertNotNull(alignment);
//		assertEquals(7, alignment.getCells().size());

//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);

		// TODO check transformation instructions
	}

	/**
	 * Test where a complex property structure from a source type is mapped to
	 * multiple properties in a simple target type.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertiesUnMix() throws Exception {
//		Alignment alignment = loadAlignment(getClass().getResource("/testdata/propmix/t2.xsd")
//				.toURI(), getClass().getResource("/testdata/propmix/t1.xsd").toURI(), getClass()
//				.getResource("/testdata/propmix/t2t1.halex.alignment.xml").toURI());
//
//		assertNotNull(alignment);
//		assertEquals(7, alignment.getCells().size());

//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);

		// TODO check transformation instructions
	}

	/**
	 * Test where multiple properties from a simple source type are mapped to a
	 * complex property structure in the target type. In this case there are no
	 * facts that the decision which way to group the source properties to an
	 * address can be based on.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testPropertiesMix2() throws Exception {
//		Alignment alignment = loadAlignment(getClass().getResource("/testdata/propmix2/t1.xsd")
//				.toURI(), getClass().getResource("/testdata/propmix2/t2.xsd").toURI(), getClass()
//				.getResource("/testdata/propmix2/t1t2.halex.alignment.xml").toURI());
//
//		assertNotNull(alignment);
//		assertEquals(9, alignment.getCells().size());

//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);

		// TODO check transformation instructions
	}

	/**
	 * Test where multiple properties from a simple source type are mapped to a
	 * complex property structure including a repeatable group in the target
	 * type. In this case there are no facts that the decision which way to
	 * group the source properties to an address can be based on.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testGroup() throws Exception {
//		Alignment alignment = loadAlignment(getClass().getResource("/testdata/group/t1.xsd")
//				.toURI(), getClass().getResource("/testdata/group/t2.xsd").toURI(), getClass()
//				.getResource("/testdata/group/t1t2.halex.alignment.xml").toURI());
//
//		assertNotNull(alignment);
//		assertEquals(9, alignment.getCells().size());

//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);

		// TODO check transformation instructions
	}

	/**
	 * Test where properties from a simple source type are mapped to to a
	 * complex property with simple content and attributes.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testSimpleContentAttribute() throws Exception {
//		Alignment alignment = loadAlignment(getClass().getResource("/testdata/simpleatt/t1.xsd")
//				.toURI(), getClass().getResource("/testdata/simpleatt/t2.xsd").toURI(), getClass()
//				.getResource("/testdata/simpleatt/t1t2.halex.alignment.xml").toURI());
//
//		assertNotNull(alignment);
//		assertEquals(7, alignment.getCells().size());

//		Transformation transformation = processor.process(alignment);
//		assertNotNull(transformation);

		// TODO check transformation instructions
	}

	@Override
	protected List<Instance> transformData(TransformationExample example) throws Exception {
		ConceptualSchemaTransformer transformer = new ConceptualSchemaTransformer();
		DefaultInstanceSink sink = new DefaultInstanceSink();
		// FIXME for now using null service provider
		ServiceProvider serviceProvider = new ServiceProvider() {

			@Override
			public <T> T getService(Class<T> serviceInterface) {
				return null;
			}
		};
		transformer.transform(example.getAlignment(), example.getSourceInstances(), sink,
				serviceProvider, new NullProgressIndicator());

		return sink.getInstances();
	}

}
