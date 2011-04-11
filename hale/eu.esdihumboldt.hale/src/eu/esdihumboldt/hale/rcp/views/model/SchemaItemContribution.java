package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.map.style.FeatureTypeStyleAction;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.rcp.views.model.dialogs.PropertiesAction;
import eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardContribution;
import eu.esdihumboldt.hale.schemaprovider.model.DefinitionUtil;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Context menu contribution
 */
public class SchemaItemContribution extends
		FunctionWizardContribution {
	
	private final TreeViewer tree;

	/**
	 * Create a new contribution
	 * 
	 * @param tree the tree for retrieving the selected item
	 * 
	 * @param showAugmentations if augmentations shall be shown in the menu
	 */
	public SchemaItemContribution(TreeViewer tree, boolean showAugmentations) {
		super(showAugmentations);
		
		this.tree = tree;
	}

	/**
	 * @see FunctionWizardContribution#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		if (tree.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) tree.getSelection();
			Object tmp = selection.getFirstElement();
			if (tmp != null && tmp instanceof SchemaItem) {
				SchemaItem item = (SchemaItem) tmp;
				boolean addSep = false;
				
				// properties
				if (item.isType() || item.isAttribute()) {
					IAction action = new PropertiesAction(item);
					IContributionItem contrib = new ActionContributionItem(action);
					contrib.fill(menu, index++);
					
					addSep = true;
				}
				
				// SLD
				TypeDefinition typeDef = DefinitionUtil.getType(item.getDefinition());
				if (item.isType() && typeDef.hasGeometry() && !typeDef.isAbstract()) {
					IAction action = new FeatureTypeStyleAction((FeatureType) item.getPropertyType());
					action.setText(Messages.ModelNavigationView_ActionText);
					action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
							HALEActivator.PLUGIN_ID, "/icons/styles.gif")); //$NON-NLS-1$
					IContributionItem contrib = new ActionContributionItem(action);
					contrib.fill(menu, index++);
					
					addSep = true;
				}
				
				// default geometry
				if (item.isAttribute() && item.getType() == TreeObjectType.GEOMETRIC_ATTRIBUTE 
						&& item.getParent() != null && item.getParent().isType()
						&& !((FeatureType) item.getParent().getPropertyType()).getGeometryDescriptor().getLocalName().equals(item.getName().getLocalPart())) {
					IAction action = new SetAsDefaultGeometryAction(item);
					action.setText(Messages.SchemaItemContribution_0); //$NON-NLS-1$
					IContributionItem contrib = new ActionContributionItem(action);
					contrib.fill(menu, index++);
					
					addSep = true;
				}
				
				if (addSep) {
					new Separator().fill(menu, index++);
				}
			}
		}
		
		super.fill(menu, index);
	}

}