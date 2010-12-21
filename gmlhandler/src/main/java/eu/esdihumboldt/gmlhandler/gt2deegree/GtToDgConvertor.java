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
package eu.esdihumboldt.gmlhandler.gt2deegree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.deegree.commons.tom.TypedObjectNode;
import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.cs.CRS;
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
import org.deegree.geometry.standard.primitive.DefaultPolygon;
import org.deegree.gml.GMLVersion;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.gmlhandler.deegree.InternalFeature;


/**
 * This class contains methods to create Deegree3 Structures like Feature,
 * FeatureCollection and so using GeoSpatial data retrieved from the according
 * geotools Objects.
 * 
 * @author Jan Jezek, Anna Pitaev, Simon Templer
 * @partner ?, 04 / Logica, 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class GtToDgConvertor {
	
	private final TypeIndex types;
	
	/**
	 * Create a new converter
	 * 
	 * @param types the type index
	 */
	public GtToDgConvertor(TypeIndex types) {
		super();
		this.types = types;
	}

	/**
	 * 
	 * @param geotools
	 *            -based Feature Collection fc
	 * @return deegree-based Feature Collection
	 */

	public org.deegree.feature.FeatureCollection convertGtToDg(
			org.geotools.feature.FeatureCollection fc) {

		Collection<org.deegree.feature.Feature> dgFeatures = new ArrayList();
		org.deegree.feature.Feature dgFeature = null;
		List<org.deegree.feature.property.Property> dgProperties = null;
		List<org.deegree.feature.types.property.PropertyType> dgPropertyTypes = null;
		for (FeatureIterator i = fc.features(); i.hasNext();) {
			dgPropertyTypes = new ArrayList<org.deegree.feature.types.property.PropertyType>();
			Feature gtFeature = i.next();
			dgFeature = createDgFeature(gtFeature);
			dgFeatures.add(dgFeature);

		}
		org.deegree.feature.FeatureCollection dfFC = new GenericFeatureCollection(
				fc.getID(), dgFeatures);
		return dfFC;
	}

	/**
	 * 
	 * @param geotools
	 *            -based Feature
	 * @return deegree-based Feature
	 * 
	 */
	private static org.deegree.feature.Feature createDgFeature(Feature gtFeature) {
		
		// test for the CRS
		CoordinateReferenceSystem crs = null;
		org.opengis.feature.GeometryAttribute gp = gtFeature.getDefaultGeometryProperty();
		crs = gp.getDescriptor().getCoordinateReferenceSystem();
		if (crs == null){
			//next try - user data of value
			Object value = gp.getValue();
			if (value instanceof com.vividsolutions.jts.geom.Geometry){
				Object userData = ((com.vividsolutions.jts.geom.Geometry)value).getUserData();
				if (userData instanceof CoordinateReferenceSystem){
					crs = (CoordinateReferenceSystem) userData;
				}
			}
		}
//		System.out.println(crs);
//		System.out.println(gtFeature.getDefaultGeometryProperty().getType());
		FeatureType gtFT = gtFeature.getType();
		// convert gtFT to gtFT

		// 1. GenericFeatureType
		GenericFeatureType dgFT = createDgFt(gtFT, null);

		// 2. Feature id
		String fid = gtFeature.getIdentifier().getID();
		// 3. List<Property>
		List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
		Iterator<Property> gtPropsIter = gtFeature.getProperties().iterator();
	
		while (gtPropsIter.hasNext()) {
			Property gtProp = gtPropsIter.next();
			org.deegree.feature.property.Property dgProp = createDgProp(gtProp, crs, gp);
			dgProps.add(dgProp);
		}
		// 4. GMLVersion
		org.deegree.feature.Feature dgFeature = dgFT.newFeature(fid, dgProps,
				GMLVersion.GML_32);
		return dgFeature;

	}

	/**
	 * 
	 * @param gp 
	 * @param geotools
	 *            -based Property
	 * @return deegree-based Property
	 * 
	 */
	private static org.deegree.feature.property.Property createDgProp(
			Property gtProp, CoordinateReferenceSystem crs, GeometryAttribute gp) {
		// 1. declare a Property instance: make decision about implementing
		// class after analyze of the PropertyType
		org.deegree.feature.property.Property dgProp = null;
		// 2. define isNilled isNilled
		boolean isNilled = gtProp.isNillable();
		// 3. define property name
		QName dgPropName = new QName(gtProp.getName().getNamespaceURI(), gtProp
				.getName().getLocalPart());

		// create deegree based PropertyType from the geotools Objecy
		// if prop has xml atttribures map it to the CustomPropertyType
		boolean hasXMLAttrs = gtProp.getUserData().get("XmlAttributes") != null;
		org.deegree.feature.types.property.PropertyType dgPT = createDgPt(
				gtProp.getDescriptor(), hasXMLAttrs);

		if (dgPT instanceof org.deegree.feature.types.property.SimplePropertyType) {
			// A PropertyType that defines a property with a primitive value,
			// i.e. a value that can be represented as a single String.
			if (isNilled && gtProp.getValue() == null) {
				TypedObjectNode node = null;
				dgProp = new org.deegree.feature.property.GenericProperty(dgPT,
						dgPropName, null);
			} else {
				dgProp = new org.deegree.feature.property.SimpleProperty(
						(SimplePropertyType) dgPT, gtProp.getValue().toString(),
						((SimplePropertyType) dgPT).getPrimitiveType());
			}

		} else if (dgPT instanceof org.deegree.feature.types.property.GeometryPropertyType) {

			// TODO handle case with GeometryReference<Geometry>
			// convert gt Geometry attribute to deegree Geometry
			org.deegree.geometry.Geometry dgGeometry = createDgGeometry(gtProp, crs, gp);
			dgProp = new GenericProperty(dgPT, dgPropName, dgGeometry);

		} else if (dgPT instanceof org.deegree.feature.types.property.FeaturePropertyType) {
			// we support inline Features mapping only
			// if (gtProp instanceof SimpleFeature ){

			// create deegree generic feature based on gtProp
			GenericFeatureType ft = createDgFt(((Attribute) gtProp)
					.getType(), dgPropName);
			//org.deegree.feature.Feature featureProp = null;
			
	    	org.deegree.feature.Feature featureProp = createDgFeature((Attribute) gtProp, ft);
	    	dgProp = new org.deegree.feature.property.GenericProperty(dgPT,
					dgPropName, new InternalFeature(featureProp));
			//dgProp = createDgFeatureProperty((Attribute)gtProp, ft);
			/*
			 * //TODO find a nicer way to create fid String fid =
			 * java.util.UUID.randomUUID().toString(); GMLVersion version =
			 * GMLVersion.GML_32; List<org.deegree.feature.property.Property>
			 * properties = new
			 * ArrayList<org.deegree.feature.property.Property>();
			 * org.deegree.feature.property.Property property; //create Property
			 * from the gt Attributes List<Object> attrs =
			 * ((SimpleFeature)gtProp).getAttributes(); for (Object attr :
			 * attrs){ property = createProperty(attr); //TODOuse geotools
			 * Feature Attribute Type //properties.add(property,
			 * ((SimpleFeature)gtProp).getAttribute(attr.)); } GenericFeature
			 * dgSubProperty = new GenericFeature(ft, fid, properties, version);
			 */
			//dgProp = new GenericProperty(dgPT, dgPropName, featureProp);
			// }
		} else if (dgPT instanceof org.deegree.feature.types.property.CustomPropertyType) {
			// TODO implement if needed

		} else if (dgPT instanceof org.deegree.feature.types.property.CodePropertyType) {
			// TODO implement it if needed

		} else if (dgPT instanceof org.deegree.feature.types.property.EnvelopePropertyType) {
			// TODO clear how to retrieve envelope data
		}
		return dgProp;
	}

//	private static org.deegree.feature.property.Property createDgFeatureProperty(
//			Attribute gtProp, GenericFeatureType ft) {
//		List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
//		Collection attributesCollestion = ((Collection)gtProp.getValue());
//		if (attributesCollestion != null){
//		Iterator<Property> gtPropsIter = attributesCollestion.iterator();
//		org.deegree.feature.property.Property dgProp;
//		while (gtPropsIter.hasNext()) {
//			 gtProp = gtPropsIter.next();
//			 dgProp = createDgProp(gtProp, null, null);
//			dgProps.add(dgProp);
//		}
//		return dgProp;
////	}

	/**
	 * 
	 * Generates a feature in case the geotools attribute contains a collection of properties
	 * @param complexAttribute
	 * @param ft
	 * @return
	 */
	private static org.deegree.feature.Feature createDgFeature(Attribute complexAttribute, GenericFeatureType ft) {
		
		// 2. Feature id
		String fid = java.util.UUID.randomUUID().toString();
		
		// 3. List<Property>
		List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
		Collection attributesCollestion = ((Collection)complexAttribute.getValue());
		if (attributesCollestion != null){
		Iterator<Property> gtPropsIter = attributesCollestion.iterator();
	
		while (gtPropsIter.hasNext()) {
			//Find Complex attribute
			Property gtProp = gtPropsIter.next();
			if (gtProp instanceof ComplexAttribute){
				//retrieve a list of properties for the complex Attribute
				Iterator<Property> simplePropIter = ((ComplexAttribute) gtProp).getProperties().iterator();
				while(simplePropIter.hasNext()){
					Property gtSimpleProp = simplePropIter.next();
					org.deegree.feature.property.Property dgProp = createDgProp(gtSimpleProp, null, null);
					dgProps.add(dgProp);
				}
			}
			
		}
		}
		// 4. GMLVersion
		org.deegree.feature.Feature dgFeature = ft.newFeature(fid, dgProps,
				GMLVersion.GML_32);
		return dgFeature;
	}

	/**
	 * 
	 * <p>
	 * This method provides mapping for the following geometries:
	 * <ul>
	 * <li>POINT</li>
	 * <li>MULTIPOINT</li>
	 * <li>POLIGON</li>
	 * <li>MULTIPOLIGON</li>
	 * <li>LINESTRING</li>
	 * <li>MULTILINESTRING</li>
	 * </ul>
	 * 
	 * @param gtProp
	 *            GeometryAttribute
	 * @param gp 
	 * @return Geometry
	 */
	private static Geometry createDgGeometry(Property gtProp, CoordinateReferenceSystem crs, GeometryAttribute gp) {
		
		Geometry dgGeometry = null;
		//String geometryName = gtProp.getDescriptor().getType().getBinding()
		//		.getSimpleName();
		//if (geometryName.equals("Geometry")){
		//	geometryName = gp.getDescriptor().getType().getBinding().getSimpleName();
			//if (geometryName.equals("Geometry")){
		String geometryName;
		Geometries geomType;
		Object jtsGeom = gp.getValue();
		if (jtsGeom != null && jtsGeom instanceof com.vividsolutions.jts.geom.Geometry ){
			geometryName = jtsGeom.getClass().getSimpleName();
			geomType = Geometries.getForBinding((Class<? extends com.vividsolutions.jts.geom.Geometry>) jtsGeom.getClass());
		}
		else {
			return null;
		}
			
		gtProp = gp;
		
			
		// we provide mapping for
		/*Geometries geomType = Geometries
				.getForBinding((Class<? extends com.vividsolutions.jts.geom.Geometry>) gtProp
						.getDescriptor().getType().getBinding());
		// map common attributtes
*/		// 1. id
		// TODO test it
		String id = UUID.randomUUID().toString();
		// 2.TODO figure out CRS
		 /*CoordinateReferenceSystem crs = ((org.opengis.feature.type.GeometryType)gtProp.getType()).getCoordinateReferenceSystem();*/
		 org.deegree.cs.CRS dgCRS = createCRS(crs);
		
		// 3. precision model
		// TODO find a nicer way to define it
		org.deegree.geometry.precision.PrecisionModel pm = org.deegree.geometry.precision.PrecisionModel.DEFAULT_PRECISION_MODEL;

		switch (geomType) {
		case POLYGON:
			Polygon gtPoligon = (Polygon) gtProp.getValue();
			Ring exteriorRing = createRing(gtPoligon.getExteriorRing());
			int numOfInterRings = gtPoligon.getNumInteriorRing();
			List<Ring> interiorRings = new ArrayList<Ring>(numOfInterRings);
			Ring interiorRing = null;
			for (int i = 0; i < numOfInterRings; i++) {
				interiorRing = createRing(gtPoligon.getInteriorRingN(i));
				interiorRings.add(interiorRing);

			}
			dgGeometry = new DefaultPolygon(id, dgCRS, pm, exteriorRing,
					interiorRings);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtPoligon, dgCRS);
			break;
		case MULTIPOLYGON:
			MultiPolygon gtMultiPolygon = (MultiPolygon) gtProp.getValue();
			int numOfPolygs = gtMultiPolygon.getNumGeometries();
			List<org.deegree.geometry.primitive.Polygon> dgPolygons = new ArrayList<org.deegree.geometry.primitive.Polygon>(
					numOfPolygs);
			org.deegree.geometry.primitive.Polygon dgPolygon;
			for (int i = 0; i < numOfPolygs; i++) {
				dgPolygon = createDefaultPolygon((Polygon) gtMultiPolygon
						.getGeometryN(i));
				dgPolygons.add(dgPolygon);
			}
			dgGeometry = new DefaultMultiPolygon(id, dgCRS, pm, dgPolygons);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtMultiPolygon, dgCRS);
			break;

		case LINESTRING:
			LineString gtLineString = (LineString) gtProp.getValue();
			Points dgLineStringPoints = createDGPoints(gtLineString
					.getCoordinates());
			dgGeometry = new org.deegree.geometry.standard.primitive.DefaultLineString(
					id, dgCRS, pm, dgLineStringPoints);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtLineString, dgCRS);
			break;
		case MULTILINESTRING:
			MultiLineString gtMultiLineString = (MultiLineString) gtProp.getValue();
			int numOfLineStrings = gtMultiLineString.getNumGeometries();
			List<org.deegree.geometry.primitive.LineString> dgLineStrings = new ArrayList<org.deegree.geometry.primitive.LineString>(
					numOfLineStrings);
			org.deegree.geometry.primitive.LineString dgLineString;
			for (int i = 0; i < numOfLineStrings; i++) {
				dgLineString = createLineString(gtMultiLineString
						.getGeometryN(i));
				dgLineStrings.add(dgLineString);
			}
			dgGeometry = new org.deegree.geometry.standard.multi.DefaultMultiLineString(
					id, dgCRS, pm, dgLineStrings);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtMultiLineString, dgCRS);
			break;

		case POINT:
			Point gtPoint = (Point) (gtProp.getValue());

			double[] dgCoordinates = createCoordinates(gtPoint.getCoordinates());
			dgGeometry = new org.deegree.geometry.standard.primitive.DefaultPoint(
					id, dgCRS, pm, dgCoordinates);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtPoint, dgCRS);
			break;
		case MULTIPOINT:
			MultiPoint gtMultiPoint = (MultiPoint) gtProp.getValue();
			int numOfPoints = gtMultiPoint.getNumGeometries();
			List<org.deegree.geometry.primitive.Point> dgPoints = new ArrayList<org.deegree.geometry.primitive.Point>(
					numOfPoints);
			org.deegree.geometry.primitive.Point dgPoint;
			for (int i = 0; i < numOfPoints; i++) {
				dgPoint = createPoint(gtMultiPoint.getGeometryN(i));
				dgPoints.add(dgPoint);

			}
			dgGeometry = new org.deegree.geometry.standard.multi.DefaultMultiPoint(
					id, dgCRS, pm, dgPoints);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtMultiPoint, dgCRS);
			

			break;
			
		default:
			
			 


			break;
		}
		//set id
		dgGeometry.setId(id);
		//set srs
		dgGeometry.setCoordinateSystem(dgCRS);
		//set precision model
		dgGeometry.setPrecision(pm);
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

	private static org.deegree.geometry.primitive.Polygon createDefaultPolygon(
			Polygon geometryN) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Ring createRing(LineString exteriorRing) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param geotools coordinateReferenceSystem
	 * @return deegree-based Coordinate Reference System
	 */
	private static CRS createCRS(
			CoordinateReferenceSystem coordinateReferenceSystem) {
		CRS dgCrs = new CRS(org.geotools.referencing.CRS.toSRS(coordinateReferenceSystem));
		//if crs null set default crs
		if (dgCrs.getName() == null){
			dgCrs = CRS.EPSG_4326;
		}
		return dgCrs;
	}

	/**
	 * 
	 * @param hasXMLAttrs
	 * @param geotools
	 *            -based propertyType
	 * @return deegree-based PropertyType
	 */
	private static org.deegree.feature.types.property.PropertyType createDgPt(
			PropertyDescriptor gtPD, boolean hasXMLAttrs) {

		org.deegree.feature.types.property.PropertyType dgPT = null;
		// TODO define a better way for the value representation
		List<org.deegree.feature.types.property.PropertyType> substitutions = new ArrayList<org.deegree.feature.types.property.PropertyType>();
		PropertyType gtPT = gtPD.getType();
		Property prop = null;
		String namespace = gtPD.getName().getNamespaceURI();

		// define commons attributes
		QName dgName = new QName(gtPD.getName().getNamespaceURI(), gtPD
				.getName().getLocalPart());
		QName dgFTName = new QName(gtPT.getName().getNamespaceURI(), gtPT
				.getName().getLocalPart());
		int minOccurs = gtPD.getMinOccurs();
		int maxOccurs = gtPD.getMaxOccurs();
		boolean isAbstract = gtPT.isAbstract();
		boolean isNillable = gtPD.isNillable();
		if (gtPT instanceof org.opengis.feature.type.GeometryType) {
			org.deegree.feature.types.property.GeometryPropertyType.GeometryType dgGeomType = createGeometryType((GeometryDescriptor) gtPD);
			org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension dgCoordDim = createCoordDim((GeometryDescriptor) gtPD);
			dgPT = new org.deegree.feature.types.property.GeometryPropertyType(
					dgName, minOccurs, maxOccurs, isAbstract, isNillable, 
					substitutions, dgGeomType, dgCoordDim, ValueRepresentation.BOTH);
		} else if (gtPT instanceof org.opengis.feature.type.AttributeType
				&& !(gtPT instanceof org.opengis.feature.type.ComplexType)) {
			// TODO find a nicer way to define this binding
			PrimitiveType propPrimType = PrimitiveType
					.determinePrimitiveType(gtPT.getBinding().getName());
			dgPT = new org.deegree.feature.types.property.SimplePropertyType(
					dgName, minOccurs, maxOccurs, propPrimType, isAbstract, 
					isNillable, substitutions);
		}else if (gtPT instanceof org.opengis.feature.type.AttributeType
				&& gtPT instanceof org.opengis.feature.type.FeatureType){
			
			//dgPT = new org.deegree.feature.types.property.CustomPropertyType(dgName, maxOccurs, maxOccurs, null, isAbstract, substitutions); 
			dgPT = new org.deegree.feature.types.property.FeaturePropertyType(
					dgName, minOccurs, maxOccurs, isAbstract, isNillable,
					substitutions, dgFTName, ValueRepresentation.BOTH);
			
	}else {
			if (hasXMLAttrs) {

				// create CustomPropertyType to handle xml attrs
				// TODO XSTypeDefinition we need
				// org.apache.xerces.xs.XSElementDeclaration xs = null;
				// this.appSchema.getXSModel().getAbstractCurveSegmentElementDeclaration().getEnclosingCTDefinition().
				// List<org.apache.xerces.xs.XSElementDeclaration> xsDecl =
				// this.appSchema.getXSModel().
				// 2. Qname
				// 3. namespace - only element declarations in this namespace
				// are returned, set to null for all namespaces
				// 4. transitive - if true, also substitutions for substitutions
				// (and so one) are included
				// 5. onlyConcrete - if true, only concrete (non-abstract)
				// declarations are returned
				// dgPT = new
				// org.deegree.feature.types.property.CustomPropertyType(dgFTName,
				// maxOccurs, maxOccurs, null, isAbstract, substitutions);

			} else {
				// create Feature Property Type
				dgPT = new org.deegree.feature.types.property.FeaturePropertyType(
						dgName, minOccurs, maxOccurs, isAbstract, isNillable,
						substitutions, dgFTName, ValueRepresentation.BOTH);
			}
		}

		return dgPT;
	}

	/**
	 * 
	 * @param geotools
	 *            Geometry Descriptor
	 * @return CoordinateDimension
	 */
	private static CoordinateDimension createCoordDim(
			GeometryDescriptor descriptor) {
		if (descriptor.getCoordinateReferenceSystem() != null
				&& descriptor.getCoordinateReferenceSystem()
						.getCoordinateSystem() != null) {
			if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem()
					.getDimension() == 2)
				return CoordinateDimension.DIM_2;
			if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem()
					.getDimension() == 3)
				return CoordinateDimension.DIM_3;
		}
		return CoordinateDimension.DIM_2_OR_3;

	}

	/**
	 * 
	 * @param geotools
	 *            Geometry Descriptor
	 * @return Geometry Type
	 */
	private static GeometryType createGeometryType(GeometryDescriptor descriptor) {
		// 1. retrieve the Geometry type name
		String geometry = descriptor.getType().getBinding().getSimpleName();
		// 2. assign a string value to the GeometryType
		return GeometryType.fromGMLTypeName(geometry);
	}

	/**
	 * 
	 * @param geotools
	 *            -based FeatureType
	 * @return deegree-based FeatureType
	 * 
	 */
	private static org.deegree.feature.types.GenericFeatureType createDgFt(
			AttributeType attributeType, QName dgName) {
		QName ftName = null;
		if (dgName == null){
		// 1.0 QName
		Name gtFTName = attributeType.getName();
        ftName = new QName(gtFTName.getNamespaceURI(),
		gtFTName.getLocalPart());
		}else{
			ftName = dgName;
		}
		List<org.deegree.feature.types.property.PropertyType> propDecls = new ArrayList<org.deegree.feature.types.property.PropertyType>();
		// 1.1 List<PropertyType>
		if (attributeType instanceof ComplexType){
		for (PropertyDescriptor gtPD : ((ComplexType)attributeType).getDescriptors()) {
			// create deegree PropertyType
			org.deegree.feature.types.property.PropertyType dgPT = createDgPt(
					gtPD, false);
			propDecls.add(dgPT);
		}
		}
		// 1.2 boolean isAbstract
		boolean isAbstract = attributeType.isAbstract();

		org.deegree.feature.types.GenericFeatureType dgFT = new org.deegree.feature.types.GenericFeatureType(
				ftName, propDecls, isAbstract);
		return dgFT;
	}

}
