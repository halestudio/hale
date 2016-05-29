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

package eu.esdihumboldt.hale.ui.geometry.handler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;

/**
 * Set a property as the default geometry.
 * 
 * @author Simon Templer
 */
public class SetDefaultGeometryHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		/*
		 * Set the defaut geometry to the first valid child entity definition
		 * from the selection (for the type the entity definition is associated
		 * to)
		 */
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toList()) {
				if (element instanceof EntityDefinition) {
					EntityDefinition def = (EntityDefinition) element;
					if (!def.getPropertyPath().isEmpty()) {
						// path must not be empty
						// XXX is this true? we could set the default geometry
						// to the type to use all geometries

						List<QName> path = new ArrayList<QName>(def.getPropertyPath().size());
						for (ChildContext child : def.getPropertyPath()) {
							path.add(child.getChild().getName());
						}

						GeometrySchemaService gss = PlatformUI.getWorkbench()
								.getService(GeometrySchemaService.class);
						gss.setDefaultGeometry(def.getType(), path);
					}
				}
			}
		}

		// otherwise does nothing
		return null;
	}

}
