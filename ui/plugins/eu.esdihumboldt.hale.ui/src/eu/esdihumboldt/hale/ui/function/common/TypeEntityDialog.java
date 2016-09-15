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

package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.ContentProviderAction;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexHierarchy;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.viewer.FilterAction;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Dialog for selecting a {@link TypeEntityDefinition}.
 * 
 * @author Simon Templer
 */
public class TypeEntityDialog extends EntityDialog {

	private final boolean onlyMappingRelevant;
	private TreePathProviderAdapter flatRelevantProvider;
	private TreePathProviderAdapter hierarchicalRelevantProvider;
	private TreePathProviderAdapter flatAllProvider;
	private TreePathProviderAdapter hierarchicalAllProvider;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param ssid the schema space
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *            possible), may be <code>null</code>
	 * @param onlyMappingRelevant whether to only show mapping relevant types
	 */
	public TypeEntityDialog(Shell parentShell, SchemaSpaceID ssid, String title,
			EntityDefinition initialSelection, boolean onlyMappingRelevant) {
		super(parentShell, ssid, title, initialSelection);
		this.onlyMappingRelevant = onlyMappingRelevant;
	}

	/**
	 * @see EntityDialog#setupViewer(TreeViewer, EntityDefinition)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer, EntityDefinition initialSelection) {
		viewer.setLabelProvider(new StyledDefinitionLabelProvider(viewer));
		EntityDefinitionService entityDefinitionService = PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		flatRelevantProvider = new TreePathProviderAdapter(
				new EntityTypeIndexContentProvider(entityDefinitionService, ssid, true, true));
		if (!onlyMappingRelevant) {
			hierarchicalRelevantProvider = new TreePathProviderAdapter(
					new EntityTypeIndexHierarchy(entityDefinitionService, ssid, true, true));
			flatAllProvider = new TreePathProviderAdapter(
					new EntityTypeIndexContentProvider(entityDefinitionService, ssid, false, true));
			hierarchicalAllProvider = new TreePathProviderAdapter(
					new EntityTypeIndexHierarchy(entityDefinitionService, ssid, false, true));

			viewer.setContentProvider(flatAllProvider);
		}
		else {
			viewer.setContentProvider(flatRelevantProvider);
		}

		SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);

		viewer.setInput(ss.getSchemas(ssid));

		if (initialSelection instanceof TypeEntityDefinition) {
			viewer.setSelection(new StructuredSelection(initialSelection));
		}
	}

	/**
	 * @see EntityDialog#getObjectFromSelection(ISelection)
	 */
	@Override
	protected EntityDefinition getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof TypeEntityDefinition) {
				return (EntityDefinition) element;
			}
			if (element instanceof TypeDefinition) {
				return new TypeEntityDefinition((TypeDefinition) element, ssid, null);
			}
		}

		return null;
	}

	/**
	 * @see EntityDialog#getObject()
	 */
	@Override
	public TypeEntityDefinition getObject() {
		return (TypeEntityDefinition) super.getObject();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.common.EntityDialog#addToolBarActions(org.eclipse.jface.action.ToolBarManager)
	 */
	@Override
	protected void addToolBarActions(ToolBarManager manager) {
		// filter to only show mapped types
		manager.add(new FilterAction("Hide unmapped types", "Show unmapped types",
				HALEUIPlugin.getImageDescriptor("icons/empty.gif"), getViewer(),
				new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						AlignmentService as = PlatformUI.getWorkbench()
								.getService(AlignmentService.class);
						Alignment alignment = as.getAlignment();
						if (element instanceof TreePath)
							element = ((TreePath) element).getLastSegment();
						return isMapped(
								(ITreeContentProvider) ((TreeViewer) viewer).getContentProvider(),
								element, alignment);
					}

					private boolean isMapped(ITreeContentProvider cp, Object element,
							Alignment align) {
						if (element instanceof EntityDefinition) {
							boolean mapped = AlignmentUtil
									.entityOrChildMapped((EntityDefinition) element, align);
							if (mapped)
								return true;
						}
						// recursively check children
						Object[] children = cp.getChildren(element);
						if (children != null)
							for (Object child : children)
								if (isMapped(cp, child, align))
									return true;
						return false;
					}
				}, true, true));

		// do not add choice if only mapping relevant types should be selected
		if (onlyMappingRelevant)
			return;

		manager.add(new Separator());

		// MappingRelevant types only, flat
		manager.add(new ContentProviderAction("Mapping relevant types as list",
				HALEUIPlugin.getImageDescriptor("icons/flat_relevant.png"), getViewer(),
				flatRelevantProvider, false));
		// MappingRelevant types only, hierarchical
		manager.add(new ContentProviderAction("Mapping relevant types hierarchical",
				HALEUIPlugin.getImageDescriptor("icons/hierarchical_relevant.png"), getViewer(),
				hierarchicalRelevantProvider, false));
		// Mappable types, flat
		manager.add(new ContentProviderAction("All types as list",
				HALEUIPlugin.getImageDescriptor("icons/flat_all.png"), getViewer(), flatAllProvider,
				true));
		// Mappable types, hierarchical
		manager.add(new ContentProviderAction("All types hierarchical",
				HALEUIPlugin.getImageDescriptor("icons/hierarchical_all.png"), getViewer(),
				hierarchicalAllProvider, false));
	}
}
