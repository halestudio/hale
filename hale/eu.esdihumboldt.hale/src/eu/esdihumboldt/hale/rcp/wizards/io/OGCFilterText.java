package eu.esdihumboldt.hale.rcp.wizards.io;

import java.util.Iterator;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.type.FeatureType;

public class OGCFilterText extends StringButtonFieldEditor {
	FeatureTypeList selection;

	public OGCFilterText(FeatureTypeList selection, String name, String labelText, Composite parent){
		super(name, labelText, parent);
		this.selection = selection;
	}
	
	@Override
	protected String changePressed() {
		OGCFilterDialog filterDialog = new OGCFilterDialog(this.getShell(), Messages.OGCFilterText_CreateFilter);
		
		Iterator<FeatureType> iterator = selection.getSelection().iterator();
		if (iterator.hasNext()) {
			filterDialog.setFeatureType(iterator.next());
		}
		
		String result = filterDialog.open();
		if (result != null) {
			getTextControl().setText(result);
		}
		
		return result;
	}

}
