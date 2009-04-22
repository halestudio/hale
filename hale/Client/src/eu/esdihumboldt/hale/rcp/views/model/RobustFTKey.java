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
package eu.esdihumboldt.hale.rcp.views.model;

import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.opengis.feature.type.FeatureType;

/**
 * This type has to be used to robustly retrieve {@link FeatureType}s from 
 * Collections, as the normal {@link FeatureType} implementations such as 
 * {@link SimpleFeatureTypeImpl} don't have consistent equals/CompareTo/hashCode
 * implementations which reflect the uniquely identifying properties of the 
 * {@link FeatureType}s.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class RobustFTKey implements Comparable {
	
	private FeatureType ft = null;

	public RobustFTKey(FeatureType ft) {
		this.ft = ft;
	}
	
	@Override
	public int hashCode() {
		return (ft.getName().getNamespaceURI() 
				+ ft.getName().getLocalPart()).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		FeatureType ft2 = ((RobustFTKey) o).getFeatureType();
		return this.ft.hashCode() == ft2.hashCode();
	}
	
	public FeatureType getFeatureType() {
		return this.ft;
	}


	public int compareTo(Object o) {
		FeatureType ft2 = ((RobustFTKey) o).getFeatureType();
		return this.ft.hashCode() - ft2.hashCode();
	}
}
