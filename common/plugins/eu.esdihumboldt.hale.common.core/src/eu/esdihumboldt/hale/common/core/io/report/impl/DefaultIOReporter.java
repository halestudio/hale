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

package eu.esdihumboldt.hale.common.core.io.report.impl;

import de.cs3d.util.logging.ALogger;
import eu.esdihumboldt.hale.common.core.io.report.IOMessage;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;

/**
 * Default I/O report implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class DefaultIOReporter extends DefaultReporter<IOMessage> implements
		IOReporter {
	
	private Locatable target;

	/**
	 * Create an empty I/O report. It is set to not successful by default. But
	 * you should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @see DefaultReporter#DefaultReporter(String, Class, boolean)
	 * 
	 * @param target the locatable target
	 * @param taskName the name of the task the report is related to
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultIOReporter(Locatable target, String taskName, boolean doLog) {
		super(taskName, IOMessage.class, doLog);
		
		this.target = target;
	}

	/**
	 * @see IOReport#getTarget()
	 */
	@Override
	public Locatable getTarget() {
		return target;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.report.IOReport#setTarget(eu.esdihumboldt.hale.common.core.io.supplier.Locatable)
	 */
	@Override
	public void setTarget(Locatable locateable) {
		this.target = locateable;
	}

}
