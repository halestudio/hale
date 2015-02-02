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

package eu.esdihumboldt.hale.ui.io;

import java.util.Collection;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.target.internal.ExportTargetExtension;
import eu.esdihumboldt.hale.ui.io.target.internal.ExportTargetFactory;

/**
 * Wizard page that allows selecting a target file
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExportSelectTargetPage<P extends ExportProvider, W extends ExportWizard<P>> extends
		AbstractConfigurationPage<P, W> {

	private static final ALogger log = ALoggerFactory.getLogger(ExportSelectTargetPage.class);

	private Composite page;
	private Composite main;
	private ExportTarget<? super P> currentTarget;
	private String currentTargetId;

	private Collection<IContentType> allowedContentTypes;

	/**
	 * Default constructor
	 */
	public ExportSelectTargetPage() {
		super("export.selTarget");
		setTitle("Export destination");
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;

		GridLayoutFactory.fillDefaults().applyTo(page);

		// content will be created later based on selected provider
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		updateContentTypes();

		// determine which target to show
		ExportTargetFactory targetFactory = ExportTargetExtension.getInstance().forProvider(
				getWizard().getProviderType(), getWizard().getProviderFactory().getIdentifier());

		if (currentTargetId != null && currentTargetId.equals(targetFactory.getIdentifier())) {
			// if it is the same as the current, just call onShowPage
			currentTarget.onShowPage(false);
		}
		else {
			// otherwise, remove the old content, create the new target
			if (main != null) {
				main.dispose();
			}
			if (currentTarget != null) {
				currentTarget.dispose();
			}

			try {
				currentTarget = (ExportTarget<? super P>) targetFactory.createExtensionObject();
				currentTargetId = targetFactory.getIdentifier();
				main = new Composite(page, SWT.NONE);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(main);

				currentTarget.setParent(getWizard(), this);
				currentTarget.createControls(main);
				currentTarget.setAllowedContentTypes(allowedContentTypes);

				currentTarget.onShowPage(true);
			} catch (Exception e) {
				log.error("Could not create export target", e);
			}

			// re-layout
			page.layout(true);
		}
	}

	/**
	 * Update the content types available. Called each time the page is shown.
	 */
	protected void updateContentTypes() {
		setAllowedContentTypes(getWizard().getProviderFactory().getSupportedTypes());
	}

	/**
	 * Set the allowed content types.
	 * 
	 * @param contentTypes the content types
	 */
	public void setAllowedContentTypes(Collection<IContentType> contentTypes) {
		this.allowedContentTypes = contentTypes;
		if (currentTarget != null) {
			currentTarget.setAllowedContentTypes(contentTypes);
		}
	}

	/**
	 * @return the currently active export target
	 */
	@Nullable
	public ExportTarget<?> getExportTarget() {
		return currentTarget;
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		if (currentTarget != null) {
			return currentTarget.updateConfiguration(provider);
		}
		else {
			return false;
		}
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

}
