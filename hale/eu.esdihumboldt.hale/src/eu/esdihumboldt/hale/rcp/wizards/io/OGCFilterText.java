package eu.esdihumboldt.hale.rcp.wizards.io;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class OGCFilterText extends StringButtonFieldEditor {

	public OGCFilterText(String name, String labelText, Composite parent){
		super(name, labelText, parent);
	}
	
	@Override
	protected String changePressed() {
		OGCFilterDialog wfsDialog = new OGCFilterDialog(this.getShell(), "Create OGC Filter");
		return wfsDialog.open();
	}

}
