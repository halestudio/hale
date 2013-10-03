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

package eu.esdihumboldt.hale.ui.templates.webtemplates;

import java.awt.Desktop;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Dialog for selecting a web template to load.
 * 
 * @author Simon Templer
 */
public class WebTemplatesDialog extends AbstractViewerSelectionDialog<WebTemplate, TreeViewer> {

	private static final ALogger log = ALoggerFactory.getLogger(WebTemplatesDialog.class);
	private TreeColumnLayout layout;

	/**
	 * Create a dialog to select a web template to load.
	 * 
	 * @param parentShell the parent shell
	 */
	public WebTemplatesDialog(Shell parentShell) {
		super(parentShell, "Select a project template to load", null, false, 600, 400);
	}

	@Override
	protected TreeViewer createViewer(Composite parent) {
		layout = new TreeColumnLayout();
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, patternFilter, true) {

			@Override
			protected Control createTreeControl(Composite parent, int style) {
				Control c = super.createTreeControl(parent, style);
				c.setLayoutData(null);
				c.getParent().setLayout(layout);
				return c;
			}
		};
		return tree.getViewer();
	}

	@Override
	protected void setupViewer(final TreeViewer viewer, WebTemplate initialSelection) {
		// this label provider is used for filtering (I guess)
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof WebTemplate) {
					return ((WebTemplate) element).getName();
				}
				return super.getText(element);
			}

		});

		viewer.getTree().setHeaderVisible(true);
//		viewer.getTree().setLinesVisible(true);

		// main column
		TreeViewerColumn mainColumn = new TreeViewerColumn(viewer, SWT.NONE);
		mainColumn.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();

				StyledString text = new StyledString();
				if (element instanceof WebTemplate) {
					WebTemplate template = (WebTemplate) element;

					text.append(template.getName());
				}
				else {
					text.append(element.toString());
				}

				cell.setText(text.getString());
				cell.setStyleRanges(text.getStyleRanges());

				super.update(cell);
			}
		});
		mainColumn.getColumn().setText("Template");
		layout.setColumnData(mainColumn.getColumn(), new ColumnWeightData(7));

		// link column
		if (Desktop.isDesktopSupported()) {
			TreeViewerColumn linkColumn = new TreeViewerColumn(viewer, SWT.CENTER);
			linkColumn.setLabelProvider(new LinkLabels());

			// listener that reacts on link cell clicks
			viewer.getTree().addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDown(MouseEvent e) {
					if (e.button == 1) {
						ViewerCell cell = viewer.getCell(new Point(e.x, e.y));
						if (cell != null && cell.getColumnIndex() == 1
								&& cell.getTextBounds().contains(e.x, e.y)) {
							Object element = cell.getElement();
							if (element instanceof WebTemplate) {
								try {
									Desktop.getDesktop().browse(((WebTemplate) element).getSite());
								} catch (IOException e1) {
									log.error("Could not launch browser", e1);
								}
							}
						}
					}
				}
			});

			layout.setColumnData(linkColumn.getColumn(), new ColumnWeightData(1));
		}

		viewer.setContentProvider(new WebTemplatesContentProvider());
		try {
			List<WebTemplate> templates = WebTemplateLoader.load();
			viewer.setInput(templates);
		} catch (Exception e) {
			log.error("Failed to connect to template server", e);
			viewer.setInput(Collections.singletonList("Could not connect to template server"));
		}
	}

	@Override
	protected WebTemplate getObjectFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof WebTemplate) {
				return (WebTemplate) element;
			}
		}

		return null;
	}

}
