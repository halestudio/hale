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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
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

import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ModifiableCell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;

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
			parameters2.put("comment", new ParameterValue(Value.complex(commentParam)));
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
	 * Tests id generation, save and load.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testIDSaveLoad() throws Exception {
		DefaultAlignment alignment = new DefaultAlignment();

		Schema schema = TestUtil.loadSchema(getClass().getResource("/testdata/simple/t1.xsd")
				.toURI());

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier("trans1");

		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(null, new DefaultType(new TypeEntityDefinition(schema.getMappingRelevantTypes()
				.iterator().next(), SchemaSpaceID.SOURCE, null)));
		cell.setSource(source);

		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(schema.getMappingRelevantTypes()
				.iterator().next(), SchemaSpaceID.TARGET, null)));
		cell.setTarget(target);

		// add cell and check id generation
		assertNull(cell.getId());
		alignment.addCell(cell);
		assertNotNull(cell.getId());
		assertNotNull(alignment.getCell(cell.getId()));

		// save / load
		File alignmentFile = tmp.newFile("alignment.xml");
		System.out.println(alignmentFile.getAbsolutePath());
		saveAlignment(alignment, new BufferedOutputStream(new FileOutputStream(alignmentFile)));

		MutableAlignment alignment2 = loadAlignment(new FileInputStream(alignmentFile), schema,
				schema);

		// check cell id
		assertEquals(cell.getId(), alignment2.getCells().iterator().next().getId());
	}

	/**
	 * Tests base alignment add, save and load.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testBaseAlignmentSaveLoad() throws Exception {
		DefaultAlignment baseAlignment = new DefaultAlignment();
		MutableAlignment alignment = new DefaultAlignment();

		Schema schema = TestUtil.loadSchema(getClass().getResource("/testdata/simple/t1.xsd")
				.toURI());
		TypeDefinition t = schema.getMappingRelevantTypes().iterator().next();

		DefaultCell cell1 = new DefaultCell();
		cell1.setTransformationIdentifier("trans1");

		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(null, new DefaultType(new TypeEntityDefinition(t, SchemaSpaceID.SOURCE, null)));
		cell1.setSource(source);

		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(t, SchemaSpaceID.TARGET, null)));
		cell1.setTarget(target);

		DefaultCell cell2 = new DefaultCell();
		cell2.setTransformationIdentifier("trans2");

		List<ChildContext> childContext = new ArrayList<ChildContext>();
		PropertyDefinition child = DefinitionUtil.getChild(t, new QName("a1")).asProperty();
		childContext.add(new ChildContext(child));
		ListMultimap<String, Property> source2 = ArrayListMultimap.create();
		source2.put(null, new DefaultProperty(new PropertyEntityDefinition(t, childContext,
				SchemaSpaceID.SOURCE, null)));
		cell2.setSource(source2);

		ListMultimap<String, Property> target2 = ArrayListMultimap.create();
		target2.put(null, new DefaultProperty(new PropertyEntityDefinition(t, childContext,
				SchemaSpaceID.TARGET, null)));
		cell2.setTarget(target2);

		// add cell1 to base alignment
		baseAlignment.addCell(cell1);

		// save base alignment
		File baseAlignmentFile = tmp.newFile("alignment_base.xml");
		System.out.println(baseAlignmentFile.getAbsolutePath());
		saveAlignment(baseAlignment, new BufferedOutputStream(new FileOutputStream(
				baseAlignmentFile)));

		// add as base alignment to extended alignment
		addBaseAlignment(alignment, baseAlignmentFile.toURI(), schema, schema);
		assertEquals(1, alignment.getBaseAlignments().size());
		String usedPrefix = alignment.getBaseAlignments().keySet().iterator().next();

		assertEquals(1, alignment.getCells().size());
		assertEquals(usedPrefix + ":" + cell1.getId(), alignment.getCells().iterator().next()
				.getId());

		// add cell2 to extended alignment
		alignment.addCell(cell2);
		assertEquals(2, alignment.getCells().size());
		assertEquals(1, alignment.getPropertyCells(cell1).size());

		// save extended alignment
		File alignmentFile = tmp.newFile("alignment_extended.xml");
		System.out.println(alignmentFile.getAbsolutePath());
		saveAlignment(alignment, new BufferedOutputStream(new FileOutputStream(alignmentFile)));

		// load extended
		MutableAlignment alignment2 = loadAlignment(new FileInputStream(alignmentFile), schema,
				schema);

		assertEquals(2, alignment2.getCells().size());
		assertEquals(1, alignment2.getTypeCells().size());
		Cell typeCell = alignment2.getTypeCells().iterator().next();
		assertTrue(typeCell instanceof BaseAlignmentCell);
		assertEquals(usedPrefix + ":" + cell1.getId(), typeCell.getId());
		assertEquals(1, alignment2.getPropertyCells(typeCell).size());
		assertFalse(alignment2.getPropertyCells(typeCell).iterator().next() instanceof BaseAlignmentCell);
	}

	/**
	 * Tests cell disable save and load.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testCellDisableSaveLoad() throws Exception {
		DefaultAlignment baseAlignment = new DefaultAlignment();
		MutableAlignment alignment = new DefaultAlignment();

		Schema schema = TestUtil.loadSchema(getClass().getResource("/testdata/simple/t1.xsd")
				.toURI());

		Iterator<? extends TypeDefinition> iter = schema.getMappingRelevantTypes().iterator();
		TypeDefinition t = iter.next();

		// generate base alignment
		DefaultCell cell1 = new DefaultCell();
		cell1.setTransformationIdentifier("trans1");

		ListMultimap<String, Type> source = ArrayListMultimap.create();
		source.put(null, new DefaultType(new TypeEntityDefinition(t, SchemaSpaceID.SOURCE, null)));
		cell1.setSource(source);

		ListMultimap<String, Type> target = ArrayListMultimap.create();
		target.put(null, new DefaultType(new TypeEntityDefinition(t, SchemaSpaceID.TARGET, null)));
		cell1.setTarget(target);

		DefaultCell cell2 = new DefaultCell();
		cell2.setTransformationIdentifier("trans2");

		List<ChildContext> childContext2 = new ArrayList<ChildContext>();
		PropertyDefinition child2 = DefinitionUtil.getChild(t, new QName("a1")).asProperty();
		childContext2.add(new ChildContext(child2));
		ListMultimap<String, Property> source2 = ArrayListMultimap.create();
		source2.put(null, new DefaultProperty(new PropertyEntityDefinition(t, childContext2,
				SchemaSpaceID.SOURCE, null)));
		cell2.setSource(source2);

		ListMultimap<String, Property> target2 = ArrayListMultimap.create();
		target2.put(null, new DefaultProperty(new PropertyEntityDefinition(t, childContext2,
				SchemaSpaceID.TARGET, null)));
		cell2.setTarget(target2);

		DefaultCell cell3 = new DefaultCell();
		cell3.setTransformationIdentifier("trans3");

		List<ChildContext> childContext3 = new ArrayList<ChildContext>();
		PropertyDefinition child3 = DefinitionUtil.getChild(t, new QName("b1")).asProperty();
		childContext3.add(new ChildContext(child3));
		ListMultimap<String, Property> source3 = ArrayListMultimap.create();
		source3.put(null, new DefaultProperty(new PropertyEntityDefinition(t, childContext3,
				SchemaSpaceID.SOURCE, null)));
		cell3.setSource(source3);

		ListMultimap<String, Property> target3 = ArrayListMultimap.create();
		target3.put(null, new DefaultProperty(new PropertyEntityDefinition(t, childContext3,
				SchemaSpaceID.TARGET, null)));
		cell3.setTarget(target3);

		baseAlignment.addCell(cell1);
		baseAlignment.addCell(cell2);
		String baseDisableCellId = cell2.getId();
		baseAlignment.addCell(cell3);
		String extendedDisableCellId = cell3.getId();

		assertEquals(3, baseAlignment.getCells().size());
		Cell typeCell = baseAlignment.getTypeCells().iterator().next();
		assertEquals(2, baseAlignment.getPropertyCells(typeCell).size());
		// test disable, it should not be with the related property cells
		cell2.setDisabledFor(cell1, true);
		assertEquals(1, baseAlignment.getPropertyCells(typeCell).size());
		assertTrue(cell2.getDisabledFor().contains(cell1));
		cell2.setDisabledFor(cell1, false);
		assertFalse(cell2.getDisabledFor().contains(cell1));
		cell2.setDisabledFor(cell1, true);
		assertEquals(1, baseAlignment.getPropertyCells(typeCell).size());

		// save base alignment
		File baseAlignmentFile = tmp.newFile("alignment_base.xml");
		System.out.println(baseAlignmentFile.getAbsolutePath());
		saveAlignment(baseAlignment, new BufferedOutputStream(new FileOutputStream(
				baseAlignmentFile)));

		// load base alignment
		MutableAlignment baseAlignment2 = loadAlignment(new FileInputStream(baseAlignmentFile),
				schema, schema);
		typeCell = baseAlignment2.getTypeCells().iterator().next();
		assertEquals(3, baseAlignment2.getCells().size());
		// test again that it is still disabled
		assertEquals(1, baseAlignment2.getPropertyCells(typeCell).size());

		// disable the remaining enabled cell in extended alignment
		addBaseAlignment(alignment, baseAlignmentFile.toURI(), schema, schema);
		assertEquals(1, alignment.getBaseAlignments().size());
		String usedPrefix = alignment.getBaseAlignments().keySet().iterator().next();

		File alignmentFile = tmp.newFile("alignment_extended.xml");

		// check cells
		typeCell = alignment.getTypeCells().iterator().next();
		assertEquals(3, alignment.getCells().size());
		assertEquals(1, alignment.getPropertyCells(typeCell).size());
		// disable remaining cell
		((ModifiableCell) alignment.getPropertyCells(typeCell, false).iterator().next())
				.setDisabledFor(typeCell, true);
		assertEquals(0, alignment.getPropertyCells(typeCell).size());

		// save / load extended alignment
		System.out.println(alignmentFile.getAbsolutePath());
		saveAlignment(alignment, new BufferedOutputStream(new FileOutputStream(alignmentFile)));

		// load extended
		MutableAlignment alignment2 = loadAlignment(new FileInputStream(alignmentFile), schema,
				schema);
		typeCell = alignment2.getTypeCells().iterator().next();
		// test disabled again
		assertEquals(3, alignment2.getCells().size());
		// test again that it is still disabled
		assertEquals(0, alignment2.getPropertyCells(typeCell).size());

		// more specifically test whether the disables come from base alignment
		// or extended alignment
		Cell baseDisableCell = alignment2.getCell(usedPrefix + ":" + baseDisableCellId);
		Cell extendedDisableCell = alignment2.getCell(usedPrefix + ":" + extendedDisableCellId);

		assertTrue(baseDisableCell instanceof BaseAlignmentCell);
		assertEquals(1, baseDisableCell.getDisabledFor().size());
		assertEquals(1, ((BaseAlignmentCell) baseDisableCell).getBaseDisabledFor().size());
		assertEquals(0, ((BaseAlignmentCell) baseDisableCell).getAdditionalDisabledFor().size());

		assertTrue(extendedDisableCell instanceof BaseAlignmentCell);
		assertEquals(1, extendedDisableCell.getDisabledFor().size());
		assertEquals(0, ((BaseAlignmentCell) extendedDisableCell).getBaseDisabledFor().size());
		assertEquals(1, ((BaseAlignmentCell) extendedDisableCell).getAdditionalDisabledFor().size());

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
	 * Add the given base alignment to the given alignment.
	 * 
	 * @param align the alignment
	 * @param newBase the base alignment to add
	 * @param source the source types for resolving source entities
	 * @param target the target types for resolving target entities
	 * @throws Exception if an error occurs adding the alignment
	 */
	protected abstract void addBaseAlignment(MutableAlignment align, URI newBase, TypeIndex source,
			TypeIndex target) throws Exception;

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
