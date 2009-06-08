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
package eu.esdihumboldt.rcp.wizards.functions.filter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
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

/**
 * This class implemets a main page for the FilterWizard
 * 
 * @author Anna Pitaev, Logica
 * @version $Id$
 */
public class FilterWizardMainPage extends WizardPage{

	private Label CQLLabel;
	private Text CQLEditor;
	
	protected FilterWizardMainPage(String pageName, String title)
			 {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName); 
		setDescription("Enter your CQL-Expression to proceed filter operation."); 
	}

	@Override
	public void createControl(Composite parent) {
		 super.initializeDialogUnits(parent);
        
         //create a composite to hold the widgets
         Composite composite = new Composite(parent, SWT.NULL);
         //create layout for this wizard page
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

         //source area
         this.CQLLabel = new Label(composite, SWT.TITLE);
         this.CQLLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                 | GridData.HORIZONTAL_ALIGN_FILL));
         this.CQLLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
         FontData labelFontData = parent.getFont().getFontData()[0];
         labelFontData.setStyle(SWT.BOLD);
     		
     	this.CQLLabel.setFont(new Font(parent.getDisplay(), labelFontData));

         this.CQLLabel.setText("CQL-Request: ");
         this.CQLEditor = new Text(composite, SWT.BORDER | SWT.WRAP| SWT.MULTI |SWT.V_SCROLL);
         //TODO replace it with the selected source FeatureType value
         this.CQLEditor.setText("Enter your CQL-expression");
         GridData gd = new GridData();
         gd.horizontalAlignment = SWT.FILL;
 		 gd.grabExcessHorizontalSpace = true;
 		 gd.verticalAlignment = SWT.FILL;
 		 gd.grabExcessVerticalSpace = true;
 	     gd.horizontalSpan = 1;
         this.CQLEditor.setLayoutData(gd);
         
         //add listener to update the source feature name
        this.CQLEditor.addModifyListener(new ModifyListener(){
         	 public void modifyText(ModifyEvent event) {
                  String sourceName = CQLEditor.getText();
                  System.out.println(sourceName);
                  if(sourceName.length() == 0) setErrorMessage("CQL-String can not be empty");
                  else setErrorMessage(null);
                  setPageComplete(sourceName.length() > 0);
                  
                }
         	
         });
         
         
        setErrorMessage(null);	// should not initially have error message
		super.setControl(composite);
		
	}

}
