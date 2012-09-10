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

import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

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

			IEvaluationService ies = (IEvaluationService) PlatformUI.getWorkbench().getService(
					IEvaluationService.class);
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
