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

package eu.esdihumboldt.hale.io.gml.ui.wfs;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.ImportProvider;

/**
 * Import source based on WFS GetFeature requests. 
 * @param <P> the supported {@link IOProvider} type
 * @param <T> the supported {@link IOProviderFactory} type
 * 
 * @author Simon Templer
 */
public class WFSGetFeatureSource<P extends ImportProvider, T extends IOProviderFactory<P>> extends AbstractWFSSource<P, T> {

	/**
	 * @see AbstractWFSSource#createWfsFieldEditor(Composite)
	 */
	@Override
	protected WfsUrlFieldEditor createWfsFieldEditor(Composite parent) {
		String schemaNamespace = null; //FIXME needed?
		return new WfsUrlFieldEditor("sourceWfs", "WFS GetFeature URL:", parent,
				schemaNamespace , true);
	}

}
