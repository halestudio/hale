package eu.esdihumboldt.hale.ui.service.project.internal;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

/**
 * Testing class for {@link FilePathUpdate}
 * @author Patrick Lieb
 */
public class FilePathUpdateTest {
	
	/**
	 * Real world example
	 */
	@Test
	public void testSimple(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1t2.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		URI file = URI.create("file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1.xsd");
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Extended real world example - project file in a subfolder and renamed
	 */
	@Test
	public void testProjectSubfolder(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/project/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/project/t1t2_alt.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		URI file = URI.create("file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1.xsd");
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Extended real world example - file in a subfolder
	 */
	@Test
	public void testSubfolder(){
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/C:/Users/sitemple/Entwicklung/humboldt2/_testdata/watercourse%20-%20Kopie/test.hale"), 
				URI.create("file:/C:/Users/sitemple/Entwicklung/humboldt2/_testdata/wva2/test.hale"));
		URI file = URI.create("file:/C:/Users/sitemple/Entwicklung/humboldt2/_testdata/watercourse%20-%20Kopie/inspire3/HydroPhysicalWaters.xsd");
		String correct = "file:/C:/Users/sitemple/Entwicklung/humboldt2/_testdata/wva2/inspire3/HydroPhysicalWaters.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Extended real world example - file in a subfolder, project file renamed
	 */
	@Test
	public void testSubfolder2(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1t2_alt.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		URI file = URI.create("file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/schemas/t1.xsd");
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/schemas/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Extended real world example - file in a subsubfolder
	 */
	@Test
	public void testSubfolder3(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1t2.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		URI file = URI.create("file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/source/schemas/t1.xsd");
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/source/schemas/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Test reusing the FilePathUpdate object
	 */
	@Test
	public void testReuse() {
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1t2.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		URI file = URI.create("file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1.xsd");
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
		
		// reuse FilePathUpdate with the same settings
		file = URI.create("file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/schemas/t1.xsd");
		correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/schemas/t1.xsd";
		newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
}
