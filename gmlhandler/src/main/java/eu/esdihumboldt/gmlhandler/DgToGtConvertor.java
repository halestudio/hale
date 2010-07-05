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
import java.util.Random;

import javax.xml.namespace.QName;

import org.deegree.commons.tom.TypedObjectNode;
import org.deegree.cs.CRS;
import org.deegree.feature.GenericFeature;
import org.deegree.feature.GenericFeatureCollection;
import org.deegree.feature.property.GenericProperty;
import org.deegree.feature.types.GenericFeatureType;
import org.deegree.feature.types.property.SimplePropertyType;
import org.deegree.feature.types.property.ValueRepresentation;
import org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension;
import org.deegree.feature.types.property.GeometryPropertyType.GeometryType;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.points.Points;
import org.deegree.geometry.primitive.Ring;
import org.deegree.geometry.standard.multi.DefaultMultiPolygon;
import org.deegree.geometry.standard.primitive.DefaultLineString;
import org.deegree.geometry.standard.primitive.DefaultPolygon;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.geometry.refs.GeometryReference;
import org.geotools.data.DataUtilities;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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
			//we support inline Features mapping only
			if (gtProp instanceof SimpleFeature ){
				
				//create deegree generic feature based on gtProp 
				GenericFeatureType ft = createDgFt(((SimpleFeature) gtProp).getFeatureType());
				//TODO find a nicer way to create fid
				String fid = java.util.UUID.randomUUID().toString();
				GMLVersion version = GMLVersion.GML_32;
				List<org.deegree.feature.property.Property> properties = new ArrayList<org.deegree.feature.property.Property>();
				org.deegree.feature.property.Property property;
				//create Property from the gt Attributes
				List<Object> attrs = ((SimpleFeature)gtProp).getAttributes();
				for (Object attr : attrs){
					property = createProperty(attr);
					//TODOuse geotools Feature Attribute Type
					//properties.add(property, ((SimpleFeature)gtProp).getAttribute(attr.));
				}
				GenericFeature dgSubProperty = new GenericFeature(ft, fid, properties, version);
			    dgProp = new GenericProperty(dgPT,dgPropName,dgSubProperty );
			}
		}else if (dgPT instanceof org.deegree.feature.types.property.CustomPropertyType){
			//TODO implement if needed
		
		}else if (dgPT instanceof org.deegree.feature.types.property.CodePropertyType){
			//TODO implement it if needed
			
		}else if (dgPT instanceof org.deegree.feature.types.property.EnvelopePropertyType){
			//TODO clear how to retrieve envelope data
		}
		return dgProp;
	}

    /**
     * 
     * @param Object Attribute of  geotools Feature 
     * @return  deegree Property 
     */
	private static org.deegree.feature.property.Property createProperty(
			Object attr) {
		// Map Primitive Types
		// Map TypedObjectNode
		
		return null;
	}


	/**
     * 
     * <p>This method provides
     *  mapping for the following geometries:
     * <ul>
     * <li>POINT</li>
     * <li>MULTIPOINT</li>
     * <li>POLIGON</li>
     * <li>MULTIPOLIGON</li>
     * <li>LINESTRING</li>
     * <li>MULTILINESTRING</li>
     * </ul>  
     * 
     * @param gtProp GeometryAttribute
     * @return Geometry
     */
	private static Geometry createDgGeometry(GeometryAttribute gtProp) {
		//TODO provide junit testcasefor this method --> high priority
		Geometry dgGeometry = null;
		String geometryName = gtProp.getDescriptor().getType().getBinding().getSimpleName();
		// we provide mapping for 
		Geometries geomType = Geometries.getForBinding((Class<? extends com.vividsolutions.jts.geom.Geometry>) gtProp.getDescriptor().getType().getBinding());
		//map common attributtes
		//1. id 
		//TODO test it
		String id = gtProp.getIdentifier().getID().toString();
		//2.crs
		org.deegree.cs.CRS dgCRS = createCRS(gtProp.getType().getCoordinateReferenceSystem());
		//3. precision model 
		//TODO find a nicer way to define it
		org.deegree.geometry.precision.PrecisionModel pm = org.deegree.geometry.precision.PrecisionModel.DEFAULT_PRECISION_MODEL;
		
         		
		switch (geomType) {
		     case POLYGON:
		    	 Polygon gtPoligon = (Polygon) gtProp.getDescriptor().getType(); 
		    	 Ring exteriorRing = createRing(gtPoligon.getExteriorRing());
		    	 int numOfInterRings = gtPoligon.getNumInteriorRing();
		    	 List<Ring> interiorRings = new ArrayList<Ring>(numOfInterRings);
		    	 Ring interiorRing = null;
		    	 for (int i = 0; i< numOfInterRings; i++ ){
		    		 interiorRing = createRing(gtPoligon.getInteriorRingN(i));
		    		 interiorRings.add(interiorRing);
		    		 
		    	 }
		    	 dgGeometry = new DefaultPolygon (id, dgCRS, pm, exteriorRing,interiorRings );
		    	 dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry)dgGeometry).createFromJTS(gtPoligon);
		    	 break;
		     case MULTIPOLYGON:
		    	 MultiPolygon gtMultiPolygon = (MultiPolygon) gtProp.getDescriptor().getType();
		    	 int numOfPolygs = gtMultiPolygon.getNumGeometries();
		    	 List<org.deegree.geometry.primitive.Polygon> dgPolygons = new ArrayList<org.deegree.geometry.primitive.Polygon>(numOfPolygs);
		    	 org.deegree.geometry.primitive.Polygon dgPolygon;
		    	 for (int i = 0; i< numOfPolygs; i++){
		    		 dgPolygon = createDefaultPolygon((Polygon)gtMultiPolygon.getGeometryN(i));
		    		 dgPolygons.add(dgPolygon);
		    	}
		    	 dgGeometry = new DefaultMultiPolygon(id, dgCRS, pm, dgPolygons);
		    	 dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry)dgGeometry).createFromJTS(gtMultiPolygon);
		         break;

		     case LINESTRING:
		    	 LineString gtLineString = (LineString) gtProp.getDescriptor().getType();
		    	 Points dgLineStringPoints = createDGPoints(gtLineString.getCoordinates());
		    	 dgGeometry = new org.deegree.geometry.standard.primitive.DefaultLineString(id , dgCRS, pm, dgLineStringPoints );
		    	 dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry)dgGeometry).createFromJTS(gtLineString);
		    	 break;
		     case MULTILINESTRING:
		         MultiLineString gtMultiLineString = (MultiLineString) gtProp.getDescriptor().getType();
		         int numOfLineStrings = gtMultiLineString.getNumGeometries();
		         List<org.deegree.geometry.primitive.LineString> dgLineStrings = new ArrayList<org.deegree.geometry.primitive.LineString>(numOfLineStrings);
		         org.deegree.geometry.primitive.LineString dgLineString;
		         for (int i=0; i< numOfLineStrings; i++){
		        	 dgLineString = createLineString(gtMultiLineString.getGeometryN(i));
		        	 dgLineStrings.add(dgLineString);
		         }
		         dgGeometry = new org.deegree.geometry.standard.multi.DefaultMultiLineString (id, dgCRS, pm, dgLineStrings);
		         dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry)dgGeometry).createFromJTS(gtMultiLineString);
		         break;

		     case POINT:
		    	 Point gtPoint = (Point)gtProp.getDescriptor().getType();
		    	 double [] dgCoordinates = createCoordinates(gtPoint.getCoordinates());
		    	 dgGeometry = new org.deegree.geometry.standard.primitive.DefaultPoint(id, dgCRS, pm, dgCoordinates);
		    	 dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry)dgGeometry).createFromJTS(gtPoint);
		    	 break;
		     case MULTIPOINT:
		        MultiPoint gtMultiPoint = (MultiPoint)gtProp.getDescriptor().getType();
		        int numOfPoints = gtMultiPoint.getNumGeometries();
		        List<org.deegree.geometry.primitive.Point> dgPoints = new ArrayList<org.deegree.geometry.primitive.Point>(numOfPoints); 
		        org.deegree.geometry.primitive.Point dgPoint;
		        for (int i = 0; i < numOfPoints; i++){
		        	dgPoint = createPoint(gtMultiPoint.getGeometryN(i));
		        	dgPoints.add(dgPoint);
		        	
		        }
		        dgGeometry = new org.deegree.geometry.standard.multi.DefaultMultiPoint(id, dgCRS, pm, dgPoints);
		        dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry)dgGeometry).createFromJTS(gtMultiPoint);

		         break;

		     default:
		    	
		         break;
		 }
		 
		
		return dgGeometry;
	}


	private static org.deegree.geometry.primitive.Point createPoint(
			com.vividsolutions.jts.geom.Geometry geometryN) {
		// TODO Auto-generated method stub
		return null;
	}


	private static double[] createCoordinates(Coordinate[] coordinates) {
		// TODO Auto-generated method stub
		return null;
	}


	private static org.deegree.geometry.primitive.LineString createLineString(
			com.vividsolutions.jts.geom.Geometry geometryN) {
		// TODO Auto-generated method stub
		return null;
	}


	private static Points createDGPoints(Coordinate[] coordinates) {
		// TODO Auto-generated method stub
		return null;
	}


	private static org.deegree.geometry.primitive.Polygon createDefaultPolygon(Polygon geometryN) {
		// TODO Auto-generated method stub
		return null;
	}


	private static Ring createRing(LineString exteriorRing) {
		// TODO Auto-generated method stub
		return null;
	}


	private static CRS createCRS(
			CoordinateReferenceSystem coordinateReferenceSystem) {
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


	/**
	 * 
	 * @param geotools Geometry Descriptor
	 * @return CoordinateDimension
	 */
    private static CoordinateDimension createCoordDim(
			GeometryDescriptor descriptor) {
		if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() == 2) return CoordinateDimension.DIM_2;
		else if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() == 3) return CoordinateDimension.DIM_3;
		else return CoordinateDimension.DIM_2_OR_3;
			
		
	}

    /**
     * 
     * @param geotools Geometry Descriptor
     * @return Geometry Type
     */
	private static GeometryType createGeometryType(GeometryDescriptor descriptor) {
		//1. retrieve the Geometry type name
		String geometry = descriptor.getType().getBinding().getSimpleName();
		//2. assign a string value to the GeometryType
		return GeometryType.fromString(geometry);
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
