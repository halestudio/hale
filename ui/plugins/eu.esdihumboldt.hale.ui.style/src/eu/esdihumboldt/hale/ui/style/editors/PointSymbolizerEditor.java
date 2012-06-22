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
		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(
				null, markEditor.getValue(), null));
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
