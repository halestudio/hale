/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.report.service;

import java.util.ArrayList;

import eu.esdihumboldt.hale.io.xml.validator.Report;
import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportReport;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.service.UpdateService;
import eu.esdihumboldt.hale.ui.views.report.ReportModel;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportServiceImpl implements ReportService {

	/**
	 * Contains all classes which will be notified.
	 */
	private ArrayList<HaleServiceListener> listeners = new ArrayList<HaleServiceListener>();
	
	/**
	 * Contains all {@link Report}s.
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList reports = new ArrayList();
	
	@Override
	public void addListener(HaleServiceListener sl) {
		this.listeners.add(sl);
	}

	/**
	 * @see UpdateService#removeListener(HaleServiceListener)
	 */
	@Override
	public void removeListener(HaleServiceListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(ReportServiceImpl.class, null));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addReport(Report report) {
		this.reports.add(report);
		this.updateListeners();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void addReport(MappingExportReport report) {
		this.reports.add(report);
		this.updateListeners();
	}

	@Override
	public ReportModel getLastReport() {
		return this.getReport(this.reports.size()-1);
	}
	
	@Override
	public ReportModel getReport(int index) {
		ReportModel model;
		Object obj = this.reports.get(index);
		
		if (obj instanceof Report) {
			model = new ReportModel((Report) obj);
		} else {
			model = new ReportModel((MappingExportReport) obj);
		}
		
		return model;
	}
}
