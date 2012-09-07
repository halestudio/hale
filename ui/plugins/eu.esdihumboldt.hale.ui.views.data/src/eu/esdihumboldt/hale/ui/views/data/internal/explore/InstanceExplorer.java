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

package eu.esdihumboldt.hale.ui.views.data.internal.explore;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

import com.google.common.collect.Iterables;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataActionExtension;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataActionFactory;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.util.viewer.PairLabelProvider;
import eu.esdihumboldt.hale.ui.views.data.InstanceViewer;
import eu.esdihumboldt.hale.ui.views.data.internal.SimpleInstanceSelectionProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Instance explorer
 * 
 * @author Simon Templer
 */
public class InstanceExplorer implements InstanceViewer {

	private final SimpleInstanceSelectionProvider selectionProvider = new SimpleInstanceSelectionProvider();

	private Composite selectorComposite;

	private Composite main;

	private TreeViewer viewer;

	private List<Instance> instances = new ArrayList<Instance>();

	private final List<Control> selectButtons = new ArrayList<Control>();

	private int selectedIndex = 0;

	private TreeEditor metaEditor;

	private static ALogger _log = ALoggerFactory.getLogger(InstanceExplorer.class);

	private final SelectionListener selectListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			int index = 0;
			for (Control button : selectButtons) {
				if (button instanceof Button && ((Button) button).getSelection()) {
					selectedIndex = index;
					updateSelection();
					return;
				}

				index++;
			}
		}

	};

	/**
	 * @see InstanceViewer#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());

		// selector composite
		selectorComposite = new Composite(main, SWT.NONE);
		selectorComposite.setLayoutData(GridDataFactory.swtDefaults().create());
		selectorComposite.setLayout(GridLayoutFactory.fillDefaults().create());

		// viewer composite
		Composite viewerComposite = new Composite(main, SWT.NONE);
		viewerComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		TreeColumnLayout layout = new TreeColumnLayout();
		viewerComposite.setLayout(layout);
		viewer = new TreeViewer(viewerComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getTree().setLinesVisible(true);

		// set content provider
		viewer.setContentProvider(new InstanceContentProvider());
		viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		// TODO set label provider?

		// add definition columns
		TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText("Definition");
		column.setLabelProvider(new TreeColumnViewerLabelProvider(new PairLabelProvider(true,
				new DefinitionMetaLabelProvider(false, true))));
		layout.setColumnData(column.getColumn(), new ColumnWeightData(1));

		// add value column
		column = new TreeViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText("Value");
		column.setLabelProvider(new InstanceValueLabelProvider());
		// new PairLabelProvider(false, new LabelProvider())));

		ColumnViewerToolTipSupport.enableFor(viewer);

		layout.setColumnData(column.getColumn(), new ColumnWeightData(1));

		// /Additional functionality for meta data
		metaEditor = new TreeEditor(viewer.getTree());
		metaEditor.horizontalAlignment = SWT.LEFT;
		metaEditor.verticalAlignment = SWT.TOP;

		viewer.getTree().addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				final ViewerCell cell = viewer.getCell(new Point(e.x, e.y));

				// Selected cell changed?
				if (cell == null || metaEditor.getItem() != cell.getItem()
						|| metaEditor.getColumn() != cell.getColumnIndex()) {
					// Clean up any previous editor control
					Control oldmetaEditor = metaEditor.getEditor();
					if (oldmetaEditor != null) {
						oldmetaEditor.dispose();
						metaEditor.setEditor(null, null, 0);
					}
				}

				// No selected cell or selected cell didn't change.
				if (cell == null
						|| cell.getColumnIndex() == 0
						|| (metaEditor.getItem() == cell.getItem() && metaEditor.getColumn() == cell
								.getColumnIndex())) {
					return;
				}

				// Initiate the extensionpoint
				MetadataActionExtension mae = MetadataActionExtension.getInstance();

				// get all defined actions
				Pair<?, ?> cellContent = (Pair<?, ?>) cell.getElement();
				final Object key = cellContent.getFirst();
				final Object value = cellContent.getSecond();

				final List<MetadataActionFactory> actionSources = mae
						.getMetadataActions(cellContent.getFirst().toString());
				if (actionSources == null || actionSources.isEmpty()) {
					return;
				}

				// Toolbar used to view and trigger the different possible
				// actions
				ToolBar tooli = new ToolBar(viewer.getTree(), SWT.NONE);

				for (final MetadataActionFactory source : actionSources) {
					ToolItem actionItem = new ToolItem(tooli, SWT.PUSH);

					// get the Icon of the action
					URL iconURL = source.getIconURL();
					Image image = ImageDescriptor.createFromURL(iconURL).createImage();
					actionItem.setImage(image);
					actionItem.setToolTipText(source.getDisplayName());

					actionItem.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {

							try {
								source.createExtensionObject().execute(key, value);
							} catch (Exception e1) {
								_log.userError("error creating metadata action", e1);
							}
						}
					});
				}
				metaEditor.setEditor(tooli, (TreeItem) cell.getItem(), cell.getColumnIndex());
				tooli.pack();
			}
		});

		update();
	}

	/**
	 * Update the controls.
	 */
	private void update() {
		// dispose old selector buttons
		for (Control button : selectButtons) {
			button.dispose();
		}
		selectButtons.clear();

		selectorComposite.setLayout(GridLayoutFactory.swtDefaults()
				.numColumns((instances.isEmpty()) ? (1) : (instances.size())).margins(3, 0)
				.create());
		// create new buttons for each instance
		for (int index = 0; index < instances.size(); index++) {
			Button button = new Button(selectorComposite, SWT.RADIO);
			button.setText("#" + (index + 1));
			button.setSelection(selectedIndex == index);
			button.addSelectionListener(selectListener);

			selectButtons.add(button);
		}

		if (selectButtons.isEmpty()) {
			Label none = new Label(selectorComposite, SWT.NONE);
			none.setText("No matching instances available");
			selectButtons.add(none);
		}

		if (selectedIndex >= selectButtons.size()) {
			selectedIndex = 0;
			if (!selectButtons.isEmpty()) {
				Control c1 = selectButtons.get(0);
				if (c1 instanceof Button) {
					((Button) c1).setSelection(true);
				}
			}
		}

		// both layout steps needed
		selectorComposite.layout();
		selectorComposite.getParent().layout();

		updateSelection();
	}

	/**
	 * Update the viewer according to the current selection.
	 */
	private void updateSelection() {
		if (selectedIndex < instances.size()) {
			Instance instance = instances.get(selectedIndex);
			viewer.setInput(instance);
			selectionProvider.updateSelection(Collections.singleton(instance));
		}
		else {
			viewer.setInput(null);
			selectionProvider.updateSelection(null);
		}
	}

	/**
	 * @see InstanceViewer#setInput(TypeDefinition, Iterable)
	 */
	@Override
	public void setInput(TypeDefinition type, Iterable<Instance> instances) {
		List<Instance> instanceList = new ArrayList<Instance>();
		if (instances != null) {
			Iterables.addAll(instanceList, instances);
		}
		this.instances = instanceList;
		update();
	}

	/**
	 * @see InstanceViewer#getViewer()
	 */
	@Override
	public Viewer getViewer() {
		return viewer;
	}

	/**
	 * @see InstanceViewer#getControl()
	 */
	@Override
	public Control getControl() {
		return main;
	}

	/**
	 * @see InstanceViewer#getInstanceSelectionProvider()
	 */
	@Override
	public ISelectionProvider getInstanceSelectionProvider() {
		return selectionProvider;
	}

}
