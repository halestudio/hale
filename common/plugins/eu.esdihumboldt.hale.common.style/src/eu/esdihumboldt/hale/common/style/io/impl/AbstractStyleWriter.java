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

package eu.esdihumboldt.hale.common.style.io.impl;

import org.geotools.styling.Style;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.style.io.StyleWriter;

/**
 * Base class for {@link StyleWriter} implementations.
 * 
 * @author Simon Templer
 */
public abstract class AbstractStyleWriter extends AbstractExportProvider implements StyleWriter {

	private Style style;

	/**
	 * @see StyleWriter#setStyle(Style)
	 */
	@Override
	public void setStyle(Style style) {
		this.style = style;
	}

	/**
	 * Get the style to write.
	 * 
	 * @return the style to write
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * @see AbstractExportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		if (getStyle() == null) {
			fail("Style to write not set");
		}

		super.validate();
	}

}
