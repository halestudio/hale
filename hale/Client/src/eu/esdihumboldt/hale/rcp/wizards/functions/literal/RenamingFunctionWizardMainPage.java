/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.literal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.SchemaSelectionHelper;

/**
 * This {@link WizardPage} is used to define a renaming mapping.
 * 
 * @author Anna Pitaev
 * @version {$Id}
 */
public class RenamingFunctionWizardMainPage 
		extends WizardPage implements
		ISelectionListener {

	//private static Logger _log = Logger.getLogger(RenamingFunctionWizardMainPage.class);

	protected Text sourceFeatureTypeName;
	protected Text targetFeatureTypeName;

	private Label sourceFeatureTypeLabel;
	private Label targetFeatureTypeLabel;

	protected RenamingFunctionWizardMainPage(String pageName, String title) {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName);
		setDescription("Enter parameters to adopt the source FeatureType to the target Naming Convention.");

	}

	/**
	 * The parent methods where all controls are created for this
	 * {@link WizardPage}.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {

		super.initializeDialogUnits(parent);
		this.setPageComplete(true);
		// create a composite to hold the widgets
		Composite composite = new Composite(parent, SWT.NULL);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		composite.setLayout(gl);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setFont(parent.getFont());

		// source area
		this.sourceFeatureTypeLabel = new Label(composite, SWT.TITLE);
		this.sourceFeatureTypeLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		this.sourceFeatureTypeLabel.setSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		FontData labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);

		this.sourceFeatureTypeLabel.setFont(new Font(parent.getDisplay(),
				labelFontData));

		this.sourceFeatureTypeLabel.setText("Source Type");
		this.sourceFeatureTypeName = new Text(composite, SWT.BORDER);
		// TODO replace it with the selected source FeatureType value
		this.sourceFeatureTypeName.setText(SchemaSelectionHelper
				.getSchemaSelection().getFirstSourceItem().getName().getLocalPart());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		this.sourceFeatureTypeName.setLayoutData(gd);

		// add listener to update the source feature name
		this.sourceFeatureTypeName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String sourceName = sourceFeatureTypeName.getText();
				System.out.println(sourceName);
				if (sourceName.length() == 0)
					setErrorMessage("Source Name can not be empty");
				else if (sourceFeatureTypeName.getText().equals(
						targetFeatureTypeName.getText()))
					setErrorMessage("Source and Target Name cannot be the same");
				else
					setErrorMessage(null);
				setPageComplete(sourceName.length() > 0
						&& targetFeatureTypeName.getText().length() > 0
						&& (!sourceFeatureTypeName.getText().equals(
								targetFeatureTypeName.getText())));

			}

		});

		// target area
		this.targetFeatureTypeLabel = new Label(composite, SWT.BOLD);
		this.targetFeatureTypeLabel.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		this.targetFeatureTypeLabel.setSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		this.targetFeatureTypeLabel.setFont(new Font(parent.getDisplay(),
				labelFontData));
		this.targetFeatureTypeLabel.setText("Target Type");
		this.targetFeatureTypeName = new Text(composite, SWT.BORDER);
		// TODO replace it with the selected target FeatureType value
		this.targetFeatureTypeName.setText(SchemaSelectionHelper
				.getSchemaSelection().getFirstTargetItem().getName().getLocalPart());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		this.targetFeatureTypeName.setLayoutData(gd);

		// add listener to update the target feature name
		this.targetFeatureTypeName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String targetName = targetFeatureTypeName.getText();

				if (targetName.length() == 0)
					setErrorMessage("Target Name can not be empty");

				else if (sourceFeatureTypeName.getText().equals(
						targetFeatureTypeName.getText()))
					setErrorMessage("Source and Target Name cannot be the same");
				else
					setErrorMessage(null);
				setPageComplete(targetName.length() > 0
						&& sourceFeatureTypeName.getText().length() > 0
						&& (!sourceFeatureTypeName.getText().equals(
								targetFeatureTypeName.getText())));
			}

		});
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService().addSelectionListener(this);
		setErrorMessage(null); // should not initially have error message
		super.setControl(composite);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	/*
	 * @Override public boolean isPageComplete() {
	 * 
	 * if (this.sourceFeatureTypeName != null && this.targetFeatureTypeName !=
	 * null){ //TODO add error handling if source name = target name_
	 * _log.debug("sourceFeatureType " + this.sourceFeatureTypeName.getText());
	 * _log.debug("sourceFeatureType " + this.sourceFeatureTypeName.getText());
	 * _log.debug("Page is complete."); return true; }else { //TODO add error
	 * handling if source and/or target name are empty. return false; }
	 * 
	 * }
	 */
	/*
	 * if (this.fileFieldEditor != null && this.wfsFieldEditor != null) {
	 * _log.debug("fileFieldEditor: " + this.fileFieldEditor.getStringValue());
	 * try { if (this.useWfsRadio.getSelection()) { // test whether content of
	 * the WFS Field Editor validates to URL. String test =
	 * this.wfsFieldEditor.getStringValue(); if (test != null &&
	 * !test.equals("")) { new URL(test);
	 * _log.debug("wfsFieldEditor URL was OK."); } else { return false; } } else
	 * { // test whether content of the File Field Editor validates to URI.
	 * String test = this.fileFieldEditor.getStringValue(); if (test != null &&
	 * !test.equals("")) { new URI(test.replaceAll("\\\\", "/"));
	 * _log.debug("fileFieldEditor URI was OK."); } else { return false; } } }
	 * catch (Exception ex) { ex.printStackTrace(); return false; }
	 * _log.debug("Page is complete."); return true; } else { return false; }
	 * return true; }
	 */

	/*
	 * @Override public void handleEvent(Event e) { if (e.widget ==
	 * this.sourceFeatureTypeName){
	 * System.out.println(this.sourceFeatureTypeName.getSelectionText()); }
	 * 
	 * }
	 */
	
	/**
	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final Object selectionObject = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selectionObject != null) {

				TreeItem treeItem = (TreeItem) selectionObject;
				String selectedFeatureType = treeItem.getText();
				System.out.println("From RenamingFunctionWizard: "
						+ selectedFeatureType);
			}
		}

	}

}
