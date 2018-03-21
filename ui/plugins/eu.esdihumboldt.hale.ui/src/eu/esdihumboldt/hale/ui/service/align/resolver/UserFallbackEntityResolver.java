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

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.io.impl.DefaultEntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.dummy.EntityToDef;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.EntityCandidates;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.PropertyEntityResolverDialog;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.ResolveCache;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.TypeEntityResolverDialog;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.ViewerEntityTray;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Entity resolver that asks the user for replacement of entities that were not
 * found.
 * 
 * @author Simon Templer
 */
public class UserFallbackEntityResolver extends DefaultEntityResolver {

	/**
	 * Name of the temporary property to indicate that the user wants to skip
	 * resolving entities until a new project is loaded
	 */
	public static final String RESOLVE_SKIP_PROPERTY = "resolve.skip";

	/**
	 * Name of the temporary property that contains the resolve cache.
	 */
	public static final String PROPERTY_RESOLVE_CACHE = "resolve.cache";

	@Override
	public Property resolveProperty(final PropertyType entity, final TypeIndex schema,
			final SchemaSpaceID schemaSpace) {
		try {
			return super.resolveProperty(entity, schema, schemaSpace);
		} catch (RuntimeException e) {
			// use PropertyEntityDialog as fall-back
			final EntityDefinition candidate = EntityCandidates.find(entity, schema, schemaSpace);
			if (candidate != null) {
				// ensure the corresponding contexts are present
				EntityDefinitionService es = HaleUI.getServiceProvider()
						.getService(EntityDefinitionService.class);
				es.addContexts(candidate);
			}

			PropertyEntityDefinition dummy = EntityToDef.toDummyDef(entity, schemaSpace);

			return resolveProperty(dummy, candidate, schemaSpace);
		}
	}

	/**
	 * Ask the user to select a replacement for a property.
	 * 
	 * @param original the original entity
	 * @param candidate a candidate for the replacement
	 * @param schemaSpace the schema space
	 * @return the resolved property (may be the original)
	 */
	public static Property resolveProperty(PropertyEntityDefinition original,
			@Nullable EntityDefinition candidate, SchemaSpaceID schemaSpace) {
		ResolveCache cache = getCache();
		PropertyEntityDefinition replacement = cache.getReplacement(original);
		if (replacement != null) {
			// use cached replacement
			return new DefaultProperty(replacement);
		}

		ProjectService ps = HaleUI.getServiceProvider().getService(ProjectService.class);

		final AtomicBoolean canceled;
		final AtomicBoolean skipped = new AtomicBoolean(false);
		if (ps.getTemporaryProperty(RESOLVE_SKIP_PROPERTY, Value.of(false)).as(Boolean.class)) {
			canceled = new AtomicBoolean(true);
		}
		else {
			canceled = new AtomicBoolean(false);
		}

		final AtomicReference<EntityDefinition> result = new AtomicReference<>();

		if (!canceled.get()) {

			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					PropertyEntityResolverDialog dlg = new PropertyEntityResolverDialog(
							Display.getCurrent().getActiveShell(), schemaSpace, null,
							"Cell entity could not be resolved", candidate) {

						@Override
						public void create() {
							super.create();
							openTray(new ViewerEntityTray(original));
						}

					};
					switch (dlg.open()) {
					case Window.OK:
						result.set(dlg.getObject());
						break;
					case Window.CANCEL:
						// Don't try to resolve further entities
						ps.setTemporaryProperty(RESOLVE_SKIP_PROPERTY, Value.of(true));
						canceled.set(true);
						break;
					case PropertyEntityResolverDialog.SKIP:
						// skip this entity
						skipped.set(true);
						break;
					default:
						canceled.set(true);
					}
				}
			});
		}

		EntityDefinition def = result.get();
		if (canceled.get() || skipped.get()) {
			// return the original so the cell is not lost
			return new DefaultProperty(original);
		}
		else if (def == null) {
			// caller must take care about this
			return null;
		}
		else {
			PropertyEntityDefinition propDef = (PropertyEntityDefinition) def;
			cache.put(original, propDef);
			return new DefaultProperty(propDef);
		}
	}

	/**
	 * @return the current resolve cache
	 */
	public static ResolveCache getCache() {
		ProjectService ps = HaleUI.getServiceProvider().getService(ProjectService.class);
		ResolveCache result = ps.getTemporaryProperty(PROPERTY_RESOLVE_CACHE)
				.as(ResolveCache.class);
		if (result == null) {
			result = new ResolveCache();
			ps.setTemporaryProperty(PROPERTY_RESOLVE_CACHE, Value.of(result));
		}
		return result;
	}

	/**
	 * Reset the resolve cache.
	 */
	public static void resetCache() {
		ProjectService ps = HaleUI.getServiceProvider().getService(ProjectService.class);
		ps.setTemporaryProperty(PROPERTY_RESOLVE_CACHE, Value.of(new ResolveCache()));
	}

	@Override
	public Type resolveType(final ClassType entity, final TypeIndex schema,
			final SchemaSpaceID schemaSpace) {
		try {
			return super.resolveType(entity, schema, schemaSpace);
		} catch (RuntimeException e) {
			// use TypeEntityDialog as fall-back
			final EntityDefinition candidate = EntityCandidates.find(entity, schema, schemaSpace);
			if (candidate != null) {
				// ensure the corresponding contexts are present
				EntityDefinitionService es = HaleUI.getServiceProvider()
						.getService(EntityDefinitionService.class);
				es.addContexts(candidate);
			}

			TypeEntityDefinition dummy = EntityToDef.toDummyDef(entity, schemaSpace);

			return resolveType(dummy, candidate, schemaSpace);
		}
	}

	/**
	 * Ask the user to select a replacement for a type.
	 * 
	 * @param original the original entity
	 * @param candidate a candidate for the replacement
	 * @param schemaSpace the schema space
	 * @return the resolved type (may be the original)
	 */
	public static Type resolveType(TypeEntityDefinition original,
			@Nullable EntityDefinition candidate, SchemaSpaceID schemaSpace) {
		ResolveCache cache = getCache();
		TypeEntityDefinition replacement = cache.getReplacement(original);
		if (replacement != null) {
			// use cached replacement
			return new DefaultType(replacement);
		}

		ProjectService ps = HaleUI.getServiceProvider().getService(ProjectService.class);

		final AtomicBoolean canceled;
		final AtomicBoolean skipped = new AtomicBoolean(false);
		if (ps.getTemporaryProperty(RESOLVE_SKIP_PROPERTY, Value.of(false)).as(Boolean.class)) {
			canceled = new AtomicBoolean(true);
		}
		else {
			canceled = new AtomicBoolean(false);
		}

		final AtomicReference<EntityDefinition> result = new AtomicReference<>();

		if (!canceled.get()) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					TypeEntityResolverDialog dlg = new TypeEntityResolverDialog(
							Display.getCurrent().getActiveShell(), schemaSpace,
							"Cell entity could not be resolved", candidate, false) {

						@Override
						public void create() {
							super.create();
							openTray(new ViewerEntityTray(original));
						}

					};

					switch (dlg.open()) {
					case Window.OK:
						result.set(dlg.getObject());
						break;
					case Window.CANCEL:
						// Don't try to resolve further entities
						ps.setTemporaryProperty(RESOLVE_SKIP_PROPERTY, Value.of(true));
						canceled.set(true);
						break;
					case TypeEntityResolverDialog.SKIP:
						// skip this entity
						skipped.set(true);
						break;
					default:
						canceled.set(true);
					}
				}
			});
		}

		EntityDefinition def = result.get();
		if (canceled.get() || skipped.get()) {
			// return the original so the cell is not lost
			return new DefaultType(original);
		}
		else if (def == null) {
			// caller must take care about this
			return null;
		}
		else {
			TypeEntityDefinition ted = (TypeEntityDefinition) def;

			// make sure that the type is classified as mapping relevant
			if (!ted.getType().getConstraint(MappingRelevantFlag.class).isEnabled()) {
				SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
				ss.toggleMappable(schemaSpace, Collections.singleton(ted.getType()));
			}

			cache.put(original, ted);

			return new DefaultType(ted);
		}
	}

	/**
	 * Reset the project setting that will skip all properties.
	 */
	public static void resetSkip() {
		ProjectService ps = HaleUI.getServiceProvider().getService(ProjectService.class);
		ps.setTemporaryProperty(RESOLVE_SKIP_PROPERTY, Value.of(false));
	}

}
