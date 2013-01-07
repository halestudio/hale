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

package eu.esdihumboldt.hale.ui.views.data.internal;

import java.net.URL;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataActionExtension;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataActionFactory;
import eu.esdihumboldt.util.Pair;

/**
 * Provides metadata actions for {@link TreeViewer}s
 * 
 * @author Sebastian Reinhardt
 */
public abstract class MetadataActionProvider {

	private static ALogger _log = ALoggerFactory.getLogger(MetadataActionProvider.class);

	private final TreeViewer treeViewer;

	/**
	 * Standard Constructor
	 * 
	 * @param treeViewer the TreeViewer to which the action will be applied
	 */
	public MetadataActionProvider(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/**
	 * Retrieves the key and value of a certain meta datum from a
	 * {@link ViewerCell}
	 * 
	 * @param cell the ViewerCell
	 * @return a Pair containing the key and the value of a meta datum
	 */
	protected abstract Pair<Object, Object> retrieveMetadata(ViewerCell cell);

	/**
	 * Adds the action to the certain TreeViewer cell
	 */
	public void setup() {

		final TreeEditor metaEditor = new TreeEditor(treeViewer.getTree());
		metaEditor.horizontalAlignment = SWT.LEFT;
		metaEditor.verticalAlignment = SWT.TOP;

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

				// Initiate the extension-point
				MetadataActionExtension mae = MetadataActionExtension.getInstance();
				final Pair<Object, Object> data = retrieveMetadata(cell);
				if (data == null) {
					return;
				}

				// get all defined actions
				final List<MetadataActionFactory> actionSources = mae.getMetadataActions(data
						.getFirst().toString());

				if (actionSources == null || actionSources.isEmpty()) {
					return;
				}
				// Tool-bar used to view and trigger the different possible
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
								source.createExtensionObject().execute(data.getFirst(),
										data.getSecond());
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

	}

}
