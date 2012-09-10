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
package eu.esdihumboldt.hale.ui.style;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;

/**
 * Drop-down action for style editing of data set feature types
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@Deprecated
public class DatasetStyleDropdown extends Action implements IMenuCreator, SchemaServiceListener {

	private final DataSet dataset;

	private Menu menu;

	/**
	 * Creates a data set style drop-down
	 * 
	 * @param dataset the data set
	 */
	public DatasetStyleDropdown(final DataSet dataset) {
		super((dataset == DataSet.SOURCE) ? ("Source SLD") : ("Target SLD"), //$NON-NLS-1$ //$NON-NLS-2$
				Action.AS_DROP_DOWN_MENU);

		this.dataset = dataset;

		setImageDescriptor(InstanceStylePlugin.getImageDescriptor("/icons/ft_stylelist.gif")); //$NON-NLS-1$

		update();

		SchemaService schema = (SchemaService) PlatformUI.getWorkbench().getService(
				SchemaService.class);
		schema.addSchemaServiceListener(this); // FIXME remove the listener?
												// when & where?

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
		SchemaService schema = (SchemaService) PlatformUI.getWorkbench().getService(
				SchemaService.class);

		// FIXME deactivated
//		Map<Definition, FeatureType> tmp = (dataset == DataSet.SOURCE)?(schema.getSourceSchema().getTypes()):(schema.getTargetSchema().getTypes());
//		List<FeatureType> types = new ArrayList<FeatureType>();
//		for (Entry<Definition, FeatureType> entry : tmp.entrySet()) {
//			TypeDefinition type = DefinitionUtil.getType(entry.getKey());
//			if (type.hasGeometry() && !type.isAbstract()) {
//				types.add(entry.getValue());
//			}
//		}
//		Collections.sort(types, new Comparator<FeatureType>() {
//
//			@Override
//			public int compare(FeatureType o1, FeatureType o2) {
//				return o1.getName().getLocalPart().compareToIgnoreCase(o2.getName().getLocalPart());
//			}
//			
//		});
//		
//		if (types.isEmpty())
//			return;
//		
//		int index = 0;
//		
//		for (FeatureType type : types) {
//			IAction action = new FeatureTypeStyleAction(type);
//			IContributionItem item = new ActionContributionItem(action);
//			item.fill(menu, index);
//			
//			index++;
//		}
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

	private void update() {
		// FIXME deactivated
//		SchemaService schema = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
//		
//		Map<Definition, FeatureType> elements = (dataset == DataSet.SOURCE)?(schema.getSourceSchema().getTypes()):(schema.getTargetSchema().getTypes());
//		
//		setEnabled(elements != null);
	}

	/**
	 * @see SchemaServiceListener#schemaAdded(SchemaSpaceID, Schema)
	 */
	@Override
	public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
		update();
	}

	/**
	 * @see SchemaServiceListener#schemasCleared(SchemaSpaceID)
	 */
	@Override
	public void schemasCleared(SchemaSpaceID spaceID) {
		update();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener#mappableTypesChanged(eu.esdihumboldt.hale.common.schema.SchemaSpaceID,
	 *      java.util.Collection)
	 */
	@Override
	public void mappableTypesChanged(SchemaSpaceID spaceID,
			Collection<? extends TypeDefinition> types) {
		update();
	}

}
