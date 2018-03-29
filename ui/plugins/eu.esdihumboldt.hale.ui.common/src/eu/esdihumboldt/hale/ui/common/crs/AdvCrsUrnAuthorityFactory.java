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
import java.util.Objects;

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
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Wraps {@link AdvCrsAuthorityFactory} for the {@code "urn:adv:crs"} namespace.
 * An example of a complete URN is {@code "urn:adv:crs:DE_DHDN_3GK3"}.
 * 
 * @author Florian Esser
 */
public class AdvCrsUrnAuthorityFactory extends AuthorityFactoryAdapter
		implements CRSAuthorityFactory {

	/**
	 * Citation for the urn:adv:crs namespace
	 */
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

	private static class AdvCrsUrn {

		/**
		 * The parsed code as full URN.
		 */
		public final String urn;

		/**
		 * The parsed code
		 */
		public final String code;

		/**
		 * Constructor.
		 * 
		 * @param urn the full URN string
		 */
		public AdvCrsUrn(String urn) {
			Objects.requireNonNull("urn must not be null", urn);
			if (!urn.toLowerCase().startsWith("urn:adv:crs:")) {
				throw new IllegalArgumentException(
						"Can only parse URNs starting with 'urn:adv:crs:'");
			}

			this.urn = urn;
			this.code = urn.substring("urn:adv:crs:".length());
		}

		/**
		 * Creates a parser for the given URN
		 * 
		 * @param urn URN to parse
		 * @return the parser
		 */
		public static AdvCrsUrn from(String urn) {
			return new AdvCrsUrn(urn);
		}

		/**
		 * @return the concatenation of the authority and the {@linkplain #code}
		 *         , separated by ':'
		 */
		public String getAuthorityCode() {
			return "ADV:" + code;
		}

		/**
		 * Returns the URN.
		 */
		@Override
		public String toString() {
			return urn;
		}
	}

	private static AdvCrsUrnAuthorityFactory INSTANCE;

	/**
	 * Default constructor
	 */
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

	@Override
	protected String toBackingFactoryCode(final String code) throws FactoryException {
		return AdvCrsUrn.from(code).getAuthorityCode();
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
