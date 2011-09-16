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

import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.instance.io.InstanceReader;
import eu.esdihumboldt.hale.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;

/**
 * Reads XML/GML from a stream
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StreamGmlReader extends AbstractInstanceReader {

	private InstanceCollection instances;
	
	private final ContentType defaultContentType;

	private final boolean restrictToFeatures;

	/**
	 * Constructor
	 * 
	 * @param defaultContentType the default content type
	 * @param restrictToFeatures if only instances that are GML features shall
	 *   be loaded
	 */
	public StreamGmlReader(ContentType defaultContentType, 
			boolean restrictToFeatures) {
		super();
		this.defaultContentType = defaultContentType;
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
			instances = new GmlInstanceCollection(getSource(), getSourceSchema(),
					restrictToFeatures);
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
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return defaultContentType;
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
