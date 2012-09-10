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
package eu.esdihumboldt.hale.ui.style.dialog;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;

import eu.esdihumboldt.hale.ui.style.editors.PolygonSymbolizerEditor;
import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Line style editor
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SimplePolygonStylePage extends FeatureStylePage {

	private final StyleBuilder styleBuilder = new StyleBuilder();

	private PolygonSymbolizerEditor polyEditor;

	/**
	 * @param parent the parent dialog
	 */
	public SimplePolygonStylePage(FeatureStyleDialog parent) {
		super(parent, Messages.SimplePolygonStylePage_SuperTitle);
	}

	/**
	 * @see FeatureStylePage#getStyle(boolean)
	 */
	@Override
	public Style getStyle(boolean force) throws Exception {
		if (polyEditor != null) {
			if (force || polyEditor.isChanged()) {
				return styleBuilder.createStyle(polyEditor.getValue());
			}
			else {
				// nothing has changed
				return null;
			}
		}

		return null;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// create new controls
		Composite page = new Composite(parent, SWT.NONE);

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		page.setLayout(layout);

		Style style = getParent().getStyle();
		PolygonSymbolizer poly = null;
		try {
			Symbolizer[] symbolizers = SLD.symbolizers(style);
			for (Symbolizer symbol : symbolizers) {
				if (symbol instanceof PolygonSymbolizer) {
					poly = (PolygonSymbolizer) symbol;
					break;
				}
			}
		} catch (Exception e) {
			// ignore
		}

		if (poly == null) {
			poly = styleBuilder.createPolygonSymbolizer();
		}

		polyEditor = new PolygonSymbolizerEditor(page, poly);

		setControl(page);
	}

}
