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

package eu.esdihumboldt.cst.transformer;

import java.util.Map;

import junit.framework.TestCase;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.impl.FilterTransformer;
import eu.esdihumboldt.cst.transformer.impl.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

public class TransformerFactoryTest extends TestCase {
	
	private String localname1 = "LocalName";
	private String namespace1 = "http://somenamespace.org/path";
	
	public void testFeatureRenameTransfomer() throws Exception {
		CstFunctionFactory tf = CstFunctionFactory.getInstance();
		tf.initConfiguration();

		Cell c = new Cell();
		Entity entity1 = new FeatureClass(
				new About(this.namespace1, this.localname1));

		Transformation t = new Transformation();
		t.setService(new Resource(RenameFeatureFunction.class.getName()));

		entity1.setTransformation(t);
		c.setEntity1(entity1);
		c.setEntity2(entity1);

		CstFunction tr = tf.getCstFunction(c);		
		assertTrue(tr instanceof RenameFeatureFunction);

	}
	public void testCstGetRegisteredTransfomers(){
		CstFunctionFactory tf = CstFunctionFactory.getInstance();
		tf.initConfiguration();
		Map<String, Class<? extends CstFunction>> functions = tf.getRegisteredFunctions();
		functions.clear();
	    functions = tf.getRegisteredFunctions();
		assertTrue(functions.size()>0);
	}
}
