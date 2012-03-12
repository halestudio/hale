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

package eu.esdihumboldt.hale.ui.views.report;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Contains a report and the related session.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportItem {
	/**
	 * Contains the identifier.
	 */
	private long id;
	
	/**
	 * Contains the related {@link Report}.
	 */
	private Report<?> report;
	
	/**
	 * Constructor.
	 * 
	 * @param id the project
	 * @param report the related Report
	 */
	public ReportItem(long id, Report<?> report) {
		this.setIdentifier(id);
		this.setReport(report);
	}

	/**
	 * @return the identifier
	 */
	public long getIdentifier() {
		return id;
	}

	/**
	 * @param id the identifier to set
	 */
	public void setIdentifier(long id) {
		this.id = id;
	}

	/**
	 * @return the report
	 */
	public Report<?> getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(Report<?> report) {
		this.report = report;
	}
}
