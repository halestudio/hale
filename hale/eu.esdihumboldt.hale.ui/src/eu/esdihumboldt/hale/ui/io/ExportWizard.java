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

package eu.esdihumboldt.hale.ui.io;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;

/**
 * Abstract export wizard
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class ExportWizard<P extends IOProvider, T extends IOProviderFactory<P>> extends IOWizard<P, T> {

	/**
	 * @see IOWizard#IOWizard(Class)
	 */
	public ExportWizard(Class<T> factoryClass) {
		super(factoryClass);
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		// add select provider page
		addPage(new ExportSelectProviderPage<P, T, ExportWizard<P,T>>());
	}

}
