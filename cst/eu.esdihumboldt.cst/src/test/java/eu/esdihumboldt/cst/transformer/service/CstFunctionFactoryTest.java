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

import java.net.URL;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * 
 * Basic tests of {@link CstFunctionFactory}.
 * 
 * @author jezekjan
 * 
 */
public class CstFunctionFactoryTest {

	private String localname1 = "LocalName";
	private String namespace1 = "http://somenamespace.org/path";

	

	@Before
	public void addFunctions() {
		/**
		 * We should add corefunctions jar to classpath before trying to
		 * register it.
		 */
	 	AddFunctionsToPathUtility.getInstance().add();
	}

	/**
	 * Test if {@link CstFunctionFactory} we get correct transformer when
	 * requesting it by its name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFeatureRenameTransfomer() throws Exception {
		CstFunctionFactory tf = CstFunctionFactory.getInstance();

		Cell c = new Cell();
		Entity entity1 = new FeatureClass(new About(this.namespace1,
				this.localname1));

		Transformation t = new Transformation();
		t.setService(new Resource(RenameFeatureFunction.class.getName()));

		entity1.setTransformation(t);
		c.setEntity1(entity1);
		c.setEntity2(entity1);

		CstFunction tr = tf.getCstFunction(c);
		Assert.assertTrue(tr instanceof RenameFeatureFunction);

	}

	/**
	 * Test if registration of packege works
	 */
	@Test
	public void testPackageLoading() {

		CstFunctionFactory.getInstance().registerCstPackage(
				"eu.esdihumboldt.cst.corefunctions");
		Assert.assertNotNull(CstFunctionFactory.getInstance()
				.getRegisteredFunctions().size());

	}

	/**
	 * Test CstServiceFactory
	 */
	@Test
	public void testCstServiceFactory() {

		CstService factory = CstServiceFactory.getInstance();		
		for (Iterator<FunctionDescription> it = factory.getCapabilities().getFunctionDescriptions().iterator();it.hasNext();){
			System.out.println(it.next().getFunctionId());
		}
		Assert.assertTrue(factory.getCapabilities().getFunctionDescriptions().size() > 0);

	}
}
