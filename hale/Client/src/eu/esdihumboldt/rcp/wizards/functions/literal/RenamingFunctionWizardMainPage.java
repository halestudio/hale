package eu.esdihumboldt.rcp.wizards.functions.literal;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchColors;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Param;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.impl.SchemaServiceEnum;
import eu.esdihumboldt.hale.rcp.views.model.AttributeView;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject;
import eu.esdihumboldt.hale.rcp.wizards.io.UrlFieldEditor;



public class RenamingFunctionWizardMainPage 
	extends WizardPage implements  ISelectionListener {
		
		private static final String SOURCE_SELECTION_TYPE = "SourceSelectionType";

		private static final String TARGET_SELECTION_TYPE = "TargetSelectionType";
		
		private TreeViewer sourceViewer;
	
		private TreeViewer targetViewer;
		
		
		//source FeatureType that should be renamed
		private FeatureType sourceFeatureType;
		
		
		

		public FeatureType getSourceFeatureType() {
			return sourceFeatureType;
		}




		private static Logger _log = Logger.getLogger(RenamingFunctionWizardMainPage.class);
		
		protected Text sourceFeatureTypeName;
		
       protected Text targetFeatureTypeName;
		
		private Label sourceFeatureTypeLabel;
		private Label targetFeatureTypeLabel;
		

        protected RenamingFunctionWizardMainPage(String pageName, String title)
		 {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName); 
		setDescription("Enter parameters to adopt the source FeatureType to the target Naming Convention."); 
		
	}

		
        /**
    	 * The parent methods where all controls are created for this {@link WizardPage}.
    	 * @param parent
    	 */
    	public void createControl(Composite parent) {
    		
            super.initializeDialogUnits(parent);
            this.setPageComplete(true);
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
            this.sourceFeatureTypeLabel = new Label(composite, SWT.TITLE);
            this.sourceFeatureTypeLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                    | GridData.HORIZONTAL_ALIGN_FILL));
            this.sourceFeatureTypeLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            FontData labelFontData = parent.getFont().getFontData()[0];
            labelFontData.setStyle(SWT.BOLD);
        		
        	this.sourceFeatureTypeLabel.setFont(new Font(parent.getDisplay(), labelFontData));

            this.sourceFeatureTypeLabel.setText("Source Type");
            this.sourceFeatureTypeName = new Text(composite, SWT.BORDER);
            //TODO replace it with the selected source FeatureType value
            this.sourceFeatureTypeName.setText(getSelectedFeatureType(SOURCE_SELECTION_TYPE));
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 1;
            this.sourceFeatureTypeName.setLayoutData(gd);
            
            //add listener to update the source feature name
           this.sourceFeatureTypeName.addModifyListener(new ModifyListener(){
            	 public void modifyText(ModifyEvent event) {
                     String sourceName = sourceFeatureTypeName .getText();
                     System.out.println(sourceName);
                     if(sourceName.length() == 0) setErrorMessage("Source Name can not be empty");
                     else if (sourceFeatureTypeName.getText().equals(targetFeatureTypeName.getText())) setErrorMessage("Source and Target Name cannot be the same");
                     else setErrorMessage(null);
                     setPageComplete(sourceName.length() > 0 && targetFeatureTypeName.getText().length()> 0 && (!sourceFeatureTypeName.getText().equals(targetFeatureTypeName.getText())));
                     
                   }
            	
            });
            
            
          
            //target area
            this.targetFeatureTypeLabel = new Label(composite, SWT.BOLD);
            this.targetFeatureTypeLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                    | GridData.HORIZONTAL_ALIGN_FILL));
            this.targetFeatureTypeLabel.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            this.targetFeatureTypeLabel.setFont(new Font(parent.getDisplay(), labelFontData));
            this.targetFeatureTypeLabel.setText("Target Type");
            this.targetFeatureTypeName = new Text(composite, SWT.BORDER);
            //TODO replace it with the selected target FeatureType value
            this.targetFeatureTypeName.setText(getSelectedFeatureType(TARGET_SELECTION_TYPE));
            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 1;
            this.targetFeatureTypeName.setLayoutData(gd);
            
            //add listener to update the target feature name
            this.targetFeatureTypeName.addModifyListener(new ModifyListener(){
             	 public void modifyText(ModifyEvent event) {
                      String targetName = targetFeatureTypeName.getText();
                      
                      if(targetName.length() == 0) setErrorMessage("Target Name can not be empty");
                     
                      else if (sourceFeatureTypeName.getText().equals(targetFeatureTypeName.getText())) setErrorMessage("Source and Target Name cannot be the same");
                      else setErrorMessage(null);
                      setPageComplete(targetName.length() > 0 && sourceFeatureTypeName.getText().length()> 0 && (!sourceFeatureTypeName.getText().equals(targetFeatureTypeName.getText())));
                    }
             	
             });
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .addSelectionListener( this );   
            setErrorMessage(null);	// should not initially have error message
    		super.setControl(composite);
    	}
    	

    	
    	
    	private String getSelectedFeatureType(String selectedFeatureType) {
    		String typeNameSource = "";
    		String typeNameTarget ="";
    		ModelNavigationView modelNavigation = getModelNavigationView();
			if (modelNavigation!=null){
				
				if(selectedFeatureType.equals(SOURCE_SELECTION_TYPE)){
					this.sourceViewer = modelNavigation.getSourceSchemaViewer();
					this.targetViewer = modelNavigation.getTargetSchemaViewer();
					TreeItem [] sourceTreeSelection = sourceViewer.getTree().getSelection();
					TreeItem [] targetTreeSelection = targetViewer.getTree().getSelection();
					
					
					
					if(sourceTreeSelection.length ==1) {
						
					  //is a Feature Type
						typeNameSource = sourceTreeSelection[0].getText();
						typeNameTarget = targetTreeSelection[0].getText();
						 
						//get service
						SchemaService service = (SchemaService)ModelNavigationView.site.getService(SchemaService.class);
						FeatureType ft_source = service.getFeatureTypeByName(typeNameSource);
						FeatureType ft_target = service.getFeatureTypeByName(typeNameTarget);
						
						
						//get URI and local name
						List<String> nameparts = new ArrayList<String>(); 
						nameparts.add(ft_source.getName().getNamespaceURI());
						nameparts.add(ft_source.getName().getLocalPart());


						
						//evtl. move to performFinish
						Cell c = new Cell();
						FeatureClass entity1 = new FeatureClass(nameparts);
						Transformation t = new Transformation();
						t.setLabel("Rename Transformer");
						List parameters = new ArrayList<IParameter>();
//						parameters.add(new Param("SourceFeatureType", ft_source.getName().toString()));
//						parameters.add(new Param("TargetFeatureType", ft_target.getName().toString()));
						entity1.setTransformation(t); 
						c.setEntity1(entity1);
						
						List<String> nameparts_2 = new ArrayList<String>(); 
						nameparts_2.add(ft_target.getName().getNamespaceURI());
						nameparts_2.add(ft_target.getName().getLocalPart());
						FeatureClass entity2 = new FeatureClass(nameparts_2); 
						c.setEntity2(entity2);
						AlignmentService alservice = (AlignmentService)ModelNavigationView.site.getService(AlignmentService.class);
						//store transformation in AS
						alservice.addOrUpdateCell(c);

				    
					
					//TODO get Feature Type from the Tree
					/*SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
					builder.setName(typeName);
					this.sourceFeatureType = builder.buildFeatureType();*/
					}
				}else if (selectedFeatureType.equals(TARGET_SELECTION_TYPE)){
					TreeViewer targetViewer = modelNavigation.getTargetSchemaViewer();
					TreeItem [] targetTreeSelection = targetViewer.getTree().getSelection();
					if(targetTreeSelection.length ==1) typeNameSource = targetTreeSelection[0].getText();
				}
				
			}
    		
			return typeNameSource;
			
		}


		protected ModelNavigationView getModelNavigationView() {
			ModelNavigationView attributeView = null;
			// get All Views
			IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
			// get AttributeView
			// get AttributeView
			for (int count = 0; count < views.length; count++) {
				if (views[count].getId().equals(
						"eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView")) {
					attributeView = (ModelNavigationView) views[count].getView(false);
				}
				
			}
			return attributeView;
		}

		

		/**
    	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
    	 */
    	/*@Override
    	public boolean isPageComplete() {
    		
    		if (this.sourceFeatureTypeName != null && this.targetFeatureTypeName != null){
    			//TODO add error handling if source name = target name_
    			_log.debug("sourceFeatureType " + this.sourceFeatureTypeName.getText());
    			_log.debug("sourceFeatureType " + this.sourceFeatureTypeName.getText());
    			_log.debug("Page is complete.");
    			return true;
    		}else {
    			//TODO 	add error handling if source and/or target name are empty.
    			return false;
    			}
    			
    		}*/
    		/*if (this.fileFieldEditor != null && this.wfsFieldEditor != null) {
    			_log.debug("fileFieldEditor: " + this.fileFieldEditor.getStringValue());
    			try {
    				if (this.useWfsRadio.getSelection()) {
    					// test whether content of the WFS Field Editor validates to URL.
    					String test = this.wfsFieldEditor.getStringValue();
    					if (test != null && !test.equals("")) {
    						new URL(test);
    						_log.debug("wfsFieldEditor URL was OK.");
    					} 
    					else {
    						return false;
    					}
    				}
    				else {
    					// test whether content of the File Field Editor validates to URI.
    					String test = this.fileFieldEditor.getStringValue();
    					if (test != null && !test.equals("")) {
    						new URI(test.replaceAll("\\\\", "/"));
    						_log.debug("fileFieldEditor URI was OK.");
    					}
    					else {
    						return false;
    					}
    				}	
    			} catch (Exception ex) {
    				ex.printStackTrace();
    				return false;
    			} 
    			_log.debug("Page is complete.");
    			return true;
    		}
    		else {
    			return false;
    		}
    		return true;
    	}
    	*/


		/*@Override
		public void handleEvent(Event e) {
			if (e.widget == this.sourceFeatureTypeName){
				System.out.println(this.sourceFeatureTypeName.getSelectionText());
			}
			
		}*/
		public TreeViewer getSourceViewer() {
			return sourceViewer;
		}


		public TreeViewer getTargetViewer() {
			return targetViewer;
		}
		public Text getSourceFeatureTypeName() {
			return sourceFeatureTypeName;
		}


		public Text getTargetFeatureTypeName() {
			return targetFeatureTypeName;
		}
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			 if ( selection instanceof IStructuredSelection )
		        {
		            final Object selectionObject = ( ( IStructuredSelection ) selection )
		                    .getFirstElement();
		            if ( selectionObject != null )
		            {
		               
		            	TreeItem treeItem = (TreeItem) selectionObject;
		            	String selectedFeatureType = treeItem.getText();
		            	System.out.println("From RenamingFunctionWizard: " + selectedFeatureType);
		            }
		        }
			
		}

    	
}
