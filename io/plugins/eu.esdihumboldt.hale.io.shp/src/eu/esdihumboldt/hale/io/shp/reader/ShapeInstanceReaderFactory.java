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

package eu.esdihumboldt.hale.io.shp.reader;

import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.instance.io.InstanceReader;
import eu.esdihumboldt.hale.instance.io.InstanceReaderFactory;
import eu.esdihumboldt.hale.io.shp.ShapefileIO;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader;

/**
 * Factory for Shapefile {@link InstanceReader}s
 * @author Simon Templer
 */
public class ShapeInstanceReaderFactory extends
		AbstractIOProviderFactory<InstanceReader> implements InstanceReaderFactory {
	
	private static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.shape.reader.instance";

	/**
	 * Default constructor
	 */
	public ShapeInstanceReaderFactory() {
		super(PROVIDER_ID);
		
		addSupportedContentType(ShapefileIO.SHAPEFILE_CT_ID);
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return HaleIO.getDisplayName(ShapefileIO.SHAPEFILE_CT);
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public InstanceReader createProvider() {
		return new ShapeInstanceReader();
	}

}
