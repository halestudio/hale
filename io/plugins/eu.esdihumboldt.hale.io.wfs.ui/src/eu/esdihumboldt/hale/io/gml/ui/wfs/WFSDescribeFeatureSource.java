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

package eu.esdihumboldt.hale.io.gml.ui.wfs;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Import source based on WFS DescribeFeatureType requests.
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 */
public class WFSDescribeFeatureSource<P extends ImportProvider> extends AbstractWFSSource<P> {

	/**
	 * @see AbstractWFSSource#createWfsFieldEditor(Composite)
	 */
	@Override
	protected WfsUrlFieldEditor createWfsFieldEditor(Composite parent) {
		return new WfsUrlFieldEditor("sourceWfs", "URL:", parent);
	}

	/**
	 * @see AbstractWFSSource#getCaption()
	 */
	@Override
	protected String getCaption() {
		return "WFS DescribeFeatureType request";
	}

}
