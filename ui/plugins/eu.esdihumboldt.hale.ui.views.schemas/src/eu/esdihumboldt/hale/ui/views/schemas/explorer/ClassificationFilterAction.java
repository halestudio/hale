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
package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.action.Action;

import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * This is the supertype for all Toggle-type actions used in HALE that have a
 * simple boolean state. It can also be used directly if no specific behaviour
 * is expected.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ClassificationFilterAction extends Action {

	private final Classification clazz;

	private String msgDisable = ""; //$NON-NLS-1$
	private String msgEnable = ""; //$NON-NLS-1$

	private final ClassificationFilter filter;

//	/**
//	 * Contains "Source" or "Target".
//	 */
//	private String caption = ""; //$NON-NLS-1$

	/**
	 * Constructor
	 * 
	 * @param clazz the tree object type
	 * @param msgDisable the message to disable filter
	 * @param msgEnable the message to enable filter
	 * @param iconPath the icon path
	 * @param filter the pattern view filter
	 */
	public ClassificationFilterAction(Classification clazz, String msgDisable, String msgEnable,
			String iconPath, ClassificationFilter filter) {
		super(msgDisable, Action.AS_CHECK_BOX);

		setToolTipText(msgDisable);

		this.clazz = clazz;
		this.msgDisable = msgDisable;
		this.msgEnable = msgEnable;
		this.filter = filter;

		setChecked(filter.isVisible(clazz));

		setImageDescriptor(SchemasViewPlugin.getImageDescriptor(iconPath));
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		boolean active = isChecked();

		String text = (active) ? (msgDisable) : (msgEnable);
		setToolTipText(text);
		setText(text);

		filter.setVisible(clazz, active);
	}

	/**
	 * @see Action#setChecked(boolean)
	 */
	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);

//		if (!caption.equals("")) { //$NON-NLS-1$
//			this.config.addItem(caption, this.objectType.toString(), ""+this.isChecked()); //$NON-NLS-1$ //$NON-NLS-2$
//		}
	}

//	/**
//	 * Setter for {@link ClassificationFilterAction#caption}
//	 * 
//	 * @param caption the caption
//	 */
//	public void setCaption(String caption) {
//		this.caption = caption;
//		this.config.addItem(caption, this.objectType.toString(), ""+this.isChecked()); //$NON-NLS-1$ //$NON-NLS-2$
//		this.config.addListener(this, caption);
//	}

//	@Override
//	public void update(final String section, final Message message) {
//		if (message.equals(Message.ITEM_CHANGED) || message.equals(Message.CONFIG_PARSED)) {
//			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
//				@Override
//				public void run() {
//						setChecked(Boolean.parseBoolean(config.getSectionData(caption).get(objectType.toString())));
//						ClassificationFilterAction.this.run();
//				}
//			});
//		}
//	}

}
