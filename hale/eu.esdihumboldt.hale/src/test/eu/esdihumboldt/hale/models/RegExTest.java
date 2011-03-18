package test.eu.esdihumboldt.hale.models;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

public class RegExTest {

	@Test
	public void testNestedComposedProperty() {
		Cell cell = new Cell();
		ComposedProperty cp1 = new ComposedProperty(new About("OuterComposedProperty")); //$NON-NLS-1$
		ComposedProperty cp2 = new ComposedProperty(new About("NestedComposedProperty")); //$NON-NLS-1$
		Property p1 = new Property(new About("InnerProperty1")); //$NON-NLS-1$
		Property p2 = new Property(new About("InnerProperty2")); //$NON-NLS-1$
		
		cp2.getCollection().add(p1);
		cp2.getCollection().add(p2);
		cp1.getCollection().add(cp2);
		cell.setEntity1(cp1);
		cell.setEntity2(p2);
		
		Alignment al = new Alignment();
		al.getMap().add(cell);
		
		OmlRdfGenerator org = new OmlRdfGenerator();
		
		try {
			org.write(al, "d://test-nested-composedproperty.oml"); //$NON-NLS-1$
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
