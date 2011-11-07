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

package eu.esdihumboldt.hale.ui.util.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Wizard selection page based on a structured viewer.
 * @author Simon Templer
 */
public abstract class ViewerWizardSelectionPage extends WizardSelectionPage {

	private StructuredViewer viewer;

	/**
	 * @see WizardSelectionPage#WizardSelectionPage(String)
	 */
	protected ViewerWizardSelectionPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();

		// create composite for page.
		Composite outerContainer = new Composite(parent, SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(GridDataFactory.fillDefaults()
				.grab(true, true).create());
		outerContainer.setFont(font);

		viewer = createViewer(outerContainer);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults()
				.grab(true, true).create());
		
		// wire viewer
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				viewerSelectionChanged(event.getSelection());    	       			
			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener(){
	    	@Override
			public void doubleClick(DoubleClickEvent event) {
	    		doubleClicked(event);
	    	}
	    });

		Dialog.applyDialogFont(outerContainer);

		setControl(outerContainer);
	}
	
	/**
	 * Called when the viewer selection has changed.
	 * @param selection the current selection
	 */
	protected void viewerSelectionChanged(ISelection selection){
        setErrorMessage(null);
        IStructuredSelection ss = (IStructuredSelection) selection;
        Object sel = ss.getFirstElement();
        if (sel instanceof IWizardNode){
	        IWizardNode currentWizardSelection = (IWizardNode) sel;        
	        updateSelectedNode(currentWizardSelection);
        } else {
			updateSelectedNode(null);
		}
    }
	
	/**
	 * Update the selected node
	 * @param wizardNode the selected wizard node
	 */
    private void updateSelectedNode(IWizardNode wizardNode){
        setErrorMessage(null);
        if (wizardNode == null) {
        	updateMessage();
            setSelectedNode(null);
            return;
        }

        // set the message based on the wizard description
        if (wizardNode instanceof ExtendedWizardNode) {
        	setMessage(((ExtendedWizardNode) wizardNode).getDescription()); 
        }
        else {
        	setMessage(null);
        }
        
        // set the selected node
        setSelectedNode(wizardNode);
    }
    
    /**
	 * @see WizardSelectionPage#setSelectedNode(IWizardNode)
	 */
	@Override
	protected void setSelectedNode(IWizardNode node) {
		if (node != null) {
			String message = acceptWizard(node);
			
			if (message != null) {
				// display warning message
	        	setMessage(message, WARNING);
	        	// disable next
	        	super.setSelectedNode(null);
	        	return;
			}
		}
		
		super.setSelectedNode(node);
	}

	/**
     * Accepts or doesn't accept a wizard node as a valid selection.
	 * @param wizardNode the wizard node
	 * @return <code>null</code> if the node is accepted or a reason
	 * why it is not accepted.
	 */
	protected String acceptWizard(IWizardNode wizardNode) {
		return null;
	}

	/**
     * Update the selected node based on the viewer selection.
     */
    protected void updateMessage(){
    	if (viewer != null){
    		ISelection selection = viewer.getSelection();
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object sel = ss.getFirstElement();
            if (sel instanceof IWizardNode){
               	updateSelectedNode((IWizardNode)sel);
            }
            else{
            	setSelectedNode(null);
            }
    	} else {
			setMessage(null);
		}
    }
	
    /**
     * Called when a double click in the viewer occurs.
     * @param event the double click event
     */
	protected void doubleClicked(DoubleClickEvent event){
    	ISelection selection = event.getViewer().getSelection();
	    IStructuredSelection ss = (IStructuredSelection) selection;
    	viewerSelectionChanged(ss);
		
		Object element = ss.getFirstElement();
		if (element instanceof IWizardNode) {
			if (canFlipToNextPage()) {
				getContainer().showPage(getNextPage());
				return;
			}
		}    	
        getContainer().showPage(getNextPage());   			
    }

	/**
	 * Create the structured viewer and set it up with label and content 
	 * providers as well as the input. The viewer must provide
	 * {@link IStructuredSelection}s with {@link IWizardNode}s.
	 * @param parent the parent composite
	 * @return the viewer
	 */
	protected abstract StructuredViewer createViewer(Composite parent);

}
