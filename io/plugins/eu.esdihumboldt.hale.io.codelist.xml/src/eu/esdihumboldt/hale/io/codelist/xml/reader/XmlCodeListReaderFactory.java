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

package eu.esdihumboldt.hale.io.codelist.xml.reader;

import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReaderFactory;
import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProviderFactory;

/**
 * Factory for {@link XmlCodeListReader}
 * @author Patrick Lieb
 */
public class XmlCodeListReaderFactory extends AbstractIOProviderFactory<CodeListReader>
		implements CodeListReaderFactory {
	
	private static final String PROVIDER_ID = "eu.esdihumboldt.codelist.io.xml.instance";
	
	private static final String PROVIDER_CT_ID = "XMLCodelist";
	
	private static final ContentType PROVIDER_CT = ContentType.getContentType(PROVIDER_CT_ID);

	/**
	 * 
	 */
	public XmlCodeListReaderFactory() {
		super(PROVIDER_ID);
		addSupportedContentType(PROVIDER_CT_ID);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProviderFactory#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		
		return PROVIDER_CT_ID;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		
		return HaleIO.getDisplayName(PROVIDER_CT);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProviderFactory#getSupportedTypes()
	 */
	@Override
	public Set<ContentType> getSupportedTypes() {
		Set<ContentType> supportedtypes = new HashSet<ContentType>();
		supportedtypes.add(PROVIDER_CT);
		return supportedtypes;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProviderFactory#createProvider()
	 */
	@Override
	public CodeListReader createProvider() {
		
		return new XmlCodeListReader();
	}

}
