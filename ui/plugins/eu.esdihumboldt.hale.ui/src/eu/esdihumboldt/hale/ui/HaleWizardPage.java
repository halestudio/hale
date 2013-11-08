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

package eu.esdihumboldt.hale.ui;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * Abstract wizard page type with some basic functionality added, can only be
 * added to wizards with the given wizard type <W>
 * 
 * @param <W> the concrete wizard type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class HaleWizardPage<W extends Wizard> extends WizardPage {

	private IPageChangedListener changeListener;

	private boolean wasShown = false;

	/**
	 * @see WizardPage#WizardPage(String, String, ImageDescriptor)
	 */
	protected HaleWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see WizardPage#WizardPage(String)
	 */
	protected HaleWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see WizardPage#getWizard()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public W getWizard() {
		return (W) super.getWizard();
	}

	/**
	 * @see WizardPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		IWizardContainer container = getContainer();

		if (container instanceof IPageChangeProvider) {
			((IPageChangeProvider) container)
					.addPageChangedListener(changeListener = new IPageChangedListener() {

						@Override
						public void pageChanged(PageChangedEvent event) {
							if (event.getSelectedPage() == HaleWizardPage.this) {
								if (wasShown) {
									onShowPage(false);
								}
								else {
									wasShown = true;
									onShowPage(true);
								}
							}
						}

					});
		}

		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new FillLayout());

		createContent(page);

		setControl(page);
	}

	/*
	 * Make method public.
	 */
	@Override
	public IWizardContainer getContainer() {
		return super.getContainer();
	}

	/**
	 * @see DialogPage#performHelp()
	 */
	@Override
	public void performHelp() {
		boolean closed = false;
		if (getContainer() instanceof TrayDialog) {
			TrayDialog trayDialog = (TrayDialog) getContainer();
			if (trayDialog.getTray() != null) {
				closed = trayDialog.getTray().getClass().getSimpleName().equals("HelpTray");
				trayDialog.closeTray();
			}
		}
		if (!closed && getHelpContext() != null)
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(getHelpContext());
	}

	/**
	 * Returns the help context for this page. Default is <code>null</code>.
	 * 
	 * @return the help context for this page or <code>null</code>
	 */
	public String getHelpContext() {
		return null;
	}

	/**
	 * Called when this page is shown
	 * 
	 * @param firstShow specifies if it is the first time the page is shown
	 *            since its creation
	 */
	protected void onShowPage(boolean firstShow) {
		// do nothing
	}

	/**
	 * Create the page content
	 * 
	 * @param page the page composite, implementors may assign a custom layout
	 *            to this composite
	 */
	protected abstract void createContent(Composite page);

	/**
	 * @see DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		if (changeListener != null) {
			IWizardContainer container = getContainer();
			if (container instanceof IPageChangeProvider) {
				((IPageChangeProvider) container).removePageChangedListener(changeListener);
			}
		}

		super.dispose();
	}

}
