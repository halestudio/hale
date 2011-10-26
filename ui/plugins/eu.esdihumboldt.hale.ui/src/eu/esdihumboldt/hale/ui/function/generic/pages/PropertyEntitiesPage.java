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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.function.generic.pages.internal.PropertyField;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;
import eu.esdihumboldt.util.Pair;

/**
 * Entity page for properties
 * @author Simon Templer
 */
public class PropertyEntitiesPage extends EntitiesPage<PropertyFunction, PropertyParameter, PropertyField> {
	
	private ComboViewer typeRelation;

	/**
	 * @see EntitiesPage#EntitiesPage(SchemaSelection, Cell)
	 */
	public PropertyEntitiesPage(SchemaSelection initialSelection,
			Cell initialCell) {
		super(initialSelection, initialCell);
	}

	/**
	 * @see EntitiesPage#createHeader(Composite)
	 */
	@Override
	protected Control createHeader(Composite parent) {
		//XXX what about augmentations?!
		
		Set<Pair<Type, Type>> relations = new HashSet<Pair<Type,Type>>();
		
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells();
		
		for (Cell cell : typeCells) {
			for (Entity source : cell.getSource().values()) {
				if (source instanceof Type) {
					for (Entity target : cell.getTarget().values()) {
						if (target instanceof Type) {
							relations.add(new Pair<Type, Type>((Type) source, 
									(Type) target));
						}
					}
				}
			}
		}
		
		if (relations.isEmpty()) {
			//XXX this may not happen, i.e. the wizard being created in the first place should be prevented
			throw new IllegalStateException("No compatible type relations defined"); 
		}
		
		typeRelation = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeRelation.setContentProvider(ArrayContentProvider.getInstance());
		typeRelation.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				@SuppressWarnings("unchecked")
				Pair<Type, Type> types = (Pair<Type, Type>) element;
				
				return types.getFirst().getDefinition().getDefinition().getDisplayName() + 
						" - " + types.getSecond().getDefinition().getDefinition().getDisplayName(); 
			}
		});
		typeRelation.setInput(relations);
		
		// set initial selection for relation
		//TODO based on initial cell/selection?
		typeRelation.setSelection(new StructuredSelection(relations.iterator().next()));
		
		typeRelation.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				for (PropertyField field : getFunctionFields()) {
					field.setParentType(getParentType(field.getSchemaSpace()));
				}
			}
		});
		
		return typeRelation.getControl();
	}

	/**
	 * @see EntitiesPage#createField(AbstractParameter, SchemaSpaceID, Composite, Set, Cell)
	 */
	@Override
	protected PropertyField createField(PropertyParameter field, SchemaSpaceID ssid,
			Composite parent, Set<EntityDefinition> candidates, Cell initialCell) {
		return new PropertyField(field, ssid, parent, candidates, initialCell, 
				getParentType(ssid));
	}

	/**
	 * Get the parent type set for the given schema space
	 * @param ssid the schema space identifier
	 * @return the parent type
	 */
	private TypeDefinition getParentType(SchemaSpaceID ssid) {
		ISelection selection = typeRelation.getSelection();
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			@SuppressWarnings("unchecked")
			Pair<Type, Type> relation = (Pair<Type, Type>) ((IStructuredSelection) selection).getFirstElement();
			switch (ssid) {
			case SOURCE:
				return relation.getFirst().getDefinition().getDefinition();
			case TARGET:
				return relation.getSecond().getDefinition().getDefinition();
			default:
				throw new IllegalArgumentException("Illegal schema space");
			}
		}
		
		return null;
	}

}
