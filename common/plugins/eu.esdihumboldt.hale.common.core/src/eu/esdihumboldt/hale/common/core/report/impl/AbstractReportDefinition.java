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

package eu.esdihumboldt.hale.common.core.report.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportDefinition;

/**
 * Abstract report definition.
 * @author Andreas Burchert
 * @param <T> the report type
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractReportDefinition<T extends Report<?>> implements ReportDefinition<T> {

	private static final ALogger _log = ALoggerFactory.getLogger(AbstractReportDefinition.class);
	
	private final Class<T> reportClass;
	
	private final String identifier;
	
	/**
	 * Create report definition.
	 * @param reportClass the report class
	 * @param id the identifier for the definition (without prefix)
	 */
	public AbstractReportDefinition(Class<T> reportClass, String id) {
		super();
		
		this.reportClass = reportClass;
		this.identifier = ID_PREFIX + id.toUpperCase();
	}
	
	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#getObjectClass()
	 */
	@Override
	public Class<T> getObjectClass() {
		return reportClass;
	}

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#parse(java.lang.String)
	 */
	@Override
	public T parse(String value) {
		Properties props = new Properties();
		StringReader reader = new StringReader(value.trim());
		try {
			props.load(reader);
		} catch (IOException e) {
			_log.error("Error loading report properties", e);
			return null;
		} finally {
			reader.close();
		}
		
		return createReport(props);
	}
	
	/**
	 * Create a report from a set of properties.
	 * @param props the properties
	 * @return the report
	 */
	protected abstract T createReport(Properties props);

	/**
	 * @see eu.esdihumboldt.util.definition.ObjectDefinition#asString(java.lang.Object)
	 */
	@Override
	public String asString(T report) {
		String nl = System.getProperty("line.separator");
		Properties props = asProperties(report);
		
		StringWriter writer = new StringWriter();
		try {
			props.store(writer, null);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		return nl + writer.toString() + nl+nl;
	}

	/**
	 * Get a {@link Properties} representation of the given report that can be
	 * used to create a new report instance using 
	 * {@link #createReport(Properties)}.
	 * @param report the message
	 * @return the properties representing the report
	 */
	protected abstract Properties asProperties(Report<?> report);

}
