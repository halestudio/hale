/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
