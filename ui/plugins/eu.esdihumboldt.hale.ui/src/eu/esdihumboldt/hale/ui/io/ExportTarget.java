/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io;

import java.util.Collection;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;

/**
 * Component for selecting an export target.
 * 
 * @author Simon Templer
 * @param <P> the export provider type
 */
public interface ExportTarget<P extends ExportProvider> {

	/**
	 * Sets the containing wizard and wizard page. The wizard page may be used
	 * for displaying messages.
	 * 
	 * @param wizard the export wizard
	 * @param page the wizard page
	 */
	public void setParent(ExportWizard<? extends P> wizard, WizardPage page);

	/**
	 * Create the controls that enable the user to define the import source.
	 * {@link #setParent(ExportWizard, WizardPage)} must have been called before
	 * calling this method.
	 * 
	 * @param parent the parent composite, implementors may assign a custom
	 *            layout to this composite
	 */
	public void createControls(Composite parent);

	/**
	 * Called when the page is shown, e.g. to update regarding changes on other
	 * pages.
	 * 
	 * @param firstShow if the page is shown the first time
	 */
	public void onShowPage(boolean firstShow);

	/**
	 * Update the configuration (of the I/O provider). This is executed right
	 * before the execution.
	 * 
	 * @param provider the I/O provider to update
	 * @return if the source is valid and updating the provider was successful
	 */
	public boolean updateConfiguration(P provider);

	/**
	 * Perform clean-up when the instance is no longer used.
	 */
	public void dispose();

	/**
	 * Set the allowed content types.
	 * 
	 * @param contentTypes the allowed content types
	 */
	public void setAllowedContentTypes(Collection<IContentType> contentTypes);

}
