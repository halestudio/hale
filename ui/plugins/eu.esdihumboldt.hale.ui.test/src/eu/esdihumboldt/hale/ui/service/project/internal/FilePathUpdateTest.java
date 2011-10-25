package eu.esdihumboldt.hale.ui.service.project.internal;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

/**
 * Testing class for {@link FilePathUpdate}
 * @author Patrick Lieb
 */
public class FilePathUpdateTest {
	
//	/**
//	 * Testing case with two normal paths
//	 */
//	@Test
//	public void TestcaseA(){
//		String orgPath = ...;
//		String path = "c:/neu/blub/blab/dadada/";
//		FilePathUpdate update = new FilePathUpdate(
//				URI.create("file:/" + orgPath), 
//				URI.create("file:/" + path));
//		String file = "file:/d:/old/dududu/blab/dadada/iss/inspire/test.ttt";
//		String correct = "file:/c:/neu/blub/blab/dadada/iss/inspire/test.ttt";
//		String newpath = update.changePath(file);
//		assertEquals(correct, newpath);
//	}
//	
//	/**
//	 * Testing case with two normal paths
//	 */
//	@Test
//	public void TestcaseB(){
//		String orgPath = ...;
//		String path = "c:/neu/dada/duddu/lalala/dumdidum/blub/blab/dadada/";
//		FilePathUpdate update = new FilePathUpdate(
//				URI.create("file:/" + orgPath), 
//				URI.create("file:/" + path));
//		String file = "file:/d:/old/dududu/blab/dadada/dada.kk";
//		String correct = "file:/c:/neu/dada/duddu/lalala/dumdidum/blub/blab/dadada/dada.kk";
//		String newpath = update.changePath(file);
//		assertEquals(correct, newpath);
//	}
//	
//	/**
//	 * Testing case with no analogy of the two paths
//	 */
//	@Test
//	public void TestcaseC(){
//		String orgPath = ...;
//		String path = "c:/neu/dada/duddu/lalala/dumdidum/blub/blabe/dadadaesa/";
//		FilePathUpdate update = new FilePathUpdate(
//				URI.create("file:/" + orgPath), 
//				URI.create("file:/" + path));
//		String file = "file:/d:/old/dududu/blab/dadada/test.txt";
//		String correct = "file:/c:/neu/dada/duddu/lalala/dumdidum/blub/blabe/dadadaesa/test.txt";
//		String newpath = update.changePath(file);
//		assertEquals(correct, newpath);
//	}
//	
//	/**
//	 * Testing case with the same device
//	 */
//	@Test
//	public void TestcaseD(){
//		String orgPath = ...;
//		String path = "c:/dadada/";
//		FilePathUpdate update = new FilePathUpdate(
//				URI.create("file:/" + orgPath), 
//				URI.create("file:/" + path));
//		String file = "file:/c:/old/dududu/blab/dadada/hak.ex";
//		String correct = "file:/c:/dadada/hak.ex";
//		String newpath = update.changePath(file);
//		assertEquals(correct, newpath);
//	}
//	
//	/**
//	 * Testing case with no analogy of the two paths
//	 */
//	@Test
//	public void TestcaseE(){
//		String orgPath = ...;
//		String path = "c:/dadada/dududu/blab/";
//		FilePathUpdate update = new FilePathUpdate(
//				URI.create("file:/" + orgPath), 
//				URI.create("file:/" + path));
//		String file = "file:/d:/test/test/test/hale.test";
//		String correct = "file:/c:/dadada/dududu/blab/hale.test";
//		String newpath = update.changePath(file);
//		assertEquals(correct, newpath);
//	}
	
	/**
	 * Real world example
	 */
	@Test
	public void TestcaseF(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1t2.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		String file = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1.xsd";
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Extended real world example - project file in a subfolder and renamed
	 */
	@Test
	public void TestcaseG(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/project/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/project/t1t2_alt.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		String file = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1.xsd";
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Extended real world example - file in a subfolder, project file renamed
	 */
	@Test
	public void TestcaseH(){
		String orgPath = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/t1t2.hale";
		String path = "C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/t1t2_alt.hale";
		FilePathUpdate update = new FilePathUpdate(
				URI.create("file:/" + orgPath), 
				URI.create("file:/" + path));
		String file = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/unification/schemas/t1.xsd";
		String correct = "file:/C:/Users/sitemple/Entwicklung/hale/cst/plugins/eu.esdihumboldt.cst.test/src/testdata/propmerge/schemas/t1.xsd";
		String newpath = update.changePath(file);
		assertEquals(correct, newpath);
	}
	
}
