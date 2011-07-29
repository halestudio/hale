package eu.esdihumboldt.hale.ui.service.project.internal;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilePathUpdateTester {

	@Test
	public void TestcaseA(){
		String path = "c:/neu/blub/blab/dadada/";
		String file = "file:/d:/old/dududu/blab/dadada/iss/inspire/test.ttt";
		String correct = "file:/c:/neu/blub/blab/dadada/iss/inspire/test.ttt";
		String newpath = FilePathUpdate.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	@Test
	public void TestcaseB(){
		String path = "c:/neu/dada/duddu/lalala/dumdidum/blub/blab/dadada/";
		String file = "file:/d:/old/dududu/blab/dadada/dada.kk";
		String correct = "file:/c:/neu/dada/duddu/lalala/dumdidum/blub/blab/dadada/dada.kk";
		String newpath = FilePathUpdate.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	@Test
	public void TestcaseC(){
		String path = "c:/neu/dada/duddu/lalala/dumdidum/blub/blabe/dadadaesa/";
		String file = "file:/d:/old/dududu/blab/dadada/test.txt";
		String correct = "file:/c:/neu/dada/duddu/lalala/dumdidum/blub/blabe/dadadaesa/test.txt";
		String newpath = FilePathUpdate.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	@Test
	public void TestcaseD(){
		String path = "c:/dadada/";
		String file = "file:/c:/old/dududu/blab/dadada/hak.ex";
		String correct = "file:/c:/dadada/hak.ex";
		String newpath = FilePathUpdate.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	@Test
	public void TestcaseE(){
		String path = "c:/dadada/dududu/blab/";
		String file = "file:/d:/test/test/test/hale.test";
		String correct = "file:/c:/dadada/dududu/blab/hale.test";
		String newpath = FilePathUpdate.changePath(file, path);
		assertEquals(correct, newpath);
	}
	
	@Test
	public void TestcaseF(){
		String path = "C:/watercourse";
		String file = "file:/C:/humboldt2/_testdata/watercourse/hale.test";
		String correct = "file:/C:/watercourse/hale.test";
		String newpath = FilePathUpdate.changePath(file, path);
		assertEquals(correct, newpath);
	}
}
