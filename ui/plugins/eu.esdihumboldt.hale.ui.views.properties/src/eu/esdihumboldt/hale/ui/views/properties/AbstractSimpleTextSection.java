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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Simple section with a text field.
 * 
 * @author Simon Templer
 */
public abstract class AbstractSimpleTextSection extends AbstractSimpleSection<Text> {

	@Override
	protected Text createControl(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		Text text = widgetFactory.createText(parent, "");
		text.setEditable(false);
		return text;
	}

}
