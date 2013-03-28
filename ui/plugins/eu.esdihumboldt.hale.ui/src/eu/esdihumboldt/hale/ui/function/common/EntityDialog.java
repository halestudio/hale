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
package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;

/**
 * Dialog for selecting an {@link EntityDefinition}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class EntityDialog extends
		AbstractViewerSelectionDialog<EntityDefinition, TreeViewer> {

	/**
	 * The schema space
	 */
	protected final SchemaSpaceID ssid;

	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param ssid the schema space
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *            possible), may be <code>null</code>
	 */
	public EntityDialog(Shell parentShell, SchemaSpaceID ssid, String title,
			EntityDefinition initialSelection) {
		super(parentShell, title, initialSelection);

		this.ssid = ssid;
	}

	/**
	 * @see AbstractViewerSelectionDialog#createViewer(Composite)
	 */
	@Override
	protected TreeViewer createViewer(Composite parent) {
		// create viewer
		SchemaPatternFilter patternFilter = new SchemaPatternFilter() {

			@Override
			protected boolean matches(Viewer viewer, Object element) {
				boolean superMatches = super.matches(viewer, element);
				if (!superMatches)
					return false;
				return acceptObject(viewer, getFilters(), ((TreePath) element).getLastSegment());
			}
		};
		patternFilter.setUseEarlyReturnIfMatcherIsNull(false);
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new TreePathFilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
		tree.getViewer().setComparator(new DefinitionComparator());
		return tree.getViewer();
	}
}
