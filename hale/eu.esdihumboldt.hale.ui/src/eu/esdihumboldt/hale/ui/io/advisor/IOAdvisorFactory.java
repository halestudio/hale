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

import java.util.Set;

import org.eclipse.core.expressions.Expression;
import org.eclipse.ui.services.IEvaluationService;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Interface for {@link IOAdvisor} factories provided by the 
 * {@link IOAdvisorExtension}.
 * 
 * @author Simon Templer
 */
public interface IOAdvisorFactory extends ExtensionObjectFactory<IOAdvisor<?>> {

	/**
	 * Get the I/O provider type supported by the advisor.
	 * @return the I/O provider type
	 */
	public Class<? extends IOProvider> getProviderType();
	
	/**
	 * Create an I/O wizard configured with the advisor.
	 * @return the I/O wizard
	 */
	public IOWizard<?, ?> createWizard();
	
	/**
	 * Get the dependencies of the advisor.
	 * @return the list of identifiers of other advisors the advisor depends on
	 *   for sequential execution, e.g. when loading a project
	 */
	public Set<String> getDependencies();
	
	/**
	 * States if I/O operations based on this advisor represent project 
	 * resources and thus shall be remembered, i.e. the configuration stored 
	 * in the project. If stored in a project the resource will be loaded again 
	 * when the project is loaded.
	 * @return if operations based on this advisor shall be remembered
	 */
	public boolean isProjectResource();
	
	/**
	 * Get the expression that specifies whether the command to show the
	 * I/O wizard for the advisor should be enabled.
	 * @see IEvaluationService
	 * @return the expression or <code>null</code> if the command should always
	 *   be enabled 
	 */
	public Expression getEnabledWhen();
	
}
