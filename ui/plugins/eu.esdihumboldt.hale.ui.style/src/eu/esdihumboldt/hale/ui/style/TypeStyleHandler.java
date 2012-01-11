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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.style.dialog.FeatureStyleDialog;

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
		
		TypeDefinition type = null;
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			for (Object object : ((IStructuredSelection) selection).toArray()) {
				if (object instanceof TypeDefinition) {
					type = (TypeDefinition) object;
					break;
				}
				if (object instanceof EntityDefinition) {
					EntityDefinition entityDef = (EntityDefinition) object;
					if (entityDef.getPropertyPath().isEmpty()) {
						type = entityDef.getType();
						break;
					}
				}
			}
		}
		
		if (type != null) {
			try {
				FeatureStyleDialog dialog = new FeatureStyleDialog(type);
				dialog.setBlockOnOpen(false);
				dialog.open();
			} catch (Exception e) {
				throw new ExecutionException("Could not open style dialog", e);
			}
		}
		return null;
	}

}
