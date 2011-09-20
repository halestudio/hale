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

package eu.esdihumboldt.hale.io.csv.reader;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceReaderFactory;
import eu.esdihumboldt.hale.io.csv.CSVFileIO;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;

/**
 * Factory for CSVfile {@link InstanceReader}s
 * 
 * @author Kevin Mais
 */
public class CSVInstanceReaderFactory extends
		AbstractIOProviderFactory<InstanceReader> implements
		InstanceReaderFactory {

	private static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.csv.reader.instance";

	/**
	 * Default constructor
	 */
	public CSVInstanceReaderFactory() {
		super(PROVIDER_ID);

		addSupportedContentType(CSVFileIO.CSVFILE_CT_ID);
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return HaleIO.getDisplayName(CSVFileIO.CSVFILE_CT);
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public InstanceReader createProvider() {
		return new CSVInstanceReader();
	}

}
