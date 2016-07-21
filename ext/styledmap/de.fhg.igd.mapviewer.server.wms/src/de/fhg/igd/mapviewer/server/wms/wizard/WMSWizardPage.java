/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.wms.wizard;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;

/**
 * Basic wizard page for WMS configuration
 * 
 * @param <T> the WMS client configuration type
 * @author Simon Templer
 */
public abstract class WMSWizardPage<T extends WMSConfiguration> extends WizardPage {

	private final T configuration;

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS configuration
	 * @param pageName the page name
	 * @param title the title
	 * @param titleImage the title image
	 */
	public WMSWizardPage(T configuration, String pageName, String title,
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
	public WMSWizardPage(T configuration, String pageName) {
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
					if (event.getSelectedPage() == WMSWizardPage.this) {
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
	 * Create the page content
	 * 
	 * @param parent the parent composite
	 */
	protected abstract void createContent(Composite parent);

}
