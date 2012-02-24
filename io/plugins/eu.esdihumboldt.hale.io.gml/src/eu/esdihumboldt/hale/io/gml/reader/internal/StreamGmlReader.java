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

package eu.esdihumboldt.hale.io.gml.reader.internal;

import java.io.IOException;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;

/**
 * Reads XML/GML from a stream
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StreamGmlReader extends AbstractInstanceReader {

	/**
	 * The name of the parameter specifying if the root element should be
	 * ignored and thus not be loaded as an instance.
	 * Parameter value defaults to <code>true</code>. 
	 */
	public static final String PARAM_IGNORE_ROOT = "ignoreRoot";

	private InstanceCollection instances;
	
	private final boolean restrictToFeatures;

	/**
	 * Constructor
	 * 
	 * @param restrictToFeatures if only instances that are GML features shall
	 *   be loaded
	 */
	public StreamGmlReader(boolean restrictToFeatures) {
		super();
		this.restrictToFeatures = restrictToFeatures;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Prepare loading of " + getTypeName(), ProgressIndicator.UNKNOWN);
		
		try {
			String pIgnoreRoot = getParameter(PARAM_IGNORE_ROOT);
			boolean ignoreRoot = (pIgnoreRoot == null || pIgnoreRoot.isEmpty()) ? (true)
					: (Boolean.parseBoolean(pIgnoreRoot));
			instances = new GmlInstanceCollection(getSource(), getSourceSchema(),
					restrictToFeatures, ignoreRoot);
			//TODO any kind of analysis on file? e.g. types and size - would also give feedback to the user if the file can be loaded
			reporter.setSuccess(true);
		} catch (Throwable e) {
			reporter.setSuccess(false);
		}
		
		return reporter;
	}

	/**
	 * @see InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "GML";
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		//FIXME for now not
		return false;
	}

}
