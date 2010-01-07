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

package eu.esdihumboldt.cst.corefunctions;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

public class GenericMathFunctionTest {

	public static String sourceLocalname = "FT1";
	public static String sourceLocalnamePropertyA = "PropertyA";
	public static String sourceLocalnamePropertyB = "PropertyB";
	public static String sourceLocalnamePropertyC = "PropertyC";
	public static String sourceNamespace = "http://esdi-humboldt.eu";
	
	public static String targetLocalname = "FT2";
	public static String targetLocalnamePropertyD = "PropertyD";
	public static String targetNamespace = "http://xsdi.org";

	@Test
	public void testTransformFeatureFeature() {
		
		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				GenericMathFunctionTest.sourceNamespace, 
				GenericMathFunctionTest.sourceLocalname, 
				new String[]{GenericMathFunctionTest.sourceLocalnamePropertyA, 
						GenericMathFunctionTest.sourceLocalnamePropertyB, 
						GenericMathFunctionTest.sourceLocalnamePropertyC});
		SimpleFeatureType targettype = this.getFeatureType(
				GenericMathFunctionTest.targetNamespace, 
				GenericMathFunctionTest.targetLocalname, 
				new String[]{GenericMathFunctionTest.targetLocalnamePropertyD});
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{"4.5", "2", "1"}, "1");
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{}, "2");
		
		// perform actual test
		GenericMathFunction gmf = new GenericMathFunction();
		gmf.configure(GenericMathFunctionTest.getTestCell());
		gmf.transform(source, target);
		
		assertTrue(target.getProperty(
				GenericMathFunctionTest.targetLocalnamePropertyD).getValue().toString().equals("5.0"));
		
	}
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName, String[] propertyNames) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			for (String s : propertyNames) {
				ftbuilder.add(s, String.class);
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
	public static IAlignment getTestAlignment() {
		Alignment a = new Alignment();
		a.setAbout(new About("lala"));
		try {
			a.setSchema1(new Schema(
					sourceNamespace, new Formalism(
							"GML", new URI("http://schemas.opengis.org/gml"))));
			a.setSchema2(new Schema(
					targetNamespace, new Formalism(
							"GML", new URI("http://schemas.opengis.org/gml"))));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		a.getMap().add(getTestCell());
		return a;
	}
	
	private static ICell getTestCell() {
		// set up cell to use for testing
		Cell cell = new Cell();
		ComposedProperty cp = new ComposedProperty(
				new About(sourceNamespace, sourceLocalname));
		cp.getCollection().add(new Property(
				new About(sourceNamespace, sourceLocalname, 
						sourceLocalnamePropertyA)));
		cp.getCollection().add(new Property(
				new About(sourceNamespace, sourceLocalname, 
						sourceLocalnamePropertyB)));
		cp.getCollection().add(new Property(
				new About(sourceNamespace, sourceLocalname, 
						sourceLocalnamePropertyC)));
		Transformation t = new Transformation();
		t.setService(new Resource(GenericMathFunction.class.getName()));
		t.getParameters().add(
				new Parameter(
						"math_expression", 
						"0.5 * (PropertyA * PropertyB + PropertyC)"));
		cp.setTransformation(t);
		cell.setEntity1(cp);
		cell.setEntity2(new Property(
				new About(targetNamespace, targetLocalname, 
						targetLocalnamePropertyD)));
		
		return cell;
	}

}
