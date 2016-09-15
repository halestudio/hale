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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.function.common.SourceTargetTypeSelector;
import eu.esdihumboldt.hale.ui.function.generic.pages.internal.PropertyField;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Entity page for properties
 * 
 * @author Simon Templer
 */
public class PropertyEntitiesPage extends
		EntitiesPage<PropertyFunctionDefinition, PropertyParameterDefinition, PropertyField> {

//	private ComboViewer typeRelation;
	private SourceTargetTypeSelector sourceTargetSelector;

	/**
	 * @see EntitiesPage#EntitiesPage(SchemaSelection, Cell)
	 */
	public PropertyEntitiesPage(SchemaSelection initialSelection, Cell initialCell) {
		super(initialSelection, initialCell);
	}

	/**
	 * @see EntitiesPage#createHeader(Composite)
	 */
	@Override
	protected Control createHeader(Composite parent) {
		Group typeSelectionGroup = new Group(parent, SWT.NONE);
		typeSelectionGroup.setText("Type");
		typeSelectionGroup.setLayout(new GridLayout());

		sourceTargetSelector = new SourceTargetTypeSelector(typeSelectionGroup);
		sourceTargetSelector.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// set initial selection
		sourceTargetSelector.setSelection(getInitialTypeSelection(SchemaSpaceID.SOURCE),
				SchemaSpaceID.SOURCE);
		sourceTargetSelector.setSelection(getInitialTypeSelection(SchemaSpaceID.TARGET),
				SchemaSpaceID.TARGET);

		// add selection listener
		sourceTargetSelector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TypeEntityDefinition selectedType = sourceTargetSelector
						.getSelection(SchemaSpaceID.SOURCE);
				for (PropertyField field : getFunctionFields())
					if (field.getSchemaSpace() == SchemaSpaceID.SOURCE)
						field.setParentType(selectedType);
			}
		}, SchemaSpaceID.SOURCE);

		sourceTargetSelector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TypeEntityDefinition selectedType = sourceTargetSelector
						.getSelection(SchemaSpaceID.TARGET);
				for (PropertyField field : getFunctionFields())
					if (field.getSchemaSpace() == SchemaSpaceID.TARGET)
						field.setParentType(selectedType);
			}
		}, SchemaSpaceID.TARGET);

//		Set<Pair<Type, Type>> relations = new HashSet<Pair<Type, Type>>();
//
//		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
//				AlignmentService.class);
//		Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells();
//
//		for (Cell cell : typeCells) {
//			for (Entity source : cell.getSource().values()) {
//				if (source instanceof Type) {
//					for (Entity target : cell.getTarget().values()) {
//						if (target instanceof Type) {
//							relations.add(new Pair<Type, Type>((Type) source, (Type) target));
//						}
//					}
//				}
//			}
//		}
//
//		if (relations.isEmpty()) {
//			// XXX this may not happen, i.e. the wizard being created in the
//			// first place should be prevented
//			throw new IllegalStateException("No compatible type relations defined");
//		}
//
//		typeRelation = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
//		typeRelation.setContentProvider(ArrayContentProvider.getInstance());
//		typeRelation.setLabelProvider(new LabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				@SuppressWarnings("unchecked")
//				Pair<Type, Type> types = (Pair<Type, Type>) element;
//
//				return types.getFirst().getDefinition().getDefinition().getDisplayName() + " - "
//						+ types.getSecond().getDefinition().getDefinition().getDisplayName();
//			}
//		});
//		typeRelation.setInput(relations);
//
//		// set initial selection for relation
//		Pair<Type, Type> selection = determineDefaultRelation(relations);
//		typeRelation.setSelection(new StructuredSelection(selection));
//
//		typeRelation.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				for (PropertyField field : getFunctionFields()) {
//					field.setParentType(getParentType(field.getSchemaSpace()));
//				}
//			}
//		});

		return typeSelectionGroup;
	}

	/**
	 * Returns the initial selection for the type selection based on the initial
	 * property selection.
	 * 
	 * @param ssid the schema space to get the initial selection for
	 * @return the initial selection
	 */
	private TypeEntityDefinition getInitialTypeSelection(SchemaSpaceID ssid) {
		Set<EntityDefinition> candidates = getCandidates(ssid);
		if (!candidates.isEmpty()) {
			TypeEntityDefinition initialSelection = AlignmentUtil.getTypeEntity(candidates
					.iterator().next());
			for (EntityDefinition def : candidates)
				if (!AlignmentUtil.getTypeEntity(def).equals(initialSelection))
					return null;
			return initialSelection;
		}
		else
			return null;
	}

//	/**
//	 * Determine the relation to be selected by default.
//	 * 
//	 * @param relations the available relations
//	 * @return the relation to be selected
//	 */
//	private Pair<Type, Type> determineDefaultRelation(Set<Pair<Type, Type>> relations) {
//		Pair<Type, Type> relation = null;
//		// based on initial cell/selection if possible
//		if (getInitialCell() != null) {
//			Type target = new DefaultType(AlignmentUtil.getTypeEntity(getInitialCell().getTarget()
//					.values().iterator().next().getDefinition()));
//
//			if (getInitialCell().getSource() != null && !getInitialCell().getSource().isEmpty()) {
//				Type source = new DefaultType(AlignmentUtil.getTypeEntity(getInitialCell()
//						.getSource().values().iterator().next().getDefinition()));
//				relation = new Pair<Type, Type>(source, target);
//			}
//			else {
//				// augmentation, find any relation to target type
//				relation = findFirstRelation(relations, null, target);
//			}
//		}
//		else if (getInitialSelection() != null) {
//			SchemaSelection sel = getInitialSelection();
//			if (sel.getFirstSourceItem() != null && sel.getFirstTargetItem() != null) {
//				Type source = new DefaultType(AlignmentUtil.getTypeEntity(sel.getFirstSourceItem()));
//				Type target = new DefaultType(AlignmentUtil.getTypeEntity(sel.getFirstTargetItem()));
//				relation = new Pair<Type, Type>(source, target);
//			}
//			else if (sel.getFirstSourceItem() != null) {
//				Type source = new DefaultType(AlignmentUtil.getTypeEntity(sel.getFirstSourceItem()));
//				relation = findFirstRelation(relations, source, null);
//			}
//			else if (sel.getFirstTargetItem() != null) {
//				Type target = new DefaultType(AlignmentUtil.getTypeEntity(sel.getFirstTargetItem()));
//				relation = findFirstRelation(relations, null, target);
//			}
//		}
//
//		if (relation == null) {
//			return relations.iterator().next();
//		}
//		else {
//			return relation;
//		}
//	}

//	/**
//	 * Find the first relation matching the given source and target type.
//	 * 
//	 * @param relations the relations to test
//	 * @param source the source type, <code>null</code> for matching any type
//	 * @param target the target type, <code>null</code> for matching any type
//	 * @return the relation found or <code>null</code> if there is none
//	 */
//	private Pair<Type, Type> findFirstRelation(Set<Pair<Type, Type>> relations, Type source,
//			Type target) {
//		for (Pair<Type, Type> relation : relations) {
//			if ((source == null || source.equals(relation.getFirst()))
//					&& (target == null || target.equals(relation.getSecond()))) {
//				return relation;
//			}
//		}
//
//		return null;
//	}

	@Override
	protected PropertyField createField(PropertyParameterDefinition field, SchemaSpaceID ssid,
			Composite parent, Set<EntityDefinition> candidates, Cell initialCell) {
		return new PropertyField(field, ssid, parent, candidates, initialCell, getParentType(ssid));
	}

	@Override
	protected boolean acceptCandidate(EntityDefinition candidate) {
		// don't accept types as candidates
		return !candidate.getPropertyPath().isEmpty();
	}

	/**
	 * Get the parent type set for the given schema space
	 * 
	 * @param ssid the schema space identifier
	 * @return the parent type
	 */
	private TypeEntityDefinition getParentType(SchemaSpaceID ssid) {
		switch (ssid) {
		case SOURCE:
			return sourceTargetSelector.getSelection(SchemaSpaceID.SOURCE);
		case TARGET:
			return sourceTargetSelector.getSelection(SchemaSpaceID.TARGET);
		default:
			throw new IllegalArgumentException("Illegal schema space");
		}
	}

}
