package eu.esdihumboldt.hale.rcp.views.report.service;

import java.util.ArrayList;

import eu.esdihumboldt.hale.gmlvalidate.Report;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.UpdateService;
import eu.esdihumboldt.hale.rcp.views.report.ReportModel;

public class ReportServiceImpl implements ReportService {

	private ArrayList<HaleServiceListener> listeners = new ArrayList<HaleServiceListener>();
	
	private ArrayList<Report> reports = new ArrayList<Report>();
	
	@Override
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}

	/**
	 * @see UpdateService#removeListener(HaleServiceListener)
	 */
	@Override
	public void removeListener(HaleServiceListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void addReport(Report report) {
		// TODO add checks for max. amount
		this.reports.add(report);
		this.updateListeners();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(ReportServiceImpl.class, null));
		}
	}

	@Override
	public ReportModel getLastReport() {
		ReportModel model = new ReportModel(this.reports.get(this.reports.size()-1));
		return model;
	}
	
	
}
