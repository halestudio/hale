
/**
 * 
 */
package eu.esdihumboldt.mediator.constraints.impl;

import eu.esdihumboldt.mediator.constraints.Constraint;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.mediator.constraints.IdentifierConstraint;

/**
 * @author Bernd Schneiders, Logica
 *
 */
public class IdentifierConstraintImpl implements IdentifierConstraint {

    Set<String> featureIDs;

    private UUID identifier;

    /**
     * The status of this constraint.
     */
    private boolean satisfied;

    private boolean write = false;

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public IdentifierConstraintImpl(Set<String> featureIDs) {
        this.featureIDs = featureIDs;
this.satisfied=false;
    }

    /**
     * @param featureIDs the featureNames to set
     */
    public void setFeatureIDs(Set<String> featureIDs) {
        this.featureIDs = featureIDs;
    }

    /* (non-Javadoc)
     * @see eu.esdihumboldt.mediator.constraints.FeatureConstraint#getFeatureNames()
     */
    public Set<String> getFeatureIDs() {
        return this.featureIDs;
    }

    /* (non-Javadoc)
     * @see eu.esdihumboldt.mediator.constraints.Constraint#getConstraintSource()
     */
    public ConstraintSource getConstraintSource() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.esdihumboldt.mediator.constraints.Constraint#getId()
     */
    public long getId() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see eu.esdihumboldt.mediator.constraints.Constraint#isSatisfied()
     */
    public boolean isSatisfied() {
        // TODO Auto-generated method stub
        return this.satisfied;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    /* (non-Javadoc)
     * @see eu.esdihumboldt.mediator.constraints.Constraint#setId(long)
     */
    public void setId(long arg0) {
        // TODO Auto-generated method stub
    }

    public boolean isFinalized() {
        return this.write;
    }

    public void setFinalized(boolean write) {
        this.write = write;
    }

    public boolean compatible(Constraint constraint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}



/**

http://localhost:8080/geoserver/wfs?request=GetFeature&version=1.0.0&typeName=topp:states&outputFormat=GML2&FILTER=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns:gml=%22http://www.opengis.net/gml%22%3E%3CWithin%3E%3CPropertyName%3EInWaterA_1M/wkbGeom%3CPropertyName%3E%3Cgml:Envelope%3E%3Cgml:lowerCorner%3E10%2010%3C/gml:lowerCorner%3E%3Cgml:upperCorner%3E20%2020%3C/gml:upperCorner%3E%3C/gml:Envelope%3E%3C/Within%3E%3C/Filter%3E
http://localhost:8080/geoserver/wfs?request=GetFeature&version=1.0.0&typeName=topp:states&outputFormat=GML2&FILTER=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns:gml=%22http://www.opengis.net/gml%22%3E%3CIntersects%3E%3CPropertyName%3Ethe_geom%3C/PropertyName%3E%3Cgml:Point%20srsName=%22EPSG:4326%22%3E%3Cgml:coordinates%3E-74.817265,40.5296504%3C/gml:coordinates%3E%3C/gml:Point%3E%3C/Intersects%3E%3C/Filter%3E
                                wfs?request=GetFeature&version=1.0.0&typeName=topp:states&outputFormat=GML2&FILTER=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns:gml=%22http://www.opengis.net/gml%22%3E%3CIntersects%3E%3CPropertyName%3Ethe_geom%3C/PropertyName%3E%3Cgml:Point%20srsName=%22EPSG:4326%22%3E%3Cgml:coordinates%3E-74.817265,40.5296504%3C/gml:coordinates%3E%3C/gml:Point%3E%3C/Intersects%3E%3C/Filter%3E

http://localhost:8080/geoserver/wfs?request=GetFeature&version=1.0.0&typeName=topp:states&outputFormat=GML2&FILTER=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns:gml=%22http://www.opengis.net/gml%22%20xmlns:xsi=%22http://www.w3.org/2001/XMLSchema-instance%22%20xsi:schemaLocation=%22http://www.opengis.net/ogc/filter/1.1.0/filter.xsd%20http://www.opengis.net/gml/gml/3.1.1/base/gml.xsd%22%3E%3CWithin%3E%3CPropertyName%3EInWaterA_1M/wkbGeom%3CPropertyName%3E%3Cgml:Envelope%3E%3Cgml:lowerCorner%3E10%2010%3C/gml:lowerCorner%3E%3Cgml:upperCorner%3E20%2020%3C/gml:upperCorner%3E%3C/gml:Envelope%3E%3C/Within%3E%3C/Filter%3E
FILTER=<Filter xmlns="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/ogc/filter/1.1.0/filter.xsd http://www.opengis.net/gml/gml/3.1.1/base/gml.xsd">

	*/