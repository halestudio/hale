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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.viewers.ColumnViewer;

/**
 * Browser Tip for a certain column
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ColumnBrowserTip extends BrowserColumnViewerTip {

	private final int column;

	private final TipProvider tipProvider;

	/**
	 * Constructor
	 * 
	 * @param viewer the column viewer
	 * @param width the tip width
	 * @param height the tip height
	 * @param plainText if plain text or HTML shall be displayed
	 * @param column the column index
	 * @param tipProvider the tool tip provider, if a <code>null</code> tip
	 *            provider is used, the cell's text will be used for the tool
	 *            tip
	 */
	public ColumnBrowserTip(ColumnViewer viewer, int width, int height, boolean plainText,
			int column, TipProvider tipProvider) {
		super(viewer, width, height, plainText);
		this.column = column;
		this.tipProvider = tipProvider;
	}

	/**
	 * @see BrowserColumnViewerTip#getToolTip(Object, int, String)
	 */
	@Override
	protected String getToolTip(Object element, int col, String text) {
		if (col == column) {
			if (tipProvider != null) {
				return tipProvider.getToolTip(element);
			}
			else {
				return text;
			}
		}
		return null;
	}

}
