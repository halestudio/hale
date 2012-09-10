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

package eu.esdihumboldt.hale.ui.style.io.impl;

import org.geotools.styling.Style;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.ui.style.io.StyleWriter;

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
