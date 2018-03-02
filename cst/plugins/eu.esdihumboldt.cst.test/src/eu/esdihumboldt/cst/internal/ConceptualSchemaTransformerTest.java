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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.cst.ConceptualSchemaTransformer;
import eu.esdihumboldt.cst.test.DefaultTransformationTest;
import eu.esdihumboldt.cst.test.TransformationExample;
import eu.esdihumboldt.cst.test.TransformationExamples;
import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentFunctionService;
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentTransformationFunctionService;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.ThreadSafeInstanceSink;
import eu.esdihumboldt.hale.common.core.io.impl.NullProgressIndicator;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexServiceImpl;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

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

	/**
	 * Test for using the Groovy transformation function to return multiple
	 * results that should result in multiple properties.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMultiResult1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MULTI_RESULT_1));
	}

	/**
	 * Test for using the Groovy transformation function to return multiple
	 * results that should result in multiple complex properties.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMultiResult2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MULTI_RESULT_2));
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

	/**
	 * Test based on a merge.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMerge() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MERGE));
	}

	/**
	 * Test based on a merge w/ a multi-property key
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMergeMultiPropKey() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MERGE2));
	}

	/**
	 * Test based on a merge w/ a multi-property key and a complex attribute
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMergeMultiPropKeyWithComplexAtt() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MERGE3));
	}

	/**
	 * Test based on a merge w/ a complex key
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMergeComplexPropKey() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MERGE4));
	}

	/**
	 * Test based on a merge with an additional merge property
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMergeAdditionalProp() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MERGE5));
	}

	/**
	 * Test based on {@link #testMergeAdditionalProp()} but without the
	 * additional merge property
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMergeWithoutAdditionalProp() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MERGE6));
	}

	/**
	 * Test based on a join and some renames with join on properties of
	 * non-matching types
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyJoinNonMatchingTypes() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.PROPERTY_JOIN_2));
	}

	@Override
	protected List<Instance> transformData(TransformationExample example) throws Exception {
		ConceptualSchemaTransformer transformer = new ConceptualSchemaTransformer();
		ThreadSafeInstanceSink<DefaultInstanceSink> sink = new ThreadSafeInstanceSink<>(
				new DefaultInstanceSink());

		final Map<Class<?>, Object> customServices = new HashMap<>();
		customServices.put(FunctionService.class,
				new AlignmentFunctionService(example.getAlignment()));
		customServices.put(TransformationFunctionService.class,
				new AlignmentTransformationFunctionService(example.getAlignment()));

		InstanceIndexServiceImpl indexService = new InstanceIndexServiceImpl();
		customServices.put(InstanceIndexService.class, indexService);

		final ServiceProvider serviceProvider = new ServiceProvider() {

			private final ServiceProvider projectScope = new ServiceManager(
					ServiceManager.SCOPE_PROJECT);

			@SuppressWarnings("unchecked")
			@Override
			public <T> T getService(Class<T> serviceInterface) {
				if (customServices.containsKey(serviceInterface)) {
					return (T) customServices.get(serviceInterface);
				}

				// FIXME global scope not supported yet
				return projectScope.getService(serviceInterface);
			}
		};

		indexService.addPropertyMappings(example.getAlignment().getActiveTypeCells(),
				serviceProvider);

		InstanceCollection source = example.getSourceInstances();

		try (ResourceIterator<Instance> it = source.iterator()) {
			while (it.hasNext()) {
				indexService.add(it.next(), source);
			}
		}

		transformer.transform(example.getAlignment(), source, sink, serviceProvider,
				new NullProgressIndicator());

		return sink.getDecoratee().getInstances();
	}

}
