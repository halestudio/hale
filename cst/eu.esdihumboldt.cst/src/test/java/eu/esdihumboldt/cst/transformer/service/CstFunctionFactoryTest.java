package eu.esdihumboldt.cst.transformer.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test for dynamic loading of CST core function.
 * @author jezekjan
 *
 */
public class CstFunctionFactoryTest extends TestCase{
	 private static final Class[] parameters = new Class[]{URL.class};

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();		
		
		/**
		 * We should add corefunctions jar to classpath before trying to register it.
		 */
		URL functions = getClass().getResource("corefunctions-SNAPSHOT.jar");
		addURL(functions);
				
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
	@Test
	public void testPackageLoading() {
	
		CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions.inspire");
		assertNotNull(CstFunctionFactory.getInstance().getRegisteredFunctions());
		
	}
}
