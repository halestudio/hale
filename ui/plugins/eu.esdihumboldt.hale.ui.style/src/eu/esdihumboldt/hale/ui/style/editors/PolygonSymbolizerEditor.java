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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.StyleBuilder;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Editor for {@link PolygonSymbolizer}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class PolygonSymbolizerEditor implements Editor<PolygonSymbolizer> {

	private final StyleBuilder styleBuilder = new StyleBuilder();

	private StrokeEditor strokeEditor;

	private FillEditor fillEditor;

	private final Composite page;

	/**
	 * Creates a {@link PolygonSymbolizer} editor
	 * 
	 * @param parent the parent composite
	 * @param poly the initial {@link PolygonSymbolizer}
	 */
	public PolygonSymbolizerEditor(Composite parent, PolygonSymbolizer poly) {
		super();

		page = new Composite(parent, SWT.NONE);

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		page.setLayout(layout);

		// stroke
		Label strokeLabel = new Label(page, SWT.NONE);
		strokeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		strokeLabel.setText(Messages.PolygonSymbolizerEditor_StrokeLabel);
		strokeEditor = new StrokeEditor(page, poly.getStroke());

		// fill
		Label fillLabel = new Label(page, SWT.NONE);
		fillLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		fillLabel.setText(Messages.PolygonSymbolizerEditor_FillLabel);
		fillEditor = new FillEditor(page, poly.getFill());
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public PolygonSymbolizer getValue() {
		return styleBuilder.createPolygonSymbolizer(strokeEditor.getValue(), fillEditor.getValue());
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return strokeEditor.isChanged() || fillEditor.isChanged();
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(PolygonSymbolizer poly) {
		strokeEditor.setValue(poly.getStroke());
		fillEditor.setValue(poly.getFill());
	}

}
