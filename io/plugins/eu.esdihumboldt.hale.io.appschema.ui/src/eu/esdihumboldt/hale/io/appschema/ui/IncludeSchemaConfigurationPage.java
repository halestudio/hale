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

package eu.esdihumboldt.hale.io.appschema.ui;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.core.io.impl.StringValue;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator;
import eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page to specify whether the target schema should be included in
 * the exported configuration archive.
 * 
 * <p>
 * The page is automatically skipped when it does not apply (i.e. when the
 * mapping configuration files alone are exported).
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class IncludeSchemaConfigurationPage
		extends
		AbstractConfigurationPage<AbstractAppSchemaConfigurator, ExportWizard<AbstractAppSchemaConfigurator>> {

	private IPageChangingListener changeListener;
	private boolean goingBack = false;
	private Button checkInclude;

	/**
	 * Default constructor.
	 */
	public IncludeSchemaConfigurationPage() {
		super("include.schema.conf");
		setTitle("Include target schema");
		setDescription("Specify whether the target schema should be included in the exported configuration archive");
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// nothing to do... yet
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// nothing to do... yet
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#dispose()
	 */
	@Override
	public void dispose() {
		if (changeListener != null) {
			IWizardContainer container = getContainer();
			if (container instanceof WizardDialog) {
				((WizardDialog) container).removePageChangingListener(changeListener);
			}
		}

		super.dispose();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(AbstractAppSchemaConfigurator provider) {
		provider.setParameter(AppSchemaIO.PARAM_INCLUDE_SCHEMA,
				new StringValue(checkInclude.getSelection()));

		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		Group includeGroup = new Group(page, SWT.NONE);
		includeGroup.setText("Include schema");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(includeGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(includeGroup);

		checkInclude = new Button(includeGroup, SWT.CHECK);
		checkInclude.setText("Include target schema in the archive");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(checkInclude);

		IWizardContainer container = getContainer();
		if (container instanceof WizardDialog) {
			changeListener = new IPageChangingListener() {

				@Override
				public void handlePageChanging(PageChangingEvent event) {
					Object currentPage = event.getCurrentPage();
					Object targetPage = event.getTargetPage();

					if (currentPage instanceof FeatureChainingConfigurationPage
							&& targetPage instanceof IncludeSchemaConfigurationPage) {
						goingBack = true;
					}
					else if (currentPage instanceof ExportSelectTargetPage
							&& targetPage instanceof IncludeSchemaConfigurationPage) {
						goingBack = false;
					}
				}
			};

			WizardDialog dialog = (WizardDialog) container;
			dialog.addPageChangingListener(changeListener);
		}
		else {
			changeListener = null;
		}
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getPreviousPage()
	 */
	@Override
	public IWizardPage getPreviousPage() {
		return getWizard().getPreviousPage(this);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		IContentType contentType = getWizard().getContentType();
		if (!contentType.getId().equals(AppSchemaIO.CONTENT_TYPE_ARCHIVE)
				&& !contentType.getId().equals(AppSchemaIO.CONTENT_TYPE_REST)) {
			// configuration does not apply, skip page
			if (!goingBack) {
				getContainer().showPage(getNextPage());
			}
			else {
				getContainer().showPage(getPreviousPage());
			}
		}

	}
}
