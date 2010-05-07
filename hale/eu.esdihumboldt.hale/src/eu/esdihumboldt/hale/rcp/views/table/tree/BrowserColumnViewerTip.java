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

package eu.esdihumboldt.hale.rcp.views.table.tree;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.rcp.utils.definition.internal.BrowserTip;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class BrowserColumnViewerTip {
	
	private final BrowserTip tip;
	
	private final ColumnViewer viewer;
	
	private Shell toolShell = null;
	
	private int tipCharThreshold = 10;

	/**
	 * Constructor
	 * 
	 * @param viewer 
	 * @param width 
	 * @param height 
	 * @param plainText 
	 */
	public BrowserColumnViewerTip(ColumnViewer viewer, int width, int height, boolean plainText) {
		super();
		
		this.viewer = viewer;
		
		viewer.getControl().addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseHover(MouseEvent e) {
				showToolTip(e.x - 1, e.y - 1);
			}
			
		});
		
		tip = new BrowserTip(width, height, plainText);
		tip.setHeightAdjustment(5);
	}

	/**
	 * Show tooltip for the cell at the given position
	 * 
	 * @param x
	 * @param y
	 */
	protected void showToolTip(int x, int y) {
		BrowserTip.hideToolTip(toolShell);
		
		ViewerCell cell = viewer.getCell(new Point(x, y));
		
		if (cell != null) {
			Object element = cell.getElement();
			int col = cell.getColumnIndex();
			
			/**
			 * It would be very nice if we could get the column from the viewer
			 * and the attached label provider, but the getViewerColumn method
			 * is only package visible
			 */
			
			String text = cell.getText();
			
			String tipText = getToolTip(element, col, text);
			
			if (tipText != null && !tipText.isEmpty() && tipText.length() >= tipCharThreshold) {
				toolShell = tip.showToolTip(viewer.getControl(), x, y, tipText);
			}
		}
	}

	/**
	 * Get the tool tip for a viewer cell
	 * 
	 * @param element the element defining the viewer row
	 * @param col the column index
	 * @param text the cell text
	 * 
	 * @return the tool tip, <code>null</code> if no tool tip shall be shown
	 */
	protected abstract String getToolTip(Object element, int col,
			String text);

}
