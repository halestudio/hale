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

package eu.esdihumboldt.hale.ui.io.target;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.wizard.WizardPage;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.ui.io.ExportTarget;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * Base class for export target implementations. Implementors must call
 * {@link #setValid(boolean)} and {@link #setContentType(IContentType)} to
 * update the page state.
 * 
 * @author Simon Templer
 * @param <P> the export provider type
 */
public abstract class AbstractTarget<P extends ExportProvider> implements ExportTarget<P> {

	private ExportWizard<? extends P> wizard;
	private WizardPage page;
	private Set<IContentType> allowedContentTypes;

	@Override
	public void setParent(ExportWizard<? extends P> wizard, WizardPage page) {
		this.wizard = wizard;
		this.page = page;
	}

	/**
	 * @return the export wizard
	 */
	public ExportWizard<? extends P> getWizard() {
		return wizard;
	}

	/**
	 * @return the wizard page
	 */
	public WizardPage getPage() {
		return page;
	}

	@Override
	public void dispose() {
		// override me
	}

	/**
	 * Set the content type of the selected target.
	 * 
	 * @param contentType the target content type
	 */
	protected void setContentType(IContentType contentType) {
		wizard.setContentType(contentType);
	}

	/**
	 * Call to state if the target is valid.
	 * 
	 * @param valid if the target is valid
	 */
	protected void setValid(boolean valid) {
		page.setPageComplete(valid);
	}

	/**
	 * Get the allowed content types.
	 * 
	 * @return the allowed content types, by default the supported content types
	 *         of the selected provider.
	 */
	protected Set<IContentType> getAllowedContentTypes() {
		return allowedContentTypes;
	}

	@Override
	public void setAllowedContentTypes(Collection<IContentType> contentTypes) {
		this.allowedContentTypes = new HashSet<>(contentTypes);
	}

	@Override
	public void onShowPage(boolean firstShow) {
		// override me
	}

}
