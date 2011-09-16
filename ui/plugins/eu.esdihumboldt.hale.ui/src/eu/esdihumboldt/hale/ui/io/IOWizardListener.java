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

import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;

/**
 * Listener interface for {@link IOWizard}s
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface IOWizardListener<P extends IOProvider, T extends IOProviderFactory<P>, W extends IOWizard<P, T>> {

	/**
	 * Called when the I/O provider factory assigned to the wizard has changed
	 * 
	 * @param providerFactory the provider factory, may be <code>null</code>
	 */
	public void providerFactoryChanged(T providerFactory);
	
	/**
	 * Called when the content type assigned to the wizard has changed
	 * 
	 * @param contentType the content type, may be <code>null</code>
	 */
	public void contentTypeChanged(ContentType contentType);
	
}
