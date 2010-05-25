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

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.Feature;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureBuilder;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

import static org.junit.Assert.*;

/**
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OrdinatesToPointFunctionTest {
	
	private final String sourceLocalname = "waterVA/Watercourses_VA_Type";
	private final String sourceLocalnamePropertyDouble = "LAENGE_ARC";
	private final String sourceNamespace = "http://esdi-humboldt.org";
	
	private final String source2Localname = "waterVA/Watercourses_VA_Type";
	private final String source2LocalnamePropertyDouble = "LAENGE_ROU";
	private final String source2Namespace = "http://esdi-humboldt.org";
	
	private final String targetLocalname = "FT3";
	private final String targetLocalnamePropertyPoint = "PropertyPoint";
	private final String targetNamespace = "http://esdi-humboldt.eu";
	
	private static double x = 23.0;
	private static double y = 14.0;
	
	private static String x2 ="23.0";
	private static String y2 ="14.0";
	private int testInt=1;
	
	
	@Test
	public void testConfigure() {
		OrdinatesToPointFunction  otpf = new OrdinatesToPointFunction();
		otpf.configure(otpf.getParameters());
	}
	
	@Test
	public void testOrdinatesToPointFunction() {
		for(this.testInt=1; this.testInt<5; this.testInt++){
			// set up cell to use for testing
			Cell cell = new Cell();
	
			Transformation t = new Transformation();
			t.setService(new Resource(OrdinatesToPointFunction.class.getName()));
			List<IParameter> parameters = new ArrayList<IParameter>();
			
			switch(this.testInt){
			case 1:
				parameters.add(new Parameter("xExpression", "LAENGE_ARC * " + x));
				parameters.add(new Parameter("yExpression", "LAENGE_ROU * " + y));
				break;
			case 2:
				parameters.add(new Parameter("xExpression", "LAENGE_ARC * " + x2));
				parameters.add(new Parameter("yExpression", "LAENGE_ROU * " + y2));
				break;
			case 3:
				parameters.add(new Parameter("xExpression", "LAENGE_ARC * " + x));
				parameters.add(new Parameter("yExpression", "LAENGE_ROU * " + y2));
				break;
			case 4:
				parameters.add(new Parameter("xExpression", "LAENGE_ARC * " + x2));
				parameters.add(new Parameter("yExpression", "LAENGE_ROU * " + y));
			}
			
			t.setParameters(parameters);
			
			
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
			
			
			Feature source;
			Feature target;
	
			switch(this.testInt){
			case 1:
				source = (Feature) SimpleFeatureBuilder.build(sourcetype, new Object[] {new Double(x),new Double(y)}, "1");
				break;
			case 2:
				source = (Feature) SimpleFeatureBuilder.build(sourcetype, new Object[] {new String(x2),new String(y2)}, "1");
				break;
			case 3:
				source = (Feature) SimpleFeatureBuilder.build(sourcetype, new Object[] {new Double(x),new String(y2)}, "1");
				break;
			case 4:
				source = (Feature) SimpleFeatureBuilder.build(sourcetype, new Object[] {new String(x2),new Double(y)}, "1");
				break;
			default:
				source = (Feature) SimpleFeatureBuilder.build(sourcetype, new Object[] {new Double(x),new Double(y)}, "1");
			}
			
			target = FeatureBuilder.buildFeature(targettype, source, true);
			
			// perform actual test
			
			OrdinatesToPointFunction test = new OrdinatesToPointFunction();
			test.configure(cell);
			test.transform(source, target);
	
			String targetPropertyName = ((Property)cell.getEntity2()).getLocalname();
			Point point = (Point)target.getProperty(targetPropertyName).getValue();
			assertTrue(point.getX()== x * x);
//			System.out.println("Test "+this.testInt+" : "+(point.getX()== x * x )+"  "+point.getX()+" = "+x * x);
			assertTrue(point.getY()== y * y);
//			System.out.println("Test "+this.testInt+" : "+(point.getX()== x * x )+"  "+point.getY()+" = "+y * y);
		}

	}
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName, String[] propertyNames) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			switch(this.testInt){
			case 1:
				for (String s : propertyNames) {
					if(propertyNames.length==1){
						ftbuilder.add(s, Geometry.class);
					}
					else{
						ftbuilder.add(s, Double.class);
					}
				}
				break;
			case 2:
				for (String s : propertyNames) {
					if(propertyNames.length==1){
						ftbuilder.add(s, Geometry.class);
					}
					else{
						ftbuilder.add(s, String.class);
					}
				}
				break;
			case 3:
				if(propertyNames.length==1){
					ftbuilder.add(propertyNames[0], Geometry.class);
				}
				else{
					ftbuilder.add(propertyNames[0], Double.class);
					ftbuilder.add(propertyNames[1], String.class);
				}
				break;	
			case 4:
				if(propertyNames.length==1){
					ftbuilder.add(propertyNames[0], Geometry.class);
				}
				else{
					ftbuilder.add(propertyNames[0], String.class);
					ftbuilder.add(propertyNames[1], Double.class);
				}
				break;	
			default:
				for (String s : propertyNames) {
					if(propertyNames.length==1){
						ftbuilder.add(s, Geometry.class);
					}
					else{
						ftbuilder.add(s, Double.class);
					}
				}
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
