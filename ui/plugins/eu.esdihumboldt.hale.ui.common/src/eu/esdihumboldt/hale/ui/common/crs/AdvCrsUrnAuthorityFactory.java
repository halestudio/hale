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

import org.geotools.metadata.iso.IdentifierImpl;
import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.metadata.iso.citation.ContactImpl;
import org.geotools.metadata.iso.citation.OnLineResourceImpl;
import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.factory.AuthorityFactoryAdapter;
import org.geotools.util.SimpleInternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.Role;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Type description
 * 
 * @author Florian Esser
 */
public class AdvCrsUrnAuthorityFactory extends AuthorityFactoryAdapter
		implements CRSAuthorityFactory {

	public static final Citation URN_ADV;
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

		final CitationImpl c = new CitationImpl("URN in AdV namespace");
		c.getIdentifiers().add(new IdentifierImpl("urn:adv:crs"));
		c.getCitedResponsibleParties().add(advRespParty);
		c.getPresentationForm().add(PresentationForm.DOCUMENT_DIGITAL);
		c.freeze();
		URN_ADV = c;
	}

	private static class UrnParser {

		/**
		 * The beginning parts of the URN, typically {@code "urn:adv:"}. All
		 * elements in the array are treated as synonymous. Those parts are up
		 * to, but do not include, the type part ({@code "crs"}, {@code "cs"},
		 * {@code "datum"}, <cite>etc.</cite>). They must include a trailing
		 * (@value #URN_SEPARATOR} character.
		 */
		private static final String[] URN_BASES = new String[] { "urn:adv:" };

		/**
		 * Used to join the authority and code in {@link #getAuthorityCode()}
		 * getAuthorityCode.
		 */
		private static final char SEPARATOR = ':';

		/**
		 * The parsed code as full URN.
		 */
		public final String urn;

		/**
		 * The type part of the URN ({@code "crs"}).
		 */
		public final String type;

		/**
		 * The authority part of the URI (typically {@code "adv"}).
		 */
		public final String authority;

		/**
		 * The code part of the URI.
		 */
		public final String code;

		/**
		 * Constructor.
		 * 
		 * @param urn the full URN string
		 * @param type the resource type, for example "crs"
		 * @param authority the resource authority, for example "adv"
		 * @param code the resource code
		 */
		public UrnParser(String urn, String type, String authority, String code) {
			this.urn = urn;
			this.type = type;
			this.authority = authority;
			this.code = code;
		}

		/**
		 * @return the concatenation of the {@linkplain #authority} and the
		 *         {@linkplain #code}, separated by {@link #SEPARATOR}.
		 */
		public String getAuthorityCode() {
			return "ADV" + SEPARATOR + code;
		}

		/**
		 * Returns the URN.
		 */
		@Override
		public String toString() {
			return urn;
		}

		public static UrnParser buildParser(final String urn) throws NoSuchAuthorityCodeException {
			if (urn.toLowerCase().startsWith("urn:adv:crs:")) {
				return new UrnParser(urn, "crs", "urn:adv", urn.substring("urn:adv:crs:".length()));
			}

			throw new NoSuchAuthorityCodeException("", urn, urn);
		}
	}

	private static AdvCrsUrnAuthorityFactory INSTANCE;

	protected AdvCrsUrnAuthorityFactory() {
		super(AdvCrsAuthorityFactory.getInstance());
	}

	/**
	 * Install the factory with the {@link ReferencingFactoryFinder}
	 */
	public synchronized static void install() {
		ReferencingFactoryFinder.addAuthorityFactory(getInstance());
	}

	/**
	 * Get the factory instance
	 * 
	 * @return the factory instance
	 */
	public synchronized static AdvCrsUrnAuthorityFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AdvCrsUrnAuthorityFactory();
		}
		return INSTANCE;
	}

	/**
	 * @see org.geotools.referencing.factory.AuthorityFactoryAdapter#getAuthority()
	 */
	@Override
	public Citation getAuthority() {
		return URN_ADV;
	}

	/**
	 * Returns an object factory for the specified code. This method invokes one
	 * of the <code>get</code><var>Type</var><code>AuthorityFactory</code>
	 * methods where <var>Type</var> is inferred from the code.
	 *
	 * @param code The authority code given to this class.
	 * @return A factory for the specified authority code (never {@code null}).
	 * @throws FactoryException if no suitable factory were found.
	 */
	@Override
	protected AuthorityFactory getAuthorityFactory(final String code) throws FactoryException {
		if (code != null) {
			return getCRSAuthorityFactory(code);
		}
		else {
			return super.getAuthorityFactory(code);
		}
	}

	@Override
	public CoordinateReferenceSystem createCoordinateReferenceSystem(final String code)
			throws FactoryException {
		return getCRSAuthorityFactory(code)
				.createCoordinateReferenceSystem(toBackingFactoryCode(code));
	}

	/**
	 * Returns a simple authority code (like "EPSG:4236") that can be passed to
	 * the wrapped factories.
	 *
	 * @param code The code given to this factory.
	 * @return The code to give to the underlying factories.
	 * @throws FactoryException if the code can't be converted.
	 */
	@Override
	protected String toBackingFactoryCode(final String code) throws FactoryException {
		return UrnParser.buildParser(code).getAuthorityCode();
	}

	@Override
	protected CRSAuthorityFactory getCRSAuthorityFactory(final String code)
			throws FactoryException {
		if (code != null && code.toLowerCase().startsWith("urn:adv:crs:")) {
			return AdvCrsAuthorityFactory.getInstance();
		}
		return super.getCRSAuthorityFactory(code);
	}
}
