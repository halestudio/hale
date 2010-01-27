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
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;


public class CentroidTest extends TestCase {
	
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
//		Transformation t = new Transformation();
//		t.setService(new Resource(CentroidFunction.class.toString()));
		Property p1 = new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyAGeom));
//		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyBGeom)));

		// build source and target Features
//		SimpleFeatureType sourcetype = this.getFeatureType(
//				this.sourceNamespace, 
//				this.sourceLocalname, 
//				Polygon.class);
//		SimpleFeatureType targettype = this.getFeatureType(this.targetNamespace, this.targetLocalname, Point.class);
		
		
		
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(this.sourceLocalname);
		ftbuilder.setNamespaceURI(this.sourceNamespace);
		ftbuilder.add(this.sourceLocalnamePropertyAGeom, Polygon.class);
		SimpleFeatureType sourcetype = ftbuilder.buildFeatureType();
		
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName(this.targetLocalname);
		ftbuilder2.setNamespaceURI(this.targetNamespace);
		ftbuilder2.add(this.targetLocalnamePropertyBGeom, Point.class);
		SimpleFeatureType targettype = ftbuilder2.buildFeatureType();
		
		
		
		GeometryFactory fac = new GeometryFactory();
				
		
		Feature source = SimpleFeatureBuilder.build(sourcetype, new Object[] {fac.createPolygon(fac.createLinearRing(new Coordinate[] {new Coordinate(0,0), new Coordinate (2,0), new Coordinate (2,2), new Coordinate(0,2), new Coordinate(0,0)} ),null) }, "1");
		Feature target = SimpleFeatureBuilder.build(targettype, new Object[]{}, "2");
		
		// perform actual test
		CentroidFunction center = new CentroidFunction();
		center.configure(cell);

		Feature neu = center.transform(source, target);
		System.out.println(neu.getDefaultGeometryProperty().getValue());
		Geometry c = (Geometry)neu.getDefaultGeometryProperty().getValue();
		
		assertTrue(c.equals(fac.createPoint(new Coordinate(1,1))));

	}

}
