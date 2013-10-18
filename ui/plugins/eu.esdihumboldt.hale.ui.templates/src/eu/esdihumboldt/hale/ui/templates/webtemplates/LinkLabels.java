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

package eu.esdihumboldt.hale.ui.templates.webtemplates;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.PlatformUI;

/**
 * Labels for the link column.
 * 
 * @author Simon Templer
 */
public class LinkLabels extends StyledCellLabelProvider {

	private final Color foreground = JFaceColors.getHyperlinkText(PlatformUI.getWorkbench()
			.getDisplay());

	private final Cursor cursor = PlatformUI.getWorkbench().getDisplay()
			.getSystemCursor(SWT.CURSOR_HAND);

	private final Styler linkStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = foreground;
			textStyle.underline = true;
			textStyle.underlineColor = foreground;
			textStyle.underlineStyle = SWT.UNDERLINE_LINK;
		}

	};

	@Override
	public void update(ViewerCell cell) {
		StyledString text = new StyledString("Info", linkStyler);

		cell.setText(text.getString());
		cell.setStyleRanges(text.getStyleRanges());

		cell.getControl().setCursor(cursor);

		super.update(cell);
	}
}