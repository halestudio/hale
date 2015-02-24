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

package eu.esdihumboldt.hale.ui.util.wizard;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for wizard pages for a {@link ConfigurationWizard}.
 * 
 * @author Simon Templer
 * @param <T> the configuration type
 */
public abstract class ConfigurationWizardPage<T> extends WizardPage {

	private final ConfigurationWizard<? extends T> wizard;

	/**
	 * The currently selected wizard page.
	 */
	protected Object currentPage;

	/**
	 * Constructor
	 * 
	 * @param wizard the parent configuration wizard
	 * @param pageName the page name
	 * @param title the title
	 * @param titleImage the title image
	 */
	public ConfigurationWizardPage(ConfigurationWizard<? extends T> wizard, String pageName,
			String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		this.wizard = wizard;
	}

	/**
	 * Constructor
	 * 
	 * @param wizard the configuration wizard
	 * @param pageName the page name
	 */
	public ConfigurationWizardPage(ConfigurationWizard<? extends T> wizard, String pageName) {
		super(pageName);
		this.wizard = wizard;
	}

	/**
	 * @return the wizard
	 */
	@Override
	public ConfigurationWizard<? extends T> getWizard() {
		return wizard;
	}

	/**
	 * Update the configuration
	 * 
	 * @param configuration the WMS configuration
	 * @return if the page is valid
	 */
	public abstract boolean updateConfiguration(T configuration);

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		IWizardContainer container = getContainer();

		if (container instanceof IPageChangeProvider) {
			((IPageChangeProvider) container).addPageChangedListener(new IPageChangedListener() {

				@Override
				public void pageChanged(PageChangedEvent event) {
					if (currentPage == ConfigurationWizardPage.this) {
						onHidePage();
					}
					currentPage = event.getSelectedPage();
					if (event.getSelectedPage() == ConfigurationWizardPage.this) {
						onShowPage();
					}
				}
			});
		}

		createContent(parent);
	}

	/**
	 * Called when this page is shown
	 */
	protected void onShowPage() {
		// do nothing
	}

	/**
	 * Called when this page is hidden
	 */
	protected void onHidePage() {
		// do nothing
	}

	/**
	 * Create the page content
	 * 
	 * @param parent the parent composite
	 */
	protected abstract void createContent(Composite parent);

}
