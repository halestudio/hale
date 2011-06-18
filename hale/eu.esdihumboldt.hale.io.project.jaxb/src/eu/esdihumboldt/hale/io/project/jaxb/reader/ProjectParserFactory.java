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

package eu.esdihumboldt.hale.io.project.jaxb.reader;

import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProviderFactory;
import eu.esdihumboldt.hale.core.io.project.ProjectReader;

/**
 * Factory for the {@link ProjectParser}
 * @author Simon Templer
 */
public class ProjectParserFactory extends
		AbstractIOProviderFactory<ProjectReader> {

	private static final String ID = "eu.esdihumboldt.hale.io.project.jaxb.parser";

	/**
	 * Default constructor
	 */
	protected ProjectParserFactory() {
		super(ID);
		
		addSupportedContentType("HaleProject");
	}

	/**
	 * @see IOProviderFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return HaleIO.getDisplayName(getSupportedTypes().iterator().next());
	}

	/**
	 * @see IOProviderFactory#createProvider()
	 */
	@Override
	public ProjectReader createProvider() {
		return new ProjectParser();
	}

}
