/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.model.filtering;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * This is the supertype for all Toggle-type actions used in HALE that have a 
 * simple boolean state. It can also be used directly if no specific behaviour 
 * is expected.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleToggleAction 
	extends Action {
	
	private final TreeObjectType objectType;
	
	/**
	 * The {@link Composite} that needs to be updated when this action is 
	 * performed.
	 * TODO: Put this functionality in subclass.
	 */
	private TreeViewer actionTarget;
	
	private String msgDisable = "";
	private String msgEnable = "";
	
	private PatternViewFilter filterListener; 
	
	/**
	 * Constructor
	 * 
	 * @param objectType the tree object type
	 * @param msgDisable the message to disable filter
	 * @param msgEnable the message to enable filter
	 * @param iconPath the icon path
	 * @param pvf the pattern view filter
	 */
	public SimpleToggleAction(TreeObjectType objectType, String msgDisable, 
			String msgEnable, String iconPath, PatternViewFilter pvf) {
		super(msgDisable, Action.AS_CHECK_BOX);
		setChecked(true);
		setToolTipText(msgDisable);
		
		this.objectType = objectType;
		this.msgDisable = msgDisable;
		this.msgEnable = msgEnable;
		this.filterListener = pvf;
		
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, iconPath));
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		boolean active = isChecked();
		
		String text = (active)?(msgDisable):(msgEnable);
		setToolTipText(text);
		setText(text);
		
		if (active) { // active means visible means filter not active
			filterListener.removeAttributeFilter(this.objectType);
		}
		else { // not active means not visible means filter active
			filterListener.addAttributeFilter(this.objectType);
		}
		if (actionTarget != null) {
			actionTarget.refresh();
		}
	}
	
	/**
	 * @return this {@link SimpleToggleAction}s {@link TreeObjectType}, which 
	 * indicates the effect it should have.
	 */
	public TreeObjectType getActionName() {
		return this.objectType;
	}
	
	/**
	 * Set the action target
	 * 
	 * @param tv the action target tree viewer
	 */
	public void setActionTarget(TreeViewer tv) {
		this.actionTarget = tv;
	}

}
