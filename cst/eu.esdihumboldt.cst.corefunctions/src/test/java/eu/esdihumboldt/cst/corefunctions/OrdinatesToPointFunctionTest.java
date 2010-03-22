/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.cst.corefunctions;

import org.opengis.feature.Feature;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

import junit.framework.TestCase;

/**
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OrdinatesToPointFunctionTest extends TestCase {
	
	private final String sourceLocalname = "waterVA/Watercourses_VA_Type";
	private final String sourceLocalnamePropertyDouble = "LAENGE_ARC";
	private final String sourceNamespace = "http://esdi-humboldt.org";
	
	private final String source2Localname = "waterVA/Watercourses_VA_Type";
	private final String source2LocalnamePropertyDouble = "LAENGE_ROU";
	private final String source2Namespace = "http://esdi-humboldt.org";
	
	private final String targetLocalname = "FT3";
	private final String targetLocalnamePropertyPoint = "PropertyPoint";
	private final String targetNamespace = "http://esdi-humboldt.eu";
	
	
	@Test
	public void testOrdinatesToPointFunction() {
		
		// set up cell to use for testing
		Cell cell = new Cell();

		Transformation t = new Transformation();
		t.setService(new Resource(OrdinatesToPointFunction.class.getName()));
		t.getParameterMap().put("xExpression", new Parameter("xExpression", "23.0d"));
		t.getParameterMap().put("yExpression", new Parameter("yExpression", "14.0d"));
		
		ComposedProperty composedProperty = new ComposedProperty(new About(""));
		composedProperty.setTransformation(t);
		
		Property entity1 = new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyDouble));
		Property entity2 = new Property(new About(this.source2Namespace, this.source2Localname, this.source2LocalnamePropertyDouble));
		Property entity3 = new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyPoint));

		composedProperty.getCollection().add(entity1);
		composedProperty.getCollection().add(entity2);
		
		cell.setEntity1(composedProperty);
		cell.setEntity2(entity3);
		

		// build source Features
		SimpleFeatureType sourcetype = this.getFeatureType(this.sourceNamespace, this.sourceLocalname, new String[]{this.sourceLocalnamePropertyDouble, this.source2LocalnamePropertyDouble});
		SimpleFeatureType targettype = this.getFeatureType(this.targetNamespace, this.targetLocalname, new String[]{this.targetLocalnamePropertyPoint});			
		
		Feature source = (Feature) SimpleFeatureBuilder.build(sourcetype, new Object[] {new Double(23.0d),new Double(14.0d)}, "1");
		Feature target = (Feature) SimpleFeatureBuilder.build(targettype, new Object[] {}, "2");
		
		// perform actual test
		
		OrdinatesToPointFunction test = new OrdinatesToPointFunction();
		test.configure(cell);
		test.transform(source, target);
	
		//so???
		Point point = (Point) target.getDefaultGeometryProperty().getValue();
		assertTrue(point.getX()==23.0d);
		assertTrue(point.getY()==14.0d);

		//oder so???
		String targetPropertyName = ((Property)cell.getEntity2()).getLocalname();
		Point point2 = (Point)((SimpleFeatureImpl)target).getAttribute(targetPropertyName);
		assertTrue(point2.getX()==23);
		assertTrue(point2.getY()==14);

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


}
