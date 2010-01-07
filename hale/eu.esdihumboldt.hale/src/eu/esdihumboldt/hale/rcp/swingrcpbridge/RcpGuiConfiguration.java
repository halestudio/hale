/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.swingrcpbridge;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;

/**
 * FIXME Add Type description.
 * 
 * @author Simon Templer, Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class RcpGuiConfiguration {
	
	private static final Logger _log = Logger.getLogger(RcpGuiConfiguration.class);
	
	private final String configFolder;

	public RcpGuiConfiguration() {
		super();
		
		// setApplicationID(Platform.getProduct().getName()); //FIXME
		
		String path = Platform.getInstanceLocation().getURL().getPath();
		if (path != null && !path.equals("")) {
			this.configFolder = path;
		} 
		else {
			this.configFolder = ""; // super.getConfigFolder(); // FIXME
		}
		_log.info("MutableGui context configuration directory: " + configFolder);
	}

	/* (non-Javadoc)
	 * @see de.fhg.igd.mutable.gui.GuiConfiguration#getConfigFolder()
	 */
	//@Override FIXME
	public String getConfigFolder() {
		return this.configFolder;
	}
	
}
