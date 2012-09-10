/**
 * 
 */
package eu.esdihumboldt.specification.mediator.constraints;

import java.util.Set;

/**
 * An IdentifierConstraint is used to restrict an WFS request to certain
 * instances of Features
 * 
 * @author Bernd Schneiders, Logica
 * 
 */
public interface IdentifierConstraint extends Constraint {
	public Set<String> getFeatureIDs();
}
/**
 * SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=InWaterA_1M,BuiltUpA_1M
 * &
 * 
 * FILTER=(<Filter><Within>
 * 
 * <PropertyName>InWaterA_1M/wkbGeom<PropertyName>
 * 
 * <gml:Envelope><gml:lowerCorner>10 10</gml:lowerCorner><gml:upperCorner>20
 * 20</gml:upperCorner></gml:Envelope>
 * 
 * </Within></Filter>)(<Filter><Within><PropertyName>BuiltUpA_1M/wkbGeom<
 * PropertyName><gml:Envelope><gml:lowerCorner>10
 * 10</gml:lowerCorner><gml:upperCorner>20
 * 20</gml:upperCorner></gml:Envelope></Within></Filter>)
 * 
 * 
 * 
 * SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=InWaterA_1M,BuiltUpA_1M
 * &FILTER=<Filter><DWithin><PropertyName>Geometry</PropertyName><gml:Point><gml
 * :coordinates>2587598.000000,404010.531250</gml:coordinates></gml:Point><
 * Distance units='m'>1000</Distance></DWithin></Filter>
 * 
 * SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=InWaterA_1M%2
 * CBuiltUpA_1M
 * &FILTER=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns
 * :gml=%22http://www.opengis.net/gml%22%
 * 3E%3CDWithin%3E%3CPropertyName%3EGeometry%3C/PropertyName%3E%3Cgml:Point%3E%3Cgml:coordinates%3E2587598.000000%2C404010.531250%3C/gml:coordinates%3E%3C/gml:Point%3E%3CDistance%20units='m'%3E1000%3C/Distance%3E%3C/DWithin%3E%3C/Filter%3
 * E
 * 
 * FILTER= <Filter xmlns="http://www.opengis.net/ogc"
 * xmlns:gml="http://www.opengis.net/gml"> <DWithin>
 * <PropertyName>Geometry</PropertyName> <gml:Point>
 * <gml:coordinates>2587598.000000,404010.531250</gml:coordinates> </gml:Point>
 * <Distance units='m'>1000</Distance> </DWithin> </Filter>
 * 
 * 
 * FILTER= <Filter xmlns="http://www.opengis.net/ogc"
 * xmlns:gml="http://www.opengis.net/gml"> <Intersects>
 * <PropertyName>the_geom</PropertyName> <gml:Point srsName="EPSG:4326">
 * <gml:coordinates>-74.817265,40.5296504</gml:coordinates> </gml:Point>
 * </Intersects> </Filter>
 * 
 * 
 * FILTER=<Filter xmlns="http://www.opengis.net/ogc"
 * xmlns:gml="http://www.opengis.net/gml"
 * ><DWithin><PropertyName>Geometry</PropertyName
 * ><gml:Point><gml:coordinates>2587598.000000
 * ,404010.531250</gml:coordinates></gml
 * :Point><Distance>1000</Distance></DWithin></Filter>
 */
