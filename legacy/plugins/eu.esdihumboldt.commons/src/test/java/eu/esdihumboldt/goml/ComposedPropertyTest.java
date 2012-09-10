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

package eu.esdihumboldt.goml;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Formalism;
import eu.esdihumboldt.commons.goml.align.Schema;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;
import eu.esdihumboldt.specification.cst.rdf.IAbout;

public class ComposedPropertyTest {

	private String sourceLocalname = "FT1";
	private String sourceLocalnamePropertyA = "PropertyA";
	private String sourceLocalnamePropertyB = "PropertyB";
	private String sourceLocalnamePropertyC = "PropertyC";
	private String sourceNamespace = "http://esdi-humboldt.eu";

	private String targetLocalname = "FT2";
	private String targetLocalnamePropertyD = "PropertyD";
	private String targetNamespace = "http://xsdi.org";
	private Alignment alignment;

	@Before
	public void setUp() {
		this.alignment = new Alignment();
		alignment.setAbout(new About(UUID.randomUUID()));
		alignment.setLevel("ComposedPropertyTest");
		try {
			alignment.setSchema1(new Schema("schemaLocation1", new Formalism(
					"schema1", new URI("location1"))));
			alignment.setSchema2(new Schema("schemaLocation2", new Formalism(
					"schema2", new URI("location2"))));
			List<ICell> map = new ArrayList<ICell>();

			// set up cell to use for testing
			Cell cell = new Cell();
			ComposedProperty cp = new ComposedProperty(
					eu.esdihumboldt.commons.goml.omwg.ComposedProperty.PropertyOperatorType.FIRST,
					new About(this.sourceNamespace, this.sourceLocalname));
			cp.getCollection().add(
					new Property(
							new About(this.sourceNamespace,
									this.sourceLocalname,
									this.sourceLocalnamePropertyA)));
			cp.getCollection().add(
					new Property(
							new About(this.sourceNamespace,
									this.sourceLocalname,
									this.sourceLocalnamePropertyB)));
			cp.getCollection().add(
					new Property(
							new About(this.sourceNamespace,
									this.sourceLocalname,
									this.sourceLocalnamePropertyC)));
			Transformation t = new Transformation();
			t.setService(new eu.esdihumboldt.commons.goml.rdf.Resource(
					"location"));
			t.getParameters().add(
					new Parameter("math_expression",
							"0.5 * (PropertyA * PropertyB + PropertyC)"));
			cp.setTransformation(t);
			cell.setEntity1(cp);
			cell.setEntity2(new Property(new About(this.targetNamespace,
					this.targetLocalname, this.targetLocalnamePropertyD)));
			map.add(cell);
			alignment.setMap(map);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testOmlRdfRead() throws MalformedURLException {
		URI uri = null;
		try {
			uri = new URI(ComposedPropertyTest.class.getResource(
					"ComposedPropertyTest.xml").getFile());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alignment alignment = new OmlRdfReader().read(uri.getPath());
		// test for ComposedProperty
		ComposedProperty compProperty = (ComposedProperty) alignment.getMap()
				.get(0).getEntity1();
		// test about
		IAbout about = compProperty.getAbout();
		String constructedAbout = this.sourceNamespace + "/"
				+ this.sourceLocalname;
		assertEquals(constructedAbout, about.getAbout());

		// test property collection
		List<Property> properties = compProperty.getCollection();
		assertEquals(3, properties.size());
		// test About for the PropertyC
		Property propC = properties.get(2);
		about = propC.getAbout();
		constructedAbout = constructedAbout + "/"
				+ this.sourceLocalnamePropertyC;
		assertEquals(constructedAbout, about.getAbout());
		// test transformation
		ITransformation transf = compProperty.getTransformation();
		assertEquals("location", transf.getService().getLocation());
		assertEquals(1, transf.getParameters().size());
		IParameter parameter = transf.getParameters().get(0);
		assertEquals("math_expression", parameter.getName());
		assertEquals("0.5 * (PropertyA * PropertyB + PropertyC)",
				parameter.getValue());
		// test operator type
		assertEquals(
				eu.esdihumboldt.commons.goml.omwg.ComposedProperty.PropertyOperatorType.FIRST
						.name(), compProperty.getPropertyOperatorType().name());
	}

}
