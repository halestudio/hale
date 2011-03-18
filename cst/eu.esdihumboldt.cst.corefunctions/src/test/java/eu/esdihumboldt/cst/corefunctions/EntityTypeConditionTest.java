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

import static org.junit.Assert.*;

import org.junit.Test;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.goml.omwg.Property;

public class EntityTypeConditionTest {

	@Test
	public void testTypeConditionForProperty() {

		Property entity1 = null;
		Property entity2 = null;
		String typeCondition = null;

		for (CstFunction f : FunctionsCellTest.getTestFunctions()) {

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
							Class.forName(typeCondition);
						}
					}
					catch (ClassNotFoundException e) {
						fail("typeCondition class can't be created for " + //$NON-NLS-1$
								"entity1 in " + f.getClass().toString()); //$NON-NLS-1$
					}
					catch (NullPointerException e) {
						fail("typeCondition doesn't exist for entity1 for" + //$NON-NLS-1$
								"function " + f.getClass().toString()); //$NON-NLS-1$
					}

				}
				// Testing of typeCondition for entity1
				if (entity2 != null) {
					try {
						for (Iterator<String> it = entity2.getTypeCondition()
								.iterator(); it.hasNext();) {
							String tc = it.next();
							Class.forName(tc);
						}
					}
					catch (ClassNotFoundException e) {
						fail("typeCondition class can't be created for " + //$NON-NLS-1$
								"entity2 in " + f.getClass().toString()); //$NON-NLS-1$
					}
					catch (NullPointerException e) {
						fail("typeCondition doesn't exist for entity2 for" + //$NON-NLS-1$
								"function " + f.getClass().toString()); //$NON-NLS-1$
					}

				}
			}
		}
	}
}
