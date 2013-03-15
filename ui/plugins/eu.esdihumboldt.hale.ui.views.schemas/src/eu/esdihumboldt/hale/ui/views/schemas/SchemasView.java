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
package eu.esdihumboldt.hale.ui.views.schemas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationListener;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.function.contribution.SchemaSelectionFunctionContribution;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaServiceListener;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection.SchemaStructuredMode;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.EntitySchemaExplorer;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer;
import eu.esdihumboldt.hale.ui.views.schemas.internal.Messages;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * This view component handles the display of source and target schemas.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class SchemasView extends PropertiesViewPart {

	private static final ALogger log = ALoggerFactory.getLogger(SchemasView.class);

	/**
	 * Function contribution that always uses this view's selection
	 */
	private class SchemaFunctionContribution extends SchemaSelectionFunctionContribution {

		/**
		 * Default constructor
		 */
		public SchemaFunctionContribution() {
			super();
		}

		/**
		 * @see SchemaSelectionFunctionContribution#getSelection()
		 */
		@Override
		public SchemaSelection getSelection() {
			if (currentSelection instanceof SchemaSelection) {
				return (SchemaSelection) currentSelection;
			}

			return super.getSelection();
		}

	}

	/**
	 * Selection provider combining selections from source and target schema
	 * explorers
	 */
	private class SchemasSelectionProvider implements ISelectionProvider, ISelectionChangedListener {

		/**
		 * The selection listeners
		 */
		private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

		private boolean lastSourceFirst = true;

		/**
		 * Default constructor
		 */
		public SchemasSelectionProvider() {
			super();

			sourceExplorer.getTreeViewer().addSelectionChangedListener(this);
			targetExplorer.getTreeViewer().addSelectionChangedListener(this);
		}

		/**
		 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
		 */
		@Override
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		/**
		 * @see ISelectionProvider#getSelection()
		 */
		@Override
		public ISelection getSelection() {
			return currentSelection;
		}

		/**
		 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
		 */
		@Override
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		/**
		 * @see ISelectionProvider#setSelection(ISelection)
		 */
		@Override
		public void setSelection(ISelection selection) {
			SchemasView.this.currentSelection = selection;
		}

		/**
		 * Update the selection and fire a selection change
		 * 
		 * @param sourceFirst if the selected objects from the source shall be
		 *            added first if the selection is a combination from source
		 *            and target
		 */
		private void updateSelection(boolean sourceFirst) {
			lastSourceFirst = sourceFirst;

			// combine the selections of both viewers

			// source items
			ITreeSelection sourceSelection = (ITreeSelection) sourceExplorer.getTreeViewer()
					.getSelection();
			// target items
			ITreeSelection targetSelection = (ITreeSelection) targetExplorer.getTreeViewer()
					.getSelection();

			/*
			 * XXX because there are problem with the properties view if we
			 * combine the objects here (multiple objects in the selection), we
			 * return only one of the original selections
			 */
			SchemaStructuredMode selectionMode = (sourceFirst) ? (SchemaStructuredMode.ONLY_SOURCE)
					: (SchemaStructuredMode.ONLY_TARGET);

			Collection<EntityDefinition> sourceItems = collectDefinitions(sourceSelection,
					SchemaSpaceID.SOURCE);
			Collection<EntityDefinition> targetItems = collectDefinitions(targetSelection,
					SchemaSpaceID.TARGET);
			DefaultSchemaSelection selection = new DefaultSchemaSelection(sourceItems, targetItems,
					selectionMode);

			fireSelectionChange(selection);
		}

		/**
		 * Collect {@link EntityDefinition} from a {@link TreeSelection}
		 * containing {@link TypeDefinition}s and {@link PropertyDefinition}s
		 * 
		 * @param selection the tree selection
		 * @param schemaSpace the schema space identifier
		 * @return the collected entity definitions
		 */
		private Collection<EntityDefinition> collectDefinitions(ITreeSelection selection,
				SchemaSpaceID schemaSpace) {
			if (selection.isEmpty()) {
				return Collections.emptyList();
			}

			TreePath[] paths = selection.getPaths();
			List<EntityDefinition> result = new ArrayList<EntityDefinition>(paths.length);

			for (TreePath path : paths) {
				Object last = path.getLastSegment();

				if (last instanceof EntityDefinition) {
					// use entity definition directly
					result.add((EntityDefinition) last);
				}
				else if (last instanceof TypeDefinition) {
					// create entity definition for type
					result.add(new TypeEntityDefinition((TypeDefinition) last, schemaSpace, null));
				}
				else if (last instanceof PropertyDefinition) {
					// create property entity definition w/ default instance
					// contexts
					List<ChildContext> propertyPath = new ArrayList<ChildContext>();
					Definition<?> element = (Definition<?>) last;
					int index = path.getSegmentCount() - 1;
					while (element != null && !(element instanceof TypeDefinition)) {
						ChildContext context = new ChildContext((ChildDefinition<?>) element);
						propertyPath.add(0, context);
						Object segment = path.getSegment(--index);
						if (segment instanceof Definition<?>) {
							element = (Definition<?>) segment;
						}
						else {
							element = null;
						}
					}

					if (element != null) {
						// remaining element is the type definition
						result.add(new PropertyEntityDefinition((TypeDefinition) element,
								propertyPath, schemaSpace, null));
					}
					else {
						log.error("No parent type definition for property path found, skipping object for selection.");
					}
				}
				else {
					// XXX include GroupPropertyDefinitions also in selection?
					log.debug("Could determine entity definition for object, skipping object for selection.");
				}
			}

			return result;
		}

		/**
		 * Sets the selection to the given selection and fires a selection
		 * change
		 * 
		 * @param selection the selection to set
		 */
		protected void fireSelectionChange(ISelection selection) {
			SchemasView.this.currentSelection = selection;

			SelectionChangedEvent event = new SelectionChangedEvent(this, currentSelection);

			for (ISelectionChangedListener listener : listeners) {
				listener.selectionChanged(event);
			}
		}

		/**
		 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
		 */
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			updateSelection(event.getSelectionProvider() == sourceExplorer.getTreeViewer());
		}

		/**
		 * @return the lastSourceFirst
		 */
		public boolean isLastSourceFirst() {
			return lastSourceFirst;
		}

	}

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.schemas"; //$NON-NLS-1$

	/**
	 * The current selection
	 */
	private ISelection currentSelection;

	/**
	 * Viewer for the source schema
	 */
	private SchemaExplorer sourceExplorer;

	/**
	 * Viewer for the target schema
	 */
	private SchemaExplorer targetExplorer;

	/**
	 * A reference to the {@link SchemaService} which serves the model for this
	 * view.
	 */
	private SchemaService schemaService;

	private Image functionImage;

	private Image augmentImage;

	private SchemaServiceListener schemaListener;

	private SchemasSelectionProvider selectionProvider;

	private EntityDefinitionServiceListener entityListener;

	private AlignmentServiceListener alignmentListener;

	private GeometrySchemaServiceListener geometryListener;

	private PopulationListener populationListener;

	private CellSyncAction cellSyncAction;

//
//	private StyleServiceListener styleListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite _parent) {
		// get schema service
		schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		schemaService.addSchemaServiceListener(schemaListener = new SchemaServiceListener() {

			@Override
			public void schemasCleared(final SchemaSpaceID spaceID) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						switch (spaceID) {
						case SOURCE:
							sourceExplorer.setSchema(null);
							break;
						case TARGET:
							targetExplorer.setSchema(null);
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
						switch (spaceID) {
						case SOURCE:
							sourceExplorer.setSchema(schemaService.getSchemas(spaceID));
							break;
						case TARGET:
							targetExplorer.setSchema(schemaService.getSchemas(spaceID));
						}
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
						switch (spaceID) {
						case SOURCE:
							sourceExplorer.setSchema(schemaService.getSchemas(spaceID));
							break;
						case TARGET:
							targetExplorer.setSchema(schemaService.getSchemas(spaceID));
						}
					}
				});
			}
		});

		Composite modelComposite = new Composite(_parent, SWT.BEGINNING);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		modelComposite.setLayout(layout);

		// source schema toolbar, filter and explorer
//		sourceExplorer = new SchemaExplorer(modelComposite, "Source");
		sourceExplorer = new EntitySchemaExplorer(modelComposite, "Source", SchemaSpaceID.SOURCE);
		sourceExplorer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// function button
		final Button functionButton = new Button(modelComposite, SWT.PUSH | SWT.FLAT);
		functionImage = SchemasViewPlugin.getImageDescriptor("icons/mapping.gif").createImage(); //$NON-NLS-1$
		augmentImage = SchemasViewPlugin.getImageDescriptor("icons/augment.gif").createImage(); //$NON-NLS-1$
		functionButton.setImage(functionImage);
		functionButton.setToolTipText(Messages.ModelNavigationView_FunctionButtonToolTipText);
		functionButton.setEnabled(false);
		functionButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		final SchemaFunctionContribution functionContribution = new SchemaFunctionContribution();
		manager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// populate context menu
				manager.add(functionContribution);
			}

		});
		final Menu functionMenu = manager.createContextMenu(functionButton);
		functionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// show menu on button press
				functionMenu.setLocation(Display.getCurrent().getCursorLocation());
				functionMenu.setVisible(true);
			}

		});

		// target schema toolbar, filter and explorer
		targetExplorer = new EntitySchemaExplorer(modelComposite, "Target", SchemaSpaceID.TARGET);
		targetExplorer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// redraw on alignment change
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		as.addListener(alignmentListener = new AlignmentServiceListener() {

			@Override
			public void cellReplaced(Cell oldCell, Cell newCell) {
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
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				// Cell disabling/enabling can affect schema view
				if (Cell.PROPERTY_DISABLE_FOR.equals(propertyName)
						|| Cell.PROPERTY_ENABLE_FOR.equals(propertyName))
					refreshInDisplayThread();
				// currently no other cell property that affects the schema view
			}
		});

		PopulationService ps = (PopulationService) PlatformUI.getWorkbench().getService(
				PopulationService.class);
		ps.addListener(populationListener = new PopulationListener() {

			@Override
			public void populationChanged(SchemaSpaceID ssid) {
				refreshInDisplayThread();
			}

		});

		// also add the alignment listener to the style service (for refreshing
		// icons when style changes)
//		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//		styleService.addListener(styleListener = new StyleServiceListener() {
//			
//			@Override
//			public void stylesRemoved(StyleService styleService) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//			
//			@Override
//			public void stylesAdded(StyleService styleService) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//			
//			@Override
//			public void backgroundChanged(StyleService styleService, RGB background) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//
//			@Override
//			public void styleSettingsChanged(StyleService styleService) {
//				// refresh to update legend images
//				refreshInDisplayThread();
//			}
//		});

		// listen on default geometry changes
		GeometrySchemaService gss = (GeometrySchemaService) PlatformUI.getWorkbench().getService(
				GeometrySchemaService.class);
		gss.addListener(geometryListener = new GeometrySchemaServiceListener() {

			@Override
			public void defaultGeometryChanged(TypeDefinition type) {
				refreshInDisplayThread();
			}
		});

		// listen on entity context changes
		EntityDefinitionService eds = (EntityDefinitionService) PlatformUI.getWorkbench()
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

		// source context menu
		new ViewerMenu(getSite(), sourceExplorer.getTreeViewer());
		// target context menu
		new ViewerMenu(getSite(), targetExplorer.getTreeViewer());

		// initialization of explorers
		sourceExplorer.setSchema(schemaService.getSchemas(SchemaSpaceID.SOURCE));
		targetExplorer.setSchema(schemaService.getSchemas(SchemaSpaceID.TARGET));

		// register selection provider
		getSite().setSelectionProvider(selectionProvider = new SchemasSelectionProvider());

		// listen for selection changes and update function button
		selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				functionButton.setEnabled(functionContribution.hasActiveFunctions());
				if (event.getSelection() instanceof SchemaSelection) {
					SchemaSelection selection = (SchemaSelection) event.getSelection();
					if (selection.getSourceItems().size() == 0
							&& selection.getTargetItems().size() > 0) {
						// augmentation
						functionButton.setImage(augmentImage);
					}
					else {
						// function
						functionButton.setImage(functionImage);
					}
				}
			}

		});

		// view toolbar
		getViewSite()
				.getActionBars()
				.getToolBarManager()
				.add(cellSyncAction = new CellSyncAction(getSite().getPage().getWorkbenchWindow()
						.getSelectionService(), sourceExplorer, targetExplorer));
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
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.schema_explorer";
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		if (selectionProvider != null && !selectionProvider.isLastSourceFirst()) {
			targetExplorer.getTreeViewer().getControl().setFocus();
		}
		else {
			sourceExplorer.getTreeViewer().getControl().setFocus();
		}
	}

//	/**
//	 * Update the viewers on changes to a schema
//	 * 
//	 * @param schema the schema that changed
//	 */
//	protected void update(SchemaSpaceID schema) {
//		switch (schema) {
//		case SOURCE:
//			sourceSchemaViewer.setInput(schemaItemService.getRoot(schema));
//			sourceSchemaViewer.refresh();
//			break;
//		case TARGET:
//			targetSchemaViewer.setInput(schemaItemService.getRoot(schema));
//			targetSchemaViewer.refresh();
//			break;
//		}
//	}

//	/**
//	 * Select the item with representing the given identifier
//	 * 
//	 * @param identifier the identifier
//	 */
//	public void selectItem(String identifier) {
//		TreeViewer tree;
//		SchemaItem item = schemaItemService.getSchemaItem(identifier, SchemaSpaceID.SOURCE);
//		if (item == null) {
//			item = schemaItemService.getSchemaItem(identifier, SchemaSpaceID.TARGET);
//			tree = targetSchemaViewer;
//		}
//		else {
//			tree = sourceSchemaViewer;
//		}
//		
//		if (item != null) {
//			tree.setSelection(new StructuredSelection(item), true);
//		}
//	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (cellSyncAction != null) {
			cellSyncAction.dispose();
		}

		if (schemaListener != null) {
			schemaService.removeSchemaServiceListener(schemaListener);
		}

		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			as.removeListener(alignmentListener);
//			StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//			styleService.removeListener(styleListener);
		}

		if (entityListener != null) {
			EntityDefinitionService eds = (EntityDefinitionService) PlatformUI.getWorkbench()
					.getService(EntityDefinitionService.class);
			eds.removeListener(entityListener);
		}

		if (geometryListener != null) {
			GeometrySchemaService gss = (GeometrySchemaService) PlatformUI.getWorkbench()
					.getService(GeometrySchemaService.class);
			gss.removeListener(geometryListener);
		}

		if (populationListener != null) {
			PopulationService ps = (PopulationService) PlatformUI.getWorkbench().getService(
					PopulationService.class);
			ps.removeListener(populationListener);
		}

		if (functionImage != null) {
			functionImage.dispose();
		}
		if (augmentImage != null) {
			augmentImage.dispose();
		}

		super.dispose();
	}

	/**
	 * Refresh both tree viewers
	 */
	public void refresh() {
		sourceExplorer.getTreeViewer().refresh(true);
		targetExplorer.getTreeViewer().refresh(true);
	}

}
