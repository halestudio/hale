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

import junit.framework.TestCase;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

public class SpatialTypeConversionTest extends TestCase {
	private final String sourceLocalname = "FT1";
	private final String sourceLocalnamePropertyAGeom = "PropertyAGeom";
	private final String sourceNamespace = "http://esdi-humboldt.eu";
	
	private final String targetLocalname = "FT2";
	private final String targetLocalnamePropertyBGeom = "PropertyBGeom";
	private final String targetNamespace = "http://xsdi.org";
	
	
	

	@Test
	public void testTransformFeatureFeature() {

		// set up cell to use for testing
		Cell cell = new Cell();
		Transformation t = new Transformation();
		t.setService(new Resource(SpatialTypeConversionFunction.class.toString()));
		t.getParameters().add(new Parameter("FROM", "com.vividsolutions.jts.geom.LineString"));
		t.getParameters().add(new Parameter("TO", "com.vividsolutions.jts.geom.MultiPoint"));
		Property p1 = new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyAGeom));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyBGeom)));

		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				LineString.class);
		SimpleFeatureType targettype = this.getFeatureType(this.targetNamespace, this.targetLocalname, MultiPoint.class);
		GeometryFactory fac = new GeometryFactory();
		Feature source = SimpleFeatureBuilder.build(sourcetype, new Object[] { fac.createLineString(new Coordinate[] {new Coordinate(0,2), new Coordinate (2,0), new Coordinate (8,6)}) }, "1");
		Feature target = SimpleFeatureBuilder.build(targettype, new Object[]{}, "2");
		
		// perform actual test
		SpatialTypeConversionFunction stcf = new SpatialTypeConversionFunction();
		stcf.configure(cell);

		Feature neu = stcf.transform(source, target);
		assertTrue(neu.getDefaultGeometryProperty().getValue().getClass().equals(MultiPoint.class));
		
//		Geometry g = (Geometry)neu.getDefaultGeometryProperty().getValue();
//		for (Coordinate c : g.getCoordinates()){
//			System.out.println(c.x + " " + c.y + " " + c.z);
//		}
		

		//Write xml
//		Alignment a = new Alignment();
//		a.setAbout(new About("lala"));
//		try {
//		a.setSchema1(new Schema(
//				sourceNamespace, new Formalism(
//						"GML", new URI("http://schemas.opengis.org/gml"))));
//		a.setSchema2(new Schema(
//				targetNamespace, new Formalism(
//						"GML", new URI("http://schemas.opengis.org/gml"))));
//		a.getMap().add(cell);
//		
//		
//		OmlRdfGenerator org = new OmlRdfGenerator();
//		
//			org.write(a, "c:\\testy.oml");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	
	
	
	@Test
	public void testTransformFeatureMultiLineStringFeatureMultiPoint() {

		// set up cell to use for testing
		Cell cell = new Cell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(SpatialTypeConversionFunction.class.toString()));
		t.getParameters().add(new Parameter("FROM", "com.vividsolutions.jts.geom.MultiLineString"));
		t.getParameters().add(new Parameter("TO", "com.vividsolutions.jts.geom.MultiPoint"));
		Property p1 = new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyAGeom));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyBGeom)));

		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				MultiLineString.class);
		SimpleFeatureType targettype = this.getFeatureType(this.targetNamespace, this.targetLocalname, MultiPoint.class);
		GeometryFactory fac = new GeometryFactory();
		
		LineString[] ls = new LineString[2];
		ls[0] = fac.createLineString(new Coordinate[] {new Coordinate(0,2), new Coordinate (2,0), new Coordinate (8,6)});
		ls[1] = fac.createLineString(new Coordinate[] {new Coordinate(2,2), new Coordinate (3,3), new Coordinate (4,4)});
		
		Feature source = SimpleFeatureBuilder.build(sourcetype, new Object[] { fac.createMultiLineString(ls)} , "1");
		Feature target = SimpleFeatureBuilder.build(targettype, new Object[]{}, "2");
		
		// perform actual test
		SpatialTypeConversionFunction stcf = new SpatialTypeConversionFunction();
		stcf.configure(cell);
//		stcf.transform(source, target);

		
		Feature neu = stcf.transform(source, target);
		assertTrue(neu.getDefaultGeometryProperty().getValue().getClass().equals(MultiPoint.class));
		
//		System.out.println(neu.getDefaultGeometryProperty().getType());
//		Geometry g = (Geometry)neu.getDefaultGeometryProperty().getValue();
//		for (Coordinate c : g.getCoordinates()){
//			System.out.println(c.x + " " + c.y + " " + c.z);
//		}
		
	}
	
	
	
	
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, String featureTypeName,  Class <? extends Geometry> geom) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add("geom", geom);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
}