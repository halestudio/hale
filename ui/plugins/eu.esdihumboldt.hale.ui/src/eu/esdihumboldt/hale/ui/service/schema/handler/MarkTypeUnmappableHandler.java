/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.service.schema.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Handler that removes the mappable flag from a single selected type.
 * 
 * @author Kai Schwierczek
 */
public class MarkTypeUnmappableHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			SchemaService schemaService = PlatformUI.getWorkbench().getService(SchemaService.class);
			Iterator<?> it = ((IStructuredSelection) selection).iterator();
			List<TypeDefinition> sourceTypes = new ArrayList<>();
			List<TypeDefinition> targetTypes = new ArrayList<>();
			while (it.hasNext()) {
				Object selected = it.next();
				TypeDefinition type = null;
				if (selected instanceof TypeEntityDefinition) {
					type = ((TypeEntityDefinition) selected).getDefinition();
				}
				else if (selected instanceof TypeDefinition) {
					type = (TypeDefinition) selected;
				}
				if (type != null) {
					if (schemaService.getSchemas(SchemaSpaceID.SOURCE).getMappingRelevantTypes()
							.contains(type)) {
						sourceTypes.add(type);
					}
					else {
						targetTypes.add(type);
					}
				}
			}

			if (!sourceTypes.isEmpty()) {
				schemaService.toggleMappable(SchemaSpaceID.SOURCE, sourceTypes);
			}

			if (!targetTypes.isEmpty()) {
				schemaService.toggleMappable(SchemaSpaceID.TARGET, targetTypes);
			}
		}

		return null;
	}
}
