/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
