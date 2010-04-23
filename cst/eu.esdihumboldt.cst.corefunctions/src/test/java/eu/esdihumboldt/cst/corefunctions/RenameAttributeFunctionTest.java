/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
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

import static org.junit.Assert.*;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.coordinate.LineString;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * Test for {@link RenameAttributeFunction}. 
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 */
public class RenameAttributeFunctionTest {
	
	private final String ns = "http://www.esdi-humboldt.eu/schema/test";

	/**
	 * Test method for {@link eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)}.
	 */
	@Test
	public void testTransform() {
		// build source and target features
		Feature source = this.buildFeature("SourceType");
		Feature target = this.buildFeature("TargetType");
		
		// build various cells and execute transformation on them
		RenameAttributeFunction raf = new RenameAttributeFunction();
		raf.configure(this.getTestingCell(
				"string", "double"));
		raf.transform(source, target);
		assertTrue(target.getProperty("double").getValue() != null);
		assertTrue(target.getProperty("double").getValue() instanceof Double);
		String newValue = target.getProperty("double").getValue().toString();
		assertTrue(newValue.equals(source.getProperty("string").getValue()));
		
		raf.configure(this.getTestingCell(
				"double", "string"));
		raf.transform(source, target);
		assertTrue(target.getProperty("string").getValue() != null);
		assertTrue(target.getProperty("string").getValue() instanceof String);
		newValue = target.getProperty("string").getValue().toString();
		assertTrue(newValue.equals(source.getProperty("double").getValue().toString()));
	
		// TODO add other combinations, including spatial ones, until coverage is 100%
	}
	
	@Test
	public void testTransformWithNesting() {
		
	}

	private Feature buildFeature(String featureTypeName) {
		SimpleFeatureType sft = this.getFeatureType(
				ns,	featureTypeName);
		
		SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
		GeometryFactory geomFactory = new GeometryFactory();
		SimpleFeature f = sfb.buildFeature(String.valueOf(featureTypeName.hashCode()), 
				new Object[]{
					"12.56", 				// string
					new Double(12.345678), 	// double
					new Long(1234567890), 	// long
					new Integer(1234), 		// int
					new Float(12.34), 		// float
					geomFactory.createPoint(
							new Coordinate(12.34, 56.78)), // point
					null, 	// linestring
					null, 	// polygon
					null, 	// multipoint
					null, 	// multipolygon
					null 	// multilinestring
		});
		
		return f;
	}
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName) {
		
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add("string", String.class);
			ftbuilder.add("double", Double.class);
			ftbuilder.add("long", Long.class);
			ftbuilder.add("int", Integer.class);
			ftbuilder.add("float", Float.class);
			ftbuilder.add("point", Point.class);
			ftbuilder.add("linestring", LineString.class);
			ftbuilder.add("polygon", Polygon.class);
			ftbuilder.add("multipoint", MultiPoint.class);
			ftbuilder.add("multipolygon", MultiPolygon.class);
			ftbuilder.add("multilinestring", MultiLineString.class);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
	private Cell getTestingCell(String sourcePropertyname, String targetPropertyName) {
		// set up cell to use for testing
		Cell cell = new Cell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(RenameAttributeFunction.class.toString()));
		Property p1 = new Property(new About(
				this.ns, "SourceType", sourcePropertyname));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(
				this.ns, "TargetType", targetPropertyName)));
		
		return cell;

	}


	/**
	 * Test method for {@link eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction#configure(eu.esdihumboldt.cst.align.ICell)}.
	 */
	@Test
	public void testConfigureICell() {
		RenameAttributeFunction raf = new RenameAttributeFunction();
		raf.configure(raf.getParameters());
	}

}
