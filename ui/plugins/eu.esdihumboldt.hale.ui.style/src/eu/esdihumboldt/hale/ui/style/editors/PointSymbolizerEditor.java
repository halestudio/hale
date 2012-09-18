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
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;

/**
 * Editor for {@link PointSymbolizer}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class PointSymbolizerEditor implements Editor<PointSymbolizer> {

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private final Editor<Mark> markEditor;

	/**
	 * Creates a {@link PointSymbolizer} editor
	 * 
	 * @param parent the parent composite
	 * @param point the initial {@link PointSymbolizer}
	 */
	public PointSymbolizerEditor(Composite parent, PointSymbolizer point) {
		super();

		markEditor = new MarkEditor(parent, SLD.mark(point));
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return markEditor.getControl();
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public PointSymbolizer getValue() throws Exception {
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(null,
				markEditor.getValue(), null));
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return markEditor.isChanged();
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(PointSymbolizer point) {
		markEditor.setValue(SLD.mark(point));
	}

}
