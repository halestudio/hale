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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract WFS wizard page
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @param <T> the WFS configuration type
 */
public abstract class AbstractWfsPage<T extends WfsConfiguration> extends WizardPage {

	private final T configuration;

	private Object currentPage = null;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration
	 * @param pageName the page name
	 * @param title the title
	 * @param titleImage the title image
	 */
	public AbstractWfsPage(T configuration, String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);

		this.configuration = configuration;
	}

	/**
	 * Constructor
	 * 
	 * @param configuration the configuration server
	 * @param pageName the page name
	 */
	public AbstractWfsPage(T configuration, String pageName) {
		super(pageName);

		this.configuration = configuration;
	}

	/**
	 * Get the WMS configuration
	 * 
	 * @return the WMS configuration
	 */
	public T getConfiguration() {
		return configuration;
	}

	/**
	 * Update the WMS configuration
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
					if (currentPage == AbstractWfsPage.this) {
						onHidePage();
					}
					currentPage = event.getSelectedPage();
					if (event.getSelectedPage() == AbstractWfsPage.this) {
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
