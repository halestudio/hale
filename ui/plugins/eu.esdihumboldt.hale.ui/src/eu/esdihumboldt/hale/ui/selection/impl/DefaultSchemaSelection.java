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
package eu.esdihumboldt.hale.ui.selection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * A selection with source and target {@link EntityDefinition}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefaultSchemaSelection implements IStructuredSelection, SchemaSelection {

	private final Set<EntityDefinition> sourceItems = new LinkedHashSet<EntityDefinition>();

	private final Set<EntityDefinition> targetItems = new LinkedHashSet<EntityDefinition>();

	private final SchemaStructuredMode mode;

	/**
	 * Defines modes specifying the behavior of the selection as
	 * {@link IStructuredSelection}
	 */
	public enum SchemaStructuredMode {
		/** Only source items are returned */
		ONLY_SOURCE,
		/** Only target items are returned */
		ONLY_TARGET,
		/** All items are returned */
		ALL
	}

	/**
	 * Creates an empty selection
	 */
	public DefaultSchemaSelection() {
		this(null, null, SchemaStructuredMode.ALL);
	}

	/**
	 * Creates a selection that is initialized with the given items
	 * 
	 * @param sourceItems the source items
	 * @param targetItems the target items
	 * @param mode the selection structured mode
	 */
	public DefaultSchemaSelection(final Collection<EntityDefinition> sourceItems,
			final Collection<EntityDefinition> targetItems, SchemaStructuredMode mode) {
		super();

		this.mode = mode;

		if (sourceItems != null) {
			this.sourceItems.addAll(sourceItems);
		}

		if (targetItems != null) {
			this.targetItems.addAll(targetItems);
		}
	}

	/**
	 * Adds a source item
	 * 
	 * @param item the item to add
	 */
	public void addSourceItem(EntityDefinition item) {
		sourceItems.add(item);
	}

	/**
	 * Adds a target item
	 * 
	 * @param item the item to add
	 */
	public void addTargetItem(EntityDefinition item) {
		targetItems.add(item);
	}

	/**
	 * @see SchemaSelection#getSourceItems()
	 */
	@Override
	public Set<EntityDefinition> getSourceItems() {
		return Collections.unmodifiableSet(sourceItems);
	}

	/**
	 * @see SchemaSelection#getTargetItems()
	 */
	@Override
	public Set<EntityDefinition> getTargetItems() {
		return Collections.unmodifiableSet(targetItems);
	}

	/**
	 * @see SchemaSelection#getFirstSourceItem()
	 */
	@Override
	public EntityDefinition getFirstSourceItem() {
		return getFirstItem(SchemaSpaceID.SOURCE);
	}

	/**
	 * @see SchemaSelection#getFirstTargetItem()
	 */
	@Override
	public EntityDefinition getFirstTargetItem() {
		return getFirstItem(SchemaSpaceID.TARGET);
	}

	/**
	 * @see SchemaSelection#getFirstItem(SchemaSpaceID)
	 */
	@Override
	public EntityDefinition getFirstItem(SchemaSpaceID schema) {
		try {
			switch (schema) {
			case SOURCE:
				return sourceItems.iterator().next();
			case TARGET:
				return targetItems.iterator().next();
			default:
				return null;
			}
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * @see IStructuredSelection#getFirstElement()
	 */
	@Override
	public Object getFirstElement() {
		if (!sourceItems.isEmpty() && !mode.equals(SchemaStructuredMode.ONLY_TARGET)) {
			return getFirstSourceItem();
		}
		else {
			return getFirstTargetItem();
		}
	}

	/**
	 * @see IStructuredSelection#iterator()
	 */
	@Override
	public Iterator<?> iterator() {
		return toList().iterator();
	}

	/**
	 * @see IStructuredSelection#size()
	 */
	@Override
	public int size() {
		switch (mode) {
		case ONLY_TARGET:
			return targetItems.size();
		case ONLY_SOURCE:
			return sourceItems.size();
		case ALL:
		default:
			return sourceItems.size() + targetItems.size();
		}
	}

	/**
	 * @see IStructuredSelection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return toList().toArray();
	}

	/**
	 * @see IStructuredSelection#toList()
	 */
	@Override
	public List<EntityDefinition> toList() {
		List<EntityDefinition> list = new ArrayList<EntityDefinition>(sourceItems.size()
				+ targetItems.size());
		switch (mode) {
		case ONLY_TARGET:
			list.addAll(targetItems);
			break;
		case ONLY_SOURCE:
			list.addAll(sourceItems);
			break;
		case ALL:
			list.addAll(sourceItems);
			list.addAll(targetItems);
		}
		return list;
	}

	/**
	 * @see ISelection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return sourceItems.isEmpty() && targetItems.isEmpty();
	}

}
