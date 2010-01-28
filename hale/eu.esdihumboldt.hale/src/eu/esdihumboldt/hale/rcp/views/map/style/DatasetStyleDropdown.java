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
package eu.esdihumboldt.hale.rcp.views.map.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.FeatureTypeHelper;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Drop-down action for style editing of data set feature types
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DatasetStyleDropdown extends Action implements IMenuCreator, HaleServiceListener {
	
	private final DatasetType dataset;
	
	private Menu menu;
	
	/**
	 * Creates a data set style drop-down
	 * 
	 * @param dataset the data set
	 */
	public DatasetStyleDropdown(final DatasetType dataset) {
		super((dataset == DatasetType.reference)?("Source SLD"):("Target SLD"),
				Action.AS_DROP_DOWN_MENU);
		
		this.dataset = dataset;
		
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, "/icons/ft_stylelist.gif"));
		
		update(null);
		
		SchemaService schema = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		schema.addListener(this);
		
		setMenuCreator(this);
	}

	/**
	 * @see IMenuCreator#dispose()
	 */
	@Override
	public void dispose() {
		if (menu != null) {
			menu.dispose();
		}
	}

	/**
	 * @see IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	@Override
	public Menu getMenu(Control parent) {
		dispose();
		
		menu = new Menu(parent);
		fillMenu(menu);
		
		return menu;
	}

	/**
	 * Fill a menu
	 * 
	 * @param menu the menu to fill
	 */
	public void fillMenu(Menu menu) {
		SchemaService schema = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		Collection<TypeDefinition> tmp = (dataset == DatasetType.reference)?(schema.getSourceSchema()):(schema.getTargetSchema());
		List<FeatureType> types = new ArrayList<FeatureType>();
		for (TypeDefinition type : tmp) {
			types.add((FeatureType) type.getType());
		}
		Collections.sort(types, new Comparator<FeatureType>() {

			@Override
			public int compare(FeatureType o1, FeatureType o2) {
				return o1.getName().getLocalPart().compareToIgnoreCase(o2.getName().getLocalPart());
			}
			
		});
		
		if (types == null || types.isEmpty())
			return;
		
		int index = 0;
		
		for (FeatureType type : types) {
			if (!FeatureTypeHelper.isAbstract(type) &&
					!FeatureTypeHelper.isPropertyType(type)) { // skip abstract types
				IAction action = new FeatureTypeStyleAction(type);
				IContributionItem item = new ActionContributionItem(action);
				item.fill(menu, index);
				
				index++;
			}
		}
	}

	/**
	 * @see IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public Menu getMenu(Menu parent) {
		dispose();
		
		menu = new Menu(parent);
		fillMenu(menu);
		
		return menu;
	}

	/**
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@SuppressWarnings("unchecked")
	public void update(UpdateMessage message) {
		SchemaService schema = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		Collection<TypeDefinition> types = (dataset == DatasetType.reference)?(schema.getSourceSchema()):(schema.getTargetSchema());
		
		setEnabled(types != null);
	}

}
