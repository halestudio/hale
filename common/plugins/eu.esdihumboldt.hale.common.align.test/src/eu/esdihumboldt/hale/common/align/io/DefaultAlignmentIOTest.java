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

package eu.esdihumboldt.hale.common.align.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Test saving and loading an alignment
 * 
 * @author Simon Templer
 */
public abstract class DefaultAlignmentIOTest {

	/**
	 * Temporary folder for tests
	 */
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	/**
	 * Test saving and loading an example alignment
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSaveLoad() throws Exception {
		// populate alignment
		MutableAlignment align = new DefaultAlignment();

		DefaultSchema source = new DefaultSchema("", null);
		DefaultSchema target = new DefaultSchema("", null);

		// cell 1
		MutableCell cell1 = new DefaultCell();
		String id1;
		cell1.setTransformationIdentifier(id1 = "trans1");

		ListMultimap<String, ParameterValue> parameters1 = LinkedListMultimap.create();
		parameters1.put("test", new ParameterValue("1"));
		parameters1.put("test", new ParameterValue("2"));
		parameters1.put("t", new ParameterValue("3"));
		cell1.setTransformationParameters(parameters1);

		ListMultimap<String, Type> source1 = ArrayListMultimap.create();
		QName source1TypeName;
		String source1EntityName;
		TypeDefinition sourceType1 = new DefaultTypeDefinition(source1TypeName = new QName(
				"source1Type"));
		String filterText = "someproperty > 12";
		Filter filter = new FilterGeoCqlImpl(filterText);
		source1.put(source1EntityName = null, new DefaultType(new TypeEntityDefinition(sourceType1,
				SchemaSpaceID.SOURCE, filter)));
		cell1.setSource(source1);
		source.addType(sourceType1);

		ListMultimap<String, Type> target1 = ArrayListMultimap.create();
		QName target1TypeName;
		String target1EntityName;
		TypeDefinition targetType1 = new DefaultTypeDefinition(target1TypeName = new QName(
				"http://some.name.space/t1", "target1Type"));
		target1.put(target1EntityName = "Some name", new DefaultType(new TypeEntityDefinition(
				targetType1, SchemaSpaceID.TARGET, null)));
		cell1.setTarget(target1);
		target.addType(targetType1);

		align.addCell(cell1);

		// cell 2
		MutableCell cell2 = new DefaultCell();

		String id2;
		cell2.setTransformationIdentifier(id2 = "trans2");

		ListMultimap<String, ParameterValue> parameters2 = LinkedListMultimap.create();
		parameters2.put("test", new ParameterValue("4"));
		parameters2.put("tx", new ParameterValue("5"));
		parameters2.put("tx", new ParameterValue("6"));

		// complex parameter value
		if (supportsComplexParameters()) {
			TestAnnotation commentParam = new TestAnnotation();
			commentParam.setAuthor("Gerd");
			commentParam.setComment("Should a comment really be used as parameter?");
			parameters2.put("comment", new ParameterValue(null, commentParam));
		}

		cell2.setTransformationParameters(parameters2);

		ListMultimap<String, Type> target2 = ArrayListMultimap.create();
		TypeDefinition targetType2 = new DefaultTypeDefinition(new QName("target2Type"));
		target2.put("Some other name", new DefaultType(new TypeEntityDefinition(targetType2,
				SchemaSpaceID.TARGET, null)));
		cell2.setTarget(target2);
		target.addType(targetType2);

		align.addCell(cell2);

		TestAnnotation ann1 = null;
		TestAnnotation ann2 = null;
		if (supportsAnnotations()) {
			// add some annotations
			ann1 = (TestAnnotation) cell2.addAnnotation("test");
			ann1.setAuthor("Simon");
			ann1.setComment("I have really no idea what I did here");

			ann2 = (TestAnnotation) cell2.addAnnotation("test");
			ann2.setAuthor("Hans");
			ann2.setComment("Me neither");
		}

		String doc1 = "This cell was created in memory of...\nSorry, forgotten.";
		String tag1 = "This is a tag";
		String tag2 = "awesome";
		if (supportsDocumentation()) {
			cell1.getDocumentation().put(null, doc1);
			cell1.getDocumentation().put("tag", tag1);
			cell1.getDocumentation().put("tag", tag2);
		}

		// write alignment
		File alignmentFile = tmp.newFile("alignment.xml");
		System.out.println(alignmentFile.getAbsolutePath());

		saveAlignment(align, new BufferedOutputStream(new FileOutputStream(alignmentFile)));

		// load alignment
		// TODO use and check reporter?
		MutableAlignment align2 = loadAlignment(new FileInputStream(alignmentFile), source, target);

		// compare loaded alignment
		Collection<? extends Cell> cells = align2.getCells();
		assertFalse(cells.isEmpty());

		Iterator<? extends Cell> it = cells.iterator();

		// cell 1
		Cell ncell1 = it.next();
		assertNotNull(ncell1);
		assertEquals(id1, ncell1.getTransformationIdentifier());

		// documentation
		if (supportsDocumentation()) {
			assertEquals(3, ncell1.getDocumentation().size());
			assertEquals(doc1, ncell1.getDocumentation().get(null).get(0));
			assertEquals(tag1, ncell1.getDocumentation().get("tag").get(0));
			assertEquals(tag2, ncell1.getDocumentation().get("tag").get(1));
		}

		// source 1
		ListMultimap<String, ? extends Entity> source1Entities = ncell1.getSource();
		assertEquals(1, source1Entities.size());
		List<? extends Entity> s1list = source1Entities.get(source1EntityName);
		assertFalse(s1list.isEmpty());
		assertEquals(source1TypeName, s1list.get(0).getDefinition().getDefinition().getName());
		// filter
		assertEquals(filter, s1list.get(0).getDefinition().getFilter());

		// target 1
		ListMultimap<String, ? extends Entity> target1Entities = ncell1.getTarget();
		assertEquals(1, target1Entities.size());
		List<? extends Entity> t1list = target1Entities.get(target1EntityName);
		assertFalse(t1list.isEmpty());
		assertEquals(target1TypeName, t1list.get(0).getDefinition().getDefinition().getName());

		// cell 2
		Cell ncell2 = it.next();
		assertNotNull(ncell2);
		assertEquals(id2, ncell2.getTransformationIdentifier());

		// parameters
		ListMultimap<String, ParameterValue> param2 = ncell2.getTransformationParameters();
		if (!supportsComplexParameters()) {
			assertEquals(2, param2.keySet().size());
			assertEquals(3, param2.values().size());
		}
		else {
			assertEquals(3, param2.keySet().size());
			assertEquals(4, param2.values().size());
			ParameterValue complexParam = param2.get("comment").get(0);
			assertTrue(complexParam.getValue() instanceof TestAnnotation);
		}

		// annotations
		if (supportsAnnotations()) {
			List<?> annotations = ncell2.getAnnotations("test");
			assertEquals(2, annotations.size());

			TestAnnotation nann1 = (TestAnnotation) annotations.get(0);
			assertEquals(ann1, nann1);

			TestAnnotation nann2 = (TestAnnotation) annotations.get(1);
			assertEquals(ann2, nann2);
		}
	}

	/**
	 * Load an alignment.
	 * 
	 * @param input the input stream to read from
	 * @param source the source types for resolving source entities
	 * @param target the target types for resolving target entities
	 * @return the loaded alignment
	 * @throws Exception if an error occurs loading the alignment
	 */
	protected abstract MutableAlignment loadAlignment(InputStream input, TypeIndex source,
			TypeIndex target) throws Exception;

	/**
	 * Save an alignment.
	 * 
	 * @param align the alignment to save
	 * @param output the output stream to write to
	 * @throws Exception if an error occurs loading the alignment
	 */
	protected abstract void saveAlignment(MutableAlignment align, OutputStream output)
			throws Exception;

	/**
	 * Determine if the alignment I/O supports annotations.
	 * 
	 * @return if annotations are supported
	 */
	protected abstract boolean supportsAnnotations();

	/**
	 * Determine if the alignment I/O supports documentation.
	 * 
	 * @return if documentations are supported
	 */
	protected abstract boolean supportsDocumentation();

	/**
	 * Determine if the alignment I/O supports complex parameter values.
	 * 
	 * @return if complex parameter values are supported
	 */
	protected abstract boolean supportsComplexParameters();

}
