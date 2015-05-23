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

package eu.esdihumboldt.hale.ui.function.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardContainer;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionContentProvider;
import eu.esdihumboldt.hale.ui.function.contribution.SchemaSelectionFunctionMatcher;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Function content provider that wraps {@link AbstractFunction} in
 * {@link FunctionWizardNode}s.
 * 
 * @author Simon Templer
 */
public class FunctionWizardNodeContentProvider extends FunctionContentProvider {

	private final IWizardContainer container;

	private final SchemaSelection initialSelection;

	private final SchemaSelectionFunctionMatcher selectionMatcher;

	/**
	 * Create a new content provider
	 * 
	 * @param container the wizard container
	 * @param initialSelection the initial selection to initialize the wizard
	 *            with, may be <code>null</code> to start with an empty
	 *            configuration
	 * @param selectionMatcher the matcher that determines if a function is
	 *            applicable for the initial selection, may be <code>null</code>
	 *            to allow all functions
	 */
	public FunctionWizardNodeContentProvider(IWizardContainer container,
			SchemaSelection initialSelection, SchemaSelectionFunctionMatcher selectionMatcher) {
		super(HaleUI.getServiceProvider());
		this.container = container;
		this.initialSelection = initialSelection;
		this.selectionMatcher = selectionMatcher;
	}

	/**
	 * @see FunctionContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return toNodes(super.getElements(inputElement));
	}

	/**
	 * @see FunctionContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return toNodes(super.getChildren(parentElement));
	}

	/**
	 * @see FunctionContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof FunctionWizardNode) {
			element = ((FunctionWizardNode) element).getFunction();
		}

		return super.getParent(element);
	}

	@Override
	public boolean apply(FunctionDefinition<?> function) {
		if (initialSelection != null && selectionMatcher != null) {
			// use selection matcher to determine if function should be
			// displayed
			if (!selectionMatcher.matchFunction(function, initialSelection)) {
				return false;
			}
		}

		return true;
	}

	private Object[] toNodes(Object[] children) {
		if (children == null || children.length == 0) {
			return children;
		}

		List<Object> result = new ArrayList<Object>(children.length);

		for (Object child : children) {
			if (child instanceof FunctionDefinition<?>) {
				child = new FunctionWizardNode((FunctionDefinition<?>) child, container,
						initialSelection);
			}

			result.add(child);
		}
		return result.toArray();
	}

}
