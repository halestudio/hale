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

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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

	/**
	 * Create a dialog to select a web template to load.
	 * 
	 * @param parentShell the parent shell
	 */
	public WebTemplatesDialog(Shell parentShell) {
		super(parentShell, "Select a project template to load", null, false);
	}

	@Override
	protected TreeViewer createViewer(Composite parent) {
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, patternFilter, true);
		return tree.getViewer();
	}

	@Override
	protected void setupViewer(TreeViewer viewer, WebTemplate initialSelection) {
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof WebTemplate) {
					return ((WebTemplate) element).getName();
				}
				return super.getText(element);
			}

		});
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
