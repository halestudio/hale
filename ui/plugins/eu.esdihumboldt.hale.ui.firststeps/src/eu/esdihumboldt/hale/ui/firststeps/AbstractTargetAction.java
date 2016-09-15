/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.firststeps;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Cheat sheet action that performs an action related to a specific target type
 * or property.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTargetAction extends Action implements ICheatSheetAction, Runnable {

	private List<String> params;
	private ICheatSheetManager manager;

	@Override
	public void run() {
		// if Display not the active Thread
		if (Display.getCurrent() == null) {
			// execute in display thread
			PlatformUI.getWorkbench().getDisplay().asyncExec(this);
			return;
		}

		if (params == null || params.isEmpty()) {
			return;
		}

		// retrieve the target schema
		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
		SchemaSpace targetSchema = ss.getSchemas(SchemaSpaceID.TARGET);

		// find type
		QName typeName = QName.valueOf(params.get(0));
		TypeDefinition type = targetSchema.getType(typeName);
		if (type == null) {
			// check all mapping relevant types for local name only
			for (TypeDefinition candidate : targetSchema.getMappingRelevantTypes()) {
				if (candidate.getName().getLocalPart().equals(params.get(0))) {
					// use the first found
					type = candidate;
					break;
				}
			}
		}

		if (type != null) {
			EntityDefinition entity = new TypeEntityDefinition(type, SchemaSpaceID.TARGET, null);

			if (params.size() > 1) {
				// determine property entity
				EntityAccessor accessor = new EntityAccessor(entity);
				for (int i = 1; i < params.size(); i++) {
					QName propertyName = QName.valueOf(params.get(i));
					String namespace = propertyName.getNamespaceURI();
					if (namespace != null && namespace.isEmpty()) {
						// treat empty namespace as ignoring namespace
						namespace = null;
					}
					accessor = accessor.findChildren(propertyName.getLocalPart(), namespace);
				}
				entity = accessor.toEntityDefinition();
			}

			if (entity != null) {
				run(entity, manager);
			}
			else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"Schema element not found",
						"The schema element was not found in the target schema, please make sure the correct schema is loaded.");
			}
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Schema element not found",
					MessageFormat.format(
							"The type {0} was not found in the target schema, please make sure the correct schema is loaded.",
							typeName.getLocalPart()));
		}
	}

	/**
	 * Execute the action based on the given target entity. This method is
	 * invoked in the SWT thread.
	 * 
	 * @param target the target entity
	 * @param manager the cheat sheet manager
	 * @return a result that may be used when overriding the method, not used
	 *         after the execution and thus may be <code>null</code>
	 */
	protected abstract Object run(EntityDefinition target, ICheatSheetManager manager);

	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		Builder<String> builder = ImmutableList.builder();
		for (String param : params) {
			// ignore null params
			if (param != null) {
				builder.add(param);
			}
		}
		this.params = builder.build();
		this.manager = manager;
		run();
	}
}
