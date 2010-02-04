/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;


public class CstFunctionFactoryTest extends TestCase{
	
	private String localname1 = "LocalName";
	private String namespace1 = "http://somenamespace.org/path";

	private static final Class[] parameters = new Class[]{URL.class};
	@Override
	protected void setUp() throws Exception {		
		super.setUp();				
		/**
		 * We should add corefunctions jar to classpath before trying to register it.
		 */
		URL functions = getClass().getResource("corefunctions-SNAPSHOT.jar");		
		addURL(functions);				
	}
	@Test
	public void testFeatureRenameTransfomer() throws Exception {
		CstFunctionFactory tf = CstFunctionFactory.getInstance();

		Cell c = new Cell();
		Entity entity1 = new FeatureClass(
				new About(this.namespace1, this.localname1));

		Transformation t = new Transformation();
		t.setService(new Resource(RenameFeatureFunction.class.getName()));

		entity1.setTransformation(t);
		c.setEntity1(entity1);
		c.setEntity2(entity1);

		CstFunction tr = tf.getCstFunction(c);		
		Assert.assertTrue(tr instanceof RenameFeatureFunction);

	}
	@Test
	public void testPackageLoading() {
			
		CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions");			
		Assert.assertNotNull(CstFunctionFactory.getInstance().getRegisteredFunctions().size());
		
	}
	
	/**
	 * Helper method that add URL to classpath
	 * @param u
	 * @throws IOException
	 */
	public static void addURL(URL u) throws IOException {
	  
	      URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	      Class sysclass = URLClassLoader.class;

	      try {
	         Method method = sysclass.getDeclaredMethod("addURL", parameters);
	         method.setAccessible(true);
	         method.invoke(sysloader, new Object[]{u});
	      } catch (Throwable t) {
	         t.printStackTrace();
	         throw new IOException("Error, could not add URL to system classloader");
	      }

	   }

}
	

