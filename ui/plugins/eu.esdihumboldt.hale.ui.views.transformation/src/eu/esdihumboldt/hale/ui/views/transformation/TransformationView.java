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

package eu.esdihumboldt.hale.ui.views.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.content.TransformationTreeContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.TransformationTreeLabelProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceSampleService;
import eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView;
import eu.esdihumboldt.hale.ui.views.transformation.internal.TransformationViewPlugin;
import eu.esdihumboldt.util.Pair;

/**
 * View displaying transformation tree(s).
 * 
 * @author Simon Templer
 */
public class TransformationView extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.transformation";

	private static final String MEMENTO_KEY_INSTANCE_SAMPLE = "apply_sample_instances";

	private AlignmentServiceListener alignmentListener;

	private Observer instanceSampleObserver;

	private IAction instanceAction;

	private boolean initInstanceAction = false;

	/**
	 * @see ViewPart#init(IViewSite, IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (memento != null) {
			Boolean value = memento.getBoolean(MEMENTO_KEY_INSTANCE_SAMPLE);
			if (value != null) {
				initInstanceAction = value;
			}
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);
		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager()
				.add(instanceAction = new Action("Apply sample instances", IAction.AS_CHECK_BOX) {

					@Override
					public void run() {
						update();
					}

				});
		instanceAction.setImageDescriptor(
				TransformationViewPlugin.getImageDescriptor("icons/samples.gif"));
		instanceAction.setChecked(initInstanceAction);

		update();

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				update();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				update();
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				update();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				update();
			}

			@Override
			public void alignmentChanged() {
				update();
			}

			@Override
			public void customFunctionsChanged() {
				update();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				if (Cell.PROPERTY_DISABLE_FOR.equals(propertyName)
						|| Cell.PROPERTY_ENABLE_FOR.equals(propertyName))
					update(); // Could add/remove cells from transformation tree
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							// refresh view
							getViewer().refresh();
						}
					});
				}
			}

		});

		final InstanceSampleService iss = PlatformUI.getWorkbench()
				.getService(InstanceSampleService.class);
		iss.addObserver(instanceSampleObserver = new Observer() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(Observable o, Object arg) {
				if (!instanceAction.isChecked()) {
					return;
				}

				Object input = getViewer().getInput();

				Collection<Instance> oldInstances = null;
				int sampleCount = 0;
				if (input instanceof Pair<?, ?>) {
					Object second = ((Pair<?, ?>) input).getSecond();
					if (second instanceof Collection<?>) {
						oldInstances = (Collection<Instance>) second;
						sampleCount = oldInstances.size();
					}
				}

				Collection<Instance> newSamples = iss.getReferenceInstances();
				if (sampleCount == newSamples.size()) {
					// still to decide if update is necessary
					if (sampleCount == 0) {
						return;
					}

					// check if instances equal?
					Set<Instance> test = new HashSet<Instance>(oldInstances);
					test.removeAll(newSamples);
					if (test.isEmpty()) {
						return;
					}
				}

				TransformationView.this.update();
			}
		});
	}

	/**
	 * Set the current alignment
	 */
	private void update() {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		// TODO add configuration option if instances should be included?
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
				Alignment alignment = as.getAlignment();

				InstanceSampleService iss = PlatformUI.getWorkbench()
						.getService(InstanceSampleService.class);
				Collection<Instance> instances = iss.getReferenceInstances();
				if (instanceAction.isChecked()) {
					if (instances != null && !instances.isEmpty()) {
						instances = new ArrayList<Instance>(instances);
						// alignment paired with instances as input
						getViewer().setInput(new Pair<Object, Object>(alignment, instances));
					}
					else {
						getViewer().setInput(null);
					}
				}
				else {
					// only the alignment as input
					getViewer().setInput(alignment);
				}

				getViewer().applyLayout();
			}
		});
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (alignmentListener != null) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.removeListener(alignmentListener);
		}

		if (instanceSampleObserver != null) {
			InstanceSampleService iss = PlatformUI.getWorkbench()
					.getService(InstanceSampleService.class);
			iss.deleteObserver(instanceSampleObserver);
		}

		super.dispose();
	}

	/**
	 * @see AbstractMappingView#createLabelProvider(GraphViewer)
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider(GraphViewer viewer) {
		return new TransformationTreeLabelProvider(viewer, HaleUI.getServiceProvider());
	}

	/**
	 * @see AbstractMappingView#createContentProvider()
	 */
	@Override
	protected IContentProvider createContentProvider() {
		return new TransformationTreeContentProvider();
	}

	/**
	 * @see AbstractMappingView#createLayout()
	 */
	@Override
	protected LayoutAlgorithm createLayout() {
		return new TreeLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);
	}

	/**
	 * @see ViewPart#saveState(IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		memento.putBoolean(MEMENTO_KEY_INSTANCE_SAMPLE, instanceAction.isChecked());
	}

}
