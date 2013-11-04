/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.groovy;

import java.util.Collection;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider;
import eu.esdihumboldt.hale.ui.util.IColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovyColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovySourceViewerUtil;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Dialog tray displaying a type structure.
 * 
 * @author Simon Templer
 */
public class TypeStructureTray extends DialogTray {

	/**
	 * Retrieves a list of types.
	 */
	public interface TypeProvider {

		/**
		 * @return the collection of associated types
		 */
		public Collection<? extends TypeDefinition> getTypes();

	}

	/**
	 * Create a tool item for displaying the source or target type structure in
	 * the dialog tray.
	 * 
	 * @param bar the tool bar to add the item to
	 * @param page the associated wizard page
	 * @param types the provider for the types to display
	 * @param schemaSpace the schema space
	 */
	public static void createToolItem(ToolBar bar, final HaleWizardPage<?> page,
			final SchemaSpaceID schemaSpace, final TypeProvider types) {
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		switch (schemaSpace) {
		case SOURCE:
			item.setImage(CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_SOURCE_SCHEMA));
			item.setToolTipText("Show source structure");
			break;
		case TARGET:
			item.setImage(CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_TARGET_SCHEMA));
			item.setToolTipText("Show target structure");
			break;
		}
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (page.getContainer() instanceof TrayDialog) {
					TrayDialog dialog = (TrayDialog) page.getContainer();

					// close existing tray
					if (dialog.getTray() != null) {
						dialog.closeTray();
					}

					dialog.openTray(new TypeStructureTray(types, schemaSpace));
				}
				else {
					// TODO show dialog instead?
				}
			}
		});
	}

	private final TypeProvider types;
	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a type structure tray.
	 * 
	 * @param types the type provider
	 * @param schemaSpace the schema space
	 */
	public TypeStructureTray(TypeProvider types, SchemaSpaceID schemaSpace) {
		super();

		this.types = types;
		this.schemaSpace = schemaSpace;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(page);

		// retrieve the types
		final Collection<? extends TypeDefinition> types = this.types.getTypes();

		// heading
		Label caption = new Label(page, SWT.NONE);
		switch (schemaSpace) {
		case SOURCE:
			caption.setText("Source structure");
			break;
		case TARGET:
			caption.setText("Target structure");
			break;
		}
		caption.setFont(JFaceResources.getHeaderFont());

		// create tree viewer
		PatternFilter patternFilter = new SchemaPatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		final FilteredTree filteredTree = new TreePathFilteredTree(page, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER, patternFilter, true);

		TreeViewer tree = filteredTree.getViewer();
		tree.setUseHashlookup(true);
		StyledDefinitionLabelProvider labelProvider = new StyledDefinitionLabelProvider();
		tree.setLabelProvider(labelProvider);
		IContentProvider contentProvider;
		if (types.size() == 1) {
			contentProvider = new TreePathProviderAdapter(new TypePropertyContentProvider(tree));
		}
		else {
			contentProvider = new TreePathProviderAdapter(new TypeDefinitionContentProvider(tree));
		}
		tree.setContentProvider(contentProvider);
		GridDataFactory.fillDefaults().grab(true, true).hint(250, 400).applyTo(filteredTree);

		tree.setComparator(new DefinitionComparator());

		// set input
		if (types.size() == 1) {
			tree.setInput(types.iterator().next());
		}
		else {
			tree.setInput(types);
		}

		/*
		 * Groovy specific part
		 */

		// caption
		Label example = new Label(page, SWT.NONE);
		example.setText("Example");
		example.setFont(JFaceResources.getHeaderFont());

		// source viewer
		final SourceViewer viewer = new SourceViewer(page, null, SWT.MULTI | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);

		final IColorManager colorManager = new GroovyColorManager();
		SourceViewerConfiguration configuration = new SimpleGroovySourceViewerConfiguration(
				colorManager, ImmutableList.of(GroovyConstants.BINDING_TARGET,
						GroovyConstants.BINDING_BUILDER, GroovyConstants.BINDING_INDEX,
						GroovyConstants.BINDING_SOURCE));
		viewer.configure(configuration);

		GridDataFactory.fillDefaults().grab(true, false).hint(200, 130)
				.applyTo(viewer.getControl());

		// make sure the color manager is disposed
		viewer.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				colorManager.dispose();
			}
		});

		// react to tree selection changes
		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IDocument doc = new Document();
				GroovySourceViewerUtil.setupDocument(doc);
				String example = null;
				if (!event.getSelection().isEmpty()) {
					switch (schemaSpace) {
					case SOURCE:
						example = createSourceSample(event.getSelection(), types);
						break;
					case TARGET:
						example = createTargetSample(event.getSelection(), types);
						break;
					}
				}

				if (example == null || example.isEmpty()) {
					doc.set("// Please select a schema element");
				}
				else {
					doc.set(example);
				}

				viewer.setDocument(doc);
			}
		});

		tree.setSelection(StructuredSelection.EMPTY);

		return page;
	}

	/**
	 * Create sample code for accessing a source property.
	 * 
	 * @param selection the selection in the tree viewer
	 * @param types the types serving as input
	 * @return the sample code
	 */
	protected String createSourceSample(ISelection selection,
			Collection<? extends TypeDefinition> types) {
		ITreeSelection treeSel = (ITreeSelection) selection;

		TreePath[] paths = treeSel.getPaths();
		if (paths != null && paths.length > 0) {
			// XXX for now only use the first path

			TreePath path = paths[0];

			DefinitionGroup parent;
			int startIndex = 0;
			String prefix;

			// determine parent type
			if (path.getFirstSegment() instanceof TypeDefinition) {
				// types are the top level elements
//				parent = (DefinitionGroup) path.getFirstSegment();
//				startIndex = 1;
				// TODO (join)
				return null;
			}
			else {
				// types are not in the tree, single type must be root
				parent = types.iterator().next();
				// assuming Retype/Merge
				prefix = GroovyConstants.BINDING_SOURCE;
			}

			StringBuilder access = new StringBuilder();
			access.append(prefix);
			if (path.getSegmentCount() > startIndex) {
				// a property is referenced
				access.append(".p");
			}
			else {
				// XXX possible?
			}

			for (int i = startIndex; i < path.getSegmentCount(); i++) {
				Definition<?> def = (Definition<?>) path.getSegment(i);
				if (def instanceof PropertyDefinition) {
					// property name
					access.append('.');
					access.append(def.getName().getLocalPart());

					// test if uniquely accessible from parent
					boolean useNamespace = true;
					if (parent instanceof Definition<?>) {
						try {
							new DefinitionAccessor((Definition<?>) parent).findChildren(
									def.getName().getLocalPart()).eval();
							useNamespace = false;
						} catch (IllegalStateException e) {
							// ignore - namespace needed
						}
					}

					// add namespace if necessary
					if (useNamespace) {
						access.append("('");
						access.append(def.getName().getNamespaceURI());
						access.append("')");
					}
				}
				else {
					// groups are ignored
				}

				// set the new parent
				parent = DefinitionUtil.getDefinitionGroup(def);
			}

			if (parent instanceof TypeDefinition) {
				// only properties at the end of the path are supported
				TypeDefinition propertyType = (TypeDefinition) parent;
				StringBuilder example = new StringBuilder();

				boolean canOccurMultipleTimes = false;
				/*
				 * Instances/values may occur multiple times if any element in
				 * the path may occur multiple times.
				 */
				for (int i = path.getSegmentCount() - 1; i >= 0 && !canOccurMultipleTimes; i--) {
					if (path.getSegment(i) instanceof ChildDefinition<?>) {
						Cardinality card = DefinitionUtil.getCardinality((ChildDefinition<?>) path
								.getSegment(i));
						canOccurMultipleTimes = card.getMaxOccurs() == Cardinality.UNBOUNDED
								|| card.getMaxOccurs() > 1;
					}
				}

				// check different cases

				if (propertyType.getConstraint(HasValueFlag.class).isEnabled()) {
					// referenced property is a value or has a value

					// single value
					if (canOccurMultipleTimes) {
						example.append("// access first value\n");
					}
					else {
						example.append("// access value\n");
					}
					example.append("def value = ");
					example.append(access);
					example.append(".value()\n\n");

					// multiple values
					if (canOccurMultipleTimes) {
						example.append("// access all values as list\n");
						example.append("def valueList = ");
						example.append(access);
						example.append(".values()\n\n");
					}
				}

				if (DefinitionUtil.hasChildren(propertyType)) {
					// referenced property is an instance

					// single instance
					if (canOccurMultipleTimes) {
						example.append("// access first instance\n");
					}
					else {
						example.append("// access instance\n");
					}
					example.append("def instance = ");
					example.append(access);
					example.append(".first()\n\n");

					if (canOccurMultipleTimes) {
						// multiple values
						example.append("// access all instances as list\n");
						example.append("def instanceList = ");
						example.append(access);
						example.append(".all()\n\n");

						// iterate over instances
						example.append("// iterate over instances\n");
						example.append(access);
						example.append(".each {\n");
						example.append("\tinstance ->\n");
						example.append("}\n\n");
					}
				}
				else if (canOccurMultipleTimes
						&& propertyType.getConstraint(HasValueFlag.class).isEnabled()) {
					// iterate over values
					example.append("// iterate over values\n");
					example.append(access);
					example.append(".each {\n");
					example.append("\tvalue ->\n");
					example.append("}\n\n");
				}

				return example.toString();
			}
		}

		return null;
	}

	/**
	 * Create sample code for populating the target property.
	 * 
	 * @param selection the selection in the tree viewer
	 * @param types the types serving as input
	 * @return the sample code
	 */
	protected String createTargetSample(ISelection selection,
			Collection<? extends TypeDefinition> types) {
		ITreeSelection treeSel = (ITreeSelection) selection;

		TreePath[] paths = treeSel.getPaths();
		if (paths != null && paths.length > 0) {
			// XXX for now only use the first path

			TreePath path = paths[0];

			DefinitionGroup parent;
			int startIndex = 0;

			// determine parent type
			if (path.getFirstSegment() instanceof TypeDefinition) {
				// types are the top level elements
				parent = (DefinitionGroup) path.getFirstSegment();
				startIndex = 1;
			}
			else {
				// types are not in the tree, single type must be root
				parent = types.iterator().next();
			}

			StringBuilder example = new StringBuilder();
			example.append(GroovyConstants.BINDING_TARGET);
			example.append(" = {\n");

			int indentCount = 0;
			for (int i = startIndex; i < path.getSegmentCount(); i++) {
				Definition<?> def = (Definition<?>) path.getSegment(i);
				if (def instanceof PropertyDefinition) {
					String indent = createIndent(++indentCount);

					// property name
					example.append(indent);
					// TODO test if poperty must be accessed explicitly through
					// builder?
					example.append(def.getName().getLocalPart());

					// test if uniquely accessible from parent
					boolean useNamespace = true;
					if (parent instanceof Definition<?>) {
						try {
							new DefinitionAccessor((Definition<?>) parent).findChildren(
									def.getName().getLocalPart()).eval();
							useNamespace = false;
						} catch (IllegalStateException e) {
							// ignore - namespace needed
						}
					}

					boolean needComma = false;

					// add namespace if necessary
					if (useNamespace) {
						example.append(" namespace: '");
						example.append(def.getName().getNamespaceURI());
						example.append('\'');
						needComma = true;
					}

					TypeDefinition propertyType = ((PropertyDefinition) def).getPropertyType();
					if (propertyType.getConstraint(HasValueFlag.class).isEnabled()) {
						// add an example value
						if (needComma) {
							example.append(',');
						}
						example.append(' ');
						switch (Classification.getClassification(def)) {
						case NUMERIC_PROPERTY:
							example.append("42");
							break;
						case STRING_PROPERTY:
							example.append("'some value'");
							break;
						default:
							example.append("some_value");
						}

						needComma = true;
					}

					if (DefinitionUtil.hasChildren(propertyType)) {
						if (needComma) {
							example.append(',');
						}
						example.append(" {");
						example.append('\n');
					}
					else {
						example.append('\n');
						// no brackets to close here
						indentCount--;
						break;
					}
				}
				else {
					// groups are ignored
				}

				// set the new parent
				parent = DefinitionUtil.getDefinitionGroup(def);
			}

			// close brackets
			for (int i = indentCount; i > 0; i--) {
				example.append(createIndent(i));
				example.append('}');
				example.append('\n');
			}

			example.append("}");

			return example.toString();
		}

		return GroovyConstants.BINDING_TARGET + " = {}";
	}

	private String createIndent(int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			builder.append('\t');
		}
		return builder.toString();
	}
}
