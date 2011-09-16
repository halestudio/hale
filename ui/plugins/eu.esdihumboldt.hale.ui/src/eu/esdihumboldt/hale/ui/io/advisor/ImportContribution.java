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

package eu.esdihumboldt.hale.ui.io.advisor;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.ImportProvider;

/**
 * Contribution for import advisors
 * @author Simon Templer
 */
public class ImportContribution extends IOAdvisorContribution {

	/**
	 * Default constructor
	 */
	public ImportContribution() {
		super();
		
		setFilter(new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {
			
			@Override
			public boolean acceptFactory(IOAdvisorFactory factory) {
				return ImportProvider.class.isAssignableFrom(factory.getProviderType());
			}
			
			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<IOAdvisor<?>, IOAdvisorFactory> arg0) {
				// no collections available
				return false;
			}
		});
	}

}
