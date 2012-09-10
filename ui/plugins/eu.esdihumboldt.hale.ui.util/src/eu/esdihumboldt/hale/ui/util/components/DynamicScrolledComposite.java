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