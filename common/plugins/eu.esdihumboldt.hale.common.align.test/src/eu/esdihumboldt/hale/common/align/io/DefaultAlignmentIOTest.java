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

package eu.esdihumboldt.hale.common.align.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import eu.esdihumboldt.hale.common.align.io.impl.DefaultAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Test saving and loading a default alignment
 * @author Simon Templer
 */
public class DefaultAlignmentIOTest {
	
	/**
	 * Temporary folder for tests
	 */
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	/**
	 * Test saving and loading an example alignment
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
		
		ListMultimap<String, String> parameters1 = LinkedListMultimap.create();
		parameters1.put("test", "1");
		parameters1.put("test", "2");
		parameters1.put("t", "3");
		cell1.setTransformationParameters(parameters1);
		
		ListMultimap<String, Type> source1 = ArrayListMultimap.create();
		QName source1TypeName;
		String source1EntityName;
		TypeDefinition sourceType1 = new DefaultTypeDefinition(
				source1TypeName = new QName("source1Type"));
		source1.put(source1EntityName = null, 
				new DefaultType(new TypeEntityDefinition(sourceType1, SchemaSpaceID.SOURCE, null)));
		cell1.setSource(source1);
		source.addType(sourceType1);
		
		ListMultimap<String, Type> target1 = ArrayListMultimap.create();
		QName target1TypeName;
		String target1EntityName;
		TypeDefinition targetType1 = new DefaultTypeDefinition(
				target1TypeName = new QName("http://some.name.space/t1", "target1Type"));
		target1.put(target1EntityName = "Some name", 
				new DefaultType(new TypeEntityDefinition(targetType1, SchemaSpaceID.TARGET, null)));
		cell1.setTarget(target1);
		target.addType(targetType1);
		
		align.addCell(cell1);
		
		// cell 2
		MutableCell cell2 = new DefaultCell();
		
		String id2;
		cell2.setTransformationIdentifier(id2 = "trans2");
		
		ListMultimap<String, String> parameters2 = LinkedListMultimap.create();
		parameters2.put("test", "4");
		parameters2.put("tx", "5");
		parameters2.put("tx", "6");
		cell2.setTransformationParameters(parameters2);
		
		ListMultimap<String, Type> target2 = ArrayListMultimap.create();
		TypeDefinition targetType2 = new DefaultTypeDefinition(
				new QName("target2Type"));
		target2.put("Some other name", new DefaultType(new TypeEntityDefinition(
				targetType2, SchemaSpaceID.TARGET, null)));
		cell2.setTarget(target2);
		target.addType(targetType2);
		
		align.addCell(cell2);
		
		// write alignment
		File alignmentFile = tmp.newFile("alignment.xml");
		System.out.println(alignmentFile.getAbsolutePath());
		
		DefaultAlignmentIO.save(align, new FileOutputStream(alignmentFile));
		
		// load alignment
		//TODO use and check reporter?
		MutableAlignment align2 = DefaultAlignmentIO.load(new FileInputStream(
				alignmentFile), null, source, target);
		
		// compare loaded alignment
		Collection<? extends Cell> cells = align2.getCells();
		assertFalse(cells.isEmpty());
		
		Iterator<? extends Cell> it = cells.iterator();
		
		// cell 1
		Cell ncell1 = it.next();
		assertNotNull(ncell1);
		assertEquals(id1, ncell1.getTransformationIdentifier());
		
		// source 1
		ListMultimap<String, ? extends Entity> source1Entities = ncell1.getSource();
		assertEquals(1, source1Entities.size());
		List<? extends Entity> s1list = source1Entities.get(source1EntityName);
		assertFalse(s1list.isEmpty());
		assertEquals(source1TypeName, s1list.get(0).getDefinition().getDefinition().getName());
		
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
		ListMultimap<String, String> param2 = ncell2.getTransformationParameters();
		assertEquals(2, param2.keySet().size());
		assertEquals(3, param2.values().size());
	}

}
