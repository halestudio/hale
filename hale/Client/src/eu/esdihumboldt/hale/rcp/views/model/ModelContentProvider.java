package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;




/**
 * The ContentProvider for TreeViewers. 
 * @author cjauss
 *
 */
public class ModelContentProvider 
	implements IStructuredContentProvider, ITreeContentProvider{
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput){
		//TODO Auto-generated method stub
	} 
    
	
	public void dispose(){
		//TODO Auto-generated method stub
	}
    
	
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
    
	
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
    
	
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return new Object[0];
	}
	
	
    public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
}