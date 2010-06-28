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
package eu.esdihumboldt.gmlhandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.commons.tom.TypedObjectNode;
import org.deegree.feature.GenericFeatureCollection;
import org.deegree.feature.property.GenericProperty;
import org.deegree.feature.types.GenericFeatureType;
import org.deegree.feature.types.property.SimplePropertyType;
import org.deegree.feature.types.property.ValueRepresentation;
import org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension;
import org.deegree.feature.types.property.GeometryPropertyType.GeometryType;
import org.deegree.geometry.Geometry;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.geometry.refs.GeometryReference;
import org.geotools.data.DataUtilities;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * 
 * This class contains methods to create Deegree3 Structures like Feature, FeatureCollection and so
 * using GeoSpatial data retrieved from the according geotools Objects.
 * 
 * @author Jan Jezek, Anna Pitaev
 * @version $Id$ 
 * 
 * 
 * 
 */
public class DgToGtConvertor {

	
	/**
	 * 
	 * @param geotools-based Feature Collection fc
	 * @return deegree-based Feature Collection 
	 */
	
	public static org.deegree.feature.FeatureCollection covertGttoDg(
			org.geotools.feature.FeatureCollection fc) {
		
		Collection<org.deegree.feature.Feature> dgFeatures = new ArrayList();
		org.deegree.feature.Feature dgFeature = null;
		List<org.deegree.feature.property.Property> dgProperties = null;
		List<org.deegree.feature.types.property.PropertyType> dgPropertyTypes = null;
		for (FeatureIterator i = fc.features(); i.hasNext() ;){
			dgPropertyTypes = new ArrayList<org.deegree.feature.types.property.PropertyType> (); 
			Feature gtFeature = i.next();
			dgFeature = createDgFeature(gtFeature);
			dgFeatures.add(dgFeature);
			 
			
		}
		org.deegree.feature.FeatureCollection dfFC = new GenericFeatureCollection(fc.getID(), dgFeatures);
		return dfFC;
	}

	
	/**
	 * 
	 * @param  geotools-based Feature
	 * @return deegree-based Feature
	 * 
	 */
	private static org.deegree.feature.Feature createDgFeature(Feature gtFeature) {
		 System.out.println(gtFeature.getDefaultGeometryProperty().getType());
		  FeatureType gtFT = gtFeature.getType();
		  //convert gtFT to gtFT
		  
		  //1. GenericFeatureType
		  GenericFeatureType dgFT  = createDgFt(gtFT);
		 
		  //2. Feature id
		  String fid = gtFeature.getIdentifier().getID();
		  //3. List<Property>
		  List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
		  Iterator<Property> gtPropsIter = gtFeature.getProperties().iterator();
		   while(gtPropsIter.hasNext()){
			   Property gtProp = (Property)gtPropsIter.next();
			   org.deegree.feature.property.Property dgProp = createDgProp(gtProp);
			   dgProps.add(dgProp);
		   }
		  //4. GMLVersion 
		   org.deegree.feature.Feature dgFeature  = dgFT.newFeature(fid, dgProps, GMLVersion.GML_32);
		   return dgFeature;
		
	}


	/**
	 * 
	 * @param geotools-based Property
	 * @return deegree-based Property 
	 * 
	 */
	private static org.deegree.feature.property.Property createDgProp(
			Property gtProp) {
		//1. declare a Property instance: make decision about implementing class after analyze of the PropertyType
		org.deegree.feature.property.Property dgProp = null;
		//2. define isNilled isNilled
		boolean isNilled = gtProp.isNillable();
		//3. define property name
		QName dgPropName = new QName(gtProp.getName().getNamespaceURI(), gtProp.getName().getLocalPart());
		
		//create deegree based PropertyType from the geotools Objecy
		org.deegree.feature.types.property.PropertyType dgPT  =  createDgPt(gtProp.getDescriptor());
		
		if (dgPT instanceof org.deegree.feature.types.property.SimplePropertyType){
			//A PropertyType that defines a property with a primitive value, i.e. a value that can be represented as a single String.
			 if ( isNilled ) {
				    TypedObjectNode node = null;
	                dgProp = new org.deegree.feature.property.GenericProperty( dgPT, dgPropName, null);
			 }else {
				 dgProp = new org.deegree.feature.property.SimpleProperty((SimplePropertyType) dgPT, (String)gtProp.getValue(), ((SimplePropertyType)dgPT).getPrimitiveType());
			 }
			
		}else if (dgPT instanceof org.deegree.feature.types.property.GeometryPropertyType){
			
			//TODO handle case with GeometryReference<Geometry>
			//convert gt Geometry attribute to deegree Geometry
			org.deegree.geometry.Geometry dgGeometry = createDgGeometry((GeometryAttribute)gtProp);
			dgProp = new GenericProperty( dgPT, dgPropName, dgGeometry );
			
			
		}else if (dgPT instanceof org.deegree.feature.types.property.FeaturePropertyType){
			//TODO implement it if needed
			
		}else if (dgPT instanceof org.deegree.feature.types.property.CustomPropertyType){
			//TODO implement it if needed
		
		}else if (dgPT instanceof org.deegree.feature.types.property.CodePropertyType){
			//TODO implement it if needed
			
		}else if (dgPT instanceof org.deegree.feature.types.property.EnvelopePropertyType){
			//TODO clear how to retrieve envelope data
		}
		return dgProp;
	}

    private static Geometry createDgGeometry(GeometryAttribute gtProp) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 
	 * @param geotools-based propertyType
	 * @return deegree-based PropertyType
	 */
    private static org.deegree.feature.types.property.PropertyType createDgPt(
			PropertyDescriptor gtPD) {
		org.deegree.feature.types.property.PropertyType dgPT = null;
		//TODO define a better way for the value representation
		//think about List<PropertyType>substitutions
		PropertyType gtPT = gtPD.getType();
		//define commons attributes
		QName dgName = new QName(gtPD.getName().getNamespaceURI(), gtPT.getName().getLocalPart());
		QName dgFTName = new QName(gtPT.getName().getNamespaceURI(), gtPT.getName().getLocalPart());
		int minOccurs = gtPD.getMinOccurs();
		int maxOccurs = gtPD.getMaxOccurs();
		boolean isAbstract = gtPT.isAbstract();
		if (gtPT instanceof FeatureType || gtPT instanceof SimpleFeatureType){
			//TODO think about complex features
			//create Feature Property Type
			
			dgPT = new org.deegree.feature.types.property.FeaturePropertyType(dgName,minOccurs, maxOccurs, dgFTName, isAbstract, null, ValueRepresentation.BOTH ); 
		}else if (gtPT instanceof GeometryAttribute){
			org.deegree.feature.types.property.GeometryPropertyType.GeometryType  dgGeomType = createGeometryType(((GeometryAttribute)gtPT).getDescriptor());
			org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension dgCoordDim = createCoordDim(((GeometryAttribute)gtPT).getDescriptor());
			dgPT = new org.deegree.feature.types.property.GeometryPropertyType(dgName, minOccurs, maxOccurs, dgGeomType, dgCoordDim, isAbstract, null, ValueRepresentation.BOTH );
				
			}
		
		return dgPT;
	}


	private static CoordinateDimension createCoordDim(
			GeometryDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}


	private static GeometryType createGeometryType(GeometryDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
     * 
     * @param geotools-based FeatureType 
     * @return deegree-based FeatureType
     * 
     */
	private static  org.deegree.feature.types.GenericFeatureType createDgFt(FeatureType gtFT) {
		 //1.0 QName
		  Name gtFTName = gtFT.getName();
		  QName ftName = new QName(gtFTName.getNamespaceURI(), gtFTName.getLocalPart());
		  List<org.deegree.feature.types.property.PropertyType> propDecls = new ArrayList<org.deegree.feature.types.property.PropertyType>();
		  //1.1 List<PropertyType>
		  for (PropertyDescriptor gtPD :gtFT.getDescriptors()){
			 // create deegree PropertyType
			 org.deegree.feature.types.property.PropertyType dgPT = createDgPt(gtPD);
			 propDecls.add(dgPT);
		  }
		  //1.2 boolean isAbstract
		  boolean isAbstract = gtFT.isAbstract();
		  
		  org.deegree.feature.types.GenericFeatureType dgFT = new org.deegree.feature.types.GenericFeatureType(ftName, propDecls, isAbstract);
		return dgFT;
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
