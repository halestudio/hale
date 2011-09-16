package eu.esdihumboldt.hale.ui.service.project.internal;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing class for {@link FilePathUpdate}
 * @author Patrick Lieb
 */
public class FilePathUpdateTester {
	
	FilePathUpdate update = new FilePathUpdate();

	/**
	 * Testing case with two normal paths
	 */
	@Test
	public void TestcaseA(){
		String path = "c:/neu/blub/blab/dadada/";
		String file = "file:/d:/old/dududu/blab/dadada/iss/inspire/test.ttt";
		String correct = "file:/c:/neu/blub/blab/dadada/iss/inspire/test.ttt";
		String newpath = update.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Testing case with two normal paths
	 */
	@Test
	public void TestcaseB(){
		String path = "c:/neu/dada/duddu/lalala/dumdidum/blub/blab/dadada/";
		String file = "file:/d:/old/dududu/blab/dadada/dada.kk";
		String correct = "file:/c:/neu/dada/duddu/lalala/dumdidum/blub/blab/dadada/dada.kk";
		String newpath = update.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Testing case with no analogy of the two paths
	 */
	@Test
	public void TestcaseC(){
		String path = "c:/neu/dada/duddu/lalala/dumdidum/blub/blabe/dadadaesa/";
		String file = "file:/d:/old/dududu/blab/dadada/test.txt";
		String correct = "file:/c:/neu/dada/duddu/lalala/dumdidum/blub/blabe/dadadaesa/test.txt";
		String newpath = update.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Testing case with the same device
	 */
	@Test
	public void TestcaseD(){
		String path = "c:/dadada/";
		String file = "file:/c:/old/dududu/blab/dadada/hak.ex";
		String correct = "file:/c:/dadada/hak.ex";
		String newpath = update.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	/**
	 * Testing case with no analogy of the two paths
	 */
	@Test
	public void TestcaseE(){
		String path = "c:/dadada/dududu/blab/";
		String file = "file:/d:/test/test/test/hale.test";
		String correct = "file:/c:/dadada/dududu/blab/hale.test";
		String newpath = update.changePath(file, path);
		assertEquals(correct, newpath);
	}
}
