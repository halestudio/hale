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

import java.sql.Timestamp;
import java.util.Date;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
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
	
	/**
	 * 
	 */
	private final String ns = "http://www.esdi-humboldt.eu/schema/test"; //$NON-NLS-1$

	/**
	 * Test method for {@link eu.esdihumboldt.cst.corefunctions.RenameAttributeFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)}.
	 */
	@Test
	public void testTransform() {
		// build source and target features
		Feature source = this.buildFeature("SourceType"); //$NON-NLS-1$
		Feature target = this.buildFeature("TargetType"); //$NON-NLS-1$
		
		// build various cells and execute transformation on them
		RenameAttributeFunction raf = new RenameAttributeFunction();
		raf.configure(this.getTestingCell("stringFloat", "double")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(target.getProperty("double").getValue() instanceof Double); //$NON-NLS-1$
		String newValue = target.getProperty("double").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(source.getProperty("stringFloat").getValue())); //$NON-NLS-1$
		
		raf.configure(this.getTestingCell("double", "double")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(target.getProperty("double").getValue() instanceof Double); //$NON-NLS-1$
		newValue = target.getProperty("double").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(source.getProperty("double").getValue().toString())); //$NON-NLS-1$
		
		raf.configure(this.getTestingCell("int", "int")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(target.getProperty("int").getValue() instanceof Integer); //$NON-NLS-1$
		newValue = target.getProperty("int").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(source.getProperty("int").getValue().toString())); //$NON-NLS-1$
		
		raf.configure(this.getTestingCell("stringInt", "long")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(target.getProperty("long").getValue() instanceof Long); //$NON-NLS-1$
		newValue = target.getProperty("long").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(source.getProperty("stringInt").getValue())); //$NON-NLS-1$
		
		raf.configure(this.getTestingCell("stringInt", "int")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(target.getProperty("int").getValue() instanceof Integer); //$NON-NLS-1$
		newValue = target.getProperty("int").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(source.getProperty("stringInt").getValue())); //$NON-NLS-1$
		
		raf.configure(this.getTestingCell("stringFloat", "float")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(target.getProperty("float").getValue() instanceof Float); //$NON-NLS-1$
		newValue = target.getProperty("float").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(source.getProperty("stringFloat").getValue())); //$NON-NLS-1$
		
		raf.configure(this.getTestingCell("float", "StringFloat")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(source.getProperty("float").getValue() instanceof Float); //$NON-NLS-1$
		newValue = source.getProperty("float").getValue().toString(); //$NON-NLS-1$
		assertTrue(newValue.equals(target.getProperty("stringFloat").getValue())); //$NON-NLS-1$
		
		//Test date
		raf.configure(this.getTestingCell("date", "timestamp")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		assertTrue(source.getProperty("date").getValue() instanceof Date); //$NON-NLS-1$
		long newValue2 = ((Date)source.getProperty("date").getValue()).getTime(); //$NON-NLS-1$
		assertTrue(newValue2==((Timestamp)target.getProperty("timestamp").getValue()).getTime()); //$NON-NLS-1$
		
	}
	
	/**
	 * 
	 */
	@Test
    public void testGeometry() {
		// build source and target features
		Feature source = this.buildFeature("SourceType"); //$NON-NLS-1$
		Feature target = this.buildFeature("TargetType"); //$NON-NLS-1$
		
		// build various cells and execute transformation on them
		RenameAttributeFunction raf = new RenameAttributeFunction();
		raf.configure(this.getTestingCell("point", "multipoint")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("linestring", "multipoint")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("polygon", "multipoint")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("multipolygon", "multipoint")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("multilinestring", "multipoint")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("polygon", "multilinestring")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("multipolygon", "multilinestring")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("polygon", "linestring")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
		raf.configure(this.getTestingCell("multipolygon", "linestring")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
    }

	
	/**
	 * 
	 */
	@Test(expected=UnsupportedOperationException.class)
    public void testException() {
		// build source and target features
		Feature source = this.buildFeature("SourceType"); //$NON-NLS-1$
		Feature target = this.buildFeature("TargetType"); //$NON-NLS-1$
		
		// build various cells and execute transformation on them
		RenameAttributeFunction raf = new RenameAttributeFunction();
		raf.configure(this.getTestingCell("date", "timestampexception")); //$NON-NLS-1$ //$NON-NLS-2$
		raf.transform(source, target);
    }

			
	/**
	 * @param featureTypeName
	 * @return the simpleFeature
	 */
	private Feature buildFeature(String featureTypeName) {
		SimpleFeatureType sft = this.getFeatureType(
				this.ns,	featureTypeName);
		
		SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
		GeometryFactory geomFactory = new GeometryFactory();
		
		//Variable Declaration
		Coordinate coord1 = new Coordinate(-0.318987,47.003018);
		Coordinate coord2 = new Coordinate(-0.768746,47.358268);
		Coordinate coord3 = new Coordinate(-0.574463,47.684285);
		Coordinate coord4 = new Coordinate(-0.347374,47.854602);
		Coordinate coord5 = new Coordinate(-0.006740,47.925567);
		Coordinate coord6 = new Coordinate(0.135191,47.726864);
		Coordinate coord7 = new Coordinate(0.149384,47.599127);
		Coordinate coord8 = new Coordinate(0.419052,47.670092);
		Coordinate coord9 = new Coordinate(0.532597,47.428810);
		Coordinate coord10 = new Coordinate(0.305508,47.443003);
		Coordinate coord11 = new Coordinate(0.475824,47.144948);
		Coordinate coord12 = new Coordinate(0.064225,47.201721);
		Coordinate coord13 = new Coordinate(-0.318987,47.003018);
		Coordinate[] coordArray = new Coordinate[13];
		coordArray[0] = coord1;
		coordArray[1] = coord2;
		coordArray[2] = coord3;
		coordArray[3] = coord4;
		coordArray[4] = coord5;
		coordArray[5] = coord6;
		coordArray[6] = coord7;
		coordArray[7] = coord8;
		coordArray[8] = coord9;
		coordArray[9] = coord10;
		coordArray[10] = coord11;
		coordArray[11] = coord12;
		coordArray[12] = coord13;
		LinearRing[] linearRingArray = new LinearRing[2];
		linearRingArray[0] = geomFactory.createLinearRing(coordArray);
		linearRingArray[1] = geomFactory.createLinearRing(coordArray);
		Polygon[] polygonArray = new Polygon[2];
		polygonArray[0] = geomFactory.createPolygon(geomFactory.createLinearRing(coordArray),linearRingArray);
		polygonArray[1] = geomFactory.createPolygon(geomFactory.createLinearRing(coordArray),linearRingArray);
		
		SimpleFeature f = sfb.buildFeature(String.valueOf(featureTypeName.hashCode()), 
				new Object[]{
					new String("12.34"), 	// string //$NON-NLS-1$
					new String("1234"),		//stringInt //$NON-NLS-1$
					new Double(12.34), 	// double
					new Long(1234), 	// long
					new Integer(1234), 		// int
					new Float(12.34), 		// float
					geomFactory.createPoint(new Coordinate(12.34, 56.78)), // point
					geomFactory.createLineString(coordArray), 	// linestring
					geomFactory.createPolygon(geomFactory.createLinearRing(coordArray),linearRingArray), 	// polygon
					geomFactory.createMultiPoint(coordArray), 	// multipoint
					geomFactory.createMultiPolygon(polygonArray), 	// multipolygon
					geomFactory.createMultiLineString(linearRingArray), 	// multilinestring
					new Date(System.currentTimeMillis()), //date
					new Timestamp(System.currentTimeMillis()), //timestamp
					null //timestamp for exception
		});
		return f;
	}
	
	/**
	 * @param featureTypeNamespace
	 * @param featureTypeName
	 * @return the simpleFeatureType
	 */
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName) {
		
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add("stringFloat", String.class); //$NON-NLS-1$
			ftbuilder.add("stringInt", String.class); //$NON-NLS-1$
			ftbuilder.add("double", Double.class); //$NON-NLS-1$
			ftbuilder.add("long", Long.class); //$NON-NLS-1$
			ftbuilder.add("int", Integer.class); //$NON-NLS-1$
			ftbuilder.add("float", Float.class); //$NON-NLS-1$
			ftbuilder.add("point", Point.class); //$NON-NLS-1$
			ftbuilder.add("linestring", LineString.class); //$NON-NLS-1$
			ftbuilder.add("polygon", Polygon.class); //$NON-NLS-1$
			ftbuilder.add("multipoint", MultiPoint.class); //$NON-NLS-1$
			ftbuilder.add("multipolygon", MultiPolygon.class); //$NON-NLS-1$
			ftbuilder.add("multilinestring", MultiLineString.class); //$NON-NLS-1$
			ftbuilder.add("date", Date.class); //$NON-NLS-1$
			ftbuilder.add("timestamp", Timestamp.class); //$NON-NLS-1$
//			ftbuilder.add("timestampexception", com.sun.jmx.snmp.Timestamp.class);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
	/**
	 * @param sourcePropertyname
	 * @param targetPropertyName
	 * @return the new cell for testing
	 */
	private Cell getTestingCell(String sourcePropertyname, String targetPropertyName) {
		// set up cell to use for testing
		Cell cell = new Cell();
		
		Transformation t = new Transformation();
		t.setService(new Resource(RenameAttributeFunction.class.toString()));
		Property p1 = new Property(new About(
				this.ns, "SourceType", sourcePropertyname)); //$NON-NLS-1$
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(
				this.ns, "TargetType", targetPropertyName))); //$NON-NLS-1$
		
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
