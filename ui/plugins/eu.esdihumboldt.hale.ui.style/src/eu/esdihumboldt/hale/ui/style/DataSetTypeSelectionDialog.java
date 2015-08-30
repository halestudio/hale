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

package eu.esdihumboldt.hale.ui.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;
import eu.esdihumboldt.util.Pair;

/**
 * Selection dialog for {@link TypeDefinition}s.
 * 
 * @author Simon Templer
 */
public class DataSetTypeSelectionDialog extends
		AbstractViewerSelectionDialog<Pair<TypeDefinition, DataSet>, TreeViewer> {

	/**
	 * The types, either a {@link TypeIndex} or an {@link Iterable} of
	 * {@link TypeDefinition}s.
	 */
	private final Multimap<DataSet, TypeDefinition> types;

	/**
	 * Create a type definition selection dialog.
	 * 
	 * @param parentShell the parent shell
	 * @param title the dialog title
	 * @param initialSelection the initial selection
	 * @param types the type index
	 */
	public DataSetTypeSelectionDialog(Shell parentShell, String title,
			Pair<TypeDefinition, DataSet> initialSelection, Multimap<DataSet, TypeDefinition> types) {
		super(parentShell, title, initialSelection);

		this.types = types;

		setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return !(element instanceof DataSet);
			}
		} });
	}

	@Override
	protected TreeViewer createViewer(Composite parent) {
		// create viewer
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, patternFilter, true);
		tree.getViewer().setComparator(new DefinitionComparator());
		return tree.getViewer();
	}

	@Override
	protected void setupViewer(TreeViewer viewer, Pair<TypeDefinition, DataSet> initialSelection) {
		viewer.setLabelProvider(new DefinitionLabelProvider(viewer) {

			@Override
			public String getText(Object element) {
				if (element instanceof DataSet) {
					switch ((DataSet) element) {
					case TRANSFORMED:
						return "Transformed";
					case SOURCE:
					default:
						return "Source";
					}
				}

				if (element instanceof Pair) {
					element = ((Pair<?, ?>) element).getFirst();
				}

				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof DataSet) {
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(ISharedImages.IMG_OBJ_FOLDER);
				}

				if (element instanceof Pair) {
					element = ((Pair<?, ?>) element).getFirst();
				}

				return super.getImage(element);
			}

		});
		viewer.setContentProvider(new TypeIndexContentProvider(viewer) {

			@Override
			public Object[] getElements(Object inputElement) {
				return types.keySet().toArray();
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof DataSet) {
					DataSet dataSet = (DataSet) parentElement;
					List<Pair<TypeDefinition, DataSet>> typeList = new ArrayList<Pair<TypeDefinition, DataSet>>();
					for (TypeDefinition type : types.get(dataSet)) {
						typeList.add(new Pair<TypeDefinition, DataSet>(type, dataSet));
					}
					return typeList.toArray();
				}

				return new Object[] {};
			}

			@Override
			public boolean hasChildren(Object parentElement) {
				return parentElement instanceof DataSet
						&& !types.get((DataSet) parentElement).isEmpty();
			}

		});

		viewer.setAutoExpandLevel(2);
		viewer.setInput(types);

		if (initialSelection != null) {
			viewer.setSelection(new StructuredSelection(initialSelection));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Pair<TypeDefinition, DataSet> getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof Pair) {
				return (Pair<TypeDefinition, DataSet>) element;
			}
		}

		return null;
	}

}
