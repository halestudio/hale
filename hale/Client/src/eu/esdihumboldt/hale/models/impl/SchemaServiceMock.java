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
package eu.esdihumboldt.hale.models.impl;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opengis.feature.type.FeatureType;

import test.eu.esdihumboldt.hale.models.factory.FeatureCollectionUtilities;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;

/**
 * TODO: Enter an explanation what this type does here.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class SchemaServiceMock implements SchemaService {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanSourceSchema()
	 */
	@Override
	public boolean cleanSourceSchema() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	@Override
	public boolean cleanTargetSchema() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#getSourceSchema()
	 */
	@Override
	public Collection<FeatureType> getSourceSchema() {
		Set<FeatureType> result = new HashSet<FeatureType>();
		FeatureType ft_abstract = FeatureCollectionUtilities.getFeatureType(
				null, "TransportNetworkPart", true);
		result.add(ft_abstract);
		FeatureType ft_road = FeatureCollectionUtilities.getFeatureType(
				ft_abstract, LineString.class, "Road", false);
		result.add(ft_road);
		result.add(FeatureCollectionUtilities.getFeatureType(ft_road, 
				null, "Motorway", false));
		result.add(FeatureCollectionUtilities.getFeatureType(ft_abstract,
				LineString.class, "River", false));
		return result;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#getTargetSchema()
	 */
	@Override
	public Collection<FeatureType> getTargetSchema() {
		Set<FeatureType> result = new HashSet<FeatureType>();
		FeatureType ft_road = FeatureCollectionUtilities.getFeatureType(
				Polygon.class, "Straße", false);
		result.add(ft_road);
		result.add(FeatureCollectionUtilities.getFeatureType(ft_road, 
				null, "Autobahn", false));
		return result;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadSourceSchema(java.net.URI)
	 */
	@Override
	public boolean loadSourceSchema(URI file) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadTargetSchema(java.net.URI)
	 */
	@Override
	public boolean loadTargetSchema(URI file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addListener(HaleServiceListener sl) {
		// TODO Auto-generated method stub
		return false;
	}

}
