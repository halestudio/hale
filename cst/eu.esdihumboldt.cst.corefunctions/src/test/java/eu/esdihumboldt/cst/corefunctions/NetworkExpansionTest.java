package eu.esdihumboldt.cst.corefunctions;

import static org.junit.Assert.assertTrue;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;


public class NetworkExpansionTest {
	
	private final String sourceLocalname = "FT1"; //$NON-NLS-1$
	private final String sourceLocalnamePropertyAGeom = "PropertyAGeom"; //$NON-NLS-1$
	private final String sourceNamespace = "http://esdi-humboldt.eu"; //$NON-NLS-1$
	
	private final String targetLocalname = "FT2"; //$NON-NLS-1$
	private final String targetLocalnamePropertyBGeom = "PropertyBGeom"; //$NON-NLS-1$
	private final String targetNamespace = "http://xsdi.org"; //$NON-NLS-1$
	
	@Test
	public void testConfigure() {
		NetworkExpansionFunction nef = new NetworkExpansionFunction();
		nef.configure(nef.getParameters());
	}
	
	
	//FIXME @Test
	public void testTransformFeatureFeature() {
		
		// set up cell to use for testing
		Cell cell = new Cell();

		Transformation t = new Transformation();
		t.setService(new Resource(NetworkExpansionFunction.class.toString()));
		
		Property p1 = new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyAGeom));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyBGeom)));

		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				"geom", //$NON-NLS-1$
				Polygon.class);
		SimpleFeatureType targettype = this.getFeatureType(
				this.targetNamespace, 
				this.targetLocalname, 
				this.targetLocalnamePropertyBGeom, 
				Polygon.class);
		GeometryFactory fac = new GeometryFactory();
				
		
		Feature source = SimpleFeatureBuilder.build(sourcetype, new Object[] {fac.createPolygon(fac.createLinearRing(new Coordinate[] {new Coordinate(0,2), new Coordinate (2,0), new Coordinate (8,6), new Coordinate(0,2)} ),null) }, "1"); //$NON-NLS-1$
		Feature target = SimpleFeatureBuilder.build(targettype, new Object[]{}, "2"); //$NON-NLS-1$
		
		// perform actual test
		NetworkExpansionFunction buffer = new NetworkExpansionFunction();
		buffer.configure(cell);

		Feature neu = buffer.transform(source, target);
		
		Polygon pOld = (Polygon)source.getDefaultGeometryProperty().getValue();
		Polygon pNew = (Polygon)neu.getDefaultGeometryProperty().getValue();
		
		Double areaBuff = pNew.getArea();
		Double sourceArea = pOld.getArea();
		
//		System.out.println(areaBuff + " Old: " + sourceArea);
		assertTrue(areaBuff > sourceArea);

	}

	private SimpleFeatureType getFeatureType(
			String featureTypeNamespace, 
			String featureTypeName, 
			String featuretypeGeometryPropertyName,
			Class <? extends Geometry> geom) {
		
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add(featuretypeGeometryPropertyName, geom);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
