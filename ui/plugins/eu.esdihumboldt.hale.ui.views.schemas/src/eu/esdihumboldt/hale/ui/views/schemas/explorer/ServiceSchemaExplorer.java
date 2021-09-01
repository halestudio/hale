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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import java.util.Collection;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationListener;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;

/**
 * Schema explorer wrapper associating it with a schema space.
 * 
 * @author Simon Templer
 */
public class ServiceSchemaExplorer {

	private final SchemaExplorer explorer;

//	private final SchemaSpaceID schemaSpace;

	/**
	 * A reference to the {@link SchemaService} which serves the model for this
	 * view.
	 */
	private final SchemaService schemaService;

	private SchemaServiceListener schemaListener;

	private EntityDefinitionServiceListener entityListener;

	private AlignmentServiceListener alignmentListener;

	private GeometrySchemaServiceListener geometryListener;

	private PopulationListener populationListener;

	/**
	 * Associate the given schema explorer to the schema service.
	 * 
	 * @param explorer the schema explorer
	 * @param schemaSpace the schema space
	 */
	public ServiceSchemaExplorer(final SchemaExplorer explorer, final SchemaSpaceID schemaSpace) {
		super();
		this.explorer = explorer;
//		this.schemaSpace = schemaSpace;

		schemaService = PlatformUI.getWorkbench().getService(SchemaService.class);
		schemaService.addSchemaServiceListener(schemaListener = new SchemaServiceListener() {

			@Override
			public void schemaRemoved(SchemaSpaceID spaceID) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						if (spaceID.equals(schemaSpace)) {
							SchemaSpace schemas = schemaService.getSchemas(spaceID);
							if (StreamSupport.stream(schemas.getSchemas().spliterator(), false)
									.count() == 0) {
								explorer.setSchema(null);
							}
							else {
								explorer.setSchema(schemaService.getSchemas(spaceID));
							}
						}
						refreshInDisplayThread();
					}
				});

			}

			@Override
			public void schemasCleared(final SchemaSpaceID spaceID) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						if (spaceID.equals(schemaSpace)) {
							explorer.setSchema(null);
							refreshInDisplayThread();
						}
					}
				});
			}

			@Override
			public void schemaAdded(final SchemaSpaceID spaceID, Schema schema) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						if (spaceID.equals(schemaSpace)) {
							explorer.setSchema(schemaService.getSchemas(spaceID));
						}
						refreshInDisplayThread();
					}
				});
			}

			@Override
			public void mappableTypesChanged(final SchemaSpaceID spaceID,
					Collection<? extends TypeDefinition> types) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						if (spaceID.equals(schemaSpace)) {
							explorer.setSchema(schemaService.getSchemas(spaceID));
						}
						refreshInDisplayThread();
					}
				});
			}
		});

		// redraw on alignment change
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addListener(alignmentListener = new AlignmentServiceListener() {

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				refreshInDisplayThread();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				refreshInDisplayThread();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				refreshInDisplayThread();
			}

			@Override
			public void alignmentCleared() {
				refreshInDisplayThread();
			}

			@Override
			public void alignmentChanged() {
				refreshInDisplayThread();
			}

			@Override
			public void customFunctionsChanged() {
				// no update needed
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				// Cell disabling/enabling can affect schema view
				if (Cell.PROPERTY_DISABLE_FOR.equals(propertyName)
						|| Cell.PROPERTY_ENABLE_FOR.equals(propertyName))
					refreshInDisplayThread();
				// currently no other cell property that affects the schema view
			}
		});

		PopulationService ps = PlatformUI.getWorkbench().getService(PopulationService.class);
		ps.addListener(populationListener = new PopulationListener() {

			@Override
			public void populationChanged(SchemaSpaceID ssid) {
				refreshInDisplayThread();
			}

		});

		// listen on default geometry changes
		GeometrySchemaService gss = PlatformUI.getWorkbench()
				.getService(GeometrySchemaService.class);
		gss.addListener(geometryListener = new GeometrySchemaServiceListener() {

			@Override
			public void defaultGeometryChanged(TypeDefinition type) {
				refreshInDisplayThread();
			}
		});

		// listen on entity context changes
		EntityDefinitionService eds = PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);
		eds.addListener(entityListener = new EntityDefinitionServiceListener() {

			@Override
			public void contextsAdded(Iterable<EntityDefinition> contextEntities) {
				// XXX improve?
				refreshInDisplayThread();
			}

			@Override
			public void contextRemoved(EntityDefinition contextEntity) {
				// FIXME if the entity is a type the explorer doesn't get
				// updated correctly -> reset input?
				// XXX improve?
				refreshInDisplayThread();
			}

			@Override
			public void contextAdded(EntityDefinition contextEntity) {
				// XXX improve?
				refreshInDisplayThread();
			}
		});

		explorer.setSchema(schemaService.getSchemas(schemaSpace));
	}

	/**
	 * @return the explorer
	 */
	public SchemaExplorer getExplorer() {
		return explorer;
	}

	/**
	 * Refresh map in the display thread
	 */
	protected void refreshInDisplayThread() {
		if (Display.getCurrent() != null) {
			refresh();
		}
		else {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					refresh();
				}
			});
		}
	}

	/**
	 * Refresh both tree viewers
	 */
	public void refresh() {
		explorer.getTreeViewer().refresh(true);
	}

	/**
	 * Remove all service listeners.
	 */
	public void dispose() {
		if (schemaListener != null) {
			schemaService.removeSchemaServiceListener(schemaListener);
		}

		if (alignmentListener != null) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.removeListener(alignmentListener);
		}

		if (entityListener != null) {
			EntityDefinitionService eds = PlatformUI.getWorkbench()
					.getService(EntityDefinitionService.class);
			eds.removeListener(entityListener);
		}

		if (geometryListener != null) {
			GeometrySchemaService gss = PlatformUI.getWorkbench()
					.getService(GeometrySchemaService.class);
			gss.removeListener(geometryListener);
		}

		if (populationListener != null) {
			PopulationService ps = PlatformUI.getWorkbench().getService(PopulationService.class);
			ps.removeListener(populationListener);
		}
	}

}
