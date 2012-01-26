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

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportItem {
	/**
	 * Contains the {@link ProjectInfo}.
	 */
	private String project;
	
	/**
	 * Contains the related {@link Report}.
	 */
	@SuppressWarnings("rawtypes")
	private Report report;
	
	/**
	 * Constructor.
	 * 
	 * @param project the project
	 * @param report the realted Report
	 */
	public ReportItem(String project, Report<?> report) {
		this.setProject(project);
		this.setReport(report);
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(String project) {
		this.project = project;
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
