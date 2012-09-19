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
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Editor for {@link Stroke}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StrokeEditor implements Editor<Stroke> {

	private final StyleBuilder styleBuilder = new StyleBuilder();

	private boolean changed = false;

	private Composite page;

	private final Editor<RGB> color;

	private final Spinner width;

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
		label.setText(Messages.StrokeEditor_ColorLabel);

		Color strokeColor = SLD.color(stroke);
		color = new ColorEditor(page, new RGB(strokeColor.getRed(), strokeColor.getGreen(),
				strokeColor.getBlue()));
		color.getControl().setLayoutData(editor);

		// width spinner
		caption = new GridData(SWT.END, SWT.CENTER, false, false);
		editor = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label = new Label(page, SWT.NONE);
		label.setLayoutData(caption);
		label.setText(Messages.StrokeEditor_WidthLabel);

		width = new Spinner(page, SWT.BORDER);
		width.setLayoutData(editor);
		width.setDigits(2);
		width.setMinimum(0);
		width.setMaximum(Integer.MAX_VALUE);
		width.setIncrement(100);
		width.setPageIncrement(100);
		try {
			width.setSelection(Math.round(Float.parseFloat(stroke.getWidth().toString()) * 100.0f));
		} catch (Exception e) {
			width.setSelection(100);
		}
		width.addSelectionListener(changeListener);

		// opacity spinner
		caption = new GridData(SWT.END, SWT.CENTER, false, false);
		editor = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label = new Label(page, SWT.NONE);
		label.setLayoutData(caption);
		label.setText(Messages.StrokeEditor_OpacityLabel);

		Composite opc = new Composite(page, SWT.NONE);
		opc.setLayoutData(editor);
		opc.setLayout(new RowLayout(SWT.HORIZONTAL));

		opacity = new Spinner(opc, SWT.BORDER);
		opacity.setMinimum(0);
		opacity.setMaximum(100);
		opacity.setIncrement(1);
		opacity.setPageIncrement(10);
		try {
			opacity.setSelection(Math.round(Float.parseFloat(stroke.getOpacity().toString()) * 100.0f));
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
	public Stroke getValue() {
		return styleBuilder.createStroke(getColor(), getWidth(), getOpacity());
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
	 * Get the {@link Stroke} width
	 * 
	 * @return the {@link Stroke} width
	 */
	private double getWidth() {
		return (width.getSelection()) / 100.0;
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
	public void setValue(Stroke stroke) {
		Color strokeColor = SLD.color(stroke);
		color.setValue(new RGB(strokeColor.getRed(), strokeColor.getGreen(), strokeColor.getBlue()));

		try {
			width.setSelection(Math.round(Float.parseFloat(stroke.getWidth().toString()) * 100.0f));
		} catch (Exception e) {
			width.setSelection(100);
		}

		try {
			opacity.setSelection(Math.round(Float.parseFloat(stroke.getOpacity().toString()) * 100.0f));
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
