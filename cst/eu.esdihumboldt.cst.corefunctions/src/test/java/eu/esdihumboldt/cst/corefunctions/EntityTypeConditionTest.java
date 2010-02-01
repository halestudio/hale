/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : cst
 * 	 
 * Classname    : eu.esdihumboldt.cst.corefunctions. EntityTypeConditionTest.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.cst.corefunctions;

import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Test;

import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.goml.omwg.Property;

public class EntityTypeConditionTest extends TestCase {

	@Test
	public void testTypeConditionForProperty() {

		CstFunctionFactory.getInstance().registerCstPackage(
				"eu.esdihumboldt.cst.corefunctions");
		// System.out.println("CST contains " +
		// CstFunctionFactory.getInstance().getRegisteredFunctions().size() +
		// "functions");
		CstFunction f = null;
		Property entity1 = null;
		Property entity2 = null;
		String typeCondition = null;
		Boolean bug = false;

		for (Iterator<String> i = CstFunctionFactory.getInstance()
				.getRegisteredFunctions().keySet().iterator(); i.hasNext();) {

			try {
				f = CstFunctionFactory.getInstance().getRegisteredFunctions()
						.get(i.next()).newInstance();
				// System.out.println("********"+f.toString());
			} catch (Exception e) {
				f = null;
			}

			if (f != null) {
				// Setting of entity1
				try {
					entity1 = (Property) f.getParameters().getEntity1();
				} catch (Exception e) { // only for Property type and
										// ComposedProperty
					entity1 = null;
				}
				// Setting of entity2
				try {
					entity2 = (Property) f.getParameters().getEntity2();
				} catch (Exception e) { // only for Property type and
										// ComposedProperty
					entity2 = null;
				}
				// Testing of typeCondition for entity1
				if (entity1 != null) {
					try {
						for (Iterator<String> it = entity1.getTypeCondition()
								.iterator(); it.hasNext();) {
							typeCondition = it.next();
							Class c = Class.forName(typeCondition);
						}
					}
					// typeCondition class can't be created
					catch (ClassNotFoundException e) {
						System.out.println("---------------");
						System.out.println(f.getClass().toString());
						System.out.println(e + " --typeCondition for entity1 ");
						bug = true;
					}
					// typeCondition doesn't exist for existing entity
					catch (NullPointerException e) {
						System.out.println("---------------");
						System.out.println(f.getClass().toString()
								+ "-- missing typeCondition for entity1");
						bug = true;
					}

				}
				// Testing of typeCondition for entity1
				if (entity2 != null) {
					try {
						for (Iterator<String> it = entity2.getTypeCondition()
								.iterator(); it.hasNext();) {
							String tc = it.next();
							Class c = Class.forName(tc);
						}
					}
					// typeCondition class can't be created
					catch (ClassNotFoundException e) {
						System.out.println("---------------");
						System.out.println(f.getClass().toString());
						System.out
								.println(e + " --typeCondition for entity2  ");
						bug = true;
					}
					// typeCondition doesn't exist for existing entity
					catch (NullPointerException e) {
						System.out.println("---------------");
						System.out.println(f.getClass().toString()
								+ "-- missing typeCondition for entity2");
						bug = true;
					}

				}
			}
			if (bug) {
				assertTrue(false);
			}
		}
	}
}
