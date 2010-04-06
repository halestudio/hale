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
package eu.esdihumboldt.hale.rcp.wizards.functions.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.IViewerLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.hale.rcp.utils.definition.AttributeInputDialog;
import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;

/**
 * TODO Typedescription
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ConcatenationOfAttributesWizardPage extends
AbstractSingleComposedCellWizardPage{
	
private final Map<String, Set<String>> classifications = new TreeMap<String, Set<String>>();
	
	private final Image addImage = CoreFunctionWizardsPlugin.getImageDescriptor("icons/add.gif").createImage();
	private final Image removeImage = CoreFunctionWizardsPlugin.getImageDescriptor("icons/remove.gif").createImage();
	private ListViewer listViewer;
	private Text seperatorText;

	public ConcatenationOfAttributesWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        this.setPageComplete(this.isPageComplete());
		
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		Composite page = new Composite(parent, SWT.NONE);
		super.setControl(page);
		page.setLayout(new GridLayout(3, false));
		Group sourceGroup = new Group(page, SWT.NONE);
		sourceGroup.setLayout(new GridLayout(SWT.FILL_WINDING, true));
		sourceGroup.setText("Source Items");
		
		final Label seperatorLabel = new Label(sourceGroup, SWT.NONE);
		seperatorLabel.setText("Seperator: ");
		seperatorText = new Text(sourceGroup, SWT.SINGLE | SWT.BORDER);
		seperatorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		seperatorText.setText(":");
		TreeSet<SchemaItem> sourceTreeSet = (TreeSet<SchemaItem>) getParent().getSourceItems();
		String[] localNames = new String[sourceTreeSet.size()];
		int k=0;
		for(Iterator<SchemaItem> iterator = sourceTreeSet.iterator(); iterator.hasNext();k++) {
			localNames[k]=iterator.next().getEntity().getLocalname();
		}

		// source value selection
		final Label comboLabel = new Label(sourceGroup, SWT.NONE);
		comboLabel.setText("Source Items: ");
		final Combo combo = new Combo(sourceGroup, SWT.DROP_DOWN | SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final ComboViewer comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setInput(localNames);
		

		// add source value
		Button addButton = new Button(page, SWT.PUSH);
		addButton.setImage(addImage);
		addButton.setToolTipText("Add a Concatenation Attribute");
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!combo.getText().equals("")){
					listViewer.add(combo.getText());
				}
			}		
		});
		
		// remove target value
		final Button removeButton = new Button(page, SWT.PUSH);
		removeButton.setImage(removeImage);
		removeButton.setToolTipText("Remove selected Concatenations Attributes");
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(listViewer.getList().getItems().length!=0){
					for(String selection : listViewer.getList().getSelection()){
						listViewer.getList().remove(selection);
					}
				}
			}	
		});
			
		// list
		List list = new List(page, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		listViewer = new ListViewer(list);
		listViewer.setContentProvider(new ArrayContentProvider());	
		
	}
	
	public ListViewer getListViewer() {
		return listViewer;
	}

	public void setListViewer(ListViewer listViewer) {
		this.listViewer = listViewer;
	}

	public Text getSeperatorText() {
		return seperatorText;
	}

	public void setSeperatorText(Text seperatorText) {
		this.seperatorText = seperatorText;
	}

}
