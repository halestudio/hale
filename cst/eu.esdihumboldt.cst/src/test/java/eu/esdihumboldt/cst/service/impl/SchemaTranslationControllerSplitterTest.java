package eu.esdihumboldt.cst.service.impl;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import java.util.UUID;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.metadata.iso.lineage.LineageImpl;
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
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.transformer.service.AddFunctionsToPathUtility;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory.ToleranceLevel;
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
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * This is a simple example test for the {@link SchemaTranslationController} in splitt cases.
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 * @version $Id$ 
 */
public class SchemaTranslationControllerSplitterTest {
	private static Logger _log = Logger.getLogger(SchemaTranslationControllerSplitterTest.class);
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
		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
			Appender appender = new ConsoleAppender(
					new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), 
					 ConsoleAppender.SYSTEM_OUT );
			appender.setName("A1");
			Logger.getRootLogger().addAppender(appender);
		}
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
		ftbuilder.add("the_geom", MultiLineString.class);
		ftbuilder.setDefaultGeometry("the_geom");
		sourceType = ftbuilder.buildFeatureType();
		

		
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName(NameHelper.targetLocalname);
		ftbuilder2.setNamespaceURI(NameHelper.targetNamespace);
		ftbuilder2.add("the_geom2", MultiLineString.class);
		ftbuilder2.setDefaultGeometry("the_geom2");
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
			LineString[] lineStrings = new LineString[10];
			for (int i2 = 0; i2 < lineStrings.length; i2++) {
				Coordinate[] coordinates = new Coordinate[10];
				for (int n = 0; n < lineStrings.length; n++) {
					coordinates[n] = new Coordinate(
							Math.random(), Math.random());
				}
				lineStrings[i2] = geomFactory.createLineString(coordinates);
			}
			geometry = geomFactory.createMultiLineString(lineStrings);
			((SimpleFeature)f).setDefaultGeometry(geometry);
			features.add(f);
		}
		
		// now it's time to actually execute the translation
		FeatureCollection result = stc.translate(features);
		
		// and finally, we have to assert that what we got back is what we expected.
		assertTrue(result != null);
		
		_log.info("RESULT Size: " +result.size());
		
		assertTrue(result.getSchema().getName().getLocalPart().equals(
				NameHelper.targetLocalname));
		
		for ( Iterator i = result.iterator(); i.hasNext(); )
		{
		  SimpleFeature target = (SimpleFeature) i.next();
		  _log.info("Target def Geom: " + target.getDefaultGeometry()); 
		}

	}
	

	
	/**
	 * @return the {@link Alignment} to use as input for this test.
	 */
	private static IAlignment getTestAlignment(){
		// first, use an alignment created by a different test as a basis.
		Alignment a = new Alignment();
		a.setAbout(new About("lala"));
		try {
			a.setSchema1(new Schema(
					NameHelper.sourceNamespace, new Formalism(
							"GML", new URI("http://schemas.opengis.org/gml"))));
			a.setSchema2(new Schema(
					NameHelper.targetNamespace, new Formalism(
							"GML", new URI("http://schemas.opengis.org/gml"))));
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
						"SelectedAttribute", 
						"the_geom"));
		t.getParameters().add(
				new Parameter(
						"InstanceSplitCondition", 
						"split:extractSubgeometry(LineString)"));
		((FeatureClass)cell.getEntity1()).setTransformation(t);
		a.getMap().add(cell);
		
		return a;
	}


}

