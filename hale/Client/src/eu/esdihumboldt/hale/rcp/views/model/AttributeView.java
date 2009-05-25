package eu.esdihumboldt.hale.rcp.views.model;





import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * The {@link AttributeView_copy} displays the attributes from the selected data
 * class in the {@link ModelNavigationView}. The {@link AttributeView_copy} consist
 * of the Labels for the names of the selected data classes and the operator
 * between them and Lists for the attributes.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class AttributeView extends ViewPart {

	public static final String ID = "eu.esdihumboldt.hale.rcp.views.model.AttributeView";

	private static final String FEATURE_TYPE_SUFFIX = "Type";

	// List for the attributes from the selected User Model class
	private List sourceAttributeList;
	// List for the attributes from the selected INSPIRE Model class
	private List targetAttributeList;
	// Label for the class name selected in ModelnavigationView source Model
	private Label sourceModelLabel;
	// Label for the class name selected in ModelnavigationView target Model.
	private Label targetModelLabel;
	// Button to open FunctionWizard
	private Button selectFunctionButton;

	@Override
	public void createPartControl(Composite _parent) {

		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 6;
		layout.horizontalSpacing = 3;
		modelComposite.setLayout(layout);
		
		
		/*layout.marginHeight = 10;
		layout.marginWidth = 5;*/
		/*layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;*/
//		layout.verticalSpacing = 20;
//		layout.horizontalSpacing = 10;
		

		Composite sourceComposite = new Composite(modelComposite, SWT.BEGINNING);
		sourceComposite.setLayout(new GridLayout(1, false));
		sourceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		
		//GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

		sourceModelLabel = new Label(sourceComposite, SWT.CENTER);
		
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		sourceModelLabel.setLayoutData(gData);

	/*	Label operatorLabel = new Label(modelComposite, SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gData.horizontalAlignment = SWT.CENTER;
		operatorLabel.setText("placeholder");
		operatorLabel.setLayoutData(gData);
*/
		Composite targetComposite = new Composite(modelComposite, SWT.BEGINNING);
		targetComposite.setLayout(new GridLayout(1, false));
		targetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		targetModelLabel = new Label(targetComposite, SWT.CENTER);
	    gData = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		
		targetModelLabel.setLayoutData(gData);

		
		this.sourceAttributeList = this.attributeListSetup(sourceComposite);
		
		
		/*gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;*/
       /* gData = new GridData(SWT.FILL, SWT.FILL, true,
				false);
    	sourceAttributeList.setLayoutData(gData);*/
		//Allow data to be linked from the drag source
		int operations = DND.DROP_LINK;
		DragSource source = new DragSource(sourceAttributeList,operations);
		//provide data in Text format
		Transfer[] types = new Transfer[]{TextTransfer.getInstance()};
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
				   public void dragStart(DragSourceEvent event) {
			 	     System.out.println("drag start");
					   // Only start the drag if some attribute selected
			 	     System.out.println("selected element: " + sourceAttributeList.getSelection()[0]);
			 	      if (sourceAttributeList.getSelection()[0]==null){
			 	               event.doit = false;
				      }
			 	      else event.detail = DND.DROP_LINK;
			 	   }
			 	   public void dragSetData(DragSourceEvent event) {
			 	    System.out.println("drag set data");
			 		   // Provide the data of the requested type.
			 	     if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			 	          event.data = sourceAttributeList.getSelection()[0];
			 	          
			 	     }
			 	   }
			 	   public void dragFinished(DragSourceEvent event) {
			 		   System.out.println("Drag Finished");
			 	    
			 	   }
			 	});
		

		/*Label placeHolder = new Label(modelComposite, SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		placeHolder.setLayoutData(gData);*/

		//TODO drag and drop or selection button
		/*selectFunctionButton = new Button(modelComposite, SWT.PUSH);
		selectFunctionButton.setText("Select Function");
		gData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER |GridData.VERTICAL_ALIGN_CENTER);
		selectFunctionButton.setLayoutData(gData);
		selectFunctionButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectFunctionButton.setText(sourceAttributeList.getSelection()[0]);
				
			}
			
		});*/
		
		
		
		targetAttributeList = this.attributeListSetup(targetComposite);
		/*gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;*/
	/*	gData = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		targetAttributeList.setLayoutData(gData);*/
		// Allow data to be linked to the drop target
		operations = DND.DROP_LINK;
		DropTarget target = new DropTarget(targetAttributeList, operations);
		
		// Receive data in Text format
		
//		targetAttributeList.addMouseMoveListener(new MouseMoveListener() {
//
//			@Override
//			public void mouseMove(MouseEvent e) {
//				targetAttributeList.
//				
//			}
//			
//		});
//		
		
		final TextTransfer textTransfer = TextTransfer.getInstance();
		types = new Transfer[] {textTransfer};
		target.setTransfer(types);
		target.addDropListener(new DropTargetListener() {
			
			  public void dragEnter(DropTargetEvent event) {
				  System.out.println("dragEnter");
				  event.detail = DND.FEEDBACK_INSERT_AFTER;
				  
			 	     
			 	   }
			 	   public void dragOver(DropTargetEvent event) {
			 		   System.out.println("dragOver");
			 	        
			 	    }
			 	    public void dragOperationChanged(DropTargetEvent event) {
			 	        
			 	    }
			 	    public void dragLeave(DropTargetEvent event) {
			 	    	System.out.println("dragLeave");
			 	    }
			 	    public void dropAccept(DropTargetEvent event) {
			 	    	System.out.println("dropAccept");
			 	    }
			 	    public void drop(DropTargetEvent event) {
			 	       System.out.println("drop");
			 	    	if (textTransfer.isSupportedType(event.currentDataType)) {
			                String srcText = (String)event.data;
			                //String targetText = targetAttributeList.getSelection()[0];
			                System.out.println(srcText);
			                DropTarget target = (DropTarget)event.widget;
			                Text text = (Text)target.getControl().getData();
			                System.out.println(text);
			                              
			 	           
			 	        }
			 	        
			 	    }
			 	});
	}

	private List attributeListSetup(Composite attributeComposite) {
		Composite viewerLComposite = new Composite(attributeComposite, SWT.NONE);
		FillLayout fLayout = new FillLayout();
		viewerLComposite.setLayout(fLayout);
		GridData gData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		gData.verticalSpan = 32;
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		gData.verticalIndent = 12;
		viewerLComposite.setLayoutData(gData);
		List attributeList = new List(viewerLComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		
		return attributeList;
	}

	/**
	 * updateView is called when the user selects another class in the
	 * ModelNavigatiomView. The input of the Lists and Labels of the
	 * AttributeView needs to be updated.
	 * 
	 * @param _viewer
	 *            if true, sourceAttributeList selection changed else
	 *            targetAttributeList selection changed
	 * @param _classname
	 *            the name of the class that should be displayed in the
	 *            corresponding Label
	 * @param _classnameNumber 
	 *            the number of the class in the tree displyed in the ModelNavigationView         
	 */
	public void updateView(boolean _viewer, String _classname, TreeItem[] _items, int _classnameNumber) {
		
		
		if (_viewer == true) {
			sourceModelLabel.setText(_classname);
			// if selected item no attribute
			if (_items.length!=0) {
				for (TreeItem item : _items) {
					
					//display item in the attribute list only if attribute
					if(!item.getText().endsWith(FEATURE_TYPE_SUFFIX)){
					sourceAttributeList.add(_classnameNumber+":" +item.getText());
					}
				}
			} else {
				sourceAttributeList.add(_classname);
			}
		} else {
			targetModelLabel.setText(_classname);
			// if selected item no attribute
			if (_items.length!=0) {
				for (TreeItem item : _items) {
					
					//display item in the attribute list only if attribute
					if(!item.getText().endsWith(FEATURE_TYPE_SUFFIX))
					{
						targetAttributeList.add(_classnameNumber+":" +item.getText());
					}
				}
			} else {
				targetAttributeList.add(_classname);
			}
		}
	}

	/**
	 * Delete the class name from Model label and clear the corresponding list.
	 * 
	 * @param _list
	 *            if true, selection in userDataViewer changes, else selection
	 *            in inspireDataViewer changes
	 */
	public void clear(boolean _list) {
		if (_list) {
			sourceAttributeList.removeAll();
			sourceModelLabel.setText("");
		} else {
			targetAttributeList.removeAll();
			targetModelLabel.setText("");
		}
	}

	@Override
	public void setFocus() {

	}
}