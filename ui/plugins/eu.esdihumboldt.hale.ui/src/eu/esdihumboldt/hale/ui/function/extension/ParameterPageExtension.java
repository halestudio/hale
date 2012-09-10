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

package eu.esdihumboldt.hale.ui.function.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.function.extension.impl.ParameterPageFactoryImpl;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * {@link ParameterPageFactory} extension.
 * 
 * @author Kai Schwierczek
 */
public class ParameterPageExtension extends AbstractExtension<ParameterPage, ParameterPageFactory> {

	private static ParameterPageExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the parameter page extension
	 */
	public static ParameterPageExtension getInstance() {
		if (instance == null) {
			instance = new ParameterPageExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	public ParameterPageExtension() {
		super(FunctionWizardExtension.ID);
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ParameterPageFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("propertyParameterPage")
				|| conf.getName().equals("typeParameterPage"))
			return new ParameterPageFactoryImpl(conf);

		return null;
	}
}
