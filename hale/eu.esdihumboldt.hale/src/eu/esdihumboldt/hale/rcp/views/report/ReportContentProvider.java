package eu.esdihumboldt.hale.rcp.views.report;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ReportContentProvider implements ITreeContentProvider {

	private ReportModel model;
	private List<TransformationResultItem> item = new ArrayList<TransformationResultItem>();
	
	@Override
	public Object[] getElements(Object inputElement) {
		System.err.println("getElements() "+inputElement);
		Object[] ret = new Object[2];
		ret[0] = new String("Warning ("+/*ReportModel.getInstance().getWarnings().size()+*/")");
		ret[1] = new String("Error ("+/*ReportModel.getInstance().getErrors().size()+*/")");
		
		return ret;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ReportModel) {
			this.model = (ReportModel) newInput;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement.toString().startsWith("Error")) {
			this.item = this.model.getErrors();
			return this.item.toArray();
		} /*else if (parentElement.toString().startsWith("Warning")) {
			this.item = this.model.getWarnings();
			return this.item.toArray();
		}*/
		else if (parentElement instanceof TransformationResultItem){
			return ((TransformationResultItem)parentElement).getLines().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		System.err.println("getParent() "+element.toString());
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		
//		if (element.toString().contains("Warning")) {
//			if (ReportModel.getInstance().getWarnings().size() > 0) {
//				hasChildren = true;
//			}
//		}
//		else if (element.toString().contains("Error")) {
//			if (ReportModel.getInstance().getErrors().size() > 0) {
//				hasChildren = true;
//			}
//		}
		
		if (this.item.size() > 0 && element instanceof TransformationResultItem) {
			hasChildren = true;
		}
		
		return true;
//		return hasChildren;
	}

}
