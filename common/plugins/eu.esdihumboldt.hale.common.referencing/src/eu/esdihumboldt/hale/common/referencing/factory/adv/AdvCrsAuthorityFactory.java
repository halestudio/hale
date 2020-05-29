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

package eu.esdihumboldt.hale.common.referencing.factory.adv;

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
import org.geotools.util.factory.Hints;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.Role;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.util.InternationalString;

/**
 * CRSAuthorityFactory for AdV CRS definitions (e.g. "DE_DHDN_3GK3")
 * 
 * @author Florian Esser
 */
public class AdvCrsAuthorityFactory extends AbstractAuthorityFactory
		implements CRSAuthorityFactory {

	/**
	 * The authority
	 */
	public static final String AUTHORITY = "ADV"; //$NON-NLS-1$

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
		c.getPresentationForm().add(PresentationForm.TABLE_DIGITAL);
		c.freeze();
		ADV = c;
	}

	/**
	 * Preferences node
	 */
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
	 * The name used in {@link Hints#FORCE_AXIS_ORDER_HONORING} for this
	 * factory.
	 */
	public static final String HINTS_AUTHORITY = "urn";

	/**
	 * Creates a new instance
	 */
	public AdvCrsAuthorityFactory() {
		this(ReferencingFactoryFinder.getCRSFactory(null));

		addCodeMapping("ETRS89_UTM32", "EPSG:25832");
		addCodeMapping("ETRS89_UTM33", "EPSG:25833");
		addCodeMapping("ETRS89_Lat-Lon", "EPSG:4258");
		addCodeMapping("DE_DHHN92_NH", "EPSG:5783");

		// EPSG:7837 is not available in EPSG registry version 7.9.0
		// which is shipped with GeoTools 12.2. See also
		// https://github.com/halestudio/hale/issues/573
		//
		// addEpsgMapping("DE_DHHN2016_NH", "EPSG:7837");
		
		//XXX These mappings provided by AdV don't seem to be correct
		// It seems there is currently no matching EPSG code and the
		// definitions would have to be provided via WKT instead.
		//
		// addCodeMapping("DE_DHDN_3GK2", "EPSG:31466");
		// addCodeMapping("DE_DHDN_3GK3", "EPSG:31467");
	}

	/**
	 * Create a new instance, use the given CRS factory
	 * 
	 * @param factory the CRS factory to use
	 */
	public AdvCrsAuthorityFactory(final CRSFactory factory) {
		super(MAXIMUM_PRIORITY); // allow overriding CRS definitions in the
									// database
		this.crsFactory = factory;
	}

	/**
	 * Add an AdV CRS definition
	 * 
	 * @param code the CRS code (e.g. DE_DHDN_3GK3)
	 * @param epsgCode the code of the corresponding EPSG CRS (must include
	 *            prefix)
	 */
	public void addCodeMapping(String code, String epsgCode) {
		Objects.requireNonNull("AdV code must not be null", code);
		if (code.startsWith(AUTHORITY_PREFIX)) {
			code = code.substring(AUTHORITY_PREFIX.length());
		}

		Objects.requireNonNull("EPSG code must not be null", epsgCode);
		if (!epsgCode.startsWith("EPSG:")) {
			throw new IllegalArgumentException(
					"EPSG code must start with authority prefix \"EPSG:\"");
		}

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
		if (code == null) {
			return null;
		}

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

		String epsgCode = getEpsgCode(advCode);
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

	@Override
	public ProjectedCRS createProjectedCRS(String code) throws FactoryException {
		return (ProjectedCRS) createCoordinateReferenceSystem(code);
	}

	@Override
	public GeographicCRS createGeographicCRS(String code) throws FactoryException {
		return (GeographicCRS) createCoordinateReferenceSystem(code);
	}

	@Override
	public Citation getAuthority() {
		return ADV;
	}

	@Override
	public Set<String> getAuthorityCodes(Class<? extends IdentifiedObject> clazz)
			throws FactoryException {

		return cache.keySet();
	}

	@Override
	public Citation getVendor() {
		return Citations.GEOTOOLS; // XXX
	}

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
