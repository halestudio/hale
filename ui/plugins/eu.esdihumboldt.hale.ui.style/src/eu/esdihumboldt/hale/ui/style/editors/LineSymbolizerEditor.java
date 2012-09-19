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
package eu.esdihumboldt.hale.ui.style.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.StyleBuilder;

/**
 * Editor for {@link LineSymbolizer}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class LineSymbolizerEditor implements Editor<LineSymbolizer> {

	private final StyleBuilder styleBuilder = new StyleBuilder();

	private final StrokeEditor strokeEditor;

	/**
	 * Creates a {@link LineSymbolizer} editor
	 * 
	 * @param parent the parent composite
	 * @param line the initial {@link LineSymbolizer}
	 */
	public LineSymbolizerEditor(Composite parent, LineSymbolizer line) {
		super();

		strokeEditor = new StrokeEditor(parent, line.getStroke());
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return strokeEditor.getControl();
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public LineSymbolizer getValue() {
		return styleBuilder.createLineSymbolizer(strokeEditor.getValue());
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return strokeEditor.isChanged();
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(LineSymbolizer line) {
		strokeEditor.setValue(line.getStroke());
	}

}
