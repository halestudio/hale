package eu.esdihumboldt.hale.rcp.views.report;

import java.util.ArrayList;

public class TransformationResultItem {

	private String message;
	private ArrayList<String> lines = new ArrayList<String>();
	
	public TransformationResultItem(String message, String line) {
		this.message = message;
		this.addLine(line);
	}
	
	public TransformationResultItem(String message, int line) {
		this.message = message;
		this.addLine(line);
	}
	
	public void addLine(String line) {
		this.lines.add(line);
	}
	
	public void addLine(int line) {
		this.addLine(""+line);
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public ArrayList<String> getLines() {
		return this.lines;
	}
}
