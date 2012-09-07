/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instance.model.impl.internal;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.util.Identifiers;

/**
 * Temporary static namespace map for storing {@link OInstance}s/{@link OGroup}s
 * in a temporary database or using them inside this JVM.
 * @author Simon Templer
 */
public abstract class ONamespaceMap {
	
	private static final Identifiers<String> IDS = new Identifiers<String>("", true);
	
	/**
	 * Map the namespace of the given qualified name to a short identifier and
	 * return the adapted name.
	 * @param org the original qualified name
	 * @return the adapted qualified name
	 */
	public static QName map(QName org) {
		if (XMLConstants.NULL_NS_URI.equals(org.getNamespaceURI())) {
			return org;
		}
		
		return new QName(IDS.getId(org.getNamespaceURI()), org.getLocalPart());
	}
	
	/**
	 * Determine the original namespace of the given qualified name with a
	 * namespace previously mapped with {@link #map(QName)} and return the
	 * original name.
	 * @param mapped the adapted qualified name
	 * @return the original qualified name
	 */
	public static QName unmap(QName mapped) {
		if (XMLConstants.NULL_NS_URI.equals(mapped.getNamespaceURI())) {
			return mapped;
		}
		
		return new QName(IDS.getObject(mapped.getNamespaceURI()), mapped.getLocalPart());
	}

}
