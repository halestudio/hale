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

package eu.esdihumboldt.hale.ui.util.graph;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

/**
 * Figure that displays text and word-wraps it if the figure would exceed a
 * maximum width.
 * 
 * @author Simon Templer
 */
public class WrappedText extends Figure {

	private final int maxWidth;

	private final FlowPage page;

	/**
	 * Create a wrapped text figure.
	 * 
	 * @param text the text to display
	 * @param maxWidth the maximum width
	 */
	public WrappedText(String text, int maxWidth) {
		super();
		this.maxWidth = maxWidth;

		setLayoutManager(new StackLayout());

		page = new FlowPage();
		add(page);

		TextFlow flow = new TextFlow(text);
		page.add(flow);
	}

	/**
	 * @see Figure#getPreferredSize(int, int)
	 */
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension size = page.getPreferredSize();
		if (size.width > maxWidth) {
			return page.getPreferredSize(maxWidth, -1);
		}
		return size;
	}

}
