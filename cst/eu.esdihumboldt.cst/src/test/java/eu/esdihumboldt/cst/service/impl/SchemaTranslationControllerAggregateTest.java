package eu.esdihumboldt.cst.service.impl;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.transformer.service.AddFunctionsToPathUtility;
import eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController;
import eu.esdihumboldt.cst.transformer.service.impl.TargetSchemaProvider;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * This is a simple example test for the {@link SchemaTranslationController} in aggregate cases.
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 * @version $Id$ 
 */
public class SchemaTranslationControllerAggregateTest {
	private static Logger _log = Logger.getLogger(SchemaTranslationControllerAggregateTest.class);
	private static SchemaTranslationController stc = null;
	private static SimpleFeatureType sourceType = null;
	private static SimpleFeatureType targetType = null;

	/**
	 * Set up method using {@link eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController#SchemaTranslationController(eu.esdihumboldt.cst.align.IAlignment)}.
	 * @throws URISyntaxException 
	 */
	@BeforeClass
	public static void testSchemaTranslationController() throws URISyntaxException {
		// set up log4j logger manually if necessary
//		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
//			Appender appender = new ConsoleAppender(
//					new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), 
//					 ConsoleAppender.SYSTEM_OUT );
//			appender.setName("A1");
//			Logger.getRootLogger().addAppender(appender);
//		}
		AddFunctionsToPathUtility.getInstance().add();
		// configure the CstFunctionFactory
//		CstFunctionFactory.getInstance().registerCstPackage(
//				"eu.esdihumboldt.cst.corefunctions");
//		CstFunctionFactory.getInstance().registerCstPackage(
//				"eu.esdihumboldt.cst.corefunctions.inspire");
//		
		// set up the Schema Translation Controller to use for testing
		stc = new SchemaTranslationController(null, true, getTestAlignment());
		
		
		
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(NameHelper.sourceLocalname);
		ftbuilder.setNamespaceURI(NameHelper.sourceNamespace);
		ftbuilder.add("the_geom", LineString.class); //$NON-NLS-1$
		ftbuilder.setDefaultGeometry("the_geom"); //$NON-NLS-1$
		sourceType = ftbuilder.buildFeatureType();
		

		
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName(NameHelper.targetLocalname);
		ftbuilder2.setNamespaceURI(NameHelper.targetNamespace);
		ftbuilder2.add("the_geom2", MultiLineString.class); //$NON-NLS-1$
		ftbuilder2.setDefaultGeometry("the_geom2"); //$NON-NLS-1$
		targetType = ftbuilder2.buildFeatureType();


		
		// also, we'll have to provide the target types to the TargetSchemaProvider
		Collection<FeatureType> types = new ArrayList<FeatureType>();
		types.add(targetType);
		TargetSchemaProvider.getInstance().addTypes(types);
	}

	/**
	 * This is a more complex test that tests the actual translation and makes
	 * assertions afterwards.
	 * 
	 * Test method for {@link eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController#translate(eu.esdihumboldt.cst.align.IAlignment, org.geotools.feature.FeatureCollection)}.
	 * @throws URISyntaxException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTranslate() throws URISyntaxException {
		
		// set up a FeatureCollection to use for transformation.
		FeatureCollection features = FeatureCollections.newCollection();
		
		// build source Feature(s)
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sourceType);
		GeometryFactory geomFactory = new GeometryFactory();
		for (int i = 0; i < 10; i++) {
			Feature f = builder.buildFeature(UUID.randomUUID().toString(), new Object[]{});
			Geometry geometry = null;
			Coordinate[] coordinates = new Coordinate[2];
			for (int n = 0; n < coordinates.length; n++) {
				coordinates[n] = new Coordinate(
							Math.random(), Math.random());
			}
			geometry = geomFactory.createLineString(coordinates);
			((SimpleFeature)f).setDefaultGeometry(geometry);
			features.add(f);
		}
		
		// now it's time to actually execute the translation
		FeatureCollection result = stc.translate(features);
		
		// and finally, we have to assert that what we got back is what we expected.
		assertTrue(result != null);
		
		_log.info("RESULT Size: " +result.size()); //$NON-NLS-1$
		
		assertTrue(result.getSchema().getName().getLocalPart().equals(
				NameHelper.targetLocalname));
		
		for ( Iterator i = result.iterator(); i.hasNext(); )
		{
		  SimpleFeature target = (SimpleFeature) i.next();
		  _log.info("Target def Geom: " + target.getDefaultGeometry());  //$NON-NLS-1$
		}

	}
	

	
	/**
	 * @return the {@link Alignment} to use as input for this test.
	 */
	private static IAlignment getTestAlignment(){
		// first, use an alignment created by a different test as a basis.
		Alignment a = new Alignment();
		a.setAbout(new About("lala")); //$NON-NLS-1$
		try {
			a.setSchema1(new Schema(
					NameHelper.sourceNamespace, new Formalism(
							"GML", new URI("http://schemas.opengis.org/gml")))); //$NON-NLS-1$ //$NON-NLS-2$
			a.setSchema2(new Schema(
					NameHelper.targetNamespace, new Formalism(
							"GML", new URI("http://schemas.opengis.org/gml")))); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		
		// second, add a FeatureType Rename operation.
		Cell cell = new Cell();
		cell.setEntity1(
				new FeatureClass(new About(
						NameHelper.sourceNamespace, 
						NameHelper.sourceLocalname)));
		cell.setEntity2(new FeatureClass(new About(
				NameHelper.targetNamespace, 
				NameHelper.targetLocalname)));
		cell.setRelation(RelationType.Equivalence);
		
		Transformation t = new Transformation();
		t.setLabel(RenameFeatureFunction.class.getName());
		t.setService(new Resource(RenameFeatureFunction.class.getName()));
		t.getParameters().add(
				new Parameter(
						"SelectedAttribute",  //$NON-NLS-1$
						"the_geom")); //$NON-NLS-1$
		t.getParameters().add(
				new Parameter(
						"InstanceMergeCondition",  //$NON-NLS-1$
						"aggregate:Multi")); //$NON-NLS-1$
		t.getParameters().add(
				new Parameter(
						"TargetAttribute",  //$NON-NLS-1$
						"the_geom2")); //$NON-NLS-1$
		((FeatureClass)cell.getEntity1()).setTransformation(t);
		a.getMap().add(cell);
		
		return a;
	}


}

