/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.graph.figures;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Font;

/**
 * Label that displays the end of a text in the sub text.
 * 
 * @author Simon Templer
 */
public class EndSubTextLabel extends Label {

	private String subStringText;

	@Override
	public String getSubStringText() {
		if (subStringText != null)
			return subStringText;

		subStringText = getText();
		int widthShrink = getPreferredSize().width - getSize().width;
		if (widthShrink <= 0)
			return subStringText;

		Dimension effectiveSize = getTextSize().getExpanded(-widthShrink, 0);
		Font currentFont = getFont();
		int dotsWidth = getTextUtilities().getTextExtents(getTruncationString(), currentFont).width;

		if (effectiveSize.width < dotsWidth)
			effectiveSize.width = dotsWidth;

		String reverseText = new StringBuilder(getText()).reverse().toString();

		int subStringLength = getTextUtilities().getLargestSubstringConfinedTo(reverseText,
				currentFont, effectiveSize.width - dotsWidth);
		subStringText = getTruncationString()
				+ new StringBuilder(reverseText.substring(0, subStringLength)).reverse().toString();
		return subStringText;
	}

	/**
	 * @see org.eclipse.draw2d.Label#invalidate()
	 */
	@Override
	public void invalidate() {
		subStringText = null;

		super.invalidate();
	}

}
