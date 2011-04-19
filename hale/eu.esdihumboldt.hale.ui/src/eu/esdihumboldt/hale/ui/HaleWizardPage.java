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

package eu.esdihumboldt.hale.ui;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract wizard page type with some basic functionality added
 * @param <W> the concrete wizard type 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class HaleWizardPage<W extends Wizard> extends WizardPage {
	
	private IPageChangedListener changeListener;

	/**
	 * @see WizardPage#WizardPage(String, String, ImageDescriptor)
	 */
	protected HaleWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
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
			((IPageChangeProvider) container).addPageChangedListener(changeListener = new IPageChangedListener() {
				
				@Override
				public void pageChanged(PageChangedEvent event) {
					if (event.getSelectedPage() == HaleWizardPage.this) {
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
