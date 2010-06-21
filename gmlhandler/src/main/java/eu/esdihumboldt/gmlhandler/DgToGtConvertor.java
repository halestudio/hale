package eu.esdihumboldt.gmlhandler;

import org.geotools.data.DataUtilities;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DgToGtConvertor {

	public static org.deegree.feature.FeatureCollection covertGttoDg(
			org.geotools.feature.FeatureCollection fc) {
		
		
		
		for (FeatureIterator i = fc.features(); i.hasNext() ;){
			  Feature feature = i.next();	
			  System.out.println(feature.getDefaultGeometryProperty().getType());
			
		}
		return null;
	}

	
	public static org.geotools.feature.FeatureCollection covertDgtoGt(
			org.deegree.feature.FeatureCollection fc) {

		// * Example of creating geotools feature*/
		FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
		collection = FeatureCollections
		.newCollection();
		try {
			SimpleFeatureType TYPE = DataUtilities.createType("Location",
					"location:Point,name:String"); // see createFeatureType();
			
			GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);

			Point point = factory.createPoint(new Coordinate(15, 50));
			SimpleFeature feature = SimpleFeatureBuilder.build(TYPE, new Object[] {
					point, "name" }, null);
			collection.add(feature);
		} catch (FactoryRegistryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SchemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return collection;
	}

}
