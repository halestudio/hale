package eu.esdihumboldt.gmlhandler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.feature.types.GenericFeatureType;
import org.deegree.feature.types.property.SimplePropertyType;
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
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DgToGtConvertor {

	public static org.deegree.feature.FeatureCollection covertGttoDg(
			org.geotools.feature.FeatureCollection fc) {
		
		
		org.deegree.feature.Feature dgFeature = null;
		GenericFeatureType dfFT = null;
		List<org.deegree.feature.property.Property> dgProperties = null;
		List<org.deegree.feature.types.property.PropertyType> dgPropertyTypes = null;
		for (FeatureIterator i = fc.features(); i.hasNext() ;){
			dgPropertyTypes = new ArrayList<org.deegree.feature.types.property.PropertyType> (); 
			Feature feature = i.next();
			  System.out.println(feature.getDefaultGeometryProperty().getType());
			  FeatureType gtFT = feature.getType();
			  //convert gtFT to gtFT
			  //1. GenericFeatureType
			  //1.0 QName
			  Name gtFTName = gtFT.getName();
			  QName ftName = new QName(gtFTName.getNamespaceURI(), gtFTName.getLocalPart());
			  //1.1 List<PropertyType>
			  for (PropertyDescriptor gtPD :gtFT.getDescriptors()){
				 PropertyType gtPT = gtPD.getType();
				 Name gtPTName = gtPT.getName();
				 
				 // create deegree PropertyType
				 org.deegree.feature.types.property.PropertyType dfPT = new SimplePropertyType(ftName, 1, 1, null, false, dgPropertyTypes);
			  }
			  //1.2 boolean isAbstract
			  boolean isAbstract = gtFT.isAbstract();
			  //2. Feature id
			  //3. List<Property>
			  //4. GMLVersion 
			
			
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
