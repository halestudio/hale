/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataActionExtension;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataActionFactory;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.views.data.InstanceViewer;
import eu.esdihumboldt.hale.ui.views.data.internal.SimpleInstanceSelectionProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Tree viewer for {@link Instance}s of a common type, based on the
 * corresponding {@link TypeDefinition}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefinitionInstanceTreeViewer implements InstanceViewer {

	private static ALogger _log = ALoggerFactory.getLogger(DefinitionInstanceTreeViewer.class);

	private TreeViewer treeViewer;

	private Composite main;

	private TreeEditor editor;

	private TreeEditor metaEditor;

	private final SimpleInstanceSelectionProvider selectionProvider = new SimpleInstanceSelectionProvider();

	private final Map<Integer, DefinitionInstanceLabelProvider> labelProviders = new HashMap<Integer, DefinitionInstanceLabelProvider>();

	/**
	 * @see InstanceViewer#createControls(Composite)
	 */
	@Override
	public void createControls(final Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new TreeColumnLayout());

		treeViewer = new TreeViewer(main, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);

		treeViewer.setContentProvider(new TypeMetaPairContentProvider(treeViewer));

		treeViewer.setLabelProvider(new DefinitionMetaCompareLabelProvider());

		// Add an editor for selecting specific paths.
		editor = new TreeEditor(treeViewer.getTree());
		editor.horizontalAlignment = SWT.RIGHT;
		editor.minimumWidth = 40;
		metaEditor = new TreeEditor(treeViewer.getTree());
		metaEditor.horizontalAlignment = SWT.LEFT;
		metaEditor.verticalAlignment = SWT.TOP;
		// metaEditor.grabHorizontal = true;
		// metaEditor.minimumHeight = 30;

		treeViewer.getTree().addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				final ViewerCell cell = treeViewer.getCell(new Point(e.x, e.y));

				// Selected cell changed?
				if (cell == null || editor.getItem() != cell.getItem()
						|| editor.getColumn() != cell.getColumnIndex()) {
					// Clean up any previous editor control
					Control oldEditor = editor.getEditor();
					if (oldEditor != null) {
						oldEditor.dispose();
						editor.setEditor(null, null, 0);
					}
				}

				// No selected cell or selected cell didn't change.
				if (cell == null
						|| cell.getColumnIndex() == 0

						|| (editor.getItem() == cell.getItem() && editor.getColumn() == cell
								.getColumnIndex()))

					return;

				// Quote the format first. Pattern.quote does the same, except,
				// that it checks the input for \Es.
				// Since we know that there will be no \Es in this case
				// do it ourselves to be safe from changes to Pattern.quote.

				String pattern = "\\Q" + DefinitionInstanceLabelProvider.MULTIPLE_VALUE_FORMAT
						+ "\\E$";
				pattern = pattern.replace("{0}", "\\E(\\d+)\\Q").replace("{1}", "\\E(\\d+)\\Q");

				Matcher m = Pattern.compile(pattern).matcher(cell.getText());
				if (!m.find())
					return;

				int current = Integer.parseInt(m.group(1));
				int total = Integer.parseInt(m.group(2));

				// Create the selection control.
				ComboViewer combo = new ComboViewer(treeViewer.getTree());
				Integer[] values = new Integer[total];
				for (int i = 1; i <= total; i++)
					values[i - 1] = i;
				combo.setContentProvider(ArrayContentProvider.getInstance());
				combo.setInput(values);
				combo.setSelection(new StructuredSelection(current));
				combo.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (event.getSelection() instanceof IStructuredSelection) {
							// Update label provider and refresh viewer.

							labelProviders.get(cell.getColumnIndex()).selectPath(
									cell.getViewerRow().getTreePath(),
									(Integer) (((IStructuredSelection) event.getSelection())
											.getFirstElement()));

							treeViewer.refresh(cell.getElement(), true);
						}
					}
				});
				editor.setEditor(combo.getControl(), (TreeItem) cell.getItem(),
						cell.getColumnIndex());
			}
		});

		// /Additional functionality for meta data
		treeViewer.getTree().addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				final ViewerCell cell = treeViewer.getCell(new Point(e.x, e.y));

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
				final List<MetadataActionFactory> actionSources = mae.getMetadataActions(cell
						.getElement().toString());
				if (actionSources == null || actionSources.isEmpty()) {
					return;
				}
				final String key = cell.getViewerRow().getCell(0).getText();
				final String value = cell.getText();

				// Toolbar used to view and trigger the different possible
				// actions
				ToolBar tooli = new ToolBar(treeViewer.getTree(), SWT.NONE);

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

		treeViewer.setComparator(new DefinitionComparator());

		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		ColumnViewerToolTipSupport.enableFor(treeViewer);

		setInput(null, null);
	}

	/**
	 * @see InstanceViewer#setInput(TypeDefinition, Iterable)
	 */
	@Override
	public void setInput(TypeDefinition type, Iterable<Instance> instances) {
		// remove old columns
		TreeColumn[] columns = treeViewer.getTree().getColumns();
		if (columns != null) {
			for (TreeColumn column : columns) {
				column.dispose();
			}
			labelProviders.clear();
		}

		// create row defs for metadata
//			if (features != null) {
//				boolean displayLineage = false;
//				int lineageLength = 0;
//				int featuresSize = 0;
//				for (Feature f : features) {
//					featuresSize++;
//					Lineage l = (Lineage) f.getUserData().get("METADATA_LINEAGE"); //$NON-NLS-1$
//					if (l != null && l.getProcessSteps().size() > 0) {
//						displayLineage = true;
//						if (lineageLength < l.getProcessSteps().size()) {
//							lineageLength = l.getProcessSteps().size();
//						}
//					}
//				}
//				
//				if (displayLineage) {
//					Object[][] processStepsText = new Object[lineageLength][featuresSize + 1];
//					int featureIndex = 0;
//					for (Feature f : features) {
//						Lineage l = (Lineage) f.getUserData().get("METADATA_LINEAGE"); //$NON-NLS-1$
//						if (l != null && l.getProcessSteps().size() > 0) {
//							int psIndex = 0;
//							for (ProcessStep ps : l.getProcessSteps()) {
//								processStepsText[psIndex][featureIndex + 1] = ps.getDescription().toString();
//								psIndex++;
//							}
//						}
//						featureIndex++;
//					}
//					
//					DefaultTreeNode lineage = new DefaultTreeNode(Messages.DefinitionFeatureTreeViewer_5); //$NON-NLS-1$
//					metadata.addChild(lineage);
//					for (int i = 0; i < lineageLength; i++) {
//						processStepsText[i][0] = Messages.DefinitionFeatureTreeViewer_6 + (i + 1); //$NON-NLS-1$
//						DefaultTreeNode processStep = new DefaultTreeNode(processStepsText[i]);
//						lineage.addChild(processStep);
//					}
//				}
		// set input
		if (type != null) {
			// pass metadatas to the treeviewer, if instances contain metadatas
			Set<String> completeMetaNames = new HashSet<String>();
			for (Instance inst : instances) {
				for (String name : inst.getMetaDataNames()) {
					completeMetaNames.add(name);
				}
			}

			Pair<TypeDefinition, Set<String>> pair = new Pair<TypeDefinition, Set<String>>(type,
					completeMetaNames);

			treeViewer.setInput(pair);
		}
		else
			treeViewer.setInput(Collections.emptySet());

		Layout layout = treeViewer.getTree().getParent().getLayout();

		// add type column
		if (type != null) {
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
			column.getColumn().setText(type.getDisplayName());
			column.setLabelProvider(new TreeColumnViewerLabelProvider(
					new DefinitionMetaCompareLabelProvider()));
			if (layout instanceof TreeColumnLayout) {
				((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(
						1));
			}
		}

		// add columns for features
		int index = 1;
		if (instances != null) {
//			// sort features
//			List<Feature> sortedFeatures = new ArrayList<Feature>();
//			for (Feature f : features) {
//				sortedFeatures.add(f);
//			}
//			Collections.sort(sortedFeatures, new Comparator<Feature>() {
//
//				@Override
//				public int compare(Feature o1, Feature o2) {
//					FeatureId id1 = FeatureBuilder.getSourceID(o1);
//					if (id1 == null) {
//						id1 = o1.getIdentifier();
//					}
//					
//					FeatureId id2 = FeatureBuilder.getSourceID(o2);
//					if (id2 == null) {
//						id2 = o2.getIdentifier();
//					}
//					
//					return id1.getID().compareTo(id2.getID());
//				}
//				
//			});

			for (Instance instance : instances) { // sortedFeatures) {
				final TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
//				FeatureId id = FeatureBuilder.getSourceID(feature);
//				if (id == null) {
//					id = feature.getIdentifier();
//				}
//				column.getColumn().setText(id.toString());
				column.getColumn().setText(String.valueOf(index)); // XXX
																	// identifier?
				DefinitionInstanceLabelProvider labelProvider = new DefinitionInstanceLabelProvider(
						instance);
				labelProviders.put(index, labelProvider);
				column.setLabelProvider(labelProvider);
				if (layout instanceof TreeColumnLayout) {
					((TreeColumnLayout) layout).setColumnData(column.getColumn(),
							new ColumnWeightData(1));
				}

				// add tool tip
//				new ColumnBrowserTip(treeViewer, 400, 300, true, index, null);

				index++;
			}
		}

		treeViewer.refresh();
		treeViewer.getTree().getParent().layout(true, true);

		selectionProvider.updateSelection(instances);

		// auto-expand attributes/metadata
		treeViewer.expandToLevel(2);
	}

	/**
	 * @see InstanceViewer#getViewer()
	 */
	@Override
	public TreeViewer getViewer() {
		return treeViewer;
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
