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

import static org.junit.Assert.assertFalse;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
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
		
		// cell 1
		MutableCell cell1 = new DefaultCell();
		String id1;
		cell1.setTransformationIdentifier(id1 = "trans1");
		
		ListMultimap<String, String> parameters1 = LinkedListMultimap.create();
		parameters1.put("test", "1");
		parameters1.put("test", "2");
		parameters1.put("t", "3");
		cell1.setTransformationParameters(parameters1);
		
		ListMultimap<String, Type> target1 = ArrayListMultimap.create();
		TypeDefinition targetType1 = new DefaultTypeDefinition(
				new QName("target1Type"));
		target1.put("Some name", new DefaultType(new TypeEntityDefinition(
				targetType1)));
		cell1.setTarget(target1);
		
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
				targetType2)));
		cell2.setTarget(target2);
		
		align.addCell(cell2);
		
		// write alignment
		File alignmentFile = tmp.newFile("alignment.xml");
		System.out.println(alignmentFile.getAbsolutePath());
		
		AlignmentIO.save(align, new FileOutputStream(alignmentFile));
		
		//FIXME remove
//		Desktop.getDesktop().open(alignmentFile);
//		Thread.sleep(1000);
		
		// load alignment
		MutableAlignment align2 = AlignmentIO.load(new FileInputStream(
				alignmentFile), null);
		
		//TODO test alignment
//		Collection<? extends MutableCell> cells = align2.getCells();
//		assertFalse(cells.isEmpty());
	}

}
