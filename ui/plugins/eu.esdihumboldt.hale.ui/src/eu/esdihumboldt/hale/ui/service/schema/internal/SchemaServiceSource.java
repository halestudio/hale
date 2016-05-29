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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;

/**
 * Provides UI variables related to the {@link SchemaService}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class SchemaServiceSource extends AbstractSourceProvider {

	/**
	 * The name of the variable which value is <code>true</code> if there is at
	 * least one mappable type present in the {@link SchemaService}
	 */
	public static final String HAS_MAPPABLE_SOURCE_SCHEMA = "hale.schemas.has_mappable_source";

	/**
	 * The name of the variable which value is <code>true</code> if there is at
	 * least one mappable type present in the {@link SchemaService}
	 */
	public static final String HAS_MAPPABLE_TARGET_SCHEMA = "hale.schemas.has_mappable_target";

	/**
	 * The name of the variable which value is <code>true</code> if there is a
	 * source schema present in the {@link SchemaService}
	 */
	public static final String HAS_SOURCE_SCHEMA = "hale.schemas.has_source";

	/**
	 * The name of the variable which value is <code>true</code> if there is a
	 * target schema present in the {@link SchemaService}
	 */
	public static final String HAS_TARGET_SCHEMA = "hale.schemas.has_target";

	private SchemaServiceListener schemaListener;

	/**
	 * Default constructor
	 */
	public SchemaServiceSource() {
		super();

		final SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
		ss.addSchemaServiceListener(schemaListener = new SchemaServiceAdapter() {

			/**
			 * @see SchemaServiceAdapter#schemaAdded(SchemaSpaceID, Schema)
			 */
			@Override
			public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
				update(spaceID);
			}

			/**
			 * @see SchemaServiceAdapter#schemasCleared(SchemaSpaceID)
			 */
			@Override
			public void schemasCleared(SchemaSpaceID spaceID) {
				update(spaceID);
			}

			@Override
			public void mappableTypesChanged(SchemaSpaceID spaceID,
					Collection<? extends TypeDefinition> types) {
				update(spaceID);
			}

			private void update(final SchemaSpaceID spaceID) {
				// perform update in Display thread
				// else invalid thread access exceptions occur (since update to
				// e4)
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						switch (spaceID) {
						case SOURCE:
							fireSourceChanged(ISources.WORKBENCH, HAS_SOURCE_SCHEMA,
									hasSchema(ss, spaceID));
							fireSourceChanged(ISources.WORKBENCH, HAS_MAPPABLE_SOURCE_SCHEMA,
									hasMappableType(ss, spaceID));
							break;
						case TARGET:
							fireSourceChanged(ISources.WORKBENCH, HAS_TARGET_SCHEMA,
									hasSchema(ss, spaceID));
							fireSourceChanged(ISources.WORKBENCH, HAS_MAPPABLE_TARGET_SCHEMA,
									hasMappableType(ss, spaceID));
							break;
						}
					}
				});
			}
		});
	}

	/**
	 * @see ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
		ss.removeSchemaServiceListener(schemaListener);
	}

	/**
	 * @see ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(HAS_MAPPABLE_SOURCE_SCHEMA, hasMappableType(ss, SchemaSpaceID.SOURCE));
		result.put(HAS_MAPPABLE_TARGET_SCHEMA, hasMappableType(ss, SchemaSpaceID.TARGET));
		result.put(HAS_SOURCE_SCHEMA, hasSchema(ss, SchemaSpaceID.SOURCE));
		result.put(HAS_TARGET_SCHEMA, hasSchema(ss, SchemaSpaceID.TARGET));

		return result;
	}

	private static boolean hasSchema(SchemaService ss, SchemaSpaceID spaceID) {
		SchemaSpace schemas = ss.getSchemas(spaceID);
		return schemas != null && !schemas.getTypes().isEmpty();
	}

	private static boolean hasMappableType(SchemaService ss, SchemaSpaceID spaceID) {
		SchemaSpace schemas = ss.getSchemas(spaceID);
		return schemas != null && !schemas.getMappingRelevantTypes().isEmpty();
	}

	/**
	 * @see ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { HAS_MAPPABLE_SOURCE_SCHEMA, HAS_MAPPABLE_TARGET_SCHEMA,
				HAS_SOURCE_SCHEMA, HAS_TARGET_SCHEMA };
	}

}
