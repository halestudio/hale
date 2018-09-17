/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.deegree.mapping.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.deegree.commons.tom.gml.GMLObjectCategory;
import org.deegree.commons.tom.gml.GMLObjectType;
import org.deegree.commons.tom.gml.property.PropertyType;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.AppSchemaGeometryHierarchy;
import org.deegree.feature.types.FeatureType;
import org.deegree.gml.schema.GMLSchemaInfoSet;

/**
 * Decorator for {@link AppSchema} interface.
 * 
 * @author Simon Templer
 */
public class AppSchemaDecorator implements AppSchema {

	private final AppSchema decoratee;

	/**
	 * Create a new decorator.
	 * 
	 * @param decoratee the decoratee
	 */
	public AppSchemaDecorator(AppSchema decoratee) {
		super();
		this.decoratee = decoratee;
	}

	@Override
	public FeatureType getFeatureType(QName ftName) {
		return decoratee.getFeatureType(ftName);
	}

	@Override
	public FeatureType[] getFeatureTypes() {
		return decoratee.getFeatureTypes();
	}

	@Override
	public List<FeatureType> getFeatureTypes(String namespace, boolean includeCollections,
			boolean includeAbstracts) {
		return decoratee.getFeatureTypes(namespace, includeCollections, includeAbstracts);
	}

	@Override
	public FeatureType[] getRootFeatureTypes() {
		return decoratee.getRootFeatureTypes();
	}

	@Override
	public FeatureType[] getDirectSubtypes(FeatureType ft) {
		return decoratee.getDirectSubtypes(ft);
	}

	@Override
	public FeatureType getParent(FeatureType ft) {
		return decoratee.getParent(ft);
	}

	@Override
	public FeatureType[] getSubtypes(FeatureType ft) {
		return decoratee.getSubtypes(ft);
	}

	@Override
	public FeatureType[] getConcreteSubtypes(FeatureType ft) {
		return decoratee.getConcreteSubtypes(ft);
	}

	@Override
	public GMLSchemaInfoSet getGMLSchema() {
		return decoratee.getGMLSchema();
	}

	@Override
	public boolean isSubType(FeatureType ft, FeatureType substitution) {
		return decoratee.isSubType(ft, substitution);
	}

	@Override
	public List<PropertyType> getNewPropertyDecls(FeatureType ft) {
		return decoratee.getNewPropertyDecls(ft);
	}

	@Override
	public Map<FeatureType, FeatureType> getFtToSuperFt() {
		return decoratee.getFtToSuperFt();
	}

	@Override
	public Map<String, String> getNamespaceBindings() {
		return decoratee.getNamespaceBindings();
	}

	@Override
	public Set<String> getAppNamespaces() {
		return decoratee.getAppNamespaces();
	}

	@Override
	public List<String> getNamespacesDependencies(String ns) {
		return decoratee.getNamespacesDependencies(ns);
	}

	@Override
	public List<GMLObjectType> getGeometryTypes() {
		return decoratee.getGeometryTypes();
	}

	@Override
	public GMLObjectType getGeometryType(QName name) {
		return decoratee.getGeometryType(name);
	}

	@Override
	public List<GMLObjectType> getSubstitutions(QName name) {
		return decoratee.getSubstitutions(name);
	}

	@Override
	public List<GMLObjectType> getDirectSubstitutions(QName name) {
		return decoratee.getDirectSubstitutions(name);
	}

	@Override
	public AppSchemaGeometryHierarchy getGeometryHierarchy() {
		return decoratee.getGeometryHierarchy();
	}

	@Override
	public Map<GMLObjectType, GMLObjectType> getGeometryToSuperType() {
		return decoratee.getGeometryToSuperType();
	}

	@Override
	public GMLObjectType getGmlObjectType(QName name) {
		return decoratee.getGmlObjectType(name);
	}

	@Override
	public List<GMLObjectType> getGmlObjectTypes() {
		return decoratee.getGmlObjectTypes();
	}

	@Override
	public List<GMLObjectType> getGmlObjectTypes(GMLObjectCategory timeObject) {
		return decoratee.getGmlObjectTypes(timeObject);
	}

}
