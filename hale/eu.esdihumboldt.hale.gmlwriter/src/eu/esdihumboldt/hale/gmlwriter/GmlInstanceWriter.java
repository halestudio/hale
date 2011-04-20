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

package eu.esdihumboldt.hale.gmlwriter;

import java.io.IOException;
import java.io.OutputStream;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.gmlwriter.impl.DefaultGmlWriter;
import eu.esdihumboldt.hale.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.instance.io.impl.AbstractInstanceWriter;

/**
 * GML {@link InstanceWriter} 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class GmlInstanceWriter extends AbstractInstanceWriter {

	/**
	 * @see IOProvider#execute(ProgressIndicator)
	 */
	@Override
	public void execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		GmlWriter writer = new DefaultGmlWriter();
		OutputStream out = getTarget().getOutput();
		writer.writeFeatures(getInstances(), getTargetSchema(), out , getCommonSRSName());
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		//TODO make cancelable, e.g. on per feature basis
		return false;
	}

}
