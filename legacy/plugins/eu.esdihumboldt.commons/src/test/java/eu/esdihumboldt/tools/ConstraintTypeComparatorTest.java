//package test.eu.esdihumboldt.tools;
//
//import java.util.TreeSet;
//
//import eu.esdihumboldt.mediator.MediatorComplexRequestImpl;
//import eu.esdihumboldt.mediator.TypeKey;
//import eu.esdihumboldt.mediator.constraints.SpatialConstraint;
//import eu.esdihumboldt.mediator.constraints.ThematicConstraint;
//import eu.esdihumboldt.mediator.constraints.impl.SpatialConstraintImpl;
//import eu.esdihumboldt.mediator.constraints.impl.ThematicConstraintImpl;
//import eu.esdihumboldt.tools.ConstraintTypeKey;
//import junit.framework.TestCase;
//
//public class ConstraintTypeComparatorTest extends TestCase {
////
////	protected void setUp() throws Exception {
////		super.setUp();
////	}
////
////	public void testEquals() {
////		SpatialConstraint sc_1 = new SpatialConstraintImpl();
////		SpatialConstraint sc_2 = new SpatialConstraintImpl();
////
////		TypeKey tk1 = new ConstraintTypeKey(sc_1);
////		TypeKey tk2 = new ConstraintTypeKey(sc_2);
////
////		assertEquals(tk1, tk2);
////
////		TreeSet<TypeKey> ts = new TreeSet<TypeKey>();
////		ts.add(tk1);
////		ts.add(tk2);
////		assertTrue(ts.size() == 1);
////	}
////	
////	public void testMCR() {
////		SpatialConstraint sc_1 = new SpatialConstraintImpl();
////		SpatialConstraint sc_2 = new SpatialConstraintImpl();
////
////		TypeKey tk1 = new ConstraintTypeKey(sc_1);
////		TypeKey tk2 = new ConstraintTypeKey(sc_2);
////
////		MediatorComplexRequestImpl mcr = new MediatorComplexRequestImpl();
////
////		mcr.putConstraint(tk1, sc_1);
////		mcr.putConstraint(tk2, sc_2);
////
////		assertTrue(mcr.getConstraints().keySet().size() == 1);
////		assertEquals(sc_2, mcr.getConstraint(tk1));
////
////	}
////
////	public void testHashCode() {
////		SpatialConstraint sc_1 = new SpatialConstraintImpl();
////		SpatialConstraint sc_2 = new SpatialConstraintImpl();
////		ThematicConstraint tc_1 = new ThematicConstraintImpl();
////
////		TypeKey tk1 = new ConstraintTypeKey(sc_1);
////		TypeKey tk2 = new ConstraintTypeKey(sc_2);
////		TypeKey tk3 = new ConstraintTypeKey(tc_1);
////
////		assertEquals(tk1.hashCode(), tk2.hashCode());
////		assertTrue(tk2.hashCode() != tk3.hashCode());
////	}
//
// }
