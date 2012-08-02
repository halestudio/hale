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

package eu.esdihumboldt.hale.ui.views.styledmap;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.help.IContextProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import de.fhg.igd.mapviewer.view.MapView;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.ui.HALEContextProvider;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.util.ViewContextMenu;
import eu.esdihumboldt.hale.ui.views.styledmap.internal.StyledMapBundle;

/**
 * Extends map view with some functionality from PropertiesViewPart.
 * @author Simon Templer
 */
public class StyledMapView extends MapView {
	
	/**
	 * Action that changes the selection to a set of given instances.
	 */
	public class SelectInstancesAction extends Action {

		private final List<Object> instances;

		/**
		 * Create an action the select the given instances.
		 * @param instances the instances to select, should be from the same
		 *   data set
		 * @param dataSet the data set 
		 */
		public SelectInstancesAction(List<Object> instances, DataSet dataSet) {
			this.instances = instances;
			
			String instanceName;
			String iconPath;
			switch (dataSet) {
				case TRANSFORMED:
					instanceName = "transformed";
					iconPath = "icons/target.png";
					break;
				case SOURCE:
				default:
					instanceName = "source";
					iconPath = "icons/source.png";
			}
			
			setText(MessageFormat.format("{0} {1} instance(s)", instances.size(),
					instanceName));
			setDescription(MessageFormat.format("Select only the {0} instances", 
					instanceName));
			setImageDescriptor(StyledMapBundle.imageDescriptorFromPlugin(
					StyledMapBundle.PLUGIN_ID, iconPath));
		}

		/**
		 * @see Action#run()
		 */
		@Override
		public void run() {
			getSite().getSelectionProvider().setSelection(
					new DefaultInstanceSelection(instances));
		}

	}

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.styledmap";

	/**
	 * @see MapView#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		HaleUI.registerWorkbenchUndoRedo(getViewSite());
		
		super.createPartControl(parent);
		
		Control mainControl;
		
		// try to get the Swing embedding composite
		Control[] children = parent.getChildren();
		if (children != null && children.length > 0) {
			mainControl = children[0];
		}
		else {
			mainControl = parent;
		}
		
		new ViewContextMenu(getSite(), 
				getSite().getSelectionProvider(), mainControl) {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ISelection sel = getSite().getSelectionProvider().getSelection();
				
				// show summary about selected instances
				if (!sel.isEmpty() && sel instanceof InstanceSelection) {
					List<Object> source = new ArrayList<Object>();
					List<Object> transformed = new ArrayList<Object>();
					
					for (Object object : ((InstanceSelection) sel).toArray()) {
						DataSet ds = null;
						if (object instanceof Instance) {
							ds = ((Instance) object).getDataSet();
						}
						if (object instanceof InstanceReference) {
							ds = ((InstanceReference) object).getDataSet();
						}
						if (ds != null) {
							switch (ds) {
								case SOURCE: 
									source.add(object);
									break;
								case TRANSFORMED: 
									transformed.add(object); 
									break;
							}
						}
					}
					
					if (!source.isEmpty() || !transformed.isEmpty()) {
						Action noAction = new Action("Selection:") {
							// does nothing
						};
						noAction.setEnabled(false);
						manager.add(noAction);
					}
					
					if (!source.isEmpty()) {
						Action selectAction = new SelectInstancesAction(
								source, DataSet.SOURCE);
						selectAction.setEnabled(!transformed.isEmpty());
						manager.add(selectAction);
					}
					if (!transformed.isEmpty()) {
						Action selectAction = new SelectInstancesAction(
								transformed, DataSet.TRANSFORMED);
						selectAction.setEnabled(!source.isEmpty());
						manager.add(selectAction);
					}
				}
				
				super.menuAboutToShow(manager);
			}
			
		};
		final Menu swtMenu = mainControl.getMenu();
		
		getMapKit().getMainMap().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				mouseReleased(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							swtMenu.setLocation(e.getXOnScreen(), e.getYOnScreen());
							swtMenu.setVisible(true);
						}
						
					});
				}
			}
			
		});
	}

	/**
	 * @see WorkbenchPart#getAdapter(Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.equals(IContextProvider.class)) {
			return new HALEContextProvider(
					getSite().getSelectionProvider(),
					getViewContext());
		}
        return super.getAdapter(adapter);
	}
	
	/**
	 * Get the view's dynamic help context identifier.
	 * @return the context id or <code>null</code>
	 */
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.views.styledmap.view";
	}
	
}
