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
package eu.esdihumboldt.commons.tools;

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
		// return (ft.getName().getNamespaceURI()
		// + ft.getName().getLocalPart()).hashCode();
		return ft.getName().getLocalPart().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		FeatureType ft2 = ((RobustFTKey) o).getFeatureType();

		String s1 = this.ft.getName().getNamespaceURI();
		s1 += this.ft.getName().getLocalPart();

		String s2 = ft2.getName().getNamespaceURI();
		s2 += ft2.getName().getLocalPart();

		// FIXME: Using the hash to compare the string fails here sometimes
		if (s1.equals(s2))
			return true;
		else
			return false;
	}

	public FeatureType getFeatureType() {
		return this.ft;
	}

	public int compareTo(Object o) {
		FeatureType ft2 = ((RobustFTKey) o).getFeatureType();
		return this.ft.hashCode() - ft2.hashCode();
	}
}
