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

package eu.esdihumboldt.hale.ui.views.data.internal.explore;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.views.data.internal.Messages;
import eu.esdihumboldt.hale.ui.views.data.internal.Metadata;
import eu.esdihumboldt.util.Pair;

/**
 * Content provider showing an instance property tree with all values.
 * Elements are {@link Definition}/value {@link Pair}s.
 * @author Simon Templer
 */
public class InstanceContentProvider implements ITreeContentProvider {

	/**
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof Instance) {
			
			if (!((Instance) inputElement).getMetaDataNames().isEmpty()) {
				return new Object[] {
						new Pair<Object, Object>(
								((Instance) inputElement).getDefinition(),
								inputElement),
						(new Pair<Object, Object>(Metadata.METADATA, inputElement)) };
			}

			else
				return new Object[] { new Pair<Object, Object>(
						((Instance) inputElement).getDefinition(), inputElement) };
		} else
			return new Object[0];
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		
		boolean isMetaData = false;
		
		if (parentElement instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) parentElement;
			parentElement = pair.getSecond();
			
			if(pair.getFirst() == Metadata.METADATA && pair.getSecond() instanceof Instance){
				isMetaData = true;
			}
		}

		if (parentElement instanceof Group && isMetaData == false) {
			Group group = (Group) parentElement;
			List<Object> children = new ArrayList<Object>();
			for (QName name : group.getPropertyNames()) {
				Definition<?> childDef = group.getDefinition().getChild(name);
				for (Object value : group.getProperty(name)) {
					children.add(new Pair<Object, Object>(childDef, value));
				}
			}
			return children.toArray();
		}
		
		if (parentElement instanceof Group && isMetaData == true){
			Instance inst = (Instance) parentElement;
			List<Object> metachildren = new ArrayList<Object>();
			for(String key : inst.getMetaDataNames()){
				List<Object> values = inst.getMetaData(key);
				for(Object value : values){
					metachildren.add(new Pair<String, Object>(key, value));
				}
				
			}
			return metachildren.toArray();
		}

		return new Object[0];
	}

	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		return null; // not supported currently
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) element;
			if(pair.getFirst() == Metadata.METADATA && pair.getSecond() instanceof Instance){
				return true;
			}
			
			element = pair.getSecond();
		}
		
		if (element instanceof Group) {
			Group group = (Group) element;
			return !Iterables.isEmpty(group.getPropertyNames());
		}
		
		return false;
	}

}
