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
