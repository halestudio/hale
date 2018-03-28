/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.common.crs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.geotools.metadata.iso.IdentifierImpl;
import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.metadata.iso.citation.ContactImpl;
import org.geotools.metadata.iso.citation.OnLineResourceImpl;
import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.geotools.util.SimpleInternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.Role;
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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * CRSAuthorityFactory for AdV CRS URNs (e.g. "urn:adv:crs:DE_DHDN_3GK3")
 * 
 * @author Florian Esser
 */
public class AdvCrsAuthorityFactory extends AbstractAuthorityFactory
		implements CRSAuthorityFactory {

	private static final ALogger _log = ALoggerFactory.getLogger(AdvCrsAuthorityFactory.class);

	/**
	 * The authority
	 */
	public static final String AUTHORITY = "ADV"; //$NON-NLS-1$
//	public static final String AUTHORITY = "URN:ADV:CRS"; //$NON-NLS-1$

	/**
	 * The authority prefix
	 */
	public static final String AUTHORITY_PREFIX = "ADV:"; //$NON-NLS-1$

	/**
	 * Citation for AdV
	 */
	public static final Citation ADV;
	static {
		final OnLineResourceImpl advOnlineRes = new OnLineResourceImpl(
				URI.create("http://www.adv-online.de"));
		advOnlineRes.freeze();
		final ContactImpl advContact = new ContactImpl(advOnlineRes);
		final ResponsiblePartyImpl advRespParty = new ResponsiblePartyImpl(
				Role.PRINCIPAL_INVESTIGATOR);
		advRespParty.setOrganisationName(new SimpleInternationalString(
				"Arbeitsgemeinschaft der Vermessungsverwaltungen der Länder der Bundesrepublik Deutschland"));
		advRespParty.setContactInfo(advContact);
		advRespParty.freeze();

		final CitationImpl c = new CitationImpl(advRespParty);
		c.getIdentifiers().add(new IdentifierImpl(AUTHORITY));
//		c.getIdentifiers().add(new IdentifierImpl(AUTHORITY_PREFIX));
		c.getPresentationForm().add(PresentationForm.TABLE_DIGITAL);
		c.freeze();
		ADV = c;
	}

	/**
	 * The one and only factory instance
	 */
	protected static AdvCrsAuthorityFactory INSTANCE;

	/**
	 * Preferences node
	 */
//	private final Preferences node = Preferences.userNodeForPackage(AdvCrsAuthorityFactory.class)
//			.node(AUTHORITY);
	private final Map<String, String> mappings = new HashMap<>();

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
	protected AdvCrsAuthorityFactory() {
		this(ReferencingFactoryFinder.getCRSFactory(null));

		// TODO Read from external file/database?
		addEpsgMapping("ETRS89_UTM32", "EPSG:25832");
		addEpsgMapping("ETRS89_UTM33", "EPSG:25833");
		addEpsgMapping("ETRS89_Lat-Lon", "EPSG:4258");
		addEpsgMapping("DE_DHDN_3GK2", "EPSG:31466");
		addEpsgMapping("DE_DHDN_3GK3", "EPSG:31467");
		addEpsgMapping("DE_DHHN92_NH", "EPSG:5783");

		// EPSG:7837 is not available in EPSG registry version 7.9.0
		// addEpsgMapping("DE_DHHN2016_NH", "EPSG:7837");
	}

	/**
	 * Create a new instance, use the given CRS factory
	 * 
	 * @param factory the CRS factory to use
	 */
	protected AdvCrsAuthorityFactory(final CRSFactory factory) {
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
	public synchronized static AdvCrsAuthorityFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AdvCrsAuthorityFactory();
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
	 * Register an AdV CRS definition with the factory
	 * 
	 * @param code the CRS code (e.g. DE_DHDN_3GK3)
	 * @param epsgCode the code of the corresponding EPSG CRS
	 */
	public synchronized static void registerEpsgCode(String code, String epsgCode) {
		getInstance().addEpsgMapping(code, epsgCode);
	}

	/**
	 * Add an AdV CRS definition
	 * 
	 * @param code the CRS code (e.g. DE_DHDN_3GK3)
	 * @param epsgCode the code of the corresponding EPSG CRS (must include
	 *            prefix)
	 */
	public void addEpsgMapping(String code, String epsgCode) {
		Objects.requireNonNull("AdV code must not be null", code);
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}

		Objects.requireNonNull("EPSG code must not be null", epsgCode);
		if (!epsgCode.contains(":")) {
			throw new IllegalArgumentException(
					"EPSG code must include authority prefix (e.g. \"EPSG:\")");
		}

		// warn about ESPG codes not found in the used Geotools database
//		try {
//			if (CRS.decode(epsgCode) == null) {
//				_log.warn(MessageFormat.format(
//						"Mapping between \"{0}\" and \"{1}\" skipped: EPSG code could not be resolved.",
//						code, epsgCode));
//				return;
//			}
//		} catch (Exception e) {
//			_log.error(MessageFormat.format("Mapping between \"{0}\" and \"{1}\" skipped: {2}",
//					code, epsgCode, e.getMessage()), e);
//			return;
//		}

		mappings.put(code.toUpperCase(), epsgCode);
	}

	/**
	 * Get the EPSG code for the given AdV code
	 * 
	 * @param code the CRS code (e.g. DE_DHDN_3GK3 or urn:adv:crs:DE_DHDN_3GK3)
	 *            that the AdV CRS is associated with
	 * @return the EPSG code or <code>null</code>
	 */
	public String getEpsgCode(String code) {
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}

		return mappings.get(code);
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
			throw new NoSuchAuthorityCodeException("This factory only understands AdV codes", //$NON-NLS-1$
					AUTHORITY, code);
		}
		final String advCode = code.substring(AUTHORITY_PREFIX.length()).trim();
		if (cache.containsKey(advCode)) {
			CoordinateReferenceSystem value = cache.get(advCode);
			if (value != null) {
				// CRS was already created
				return value;
			}
		}

		String epsgCode = mappings.get(advCode);
		if (epsgCode == null) {
			throw new NoSuchAuthorityCodeException("Unknown AdV code", AUTHORITY, code); //$NON-NLS-1$
		}

		try {
			CoordinateReferenceSystem crs = CRS.decode(epsgCode, false);
			cache.put(advCode, crs);
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
		return ADV;
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

		return cache.keySet();
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
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}
		code = code.trim();
		String epsgCode = mappings.get(code);
		if (epsgCode == null) {
			throw new FactoryException("Unknown AdV code: '" + code + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String wkt = CRS.decode(epsgCode).toWKT();
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
