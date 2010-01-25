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

import java.util.Collection;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.geotools.feature.FeatureCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.instance.InstanceServiceImpl;

import static org.junit.Assert.*;


/**
 * This JUnit 4 test tests the {@link InstanceServiceImpl}.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class InstanceServiceFactoryTest {
	
	private static Logger _log = Logger.getLogger(InstanceServiceFactoryTest.class);
	
	@BeforeClass
	public static void setUp() {
		InstanceServiceFactoryTest.setUpLogger();
		
		FeatureCollection fc_reference = 
			FeatureCollectionUtilities.loadFeatureCollectionFromWKT(
				"D:/humboldt-workspace/HALE/resources/test.eu.esdihumboldt.hale.models.factory/linestring.wkt", 
				"ReferenceFT", "ReferenceFeatureID");
		InstanceServiceImpl.getInstance().addInstances(
				DatasetType.reference, fc_reference);
		FeatureCollection fc_transformed = 
			FeatureCollectionUtilities.loadFeatureCollectionFromWKT(
				"D:/humboldt-workspace/HALE/resources/test.eu.esdihumboldt.hale.models.factory/polygon.wkt", 
				"TransformedFT", "TransformedFeatureID");
		InstanceServiceImpl.getInstance().addInstances(
				DatasetType.transformed, fc_transformed);
		_log.debug("Set up of FeatureCollections completed.");
	}
	
	@AfterClass
	public static void tearDown() {
		InstanceServiceImpl.getInstance().cleanInstances(DatasetType.reference);
		InstanceServiceImpl.getInstance().cleanInstances(DatasetType.transformed);
		_log.debug("Finished Test.");
	}

	private static void setUpLogger() {
		Appender appender = new ConsoleAppender(
				new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), ConsoleAppender.SYSTEM_OUT );
		appender.setName("A1");
		Logger.getRootLogger().addAppender(appender);
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.models.instance.InstanceServiceImpl#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertEquals(InstanceServiceImpl.getInstance(),
				InstanceServiceImpl.getInstance());
	}
	
	/**
	 * Test method for {@link eu.esdihumboldt.hale.models.instance.InstanceServiceImpl#getAllFeatures(DatasetType)}.
	 */
	@Test
	public void testGetAllFeatures() {
		// execution of getAllFeatures with possible attributes
		FeatureCollection<FeatureType, Feature> fc_reference = InstanceServiceImpl.getInstance().getFeatures(DatasetType.reference);
		FeatureCollection<FeatureType, Feature> fc_transformed = InstanceServiceImpl.getInstance().getFeatures(DatasetType.transformed);
		
		// assertions
		_log.debug("fc_reference.size(): "+ fc_reference.size());
		_log.debug("fc_transformed.size(): "+ fc_transformed.size());
		assertTrue(fc_reference.size() == 1);
		assertTrue(fc_transformed.size() == 1);
	}
	
	/**
	 * Test method for {@link eu.esdihumboldt.hale.models.instance.InstanceServiceImpl#getFeaturesByType(org.opengis.feature.type.FeatureType)}.
	 */
	@Test
	public void testGetFeaturesByType() {
		FeatureType featureType = FeatureCollectionUtilities.getFeatureType(
				com.vividsolutions.jts.geom.LineString.class, "TransformedFT", false);
		
		Collection<? extends Feature> fc_reference = InstanceServiceImpl.getInstance().getFeaturesByType(DatasetType.reference, featureType);
		_log.debug("fc_reference.size(): " + fc_reference.size());
		assertTrue(fc_reference.size() == 1);
		FeatureType ft = fc_reference.iterator().next().getType();
		assertEquals(ft.getName().getLocalPart(), "TransformedFT");
	}
	
	/**
	 * Test method for {@link eu.esdihumboldt.hale.models.instance.InstanceServiceImpl#getFeatureByID(String)}.
	 */
	@Test
	public void testGetFeatureByID() {
		Feature f = InstanceServiceImpl.getInstance().getFeatureByID(DatasetType.reference, "ReferenceFeatureID");
		assertEquals(f.getIdentifier().getID(), "ReferenceFeatureID");
	}
	
}
