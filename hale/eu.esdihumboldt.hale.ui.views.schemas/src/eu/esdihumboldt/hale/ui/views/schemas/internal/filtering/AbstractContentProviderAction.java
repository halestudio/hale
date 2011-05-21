/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
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
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.models.ConfigSchemaService;
import eu.esdihumboldt.hale.models.config.ConfigSchemaServiceListener;
import eu.esdihumboldt.hale.ui.views.schemas.internal.ConfigurableModelContentProvider;

/**
 * Basic action for changing the content provider on a viewer
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractContentProviderAction extends Action implements ConfigSchemaServiceListener {
	
	private ContentViewer viewer;
	
	private ConfigSchemaService config;
	
	/**
	 * Contains "Source" or "Target".
	 */
	private String caption = ""; //$NON-NLS-1$
	
	/**
	 * Is the class name as a String.
	 */
	private String identifier = ""; //$NON-NLS-1$
	
	/**
	 * Default constructor
	 */
	protected AbstractContentProviderAction() {
		super(null, AS_CHECK_BOX);
		
		config = (ConfigSchemaService) PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
	}
	
	/**
	 * Get the content provider associated with this action
	 * 
	 * @return the content provider
	 */
	private ConfigurableModelContentProvider getContentProvider() {
		return (ConfigurableModelContentProvider) viewer.getContentProvider();
	}

	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(ContentViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		ConfigurableModelContentProvider contentProvider = getContentProvider();
		updateContentProvider(contentProvider);
		viewer.setContentProvider(contentProvider);
	}

	/**
	 * Update the content provider
	 * 
	 * @param contentProvider the content provider
	 */
	protected abstract void updateContentProvider(
			ConfigurableModelContentProvider contentProvider);	
	
	/**
	 * @see Action#setChecked(boolean)
	 */
	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		
		if (!caption.equals("")) { //$NON-NLS-1$
			this.config.addItem(caption, this.identifier, ""+this.isChecked()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Setter for {@link AbstractContentProviderAction#caption}
	 * 
	 * @param caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		this.config.addItem(caption, this.identifier, ""+this.isChecked()); //$NON-NLS-1$ //$NON-NLS-2$
		this.config.addListener(this, caption);
	}
	
	/**
	 * Setter for {@link AbstractContentProviderAction#identifier}
	 * @param ident
	 */
	public void setIdentifier(String ident) {
		this.identifier = ident;
	}
	
	@Override
	public void update(final String section, final Message message) {
		if (message.equals(Message.ITEM_CHANGED) || message.equals(Message.CONFIG_PARSED)) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
						setChecked(Boolean.parseBoolean(config.getSectionData(caption).get(identifier)));
						AbstractContentProviderAction.this.run();
				}
			});
		}
	}
}
