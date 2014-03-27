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

package eu.esdihumboldt.hale.ui.service.align.resolver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.io.impl.DefaultEntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.common.PropertyEntityDialog;
import eu.esdihumboldt.hale.ui.function.common.TypeEntityDialog;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.EntityCandidates;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.EntityToDef;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.ViewerEntityTray;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Entity resolver that asks the user for replacement of entities that were not
 * found.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class UserFallbackEntityResolver extends DefaultEntityResolver {

	@Override
	protected Entity resolveProperty(final PropertyType entity, final TypeIndex schema,
			final SchemaSpaceID schemaSpace) {
		try {
			return super.resolveProperty(entity, schema, schemaSpace);
		} catch (RuntimeException e) {
			// use PropertyEntityDialog as fall-back
			final EntityDefinition candidate = EntityCandidates.find(entity, schema, schemaSpace);
			if (candidate != null) {
				// ensure the corresponding contexts are present
				EntityDefinitionService es = HaleUI.getServiceProvider().getService(
						EntityDefinitionService.class);
				es.addContexts(candidate);
			}
			final AtomicReference<EntityDefinition> result = new AtomicReference<>();
			final AtomicBoolean canceled = new AtomicBoolean(false);
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					PropertyEntityDialog dlg = new PropertyEntityDialog(Display.getCurrent()
							.getActiveShell(), schemaSpace, null,
							"Cell entity could not be resolved", candidate) {

						@Override
						public void create() {
							super.create();
							openTray(new ViewerEntityTray(entity, schemaSpace));
						}

					};
					if (dlg.open() == Window.OK) {
						result.set(dlg.getObject());
					}
					else {
						canceled.set(true);
					}
				}
			});
			EntityDefinition def = result.get();
			if (canceled.get()) {
				// return a dummy so the cell is not lost
				return new DefaultProperty((PropertyEntityDefinition) EntityToDef.toDummyDef(
						entity, schemaSpace));
			}
			else if (def == null) {
				// caller must take care about this
				return null;
			}
			else {
				return new DefaultProperty((PropertyEntityDefinition) def);
			}
		}
	}

	@Override
	protected Entity resolveType(final ClassType entity, final TypeIndex schema,
			final SchemaSpaceID schemaSpace) {
		try {
			return super.resolveType(entity, schema, schemaSpace);
		} catch (RuntimeException e) {
			// use TypeEntityDialog as fall-back
			final EntityDefinition candidate = EntityCandidates.find(entity, schema, schemaSpace);
			if (candidate != null) {
				// ensure the corresponding contexts are present
				EntityDefinitionService es = HaleUI.getServiceProvider().getService(
						EntityDefinitionService.class);
				es.addContexts(candidate);
			}
			final AtomicReference<EntityDefinition> result = new AtomicReference<>();
			final AtomicBoolean canceled = new AtomicBoolean(false);
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					TypeEntityDialog dlg = new TypeEntityDialog(Display.getCurrent()
							.getActiveShell(), schemaSpace, "Cell entity could not be resolved",
							candidate, true) {

						@Override
						public void create() {
							super.create();
							openTray(new ViewerEntityTray(entity, schemaSpace));
						}

					};
					if (dlg.open() == Window.OK) {
						result.set(dlg.getObject());
					}
					else {
						canceled.set(true);
					}
				}
			});
			EntityDefinition def = result.get();
			if (canceled.get()) {
				// return a dummy so the cell is not lost
				return new DefaultType(EntityToDef.toDummyDef(entity, schemaSpace));
			}
			else if (def == null) {
				// caller must take care about this
				return null;
			}
			else {
				return new DefaultType((TypeEntityDefinition) def);
			}
		}
	}

}
