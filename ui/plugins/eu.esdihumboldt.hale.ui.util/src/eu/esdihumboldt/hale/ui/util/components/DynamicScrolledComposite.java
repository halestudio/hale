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

package eu.esdihumboldt.hale.ui.util.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * ScrolledComposite that sets sizes for the content Control. <br>
 * Behaves exactly like the default ScrolledComposite, but if
 * expandHorizontal/expandVertical is disabled it sets the contents size to its
 * preferred size in that direction.
 * 
 * @author Kai Schwierczek
 */
public class DynamicScrolledComposite extends ScrolledComposite {

	/**
	 * @see ScrolledComposite#ScrolledComposite(Composite, int)
	 */
	public DynamicScrolledComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void layout(boolean changed, boolean all) {
		Control content = getContent();
		if (getContent() != null) {
			Point contentPreferredSize = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point contentSize = content.getSize();
			if (!getExpandHorizontal())
				contentSize.x = contentPreferredSize.x;
			if (!getExpandVertical())
				contentSize.y = contentPreferredSize.y;
			content.setSize(contentSize);
		}
		super.layout(changed, all);
	}
}
