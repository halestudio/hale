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

import java.text.MessageFormat;
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
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.schemaprovider.EnumAttributeType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

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
	 * @param parentShell the parent shell
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
		setTitle(MessageFormat.format(Messages.PropertiesDialog_Title, EntityHelper.getShortName(item.getEntity())));
		
		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(Messages.PropertiesDialog_ShellTitle);
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
		col1.getColumn().setText(Messages.PropertiesDialog_col1Text);
		col1.setLabelProvider(new TreeColumnViewerLabelProvider(
				new MultiColumnTreeNodeLabelProvider(0)));
		
		// value column
		TreeViewerColumn col2 = new TreeViewerColumn(tree, SWT.LEFT);
		col2.getColumn().setText(Messages.PropertiesDialog_col12Text);
		col2.setLabelProvider(new TreeColumnViewerLabelProvider(
				new MultiColumnTreeNodeLabelProvider(1)));
		col2.setEditingSupport(new ReadOnlyEditingSupport(tree, new MultiColumnTreeNodeLabelProvider(1)));
		
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
		DefaultTreeNode name = new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleName, 
				item.getName().getLocalPart());//EntityHelper.getShortName(item.getEntity()));
		
		// identifier
		if (definition != null && definition.getIdentifier() != null) {
			name.addChild(new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeIndentifier, definition.getIdentifier()));
		}
		
		nodes.add(name);
		
		// type
		if (item.getPropertyType() != null) {
			TypeDefinition typeDef = null;
			if (definition instanceof TypeDefinition) {
				typeDef = (TypeDefinition) definition;
			}
			else if (definition instanceof SchemaElement) {
				typeDef = ((SchemaElement) definition).getType();
			}
			else if (definition instanceof AttributeDefinition) {
				typeDef = ((AttributeDefinition) definition).getAttributeType();
			}
			
			if (typeDef != null) {
				// type & binding
				DefaultTreeNode type = new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleType, 
						typeDef.getName().getNamespaceURI() + "/" + typeDef.getName().getLocalPart()); //$NON-NLS-1$
				nodes.add(type);
				
				addTypeNodes(typeDef, type, true, true);
			}
		}
		
		if (item instanceof AttributeItem) {
			AttributeDefinition property = ((AttributeItem) item).getAttributeDefinition();
			
			// add namespace to name
			name.addChild(new DefaultTreeNode("Namespace", property.getNamespace())); //$NON-NLS-1$
			
			// nillable
			DefaultTreeNode nillable = new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleNillable, 
					String.valueOf(property.isNillable()));
			nodes.add(nillable);
			
			// cardinality
			long maxOccurs = property.getMaxOccurs();
			String cardinalityValue = property.getMinOccurs() + ".." + ((maxOccurs == Long.MAX_VALUE)?("unbounded"):(maxOccurs)); //$NON-NLS-1$ //$NON-NLS-2$
			DefaultTreeNode cardinality = new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleCardinality, cardinalityValue);
			nodes.add(cardinality);
		}
		
		// enumeration
		if (item.getPropertyType() != null && item.getPropertyType() instanceof EnumAttributeType) {
			Set<String> allowedValues = ((EnumAttributeType) item.getPropertyType()).getAllowedValues();
			
			DefaultTreeNode enumeration = new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleEnumeration);
			for (String value : allowedValues) {
				enumeration.addChild(new DefaultTreeNode(value));
			}
			nodes.add(enumeration);
		}
		
		// attributes
		if (item.hasChildren()) {
			DefaultTreeNode attributes = new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleAttributes);
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
		
		// location
		if (definition != null) {
			String location = definition.getLocation();
			if (location != null) {
				DefaultTreeNode locNode = new DefaultTreeNode("Location", location); //$NON-NLS-1$
				nodes.add(locNode);
			}
		}
		
		return nodes;
	}

	private void addTypeNodes(TypeDefinition typeDef,
			DefaultTreeNode typeNode, boolean addSubTypes, boolean addSuperType) {
		typeNode.addChild(new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleNamespace, typeDef.getName().getNamespaceURI()));
		typeNode.addChild(new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleLocalpart, typeDef.getName().getLocalPart()));
		typeNode.addChild(new DefaultTreeNode(Messages.PropertiesDialog_TreeNodeTitleBinding,
				typeDef.getType(null).getBinding().getName()));
		typeNode.addChild(new DefaultTreeNode("Identifier", typeDef.getIdentifier())); //$NON-NLS-1$
		if (typeDef.getLocation() != null) {
			typeNode.addChild(new DefaultTreeNode("Location", typeDef.getLocation())); //$NON-NLS-1$
		}
		
		if (addSubTypes && typeDef.getSubTypes() != null && !typeDef.getSubTypes().isEmpty()) {
			DefaultTreeNode subsNode = new DefaultTreeNode("Subtypes"); //$NON-NLS-1$
			typeNode.addChild(subsNode);
			
			for (TypeDefinition subType : typeDef.getSubTypes()) {
				DefaultTreeNode subNode = new DefaultTreeNode(subType.getName().getLocalPart());
				subsNode.addChild(subNode);
				
				addTypeNodes(subType, subNode, true, false);
			}
		}
		
		if (addSuperType) {
			TypeDefinition superType = typeDef.getSuperType();
			if (superType != null) {
				DefaultTreeNode superNode = new DefaultTreeNode("Supertype", superType.getName().getNamespaceURI() + "/" + superType.getName().getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
				typeNode.addChild(superNode);
				
				addTypeNodes(superType, superNode, false, true);
			}
		}
		
		// add declared attributes w/ types
		//TODO use a lazy model instead for the whole tree to allow displaying the sub/supertypes for the attributes? (else most likely cycles will occur)
		DefaultTreeNode attsNode = null;
		for (AttributeDefinition att : typeDef.getDeclaredAttributes()) {
			if (attsNode == null) {
				attsNode = new DefaultTreeNode("Declared properties"); //$NON-NLS-1$
				typeNode.addChild(attsNode);
			}
			
			DefaultTreeNode attNode;
			if (att.getAttributeType() != null) {
				attNode = new DefaultTreeNode(att.getName(), att.getAttributeType().getDisplayName());
				attsNode.addChild(attNode);
				addTypeNodes(att.getAttributeType(), attNode, false, false);
			}
			else {
				attNode = new DefaultTreeNode(att.getName());
			}
		}
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
