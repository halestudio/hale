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
package eu.esdihumboldt.hale.rcp.views.model.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.utils.EntityHelper;
import eu.esdihumboldt.hale.rcp.utils.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNodeLabelProvider;
import eu.esdihumboldt.hale.rcp.views.model.AttributeItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.schemaprovider.EnumAttributeType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;

/**
 * Dialog showing the properties of a schema item
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertiesDialog extends TitleAreaDialog {
	
	private final SchemaItem item;
	
	private Text descriptionText;
	
	/**
	 * Constructor
	 * 
	 * @param parentShell
	 * @param item the schema item
	 */
	public PropertiesDialog(Shell parentShell, SchemaItem item) {
		super(parentShell);
		
		this.item = item;
	}
	
	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		//setMessage("");
		setTitle("Properties of " + EntityHelper.getShortName(item.getEntity()));
		
		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText("Properties");
	}

	/**
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 600;
		page.setLayoutData(data);
		
		GridLayout pageLayout = new GridLayout(1, false);
		pageLayout.marginLeft = 0;
		pageLayout.marginTop = 0;
		pageLayout.marginLeft = 0;
		pageLayout.marginBottom = 0;
		page.setLayout(pageLayout);
		
		Definition definition = item.getDefinition();
		// description
		if (definition != null && definition.getDescription() != null) {
			// text field for description
			descriptionText = new Text(page, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			//gd.heightHint = 100;
			descriptionText.setLayoutData(gd);
			descriptionText.setText(definition.getDescription());
		}
		
		Composite treeComposite = new Composite(page, SWT.NONE);
		GridData tgd = new GridData(SWT.FILL, SWT.FILL, true, true);
		tgd.heightHint = 250;
		treeComposite.setLayoutData(tgd);
		
		// tree column layout
		TreeColumnLayout layout = new TreeColumnLayout(); 
		treeComposite.setLayout(layout);
		
		// tree viewer
		TreeViewer tree = new TreeViewer(treeComposite, SWT.SINGLE | SWT.FULL_SELECTION);
		tree.setContentProvider(new TreeNodeContentProvider());
				
		// property column
		TreeViewerColumn col1 = new TreeViewerColumn(tree, SWT.LEFT);
		col1.getColumn().setText("Property");
		col1.setLabelProvider(new TreeColumnViewerLabelProvider(
				new MultiColumnTreeNodeLabelProvider(0)));
		
		// value column
		TreeViewerColumn col2 = new TreeViewerColumn(tree, SWT.LEFT);
		col2.getColumn().setText("Value");
		col2.setLabelProvider(new TreeColumnViewerLabelProvider(
				new MultiColumnTreeNodeLabelProvider(1)));
		
		tree.getTree().setHeaderVisible(true);
		tree.getTree().setLinesVisible(true);
		
		//tree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		layout.setColumnData(col1.getColumn(), new ColumnWeightData(3));
		layout.setColumnData(col2.getColumn(), new ColumnWeightData(7));
		
		// setting input must take place after columns were created!
		List<TreeNode> model = getTreeModel();
		tree.setInput(model.toArray(new TreeNode[model.size()]));
		
		return page;
	}

	/**
	 * Get the tree model
	 *  
	 * @return a list of root nodes
	 */
	protected List<TreeNode> getTreeModel() {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		Definition definition = item.getDefinition();
		
		// name
		DefaultTreeNode name = new DefaultTreeNode("Name", 
				item.getName().getLocalPart());//EntityHelper.getShortName(item.getEntity()));
		
		// identifier
		if (definition != null && definition.getIdentifier() != null) {
			name.addChild(new DefaultTreeNode("Identifier", definition.getIdentifier()));
		}
		
		nodes.add(name);
		
		if (item.getPropertyType() != null) {
			// type & binding
			DefaultTreeNode type = new DefaultTreeNode("Type", 
					item.getPropertyType().getName().getNamespaceURI() + "/" + item.getPropertyType().getName().getLocalPart());
			type.addChild(new DefaultTreeNode("Namespace", item.getPropertyType().getName().getNamespaceURI()));
			type.addChild(new DefaultTreeNode("Local part", item.getPropertyType().getName().getLocalPart()));
			type.addChild(new DefaultTreeNode("Binding",
					item.getPropertyType().getBinding().getName()));
			nodes.add(type);
		}
		
		
		
		
		if (item instanceof AttributeItem) {
			AttributeDefinition property = ((AttributeItem) item).getAttributeDefinition();
			
			// nillable
			DefaultTreeNode nillable = new DefaultTreeNode("Nillable", 
					String.valueOf(property.isNillable()));
			nodes.add(nillable);
			
			// cardinality
			long maxOccurs = property.getMaxOccurs();
			String cardinalityValue = property.getMinOccurs() + ".." + ((maxOccurs == Long.MAX_VALUE)?("unbounded"):(maxOccurs));
			DefaultTreeNode cardinality = new DefaultTreeNode("Cardinality", cardinalityValue);
			nodes.add(cardinality);
		}
		
		// enumeration
		if (item.getPropertyType() != null && item.getPropertyType() instanceof EnumAttributeType) {
			Set<String> allowedValues = ((EnumAttributeType) item.getPropertyType()).getAllowedValues();
			
			DefaultTreeNode enumeration = new DefaultTreeNode("Enumeration");
			for (String value : allowedValues) {
				enumeration.addChild(new DefaultTreeNode(value));
			}
			nodes.add(enumeration);
		}
		
		// attributes
		if (item.hasChildren()) {
			DefaultTreeNode attributes = new DefaultTreeNode("Attributes");
			for (SchemaItem child : item.getChildren()) {
				if (child.isAttribute()) {
					if (child.getPropertyType() == null) {
						attributes.addChild(new DefaultTreeNode(child.getName().getLocalPart()));
					}
					else {
						attributes.addChild(new DefaultTreeNode(child.getName().getLocalPart(),
								child.getPropertyType().getName().getLocalPart()));
					}
				}
			}
			if (attributes.hasChildren()) {
				nodes.add(attributes);
			}
		}
		
		return nodes;
	}

	/**
	 * @see Dialog#createButtonsForButtonBar(Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

}
