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

package eu.esdihumboldt.hale.common.core.io.project.impl;

import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReaderFactory;

/**
 * Factory for {@link ZipProjectReader}s
 * @author Simon Templer
 */
public class ZipProjectReaderFactory extends
		AbstractIOProviderFactory<ProjectReader> implements
		ProjectReaderFactory {

	private static final String ID = "eu.esdihumboldt.hale.common.core.project.zip.reader";

	/**
	 * Default constructor
	 */
	public ZipProjectReaderFactory() {
		super(ID);
		
		addSupportedContentType(ProjectIO.PROJECT_CT_ID);
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Alignment project";
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public ProjectReader createProvider() {
		return new ZipProjectReader();
	}

}
