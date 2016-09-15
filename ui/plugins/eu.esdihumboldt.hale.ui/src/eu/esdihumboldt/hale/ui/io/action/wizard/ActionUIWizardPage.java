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

package eu.esdihumboldt.hale.ui.io.action.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.io.action.ActionUIExtension;
import eu.esdihumboldt.hale.ui.util.wizard.ViewerWizardSelectionPage;
import eu.esdihumboldt.util.Pair;

/**
 * Wizard selection page based on {@link ActionUI}s
 * 
 * @author Simon Templer
 */
public class ActionUIWizardPage extends ViewerWizardSelectionPage {

	private static final ALogger log = ALoggerFactory.getLogger(ActionUIWizardPage.class);

	private final FactoryFilter<IOWizard<?>, ActionUI> filter;

	/**
	 * Create a page that allows selection of an {@link ActionUI} wizard
	 * 
	 * @param filter the filter to apply to the action UI extension
	 * @param title the page title
	 */
	public ActionUIWizardPage(FactoryFilter<IOWizard<?>, ActionUI> filter, String title) {
		super("actionSelect");
		this.filter = filter;

		setTitle(title);
	}

	/**
	 * @see ViewerWizardSelectionPage#createViewer(Composite)
	 */
	@Override
	protected Pair<StructuredViewer, Control> createViewer(Composite parent) {
		ListViewer viewer = new ListViewer(parent);

		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof ActionUIWizardNode) {
					return ((ActionUIWizardNode) element).getImage();
				}

				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof ActionUIWizardNode) {
					return ((ActionUIWizardNode) element).getActionUI().getDisplayName();
				}

				return super.getText(element);
			}

		});
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		List<ActionUI> list = ActionUIExtension.getInstance().getFactories(filter);

		List<ActionUIWizardNode> nodes = new ArrayList<ActionUIWizardNode>();
		for (ActionUI action : list) {
			nodes.add(new ActionUIWizardNode(action, getContainer()));
		}
		viewer.setInput(nodes);

		return new Pair<StructuredViewer, Control>(viewer, viewer.getControl());
	}

	/**
	 * @see ViewerWizardSelectionPage#acceptWizard(IWizardNode)
	 */
	@Override
	protected String acceptWizard(IWizardNode wizardNode) {
		if (wizardNode instanceof ActionUIWizardNode) {
			ActionUI actionUI = ((ActionUIWizardNode) wizardNode).getActionUI();
			Expression enabledWhen = actionUI.getEnabledWhen();
			if (enabledWhen == null) {
				return null;
			}

			IEvaluationService ies = PlatformUI.getWorkbench().getService(IEvaluationService.class);
			try {
				EvaluationResult evalResult = enabledWhen.evaluate(ies.getCurrentState());
				if (evalResult == EvaluationResult.FALSE) {
					// disabled
					return actionUI.getDisabledReason();
				}
				// enabled
				return null;
			} catch (CoreException e) {
				String message = "Could not evaluate enabledWhen expression";
				log.error(message, e);
				return message;
			}
		}

		return super.acceptWizard(wizardNode);
	}

}
