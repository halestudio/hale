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
