package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * The {@link AttributeView} displays the attributes from the selected data class in the
 * {@link ModelNavigationView}. The {@link AttributeView} consist of the Labels for the names of
 * the selected data classes and the operator between them and Lists for the 
 * attributes.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class AttributeView 
	extends ViewPart {
	
	public static final String ID ="eu.esdihumboldt.hale.rcp.views.model.AttributeView";
	
	//List for the attributes from the selected User Model class
	private List sourceAttributeList;
	//List for the attributes from the selected INSPIRE Model class
	private List targetAttributeList;
	//Label for the class name selected in ModelnavigationView source Model
	private Label sourceModelLabel;
	//Label for the class name selected in ModelnavigationView target Model.
	private Label targetModelLabel;
	
	
	@Override
	public void createPartControl(Composite _parent) {
		
		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 5;
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		modelComposite.setLayout(layout);
		
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		sourceModelLabel = new Label(modelComposite,SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		sourceModelLabel.setLayoutData(gData);
		
		Label operatorLabel = new Label(modelComposite,SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL| GridData.VERTICAL_ALIGN_FILL);
		gData.horizontalAlignment = SWT.CENTER;
		operatorLabel.setText("placeholder");
		operatorLabel.setLayoutData(gData);
		
		targetModelLabel = new Label(modelComposite,SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL| GridData.VERTICAL_ALIGN_FILL);
		targetModelLabel.setLayoutData(gData);
		
		sourceAttributeList = new List(modelComposite,SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		sourceAttributeList.setLayoutData(gData);
		
		Label placeHolder = new Label(modelComposite,SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL |GridData.VERTICAL_ALIGN_FILL);
		placeHolder.setLayoutData(gData);
		
		targetAttributeList = new List(modelComposite,SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
		gData.grabExcessHorizontalSpace = true;
		gData.grabExcessVerticalSpace = true;
		targetAttributeList.setLayoutData(gData);
	}
	
	
	/**
	 * updateView is called when the user selects another class in the
	 * ModelNavigatiomView. The input of the Lists and Labels of the AttributeView
	 * needs to be updated.
	 * 
	 * @param _viewer 	if true, sourceAttributeList selection changed
	 * 					else targetAttributeList selection changed
	 * @param _classname the name of the class that should be displayed 
	 * 					in the corresponding Label
	 */
	public void updateView(boolean _viewer, String _classname, TreeItem[] _items){
		
		if(_viewer == true){
			sourceModelLabel.setText(_classname);
			for (TreeItem item : _items) {
				sourceAttributeList.add(item.getText());
			}
		}
		else{
			targetModelLabel.setText(_classname);
			for (TreeItem item : _items) {
				targetAttributeList.add(item.getText());
			}
		}
	}
	
	
	/**
	 * Delete the class name from Model label and clear the corresponding list.
	 * @param 	_list if true, selection in userDataViewer changes, 
	 * 			else selection in inspireDataViewer changes
	 */
	public void clear(boolean _list){
		if(_list){
			sourceAttributeList.removeAll();
			sourceModelLabel.setText("");
		}
		else{
			targetAttributeList.removeAll();
			targetModelLabel.setText("");
		}
	}
	
	
	@Override
	public void setFocus() {
		
	}
}