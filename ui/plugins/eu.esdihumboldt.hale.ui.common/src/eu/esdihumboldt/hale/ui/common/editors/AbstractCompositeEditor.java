/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.common.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Base class for editors that use a composite as a main control.
 * 
 * @param <T> the type of object to edit
 * @author Simon Templer
 */
public abstract class AbstractCompositeEditor<T> extends AbstractEditor<T> {

	private final Composite mainControl;

	/**
	 * Create a new editor.
	 * 
	 * @param parent the parent composite
	 */
	public AbstractCompositeEditor(Composite parent) {
		super();
		mainControl = new Composite(parent, SWT.NONE);
		createControls(mainControl);
	}

	/**
	 * Create the editor controls.
	 * 
	 * @param page the main editor composite, you may set a layout
	 */
	protected abstract void createControls(Composite page);

	@Override
	public Control getControl() {
		return mainControl;
	}

}
