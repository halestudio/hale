/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.style;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionDialog;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.style.dialog.FeatureStyleDialog;
import eu.esdihumboldt.util.Pair;

/**
 * Shows a {@link FeatureStyleDialog} for a selected {@link TypeDefinition} or
 * entity definition that represents a type.
 * @author Simon Templer
 */
public class TypeStyleHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		//TODO instead of grabbing the first type found use a selection dialog to allow chosing?
		
		// collect types and associated data sets
		//XXX if the same type is contained in different data sets, there may be a conflict!
		Map<TypeDefinition, DataSet> types = new HashMap<TypeDefinition, DataSet>();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			for (Object object : ((IStructuredSelection) selection).toArray()) {
				if (object instanceof TypeDefinition) {
					TypeDefinition type = (TypeDefinition) object;
					if (!types.containsKey(type)) {
						DataSet dataSet = findDataSet(type);
						types.put(type, dataSet);
					}
				}
				if (object instanceof EntityDefinition) {
					EntityDefinition entityDef = (EntityDefinition) object;
					if (entityDef.getPropertyPath().isEmpty()) {
						DataSet dataSet = (entityDef.getSchemaSpace() == SchemaSpaceID.SOURCE) ? (DataSet.SOURCE)
								: (DataSet.TRANSFORMED);
						types.put(entityDef.getType(), dataSet);
					}
				}
				if (object instanceof InstanceReference) {
					InstanceService is = (InstanceService) HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench().getService(InstanceService.class);
					object = is.getInstance((InstanceReference) object);
				}
				if (object instanceof Instance) {
					Instance instance = (Instance) object;
					types.put(instance.getDefinition(), 
							instance.getDataSet());
				}
			}
		}
		
		Pair<TypeDefinition, DataSet> typeInfo = null;
		
		// select a type
		if (types.size() == 1) {
			typeInfo = new Pair<TypeDefinition, DataSet>(
					types.keySet().iterator().next(), 
					types.values().iterator().next());
		}
		else if (!types.isEmpty()) {
			// choose through dialog
			//TODO instead a dialog where the types are separated into source and target?
			TypeDefinitionDialog dialog = new TypeDefinitionDialog(Display
					.getCurrent().getActiveShell(), "Select type to change the style for", null,
					types.keySet());
			if (dialog.open() == TypeDefinitionDialog.OK) {
				TypeDefinition def = dialog.getObject();
				if (def != null) {
					typeInfo = new Pair<TypeDefinition, DataSet>(def, types.get(def));
				}
			}
		}
		
		if (typeInfo != null) {
			try {
				FeatureStyleDialog dialog = new FeatureStyleDialog(
						typeInfo.getFirst(), typeInfo.getSecond());
				dialog.setBlockOnOpen(false);
				dialog.open();
			} catch (Exception e) {
				throw new ExecutionException("Could not open style dialog", e);
			}
		}
		return null;
	}

	/**
	 * Determine which data set a type represents.
	 * @param type the type definition
	 * @return the data set
	 */
	private static DataSet findDataSet(TypeDefinition type) {
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		if (ss.getSchemas(SchemaSpaceID.SOURCE).getMappingRelevantTypes().contains(type)) {
			return DataSet.SOURCE;
		}
		if (ss.getSchemas(SchemaSpaceID.TARGET).getMappingRelevantTypes().contains(type)) {
			return DataSet.TRANSFORMED;
		}
		
		// default to source
		return DataSet.SOURCE;
	}

}
