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
package eu.esdihumboldt.hale.rcp.views.map.style;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;

import eu.esdihumboldt.hale.rcp.views.map.style.editors.FillEditor;
import eu.esdihumboldt.hale.rcp.views.map.style.editors.StrokeEditor;

/**
 * Line style editor
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimplePolygonStylePage extends FeatureStylePage {
	
	private final StyleBuilder styleBuilder = new StyleBuilder();
	
	private StrokeEditor strokeEditor;
	
	private FillEditor fillEditor;
	
	/**
	 * @param parent the parent dialog
	 */
	public SimplePolygonStylePage(FeatureStyleDialog parent) {
		super(parent, "Polygon");
	}

	/**
	 * @see FeatureStylePage#getStyle(boolean)
	 */
	@Override
	public Style getStyle(boolean force) throws Exception {
		if (strokeEditor != null && fillEditor != null) {
			if (force || strokeEditor.isChanged() || fillEditor.isChanged()) {
				return styleBuilder.createStyle(styleBuilder
						.createPolygonSymbolizer(strokeEditor.getValue(),
								fillEditor.getValue()));
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
		Stroke stroke = null;
		Fill fill = null;
		try {
			Symbolizer[] symbolizers = SLD.symbolizers(style);
			for (Symbolizer symbol : symbolizers) {
				if (symbol instanceof PolygonSymbolizer) {
					stroke = SLD.stroke((PolygonSymbolizer) symbol);
					fill = SLD.fill((PolygonSymbolizer) symbol);
					break;
				}
			}
		}
		catch (Exception e) {
			// ignore
		}
		
		if (stroke == null) {
			stroke = styleBuilder.createStroke();
		}
		if (fill == null) {
			fill = styleBuilder.createFill();
		}
		
		Label strokeLabel = new Label(page, SWT.NONE);
		strokeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		strokeLabel.setText("Stroke");
		strokeEditor = new StrokeEditor(page, stroke);
		
		Label fillLabel = new Label(page, SWT.NONE);
		fillLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		fillLabel.setText("Fill");
		fillEditor = new FillEditor(page, fill);
		
		setControl(page);
	}

}
