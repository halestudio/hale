package eu.esdihumboldt.hale.rcp.wizards.functions.filter;

import javax.smartcardio.ATR;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.views.model.AttributeView;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.wizards.functions.literal.RenamingFunctionWizard;

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
	private TreeViewer sourceViewer;
	private Text selectedAttribute;
	private Text selectedOperator;
	
	
	private static Logger _log = Logger.getLogger(FilterWizardSecondPage.class);
	protected FilterWizardSecondPage(String pageName, String title)
			 {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName); 
		setDescription("Configure your CQL-Expression to proceed filter operation."); 
	}

	@Override
	public void createControl(Composite parent) {
		 super.initializeDialogUnits(parent);
		 
		 //set source viewer
		 this.sourceViewer = getModelNavigationView().getSourceSchemaViewer();
        
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
         this.featureTypeEditor.setText(getSourceViewer().getTree().getSelection()[0].getText());
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
                  _log.debug("Source Feature Type " + sourceName);
                 /* if(sourceName.length() == 0) setErrorMessage("FeatureType  can not be empty");
                  else setErrorMessage(null);
                  setPageComplete(sourceName.length() > 0);*/
                  setPageComplete(true);
                  
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
	     //add listener to update XMIN
	     this.extentXmin.addModifyListener(new ModifyListener(){
	    	public void modifyText(ModifyEvent event){
	    	 String x_min = extentXmin.getText();
	    	_log.debug("Extent x_min " + x_min);
	    	setPageComplete(true);
	    	}
	     }
	     );
	     
        
        this.extentYmin = new Text(composite, SWT.BORDER);
        this.extentYmin.setText("Y_MIN");
         gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	     this.extentYmin.setLayoutData(gd);
	     
	     //add listener to update YMIN
	     this.extentYmin.addModifyListener(new ModifyListener(){
	    	public void modifyText(ModifyEvent event){
	    	 String y_min = extentYmin.getText();
	    	_log.debug("Extent y_min " + y_min);
	    	setPageComplete(true);
	    	}
	     }
	     );
        
        this.extentXmax = new Text(composite, SWT.BORDER);
        this.extentXmax.setText("X_MAX");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	    this.extentXmax.setLayoutData(gd);
	    
	    //add listener to update XMAX
	     this.extentXmax.addModifyListener(new ModifyListener(){
	    	public void modifyText(ModifyEvent event){
	    	 String x_max = extentXmax.getText();
	    	_log.debug("Extent x_max " + x_max);
	    	setPageComplete(true);
	    	}
	     }
	     );
        
        this.extentYmax = new Text(composite, SWT.BORDER);
        this.extentYmax.setText("Y_MAX");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 1;
		 gd.grabExcessHorizontalSpace = true;
		 /*gd.verticalAlignment = SWT.FILL;
		 gd.grabExcessVerticalSpace = true;*/
	     this.extentYmax.setLayoutData(gd);
	     //add listener to update YMAX
	     this.extentYmax.addModifyListener(new ModifyListener(){
	    	public void modifyText(ModifyEvent event){
	    	 String y_max = extentYmax.getText();
	    	_log.debug("Extent y_max " + y_max);
	    	setPageComplete(true);
	    	}
	     }
	     );
        
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
        //add listener to update SRS
	     this.extentSRS.addModifyListener(new ModifyListener(){
	    	public void modifyText(ModifyEvent event){
	    	 String srs = extentSRS.getText();
	    	_log.debug("Extent srs " + srs);
	    	setPageComplete(true);
	    	}
	     }
	     );
        
         
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
        selectedAttribute = new Text(attributesCombo,SWT.NULL); 
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 4;
        attributesCombo.setLayoutData(gd);
        attributesCombo.setText("select attribute");
        // read attributes from the schema service
       TableItem [] attribs = getAttributeView().getSourceAttributeViewer().getTable().getItems();
        for (int i=0; i<attribs.length;i++){
        
        	attributesCombo.add(attribs[i].getText());
        }
        //add listener to select attribute
        attributesCombo.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
		      int selectionIndex = attributesCombo.getSelectionIndex();
		      _log.debug("Selected Property: " + attributesCombo.getItem(selectionIndex));
		     
		      selectedAttribute.setText(attributesCombo.getItem(selectionIndex));
		      
				
			}
        	
        });        
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
        selectedOperator = new Text(operatorsCombo, SWT.NULL);
        operatorsCombo.setLayoutData(gd);
        operatorsCombo.setText("select Operator Type");
        //TODO read attributes from the schema service
        String [] operators = new String[]{"PropertyIsEqualTo", "PropertyIsNotEqualTo", "PropertyIsLessThan", "PropertyIsGreaterThan", "PropertyIsLessThanOrEqualTo","PropertyIsGreaterThanOrEqualTo", "PropertyIsLike", "PropertyIsNull", "PropertyIsBetween" };
        for (int i=0; i< operators.length; i++){
        	operatorsCombo.add(operators[i]);
        }
        
        //add listener to select operator
        operatorsCombo.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
		      int selectionIndex = operatorsCombo.getSelectionIndex();
		      _log.debug("Selected Operator: " + operatorsCombo.getItem(selectionIndex));
		      selectedOperator.setText(operatorsCombo.getItem(selectionIndex));
				
			}
        	
        });        
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
        //add listener to update the comparison value
        this.comparisonValue.addModifyListener(new ModifyListener(){
         	 public void modifyText(ModifyEvent event) {
                  String comparison = comparisonValue.getText();
                  _log.debug("Comparison value " + comparison);
                  
                 /* if(sourceName.length() == 0) setErrorMessage("FeatureType  can not be empty");
                  else setErrorMessage(null);
                  setPageComplete(sourceName.length() > 0);*/
                  setPageComplete(true);
                  
                }
         	
         });
        
        
        
        
        
     
         
        setErrorMessage(null);	// should not initially have error message
		super.setControl(composite);
		
	}

	/**
	 * 
	 * @return CQL expression based on the pageinput.
	 */
	public String buildCQL() {
		//build using property, comparison operator and comparison value
	
		//get attribute name - String between ::
		String fullPropertyName = selectedAttribute.getText();
		int firstIndexOfColon = fullPropertyName.indexOf(":");
		String attributeValue = comparisonValue.getText();
		String propertyLocalName = fullPropertyName.substring(firstIndexOfColon+1);	
		if ((selectedOperator.getText()).equals(CQLOperators.PropertyIsLike.name()))  attributeValue = "'" + attributeValue + "'";
		String CQLexpression = propertyLocalName + " " + getCQLOperator(CQLOperators.valueOf(selectedOperator.getText())) + " " + attributeValue;
		_log.debug("CQL Expression "+ CQLexpression);
		
		return CQLexpression;
	}

	/**
	 * @param text
	 * @return
	 */
	private String getCQLOperator(CQLOperators operator) {
		String cqlOperator = "";
		switch (operator) {
		case PropertyIsEqualTo:
			cqlOperator = "=";		
			break;
		case PropertyIsNotEqualTo:
			cqlOperator = "!=";
			break;
		case PropertyIsLessThan:	
			cqlOperator = "<";
			break;
		case PropertyIsGreaterThan:
			cqlOperator = ">";
			break;
		case PropertyIsLessThanOrEqualTo:
			cqlOperator = "<=";
			break;
		case PropertyIsGreaterThanOrEqualTo:
			cqlOperator = ">=";
			break;
		case PropertyIsLike:
			cqlOperator = "LIKE";
			break;
		case PropertyIsNull:
			cqlOperator = "IS NULL";
			break;
		case PropertyIsBetween:
			cqlOperator = "BETWEEN";
		}
			
		return cqlOperator;
	}
	
	public TreeViewer getSourceViewer() {
		return sourceViewer;
	}

	protected ModelNavigationView getModelNavigationView() {
		ModelNavigationView modelNavigationView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		// get AttributeView
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView")) {
				modelNavigationView = (ModelNavigationView) views[count].getView(false);
			}
			
		}
		return modelNavigationView;
		}
	
	protected AttributeView getAttributeView() {
		AttributeView attributeView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		// get AttributeView
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.AttributeView")) {
				attributeView = (AttributeView) views[count].getView(false);
			}
			
		}
		return attributeView;
	}

  /**
   * enum contains allowed CQL operators for the
   * CST Filter Transformer
   */
	public enum CQLOperators{
      PropertyIsEqualTo,
	  PropertyIsNotEqualTo,
	  PropertyIsLessThan,	
	  PropertyIsGreaterThan,
	  PropertyIsLessThanOrEqualTo,
	  PropertyIsGreaterThanOrEqualTo,
	  PropertyIsLike,
	  PropertyIsNull,
	  PropertyIsBetween
	}
	 
 }


