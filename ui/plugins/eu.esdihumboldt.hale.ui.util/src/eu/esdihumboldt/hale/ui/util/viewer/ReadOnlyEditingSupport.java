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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides text editors to allow copying the string values of the cells
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReadOnlyEditingSupport extends EditingSupport {

	private final ILabelProvider labelProvider;

	/**
	 * @param viewer the viewer
	 * @param labelProvider the label provider
	 */
	public ReadOnlyEditingSupport(ColumnViewer viewer, ILabelProvider labelProvider) {
		super(viewer);

		this.labelProvider = labelProvider;
	}

	/**
	 * @see EditingSupport#canEdit(Object)
	 */
	@Override
	protected boolean canEdit(Object element) {
		String value = labelProvider.getText(element);
		return value != null && !value.isEmpty();
	}

	/**
	 * @see EditingSupport#getCellEditor(Object)
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		TextCellEditor editor = new TextCellEditor((Composite) getViewer().getControl(),
				SWT.READ_ONLY);

		// editor.set

		return editor;
	}

	/**
	 * @see EditingSupport#getValue(Object)
	 */
	@Override
	protected Object getValue(Object element) {
		return labelProvider.getText(element);
	}

	/**
	 * @see EditingSupport#setValue(Object, Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// do nothing, we're read only here
	}

}
