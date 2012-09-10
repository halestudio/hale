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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A color editor button
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ColorEditor implements Editor<RGB> {

	private boolean changed = false;

	private Button button;

	private RGB color;

	private Image image;

	private int width = 24;

	private int height = 24;

	private boolean initialized = false;

	/**
	 * Create a color editor button
	 * 
	 * @param parent the parent composite
	 * @param color the initial color
	 */
	public ColorEditor(Composite parent, RGB color) {
		button = new Button(parent, SWT.PUSH);

		this.color = color;

		init(parent);

		image = new Image(parent.getDisplay(), width, height);

		updateImage();

		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				ColorDialog colorDialog = new ColorDialog(button.getShell());
				colorDialog.setRGB(ColorEditor.this.color);
				RGB newColor = colorDialog.open();
				if (newColor != null) {
					ColorEditor.this.color = newColor;
					changed = true;
					updateImage();
				}
			}
		});

		button.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {
				if (image != null) {
					image.dispose();
					image = null;
				}
			}
		});
	}

	/**
	 * Determine with and height
	 * 
	 * @param control the parent control
	 */
	protected void init(Control control) {
		if (!initialized) {
			initialized = true;

			GC gc = new GC(control);
			try {
				Font font = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
				gc.setFont(font);
				height = gc.getFontMetrics().getHeight();
				width = height * 2;
			} finally {
				gc.dispose();
			}
		}
	}

	/**
	 * Get the color, it has to be disposed by the caller
	 * 
	 * @param device the device for the color
	 * @return the color
	 */
	public Color getColor(Device device) {
		return new Color(device, color);
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public RGB getValue() {
		return color;
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(RGB color) {
		this.color = color;

		updateImage();
	}

	/**
	 * Update the color image
	 */
	private void updateImage() {
		GC gc = new GC(image);
		Color clr = getColor(gc.getDevice());
		try {
			gc.setBackground(clr);
			gc.fillRectangle(0, 0, width, height);
		} finally {
			gc.dispose();
			clr.dispose();
		}

		button.setImage(image);
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return button;
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

}
