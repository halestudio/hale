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
package test.eu.esdihumboldt.hale.models.factory;

import static org.junit.Assert.*;

import org.geotools.feature.FeatureCollection;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO: Enter an explanation what this type does here.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class FeatureCollectionUtilitiesTest {

	/**
	 * Test method for {@link test.eu.esdihumboldt.hale.models.factory.FeatureCollectionUtilities#loadFeatureCollectionFromWKT(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLoadFeatureCollectionFromWKT() {
		// run a series of tests with different geometry types.
		String testFTName = "TestFTName"; //$NON-NLS-1$
		String testFeatureName = "TestFeatureName"; //$NON-NLS-1$
		
		// load collections from files.
		String basepath = "D:/humboldt-workspace/HALE/"; //$NON-NLS-1$
		FeatureCollection<SimpleFeatureType, SimpleFeature> fc_linestring = 
			FeatureCollectionUtilities.loadFeatureCollectionFromWKT(basepath +
				"resources/test.eu.esdihumboldt.hale.models.factory/linestring.wkt",  //$NON-NLS-1$
				testFTName, testFeatureName);
		FeatureCollection<SimpleFeatureType, SimpleFeature> fc_polygon = 
			FeatureCollectionUtilities.loadFeatureCollectionFromWKT(basepath +
				"resources/test.eu.esdihumboldt.hale.models.factory/polygon.wkt",  //$NON-NLS-1$
				testFTName, testFeatureName);
		
		// make assertions on the result to ensure completeness of results
		assertTrue(fc_linestring.size() == 1);
		Feature f = fc_linestring.features().next();
		assertTrue(f.getIdentifier() != null);
		assertEquals(f.getIdentifier().getID(), testFeatureName);
		assertTrue(f.getDefaultGeometryProperty() != null);
		assertEquals(f.getDefaultGeometryProperty().getValue().getClass(), 
				com.vividsolutions.jts.geom.LineString.class);
		assertTrue(f.getType().getName() != null);
		assertEquals(f.getType().getName().getLocalPart(), testFTName);
		
		assertTrue(fc_polygon.size() == 1);
		Feature f2 = fc_polygon.features().next();
		assertTrue(f2.getIdentifier() != null);
		assertEquals(f2.getIdentifier().getID(), testFeatureName);
		assertTrue(f2.getDefaultGeometryProperty() != null);
		assertEquals(f2.getDefaultGeometryProperty().getValue().getClass(), 
				com.vividsolutions.jts.geom.Polygon.class);
		assertTrue(f2.getType().getName() != null);
		assertEquals(f2.getType().getName().getLocalPart(), testFTName);
	}

	/**
	 * Test method for FeatureCollectionUtilities#loadFeatureCollectionFromGML2(java.lang.String).
	 */
	@Test
	public void testLoadFeatureCollectionFromGML2() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link FeatureCollectionUtilities#getFeatureType(Class, String, boolean)}.
	 */
	@Test
	public void testGetFeatureType() {
		String testFTName = "TestFTName"; //$NON-NLS-1$
		Class<? extends Geometry> geometry_class = com.vividsolutions.jts.geom.Polygon.class;
		FeatureType ft = FeatureCollectionUtilities.getFeatureType(
				geometry_class, testFTName, false);
		assertTrue(ft.getName() != null);
		assertEquals(ft.getName().getLocalPart(), testFTName);
		assertTrue(ft.getDescriptors().size() == 1);
		assertTrue(ft.getDescriptor("the_geom") != null); //$NON-NLS-1$
	}

}
