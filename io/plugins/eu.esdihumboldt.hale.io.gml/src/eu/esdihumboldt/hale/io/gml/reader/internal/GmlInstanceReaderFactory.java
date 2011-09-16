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

package eu.esdihumboldt.hale.io.gml.reader.internal;

import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceReaderFactory;

/**
 * Factory for XML instance readers
 * @author Simon Templer
 */
public class GmlInstanceReaderFactory extends AbstractIOProviderFactory<InstanceReader> implements InstanceReaderFactory {

	private static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.xml.reader";
	
	/**
	 * Default constructor
	 */
	public GmlInstanceReaderFactory() {
		super(PROVIDER_ID);
		
		addSupportedContentType("GML");
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "GML (Features)";
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public InstanceReader createProvider() {
		return new StreamGmlReader(ContentType.getContentType("GML"), true);
	}

}
