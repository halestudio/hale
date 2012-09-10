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

package eu.esdihumboldt.hale.ui.io.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Decorator for {@link WizardPage}
 * 
 * @author Simon Templer
 */
public abstract class WizardPageDecorator extends WizardPage {

	private final WizardPage decoratee;

	/**
	 * Create a decorator for the given wizard page.
	 * 
	 * @param decoratee the wizard page to decorate
	 */
	public WizardPageDecorator(WizardPage decoratee) {
		super(decoratee.getName());
		this.decoratee = decoratee;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		decoratee.createControl(parent);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return decoratee.canFlipToNextPage();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getImage()
	 */
	@Override
	public Image getImage() {
		return decoratee.getImage();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getName()
	 */
	@Override
	public String getName() {
		return decoratee.getName();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		return decoratee.getNextPage();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getPreviousPage()
	 */
	@Override
	public IWizardPage getPreviousPage() {
		return decoratee.getPreviousPage();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getShell()
	 */
	@Override
	public Shell getShell() {
		return decoratee.getShell();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		decoratee.dispose();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return decoratee.equals(obj);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#getControl()
	 */
	@Override
	public Control getControl() {
		return decoratee.getControl();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getWizard()
	 */
	@Override
	public IWizard getWizard() {
		return decoratee.getWizard();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
	 */
	@Override
	public String getDescription() {
		return decoratee.getDescription();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return decoratee.getErrorMessage();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#getMessage()
	 */
	@Override
	public String getMessage() {
		return decoratee.getMessage();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#getMessageType()
	 */
	@Override
	public int getMessageType() {
		return decoratee.getMessageType();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
	 */
	@Override
	public String getTitle() {
		return decoratee.getTitle();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return decoratee.hashCode();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return decoratee.isPageComplete();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		decoratee.setDescription(description);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String newMessage) {
		decoratee.setErrorMessage(newMessage);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setImageDescriptor(org.eclipse.jface.resource.ImageDescriptor)
	 */
	@Override
	public void setImageDescriptor(ImageDescriptor image) {
		decoratee.setImageDescriptor(image);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setMessage(java.lang.String,
	 *      int)
	 */
	@Override
	public void setMessage(String newMessage, int newType) {
		decoratee.setMessage(newMessage, newType);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setPageComplete(boolean)
	 */
	@Override
	public void setPageComplete(boolean complete) {
		decoratee.setPageComplete(complete);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public void setPreviousPage(IWizardPage page) {
		decoratee.setPreviousPage(page);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		decoratee.setTitle(title);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setWizard(org.eclipse.jface.wizard.IWizard)
	 */
	@Override
	public void setWizard(IWizard newWizard) {
		decoratee.setWizard(newWizard);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#toString()
	 */
	@Override
	public String toString() {
		return decoratee.toString();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#performHelp()
	 */
	@Override
	public void performHelp() {
		decoratee.performHelp();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
	 */
	@Override
	public void setMessage(String newMessage) {
		decoratee.setMessage(newMessage);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		decoratee.setVisible(visible);
	}

}
