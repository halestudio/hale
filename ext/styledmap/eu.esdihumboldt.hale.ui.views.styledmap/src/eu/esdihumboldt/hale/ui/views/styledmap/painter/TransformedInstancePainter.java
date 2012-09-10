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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Painter for transformed instances.
 * 
 * @author Simon Templer
 */
public class TransformedInstancePainter extends AbstractInstancePainter {

	/**
	 * Default constructor
	 */
	public TransformedInstancePainter() {
		super((InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class),
				DataSet.TRANSFORMED);
	}

}
