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

package eu.esdihumboldt.hale.ui.views.resources;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;
import eu.esdihumboldt.hale.ui.views.resources.internal.ProjectToken;
import eu.esdihumboldt.hale.ui.views.resources.internal.ResourcesContentProvider;
import eu.esdihumboldt.hale.ui.views.resources.internal.ResourcesLabelProvider;

/**
 * View displaying project resources.
 * 
 * @author Simon Templer
 */
public class ResourcesView extends PropertiesViewPart {

	private TreeViewer viewer;
	private ProjectServiceAdapter projectServiceListener;
	private SchemaServiceListener ssListener;
	private InstanceServiceAdapter isa;

	@Override
	protected void createViewControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new ResourcesContentProvider());
		viewer.setLabelProvider(new ResourcesLabelProvider());

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);

		ps.addListener(projectServiceListener = new ProjectServiceAdapter() {

			@Override
			public void resourceAdded(String actionId, Resource resource) {
				updateInDisplayThread();
			}

			@Override
			public void resourcesRemoved(String actionId, List<Resource> resources) {
				updateInDisplayThread();
			}

			@Override
			public void afterLoad(ProjectService projectService) {
				updateInDisplayThread();
			}

			@Override
			public void projectInfoChanged(ProjectInfo info) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						viewer.update(ProjectToken.TOKEN, null);
						updateInDisplayThread();
					}
				});
			}

		});

		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(3);

		update();

		new ViewerMenu(getSite(), viewer);
		getSite().setSelectionProvider(viewer);

		// adding listener to refresh the resource view when any schema,
		// instance is added or cleared.
		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
		ssListener = new SchemaServiceListener() {

			@Override
			public void schemaRemoved(SchemaSpaceID spaceID) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						updateInDisplayThread();
					}
				});
			}

			@Override
			public void schemasCleared(final SchemaSpaceID spaceID) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						updateInDisplayThread();
					}
				});
			}

			@Override
			public void schemaAdded(final SchemaSpaceID spaceID, Schema schema) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						updateInDisplayThread();
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
						updateInDisplayThread();
					}
				});
			}

		};

		ss.addSchemaServiceListener(ssListener);

		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addListener(isa = new InstanceServiceAdapter() {

			@Override
			public void datasetChanged(DataSet type) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						updateInDisplayThread();
					}
				});
			}

		});

	}

	/**
	 * Update the resource viewer input in the display thread.
	 */
	protected void updateInDisplayThread() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				update();
				refreshInDisplayThread();
			}
		});
	}

	/**
	 * Update the resource viewer input.
	 */
	protected void update() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		if (ps != null) {
			viewer.setInput(ps.getResources());
		}
		else {
			viewer.setInput(Collections.EMPTY_LIST);
		}
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
		refreshInDisplayThread();
	}

	@Override
	public void dispose() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		if (projectServiceListener != null) {
			ps.removeListener(projectServiceListener);
		}
		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
		if (ssListener != null) {
			ss.removeSchemaServiceListener(ssListener);
		}
		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		if (isa != null) {
			is.removeListener(isa);
		}
		super.dispose();
	}

	/**
	 * Refresh tree viewer in the display thread
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
	 * Refresh the tree viewer.
	 */
	public void refresh() {
		viewer.refresh(true);
	}

}
