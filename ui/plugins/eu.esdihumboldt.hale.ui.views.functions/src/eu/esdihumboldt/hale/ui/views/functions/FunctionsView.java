/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.functions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionContentProvider;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Functions view
 * 
 * @author Simon Templer
 */
public class FunctionsView extends PropertiesViewPart {

	/**
	 * internal action class used for triggering filter-actions on the function
	 * view
	 * 
	 * @author Sebastian Reinhardt
	 */
	private final class FilterAction extends Action {

		private ViewerFilter filter;

		/**
		 * @param text the text of the action
		 * @param style the action style
		 * @param image the action icon/image
		 */
		private FilterAction(String text, int style, ImageDescriptor image) {
			super(text, style);
			setImageDescriptor(image);

			filter = new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof FunctionDefinition<?>) {
						if (PlatformUI.getWorkbench().getService(CompatibilityService.class)
								.getCurrent()
								.supportsFunction(((FunctionDefinition<?>) element).getId(),
										HaleUI.getServiceProvider())) {
							return true;
						}
						else
							return false;
					}
					else
						return true;
				}
			};
		}

		@Override
		public void run() {
			// enable or disable the filter, change the action text
			boolean active = isChecked();

			String text = (active) ? ("Show all functions") : ("Filter incompatible functions");
			setToolTipText(text);
			setText(text);

			if (active) {
				viewer.addFilter(filter);
			}
			else {
				viewer.removeFilter(filter);
			}
			viewer.refresh();
		}
	}

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.functions";

	private TreeViewer viewer;

	private ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory> compListener;

	private AlignmentServiceAdapter alignListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setLabelProvider(new FunctionLabelProvider());
		viewer.setContentProvider(new FunctionContentProvider(HaleUI.getServiceProvider()));

		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		IAction filterAction = new FilterAction("Filter incompatible functions",
				Action.AS_CHECK_BOX,
				CommonSharedImages.getImageRegistry().getDescriptor("IMG_FILTER_CLEAR"));
		manager.add(filterAction);
		filterAction.setChecked(true);
		filterAction.run();

		CompatibilityService cs = PlatformUI.getWorkbench().getService(CompatibilityService.class);
		cs.addListener(
				compListener = new ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory>() {

					@Override
					public void currentObjectChanged(CompatibilityMode current,
							CompatibilityModeFactory definition) {
						// refresh the viewer when the compatibility mode is
						// changed
						final Display display = PlatformUI.getWorkbench().getDisplay();
						display.syncExec(new Runnable() {

							@Override
							public void run() {
								viewer.refresh();
							}
						});
					}
				});

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addListener(alignListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				customFunctionsChanged();
			}

			@Override
			public void customFunctionsChanged() {
				// refresh the viewer when the compatibility mode is changed
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						viewer.refresh();
					}
				});
			}
		});

		// no input needed, but we have to set something
		viewer.setInput(Boolean.TRUE);

		new ViewerMenu(getSite(), viewer);
		getSite().setSelectionProvider(viewer);
	}

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.functions";
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		if (compListener != null) {
			CompatibilityService cs = PlatformUI.getWorkbench()
					.getService(CompatibilityService.class);
			cs.removeListener(compListener);
		}

		if (alignListener != null) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.removeListener(alignListener);
		}

		super.dispose();
	}

}
