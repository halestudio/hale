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

import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
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
		
		// collect types and associated data sets
		SetMultimap<DataSet, TypeDefinition> types = HashMultimap.create();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			for (Object object : ((IStructuredSelection) selection).toArray()) {
				if (object instanceof TypeDefinition) {
					TypeDefinition type = (TypeDefinition) object;
					if (!types.containsValue(type)) {
						DataSet dataSet = findDataSet(type);
						types.put(dataSet, type);
					}
				}
				if (object instanceof EntityDefinition) {
					EntityDefinition entityDef = (EntityDefinition) object;
					if (entityDef.getPropertyPath().isEmpty()) {
						DataSet dataSet = (entityDef.getSchemaSpace() == SchemaSpaceID.SOURCE) ? (DataSet.SOURCE)
								: (DataSet.TRANSFORMED);
						types.put(dataSet, entityDef.getType());
					}
				}
				if (object instanceof InstanceReference) {
					InstanceService is = (InstanceService) HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench().getService(InstanceService.class);
					object = is.getInstance((InstanceReference) object);
				}
				if (object instanceof Instance) {
					Instance instance = (Instance) object;
					types.put(instance.getDataSet(), instance.getDefinition());
				}
			}
		}
		
		Pair<TypeDefinition, DataSet> typeInfo = null;
		
		// select a type
		if (types.size() == 1) {
			Entry<DataSet, TypeDefinition> entry = types.entries().iterator().next();
			typeInfo = new Pair<TypeDefinition, DataSet>(
					entry.getValue(), 
					entry.getKey());
		}
		else if (!types.isEmpty()) {
			// choose through dialog
			DataSetTypeSelectionDialog dialog = new DataSetTypeSelectionDialog(Display
					.getCurrent().getActiveShell(), "Multiple types: select which to change the style for", null,
					types);
			if (dialog.open() == DataSetTypeSelectionDialog.OK) {
				typeInfo = dialog.getObject();
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
