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

package eu.esdihumboldt.hale.ui.io.source;

import org.eclipse.jface.wizard.WizardPage;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.ui.io.ImportSource;

/**
 * Abstract {@link ImportSource} implementation
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 * @since 2.5
 */
public abstract class AbstractSource<P extends ImportProvider> implements ImportSource<P> {

	private WizardPage page;
	private SourceConfiguration<P> configuration;

	/**
	 * @see ImportSource#setPage(WizardPage)
	 */
	@Override
	public void setPage(WizardPage page) {
		this.page = page;
	}

	/**
	 * @see ImportSource#setConfiguration(SourceConfiguration)
	 */
	@Override
	public void setConfiguration(SourceConfiguration<P> configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the wizard page
	 */
	protected WizardPage getPage() {
		return page;
	}

	/**
	 * @return the wizard
	 */
	protected SourceConfiguration<P> getConfiguration() {
		return configuration;
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * By default doesn't change the provider and returns <code>true</code>.
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		return true;
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * The default implementation does nothing, override to to react on
	 * activation of the source page.
	 */
	@Override
	public void onActivate() {
		// do nothing
	}

	/**
	 * @see ImportSource#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

}
