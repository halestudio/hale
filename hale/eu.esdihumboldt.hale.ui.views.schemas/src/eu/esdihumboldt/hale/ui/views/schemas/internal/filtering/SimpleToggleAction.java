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
package eu.esdihumboldt.hale.ui.views.schemas.internal.filtering;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.model.schema.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaService;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaServiceListener;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * This is the supertype for all Toggle-type actions used in HALE that have a 
 * simple boolean state. It can also be used directly if no specific behaviour 
 * is expected.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SimpleToggleAction 
	extends Action implements ConfigSchemaServiceListener {
	
	private final TreeObjectType objectType;
	
	/**
	 * The {@link Composite} that needs to be updated when this action is 
	 * performed.
	 * TODO: Put this functionality in subclass.
	 */
	private TreeViewer actionTarget;
	
	private String msgDisable = ""; //$NON-NLS-1$
	private String msgEnable = ""; //$NON-NLS-1$
	
	private PatternViewFilter filterListener;
	
	/**
	 * Contains "Source" or "Target".
	 */
	private String caption = ""; //$NON-NLS-1$
	
	private ConfigSchemaService config;
	
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
		
		setToolTipText(msgDisable);
		config = (ConfigSchemaService) PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
		
		this.objectType = objectType;
		this.msgDisable = msgDisable;
		this.msgEnable = msgEnable;
		this.filterListener = pvf;
		
		setChecked(true);
		
		setImageDescriptor(SchemasViewPlugin.getImageDescriptor(iconPath));
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

	/**
	 * @see Action#setChecked(boolean)
	 */
	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		
		if (!caption.equals("")) { //$NON-NLS-1$
			this.config.addItem(caption, this.objectType.toString(), ""+this.isChecked()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Setter for {@link SimpleToggleAction#caption}
	 * 
	 * @param caption the caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		this.config.addItem(caption, this.objectType.toString(), ""+this.isChecked()); //$NON-NLS-1$ //$NON-NLS-2$
		this.config.addListener(this, caption);
	}

	@Override
	public void update(final String section, final Message message) {
		if (message.equals(Message.ITEM_CHANGED) || message.equals(Message.CONFIG_PARSED)) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
						setChecked(Boolean.parseBoolean(config.getSectionData(caption).get(objectType.toString())));
						SimpleToggleAction.this.run();
				}
			});
		}
	}
}
