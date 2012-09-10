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

package eu.esdihumboldt.hale.schemaprovider.model;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.opengis.feature.type.Name;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Manages default geometry preferences
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public class DefaultGeometries implements IDefaultGeometries {

	private static final ALogger log = ALoggerFactory
			.getLogger(DefaultGeometries.class);

	private static final Preferences prefs = Preferences.userNodeForPackage(
			DefaultGeometries.class).node("defaultGeometries"); //$NON-NLS-1$

	private static DefaultGeometries instance = new DefaultGeometries();

	public static DefaultGeometries getInstance() {
		return instance;
	}

	/**
	 * Get the default geometry name for a given type name
	 * 
	 * @param typeName
	 *            the type name
	 * 
	 * @return the default geometry property name or <code>null</code>
	 */
	@Override
	public String getDefaultGeometryName(Name typeName) {
		try {
			prefs.sync();
			if (prefs.nodeExists(encodeNodeName(typeName.getNamespaceURI()))) {
				return prefs.node(encodeNodeName(typeName.getNamespaceURI()))
						.get(typeName.getLocalPart(), null);
			} else {
				return null;
			}
		} catch (BackingStoreException e) {
			log.warn("Error accessing the default geometry preferences", e); //$NON-NLS-1$
			return null;
		}
	}

	private String encodeNodeName(String name) {
		while (name.contains("//")) { //$NON-NLS-1$
			name = name.replaceAll("//", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return name;
	}

	/**
	 * Set the default geometry property name for a given type
	 * 
	 * @param typeName
	 *            the type name
	 * @param propertyName
	 *            the geometry property name
	 */
	@Override
	public void setDefaultGeometryName(Name typeName, String propertyName) {
		prefs.node(encodeNodeName(typeName.getNamespaceURI())).put(
				typeName.getLocalPart(), propertyName);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			log.warn("Error writing the default geometry preferences", e); //$NON-NLS-1$
		}
	}

}
