/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.crs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.Factory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.util.InternationalString;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * CRS factory based on WKT stored in Java preferences
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WKTPreferencesCRSFactory extends AbstractAuthorityFactory implements
		CRSAuthorityFactory {

	private static final ALogger _log = ALoggerFactory.getLogger(WKTPreferencesCRSFactory.class);

	/**
	 * The authority
	 */
	public static final String AUTHORITY = "EPSG"; //$NON-NLS-1$

	/**
	 * The authority prefix
	 */
	public static final String AUTHORITY_PREFIX = "EPSG:"; //$NON-NLS-1$

	/**
	 * The one and only factory instance
	 */
	protected static WKTPreferencesCRSFactory INSTANCE;

	/**
	 * Preferences node
	 */
	private final Preferences node = Preferences.userNodeForPackage(WKTPreferencesCRSFactory.class)
			.node(AUTHORITY);

	/**
	 * CRS factory
	 */
	protected CRSFactory crsFactory;

	/**
	 * Cache of parsed {@link CoordinateReferenceSystem}s
	 */
	private final Map<String, CoordinateReferenceSystem> cache = new HashMap<String, CoordinateReferenceSystem>();

	/**
	 * Creates a new instance
	 */
	protected WKTPreferencesCRSFactory() {
		this(ReferencingFactoryFinder.getCRSFactory(null));
	}

	/**
	 * Create a new instance, use the given CRS factory
	 * 
	 * @param factory the CRS factory to use
	 */
	protected WKTPreferencesCRSFactory(final CRSFactory factory) {
		super(MAXIMUM_PRIORITY); // allow overriding CRS definitions in the
									// database
		// MINIMUM_PRIORITY); // Select other factories first
		this.crsFactory = factory;
	}

	/**
	 * Get the factory instance
	 * 
	 * @return the factory instance
	 */
	public synchronized static WKTPreferencesCRSFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WKTPreferencesCRSFactory();
		}
		return INSTANCE;
	}

	/**
	 * Install the factory with the {@link ReferencingFactoryFinder}
	 */
	public synchronized static void install() {
		ReferencingFactoryFinder.addAuthorityFactory(getInstance());
	}

	/**
	 * Register a WKT with the factory
	 * 
	 * @param code the CRS code (e.g. 4326 or EPSG:4326)
	 * @param wkt the CRS well known text
	 */
	public synchronized static void registerWKT(String code, String wkt) {
		getInstance().addWKT(code, wkt);
	}

	/**
	 * Add a WKT
	 * 
	 * @param code the CRS code (e.g. 4326 or EPSG:4326)
	 * @param wkt the CRS well known text
	 */
	public void addWKT(String code, String wkt) {
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}

		node.put(code, wkt);

		try {
			node.sync();
		} catch (BackingStoreException e) {
			_log.warn("Error saving preferences", e); //$NON-NLS-1$
		}

		// XXX maybe have to reinstall it
	}

	/**
	 * Add a WKT
	 * 
	 * @param code the CRS code (e.g. 4326 or EPSG:4326) that the WKT is
	 *            associated to
	 */
	public void removeWKT(String code) {
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}

		node.remove(code);

		try {
			node.sync();
		} catch (BackingStoreException e) {
			_log.warn("Error saving preferences", e); //$NON-NLS-1$
		}

		// XXX maybe have to reinstall it
	}

	/**
	 * Get the WKT for the given code
	 * 
	 * @param code the CRS code (e.g. 4326 or EPSG:4326) that the WKT is
	 *            associated to
	 * @return the WKT or <code>null</code>
	 */
	public String getWKT(String code) {
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}

		return node.get(code, null);
	}

	/**
	 * Get the available CRS codes (with the authority prefix)
	 * 
	 * @return the CRS codes
	 */
	public List<String> getCodes() {
		try {
			String[] keys = node.keys();
			List<String> result = new ArrayList<String>();
			for (String key : keys) {
				result.add(AUTHORITY_PREFIX + key);
			}
			return result;
		} catch (BackingStoreException e) {
			_log.warn("Error accessing preferences", e); //$NON-NLS-1$
			return new ArrayList<String>();
		}
	}

	/**
	 * @see CRSAuthorityFactory#createCoordinateReferenceSystem(String)
	 */
	@Override
	public synchronized CoordinateReferenceSystem createCoordinateReferenceSystem(String code)
			throws FactoryException {
		if (code == null) {
			return null;
		}
		if (!code.startsWith(AUTHORITY_PREFIX)) {
			throw new NoSuchAuthorityCodeException(
					"This factory only understands EPSG codes", AUTHORITY, code); //$NON-NLS-1$
		}
		final String epsgNumber = code.substring(code.indexOf(':') + 1).trim();

		if (cache.containsKey(epsgNumber)) {
			CoordinateReferenceSystem value = cache.get(epsgNumber);
			if (value != null) {
				// CRS was already created
				return value;
			}
		}

		try {
			node.sync();
		} catch (BackingStoreException e) {
			_log.warn("Error synchronizing preferences", e); //$NON-NLS-1$
		}

		String wkt = node.get(epsgNumber, null);
		if (wkt == null) {
			throw new NoSuchAuthorityCodeException("Unknown EPSG code", AUTHORITY, code); //$NON-NLS-1$
		}
		if (wkt.indexOf(epsgNumber) == -1) {
			wkt = wkt.trim();
			wkt = wkt.substring(0, wkt.length() - 1);
			wkt += ",AUTHORITY[\"EPSG\",\"" + epsgNumber + "\"]]"; //$NON-NLS-1$ //$NON-NLS-2$
			_log.warn("EPSG:" + epsgNumber + " lacks a proper identifying authority in its Well-Known Text. It is being added programmatically."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			CoordinateReferenceSystem crs = crsFactory.createFromWKT(wkt);
			cache.put(epsgNumber, crs);
			return crs;
		} catch (FactoryException fex) {
			throw fex;
		}
	}

	/**
	 * @see AuthorityFactory#createObject(String)
	 */
	@Override
	public IdentifiedObject createObject(String code) throws FactoryException {
		return createCoordinateReferenceSystem(code);
	}

	/**
	 * @see CRSAuthorityFactory#createProjectedCRS(String)
	 */
	@Override
	public ProjectedCRS createProjectedCRS(String code) throws FactoryException {
		return (ProjectedCRS) createCoordinateReferenceSystem(code);
	}

	/**
	 * @see CRSAuthorityFactory#createGeographicCRS(String)
	 */
	@Override
	public GeographicCRS createGeographicCRS(String code) throws FactoryException {
		return (GeographicCRS) createCoordinateReferenceSystem(code);
	}

	/**
	 * @see AuthorityFactory#getAuthority()
	 */
	@Override
	public Citation getAuthority() {
		return Citations.EPSG;
	}

	/**
	 * @see AuthorityFactory#getAuthorityCodes(Class)
	 * 
	 *      The following implementation filters the set of codes based on the
	 *      "PROJCS" and "GEOGCS" at the start of the WKT strings. It is assumed
	 *      that we only have GeographicCRS and ProjectedCRS's here.
	 */
	@Override
	public Set<String> getAuthorityCodes(Class<? extends IdentifiedObject> clazz)
			throws FactoryException {
		Set<String> all = new HashSet<String>();

		try {
			if (clazz.getName().equalsIgnoreCase(CoordinateReferenceSystem.class.getName())) {
				for (String number : node.keys()) {
					all.add(AUTHORITY_PREFIX + number);
				}
			}
			else if (clazz.getName().equalsIgnoreCase(GeographicCRS.class.getName())) {
				for (String number : node.keys()) {
					String wkt = node.get(number, null);
					if (wkt != null && wkt.startsWith("GEOGCS")) { //$NON-NLS-1$
						all.add(AUTHORITY_PREFIX + number);
					}
				}
			}
			else if (clazz.getName().equalsIgnoreCase(ProjectedCRS.class.getName())) {
				for (String number : node.keys()) {
					String wkt = node.get(number, null);
					if (wkt != null && wkt.startsWith("PROJCS")) { //$NON-NLS-1$
						all.add(AUTHORITY_PREFIX + number);
					}
				}
			}
		} catch (BackingStoreException e) {
			throw new RuntimeException("Could not access preferences", e); //$NON-NLS-1$
		}

		return all;
	}

	/**
	 * @see Factory#getVendor()
	 */
	@Override
	public Citation getVendor() {
		return Citations.GEOTOOLS; // XXX
	}

	/**
	 * @see AuthorityFactory#getDescriptionText(String)
	 */
	@Override
	public InternationalString getDescriptionText(String code) throws FactoryException {
		if (code == null) {
			return null;
		}
		if (code.startsWith("EPSG:")) { //$NON-NLS-1$
			code = code.substring(5);
		}
		code = code.trim();
		String wkt = node.get(code, null);
		if (wkt == null) {
			throw new FactoryException("Unknown EPSG code: '" + code + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		wkt = wkt.trim();
		int start = wkt.indexOf('"');
		int end = wkt.indexOf('"', start + 1);
		return new org.geotools.util.SimpleInternationalString(wkt.substring(start + 1, end));
	}

	/**
	 * @see CRSAuthorityFactory#createCompoundCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.CompoundCRS createCompoundCRS(String str)
			throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CRSAuthorityFactory#createDerivedCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.DerivedCRS createDerivedCRS(String str)
			throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CRSAuthorityFactory#createEngineeringCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.EngineeringCRS createEngineeringCRS(String str)
			throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CRSAuthorityFactory#createGeocentricCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.GeocentricCRS createGeocentricCRS(String str)
			throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CRSAuthorityFactory#createImageCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.ImageCRS createImageCRS(String str) throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CRSAuthorityFactory#createTemporalCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.TemporalCRS createTemporalCRS(String str)
			throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}

	/**
	 * @see CRSAuthorityFactory#createVerticalCRS(String)
	 */
	@Override
	public org.opengis.referencing.crs.VerticalCRS createVerticalCRS(String str)
			throws FactoryException {
		throw new FactoryException("Not implemented"); //$NON-NLS-1$
	}
}
