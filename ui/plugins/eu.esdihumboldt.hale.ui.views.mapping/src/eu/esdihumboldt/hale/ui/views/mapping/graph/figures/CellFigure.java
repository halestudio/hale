/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.mapping.graph.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.widgets.ILabeledFigure;
import org.eclipse.zest.core.widgets.IStyleableFigure;

/**
 * Figure representing a cell.
 * @author Simon Templer
 */
public class CellFigure extends Shape implements IStyleableFigure,
		ILabeledFigure {

	private final int tipWidth = 10;
	
	private final Label label;

	private Color borderColor;
	
	/**
	 * Default constructor
	 */
	public CellFigure() {
		super();
		
		setAntialias(SWT.ON);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);
		
		label = new Label();
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		add(label, gridData);
	}

	/**
	 * @see Figure#getInsets()
	 */
	@Override
	public Insets getInsets() {
		return new Insets(0, tipWidth, 0, tipWidth);
	}

	/**
	 * @see Shape#fillShape(Graphics)
	 */
	@Override
	protected void fillShape(Graphics graphics) {
		int[] points = getPoints();
		graphics.fillPolygon(points);
	}

	/**
	 * Get the figure's outline points
	 * @return the figure's outline
	 */
	private int[] getPoints() {
		int[] points = new int[12];
		
		Rectangle bounds = getBounds();
		
		points[0] = bounds.x;
		points[1] = bounds.y + bounds.height / 2;
		
		points[2] = bounds.x + tipWidth;
		points[3] = bounds.y;
		
		points[4] = bounds.right() - tipWidth - 1;
		points[5] = bounds.y;
		
		points[6] = bounds.right() - 1;
		points[7] = bounds.y + bounds.height / 2;
		
		points[8] = bounds.right() - tipWidth - 1;
		points[9] = bounds.bottom() - 1;
		
		points[10] = bounds.x + tipWidth;
		points[11] = bounds.bottom() - 1;
		
		return points;
	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	@Override
	protected void outlineShape(Graphics graphics) {
		if (borderColor != null) {
			graphics.setForegroundColor(borderColor);
		}
		
		int[] points = getPoints();
		graphics.drawPolygon(points);
	}
	
	/**
	 * Adjust the figure size.
	 * The default implementation sets the size to the preferred size.
	 */
	protected void adjustSize() {
		setSize(getPreferredSize());
	}

	/**
	 * @see ILabeledFigure#setText(String)
	 */
	@Override
	public void setText(String text) {
		label.setText(text);
		adjustSize();
	}

	/**
	 * @see ILabeledFigure#getText()
	 */
	@Override
	public String getText() {
		return label.getText();
	}

	/**
	 * @see ILabeledFigure#setIcon(Image)
	 */
	@Override
	public void setIcon(Image icon) {
		label.setIcon(icon);
		adjustSize();
	}

	/**
	 * @see ILabeledFigure#getIcon()
	 */
	@Override
	public Image getIcon() {
		return label.getIcon();
	}

	/**
	 * @see IStyleableFigure#setBorderColor(Color)
	 */
	@Override
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @see IStyleableFigure#setBorderWidth(int)
	 */
	@Override
	public void setBorderWidth(int borderWidth) {
		setLineWidth(borderWidth);
	}

}
