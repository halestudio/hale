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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.schema.presets.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset;
import eu.esdihumboldt.hale.ui.util.selector.AbstractUniformSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Selector for {@link SchemaPreset}s.
 * 
 * @author Simon Templer
 */
public class SchemaPresetSelector extends AbstractUniformSelector<SchemaPreset> {

	/**
	 * Create a selector for {@link SchemaPreset}s.
	 * 
	 * @param parent the parent composite
	 */
	public SchemaPresetSelector(Composite parent) {
		super(parent, new SchemaPresetLabelProvider(), null);
	}

	@Override
	protected AbstractViewerSelectionDialog<SchemaPreset, ?> createSelectionDialog(Shell parentShell) {
		return new SchemaPresetSelectionDialog(parentShell, getSelectedObject());
	}

}
