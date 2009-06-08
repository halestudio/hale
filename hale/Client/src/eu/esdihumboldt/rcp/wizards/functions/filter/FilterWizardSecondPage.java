package eu.esdihumboldt.rcp.wizards.functions.filter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FilterWizardSecondPage extends WizardPage {
	
	private Label featureTypeLabel;
	private Text featureTypeEditor;
	private Label extentLabel;
	private Text extentXmin;
	private Text extentYmin;
	private Text extentXmax;
	private Text extentYmax;
	private Text extentSRS;
	private Label propertyLabel;
	private Label operatorsLabel;
	private Label comparisonValueLabel;
	private Text comparisonValue;
	
	protected FilterWizardSecondPage(String pageName, String title)
			 {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName); 
		setDescription("Configure your CQL-Expression to proceed filter operation."); 
	}

	@Override
	public void createControl(Composite parent) {
		 super.initializeDialogUnits(parent);
        
         //create a composite to hold the widgets
         Composite composite = new Composite(parent, SWT.NULL);
         //create layout for this wizard page
         GridLayout gl = new GridLayout();
         gl.numColumns = 5;
         gl.marginLeft = 0;
         gl.marginTop = 20;
         gl.marginRight = 70;
         composite.setLayout(gl);
         composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                 | GridData.HORIZONTAL_ALIGN_FILL));
         composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
         composite.setFont(parent.getFont());

         //Feature Type  area
         this.featureTypeLabel = new Label(composite, SWT.TITLE);
         this.featureTypeLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                 | GridData.HORIZONTAL_ALIGN_FILL));
         this.featureTypeLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
         FontData labelFontData = parent.getFont().getFontData()[0];
         labelFontData.setStyle(SWT.BOLD);
     		
     	this.featureTypeLabel.setFont(new Font(parent.getDisplay(), labelFontData));

         this.featureTypeLabel.setText("FeatureType: ");
         //this.featureTypeEditor = new Text(composite, SWT.BORDER | SWT.WRAP| SWT.MULTI |SWT.V_SCROLL);
         this.featureTypeEditor = new Text(composite, SWT.BORDER);
         
         //TODO replace it with the selected source FeatureType value
         this.featureTypeEditor.setText("DefaultFeatureType");
         GridData gd = new GridData();
         gd.horizontalAlignment = SWT.FILL;
 		 gd.grabExcessHorizontalSpace = true;
 		 /*gd.verticalAlignment = SWT.FILL;
 		 gd.grabExcessVerticalSpace = true;*/
 	     gd.horizontalSpan = 4;
         this.featureTypeEditor.setLayoutData(gd);
         
         //add listener to update the source feature name
        this.featureTypeEditor.addModifyListener(new ModifyListener(){
         	 public void modifyText(ModifyEvent event) {
                  String sourceName = featureTypeEditor.getText();
                  System.out.println(sourceName);
                  if(sourceName.length() == 0) setErrorMessage("FeatureType  can not be empty");
                  else setErrorMessage(null);
                  setPageComplete(sourceName.length() > 0);
                  
                }
         	
         });
        this.extentLabel = new Label(composite, SWT.TITLE);
        this.extentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        this.extentLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        labelFontData = parent.getFont().getFontData()[0];
        labelFontData.setStyle(SWT.BOLD);
    	this.extentLabel.setFont(new Font(parent.getDisplay(), labelFontData));

        this.extentLabel.setText("Extent: ");
        //Xmin,Ymin,Xmax, Ymax area
        this.extentXmin = new Text(composite, SWT.BORDER);
        this.extentXmin.setText("X_MIN");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	     this.extentXmin.setLayoutData(gd);
        
        this.extentYmin = new Text(composite, SWT.BORDER);
        this.extentYmin.setText("Y_MIN");
         gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	     this.extentYmin.setLayoutData(gd);
        
        this.extentXmax = new Text(composite, SWT.BORDER);
        this.extentXmax.setText("X_MAX");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	    this.extentXmax.setLayoutData(gd);
        
        this.extentYmax = new Text(composite, SWT.BORDER);
        this.extentYmax.setText("Y_MAX");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	     this.extentYmax.setLayoutData(gd);
        
        Label placeHolder = new Label(composite, SWT.TITLE);
        placeHolder.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        placeHolder.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        this.extentSRS = new Text(composite, SWT.BORDER);
        this.extentSRS.setText("SRS");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 4;
        this.extentSRS.setLayoutData(gd);
        
         
        //by properties 
        this.propertyLabel = new Label(composite, SWT.TITLE);
        this.propertyLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        this.propertyLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        labelFontData = parent.getFont().getFontData()[0];
        labelFontData.setStyle(SWT.BOLD);
        this.propertyLabel.setFont(new Font(parent.getDisplay(), labelFontData));
        this.propertyLabel.setText("By Property: ");
        final Combo attributesCombo = new Combo(composite, SWT.NULL);
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 4;
        attributesCombo.setLayoutData(gd);
        attributesCombo.setText("select attribute");
        //TODO read attributes from the schema service
        String [] attributes = new String[]{"Attribute1", "Attribute2", "Attribute3", "Attribute4", "Attribute5"};
        for (int i=0; i< attributes.length; i++){
        	attributesCombo.add(attributes[i]);
        }
        
        //operators
        this.operatorsLabel = new Label(composite, SWT.TITLE);
        this.operatorsLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        this.operatorsLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        labelFontData = parent.getFont().getFontData()[0];
        labelFontData.setStyle(SWT.BOLD);
        this.operatorsLabel.setFont(new Font(parent.getDisplay(), labelFontData));
        this.operatorsLabel.setText("OperatorType: ");
        final Combo operatorsCombo = new Combo(composite, SWT.NULL);
        operatorsCombo.setLayoutData(gd);
        operatorsCombo.setText("select Operator Type");
        //TODO read attributes from the schema service
        String [] operators = new String[]{"PropertyIsEqualTo", "PropertyIsNotEqualTo", "PropertyIsLessThan", "PropertyIsGreaterThan", "PropertyIsLessThanOrEqualTo","PropertyIsGreaterThanOrEqualTo", "PropertyIsLike", "PropertyIsNull", "PropertyIsBetween" };
        for (int i=0; i< operators.length; i++){
        	operatorsCombo.add(operators[i]);
        }
        //comparison value
        
       
        this.comparisonValueLabel = new Label(composite, SWT.TITLE);
        this.comparisonValueLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        this.comparisonValueLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        labelFontData = parent.getFont().getFontData()[0];
        labelFontData.setStyle(SWT.BOLD);
    		
    	this.comparisonValueLabel.setFont(new Font(parent.getDisplay(), labelFontData));

        this.comparisonValueLabel.setText("Comparison Value: ");
        //this.featureTypeEditor = new Text(composite, SWT.BORDER | SWT.WRAP| SWT.MULTI |SWT.V_SCROLL);
        this.comparisonValue = new Text(composite, SWT.BORDER);
        
        //TODO replace it with the selected source FeatureType value
        this.comparisonValue.setText("Comparison Value");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	     gd.horizontalSpan = 4;
        this.comparisonValue.setLayoutData(gd);
        
        
        
        
        
     
         
        setErrorMessage(null);	// should not initially have error message
		super.setControl(composite);
		
	}


}
