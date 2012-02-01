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

package eu.esdihumboldt.hale.ui.geometry.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.namespace.QName;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaServiceListener;
import eu.esdihumboldt.util.Pair;

/**
 * Abstract geometry schema service implementation.
 * @author Simon Templer
 */
public abstract class AbstractGeometrySchemaService implements GeometrySchemaService {

	private final TypeSafeListenerList<GeometrySchemaServiceListener> listeners = new TypeSafeListenerList<GeometrySchemaServiceListener>();
	
	/**
	 * @see GeometrySchemaService#getDefaultGeometry(TypeDefinition)
	 */
	@Override
	public List<QName> getDefaultGeometry(TypeDefinition type) {
		List<QName> path = loadDefaultGeometry(type);
		
		if (path == null) {
			path = determineDefaultGeometry(type);
			saveDefaultGeometry(type, path);
		}
		
		return path;
	}

	/**
	 * @see GeometrySchemaService#setDefaultGeometry(TypeDefinition, List)
	 */
	@Override
	public void setDefaultGeometry(TypeDefinition type, List<QName> path) {
		saveDefaultGeometry(type, path);
		notifyDefaultGeometryChanged(type);
	}

	/**
	 * Determine the path to a geometry property to be used as default geometry
	 * for the given type. By default the first geometry property found with
	 * a breadth first search is used.
	 * @param type the type definition
	 * @return the path to the default geometry property or <code>null</code>
	 *   if unknown
	 */
	protected List<QName> determineDefaultGeometry(TypeDefinition type) {
		// breadth first search for geometry properties
		Queue<Pair<List<QName>, DefinitionGroup>> groups = new LinkedList<Pair<List<QName>,DefinitionGroup>>();
		groups.add(new Pair<List<QName>, DefinitionGroup>(new ArrayList<QName>(), type));
		
		List<QName> firstGeometryPath = null;
		while (firstGeometryPath == null && !groups.isEmpty()) {
			// for each parent group...
			Pair<List<QName>, DefinitionGroup> group = groups.poll();
			DefinitionGroup parent = group.getSecond();
			List<QName> parentPath = group.getFirst();

			// max depth for default geometries
			if (parentPath.size() > 5)
				continue;
			
			// check properties if they are geometry properties, add groups to queue
			for (ChildDefinition<?> child : DefinitionUtil.getAllChildren(parent)) {
				// path for child
				List<QName> path = new ArrayList<QName>(parentPath);
				path.add(child.getName());
				
				if (child.asProperty() != null) {
					PropertyDefinition property = child.asProperty();
					TypeDefinition propertyType = property.getPropertyType();
					
					// check if we found a geometry property
					if (propertyType.getConstraint(GeometryType.class).isGeometry()) {
						// match
						firstGeometryPath = path;
					}
					else {
						// test children later on
						groups.add(new Pair<List<QName>, DefinitionGroup>(path, propertyType));
					}
				}
				else if (child.asGroup() != null) {
					// test group later on
					GroupPropertyDefinition childGroup = child.asGroup();
					groups.add(new Pair<List<QName>, DefinitionGroup>(path, childGroup));
				}
				else {
					throw new IllegalStateException("Invalid child definition encountered");
				}
			}
		}
		
		if (firstGeometryPath != null) {
			// a geometry property was found
			return generalizeGeometryProperty(type, firstGeometryPath);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Generalize the path to the geometry property for the given type. This 
	 * serves to prevent focusing on a single geometry property in a choice. 
	 * @param type the type definition
	 * @param geometryPath the geometry path
	 * @return the generalized geometry path
	 */
	private List<QName> generalizeGeometryProperty(TypeDefinition type,
			List<QName> geometryPath) {
		// collect child definitions associated to path names
		List<ChildDefinition<?>> pathChildren = new ArrayList<ChildDefinition<?>>();
		DefinitionGroup parent = type;
		for (QName name : geometryPath) {
			ChildDefinition<?> child = parent.getChild(name);
			if (child == null) {
				// invalid path
				break;
			}
			
			pathChildren.add(child);
			if (child.asProperty() != null) {
				parent = child.asProperty().getPropertyType();
			}
			else if (child.asGroup() != null) {
				parent = child.asGroup();
			}
			else {
				throw new IllegalStateException("Invalid child definition");
			}
		}
		
		// traverse the list in reverse order, peeking at the previous item
		// remove geometry properties parented by a choice
		for (int i = pathChildren.size() - 1; i > 0; i--) {
			// peek at the previous item
			ChildDefinition<?> previous = pathChildren.get(i - 1);
			if (previous.asGroup() != null 
					&& previous.asGroup().getConstraint(ChoiceFlag.class).isEnabled()) {
				// previous item is a choice:
				// delete the current item
				pathChildren.remove(i);
				// and continue
			}
			else {
				// don't continue if the parent is not a choice
				//XXX should it be reduced further if there are more choices along the path?
				//XXX then we could use another approach
				//XXX namely finding the first choice in the path and removing everything after it
				break;
			}
		}
		
		// create a name list from the child list
		List<QName> names = new ArrayList<QName>(pathChildren.size());
		for (ChildDefinition<?> child : pathChildren) {
			names.add(child.getName());
		}
		
		return names;
	}

	/**
	 * Load the path of the default geometry for the given type.
	 * @param type the type definition
	 * @return the path to the default geometry property or <code>null</code>
	 *   if unknown
	 */
	protected abstract List<QName> loadDefaultGeometry(TypeDefinition type);
	
	/**
	 * Save the association of the given property path as the default geometry
	 * of the given type.
	 * @param type the type definition
	 * @param path the property path
	 */
	protected abstract void saveDefaultGeometry(TypeDefinition type, List<QName> path);
	
	/**
	 * Notifies the listeners that the default geometry for the given type
	 * has changed.
	 * @param type the type definition
	 */
	protected void notifyDefaultGeometryChanged(TypeDefinition type) {
		for (GeometrySchemaServiceListener listener : listeners) {
			listener.defaultGeometryChanged(type);
		}
	}

	/**
	 * @see GeometrySchemaService#addListener(GeometrySchemaServiceListener)
	 */
	@Override
	public void addListener(GeometrySchemaServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see GeometrySchemaService#removeListener(GeometrySchemaServiceListener)
	 */
	@Override
	public void removeListener(GeometrySchemaServiceListener listener) {
		listeners.remove(listener);
	}

}
