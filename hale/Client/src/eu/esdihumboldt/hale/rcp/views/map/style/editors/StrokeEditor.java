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
package eu.esdihumboldt.hale.rcp.views.map.style.editors;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class StrokeEditor implements Editor<Stroke> {
	
	private final StyleBuilder styleBuilder = new StyleBuilder();
	
	private Composite page;
	
	private final Editor<RGB> color;
	
	private final Spinner width;
	
	/**
	 * Creates a {@link Stroke} editor
	 *  
	 * @param parent the parent composite
	 * @param stroke the initial stroke
	 */
	public StrokeEditor(Composite parent, Stroke stroke) {
		super();
		
		page = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		page.setLayout(layout);
		
		GridData caption = new GridData(SWT.END, SWT.CENTER, false, false);
		GridData editor = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		
		// color editor
		Label label = new Label(page, SWT.NONE);
		label.setLayoutData(caption);
		label.setText("Color");
		
		Color strokeColor = SLD.color(stroke);
		color = new ColorEditor(page, new RGB(strokeColor.getRed(), strokeColor.getGreen(), strokeColor.getBlue()));
		color.getControl().setLayoutData(editor);
		
		// width spinner
		caption = new GridData(SWT.END, SWT.CENTER, false, false);
		editor = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		
		label = new Label(page, SWT.NONE);
		label.setLayoutData(caption);
		label.setText("Width");
		
		width = new Spinner(page, SWT.BORDER);
		width.setLayoutData(editor);
		width.setDigits(2);
		width.setMinimum(1);
		width.setMaximum(Integer.MAX_VALUE);
		width.setIncrement(100);
		width.setPageIncrement(100);
		try {
			width.setSelection(Math.round(Float.parseFloat(stroke.getWidth().toString()) * 100.0f));
		} catch (Exception e) {
			width.setSelection(100);
		}
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public Stroke getValue() {
		return styleBuilder.createStroke(getColor(), getWidth(), getOpacity());
	}

	/**
	 * Get the {@link Stroke} opacity
	 * 
	 * @return the {@link Stroke} opacity
	 */
	private float getOpacity() {
		return 1;
	}

	/**
	 * Get the {@link Stroke} width
	 * 
	 * @return the {@link Stroke} width
	 */
	private double getWidth() {
		return ((double) width.getSelection()) / 100;
	}

	/**
	 * Get the {@link Stroke} color
	 * 
	 * @return the {@link Stroke} color
	 */
	public Color getColor() {
		RGB rgb = color.getValue();
		return new Color(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(Stroke stroke) {
		Color strokeColor = SLD.color(stroke);
		color.setValue(new RGB(strokeColor.getRed(), strokeColor.getGreen(), strokeColor.getBlue()));
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

}
