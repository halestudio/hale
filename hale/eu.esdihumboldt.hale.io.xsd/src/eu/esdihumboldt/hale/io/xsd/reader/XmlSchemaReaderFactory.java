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

package eu.esdihumboldt.hale.io.xsd.reader;

import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.io.xsd.XMLSchemaIO;
import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.io.SchemaReaderFactory;

/**
 * Factory for Shapefile {@link SchemaReader}s
 * @author Simon Templer
 */
public class XmlSchemaReaderFactory extends
		AbstractIOProviderFactory<SchemaReader> implements SchemaReaderFactory {
	
	private static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.xsd.reader";

	/**
	 * Default constructor
	 */
	public XmlSchemaReaderFactory() {
		super(PROVIDER_ID);
		
		addSupportedContentType("XSD");
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return HaleIO.getDisplayName(XMLSchemaIO.XSD_CT);
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public SchemaReader createProvider() {
		return new XmlSchemaReader();
	}

}
