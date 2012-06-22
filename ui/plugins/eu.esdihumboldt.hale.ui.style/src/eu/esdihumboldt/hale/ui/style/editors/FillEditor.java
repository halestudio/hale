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

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.styling.Fill;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Editor for {@link Fill}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FillEditor implements Editor<Fill> {
	
	private final StyleBuilder styleBuilder = new StyleBuilder();
	
	private boolean changed = false;
	
	private Composite page;
	
	private final Editor<RGB> color;
	
	private final Spinner opacity;
	
	private final SelectionListener changeListener = new SelectionAdapter() {

		/**
		 * @see SelectionAdapter#widgetSelected(SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			changed = true;
		}
	};
	
	/**
	 * Creates a {@link Fill} editor
	 *  
	 * @param parent the parent composite
	 * @param fill the initial fill
	 */
	public FillEditor(Composite parent, Fill fill) {
		super();
		
		page = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		page.setLayout(layout);
		
		GridData caption = new GridData(SWT.END, SWT.CENTER, false, false);
		GridData editor = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		
		// color editor
		Label label = new Label(page, SWT.NONE);
		label.setLayoutData(caption);
		label.setText(Messages.FillEditor_ColorLabelText);
		
		Color fillColor = SLD.color(fill);
		color = new ColorEditor(page, new RGB(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue()));
		color.getControl().setLayoutData(editor);
		
		// opacity spinner
		caption = new GridData(SWT.END, SWT.CENTER, false, false);
		editor = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		
		label = new Label(page, SWT.NONE);
		label.setLayoutData(caption);
		label.setText(Messages.FillEditor_OpacityText);
		
		Composite opc = new Composite(page, SWT.NONE);
		opc.setLayoutData(editor);
		opc.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		opacity = new Spinner(opc, SWT.BORDER);
		opacity.setMinimum(0);
		opacity.setMaximum(100);
		opacity.setIncrement(1);
		opacity.setPageIncrement(10);
		try {
			opacity.setSelection(Math.round(Float.parseFloat(fill.getOpacity().toString()) * 100.0f));
		} catch (Exception e) {
			opacity.setSelection(100);
		}
		opacity.addSelectionListener(changeListener);
		
		label = new Label(opc, SWT.NONE);
		label.setText("%"); //$NON-NLS-1$
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public Fill getValue() {
		return styleBuilder.createFill(getColor(), getOpacity());
	}

	/**
	 * Get the {@link Stroke} opacity
	 * 
	 * @return the {@link Stroke} opacity
	 */
	private float getOpacity() {
		return (opacity.getSelection()) / 100.0f;
	}

	/**
	 * Get the {@link Stroke} color
	 * 
	 * @return the {@link Stroke} color
	 */
	public Color getColor() {
		RGB rgb;
		try {
			rgb = color.getValue();
		} catch (Exception e) {
			throw new IllegalStateException("Could not get Color from Editor.", e);
		}
		return new Color(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(Fill fill) {
		Color fillColor = SLD.color(fill);
		color.setValue(new RGB(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue()));
		
		try {
			opacity.setSelection(Math.round(Float.parseFloat(fill.getOpacity().toString()) * 100.0f));
		} catch (Exception e) {
			opacity.setSelection(100);
		}
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed || color.isChanged();
	}

}
