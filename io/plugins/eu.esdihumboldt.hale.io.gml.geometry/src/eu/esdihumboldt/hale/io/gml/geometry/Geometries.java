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

package eu.esdihumboldt.hale.io.gml.geometry;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.geometry.extension.InterlisHandlerConfExtension;
import eu.esdihumboldt.util.reflection.ReflectionHelper;

/**
 * Manages geometry handlers.
 * 
 * @author Simon Templer
 */
public class Geometries implements GeometryHandler {

	private static final ALogger log = ALoggerFactory.getLogger(Geometries.class);

	private static Geometries instance;

	/**
	 * Get the geometries instance.
	 * 
	 * @return the geometry handler manager
	 */
	public static synchronized Geometries getInstance() {
		if (instance == null) {
			instance = new Geometries();
		}
		return instance;
	}

	/**
	 * Type names mapped to geometry handlers
	 */
	private final Multimap<QName, GeometryHandler> handlers = HashMultimap.create();
	private final Map<String, GeometryHandler> interlisHandlers = new HashMap<String, GeometryHandler>();

	/**
	 * Default constructor
	 */
	private Geometries() {
		// register default geometry handlers from handler package
		try {
			List<Class<?>> classes = ReflectionHelper.getClassesFromPackage(getClass().getPackage()
					.getName() + ".handler", getClass().getClassLoader());
			// Interlis classes
			InterlisHandlerConfExtension extension = InterlisHandlerConfExtension.getInstance();
			List<Class<?>> handlers = extension.getInterlisHandlers();

			classes.addAll(handlers);
			for (Class<?> clazz : classes) {
				try {
					if (!Modifier.isAbstract(clazz.getModifiers())
							&& GeometryHandler.class.isAssignableFrom(clazz)) {
						GeometryHandler handler = (GeometryHandler) clazz.newInstance();
						register(handler);
					}
				} catch (Exception e) {
					log.error("Error registering geometry handler " + clazz.getSimpleName(), e);
				}
			}
		} catch (IOException e) {
			log.error(
					"Failed to retrieve classes from package, skipping registering default geometry handlers",
					e);
		}
	}

	/**
	 * Register a geometry handler.
	 * 
	 * @param handler the geometry handler
	 */
	public void register(GeometryHandler handler) {

		if (handler.identifiesTypeByName()) {
			synchronized (interlisHandlers) {
				for (QName name : handler.getSupportedTypes()) {
					interlisHandlers.put(name.getLocalPart(), handler);
				}
			}
		}
		else {
			synchronized (handlers) {
				for (QName name : handler.getSupportedTypes()) {
					handlers.put(name, handler);
				}
			}
		}
	}

	/**
	 * @see GeometryHandler#getSupportedTypes()
	 */
	@Override
	public Set<QName> getSupportedTypes() {
		return Collections.unmodifiableSet(handlers.keySet());
	}

	/**
	 * @see GeometryHandler#getTypeConstraints(TypeDefinition)
	 */
	@Override
	public Iterable<TypeConstraint> getTypeConstraints(TypeDefinition type)
			throws GeometryNotSupportedException {
		if (supportsType(type)) {
			synchronized (handlers) {

				for (GeometryHandler handler : handlers.get(type.getName())) {
					try {
						return handler.getTypeConstraints(type);
					} catch (Throwable e) {
						// ignore
					}
				}
			}
		}
		else if (type.getName().getNamespaceURI().startsWith(INTERLIS_NAME)) {

			String[] n = type.getName().getNamespaceURI().split("/");
			String name = n[n.length - 1];
			GeometryHandler handler = interlisHandlers.get(name);

			if (handler != null)
				synchronized (interlisHandlers) {

					try {
						return handler.getTypeConstraints(type);
					} catch (Throwable e) {
						// do nothing
					}

				}

		}

		throw new GeometryNotSupportedException("No geometry handler for type available");
	}

	/**
	 * @see GeometryHandler#createGeometry(Instance, int)
	 */
	@Override
	public Object createGeometry(Instance instance, int srsDimension)
			throws GeometryNotSupportedException {
		// TODO support retrieving handler from a constraint?

		synchronized (handlers) {
			for (GeometryHandler handler : handlers.get(instance.getDefinition().getName())) {
				try {
					return handler.createGeometry(instance, srsDimension);
				} catch (Throwable e) {
					// ignore
				}
			}
		}

		throw new GeometryNotSupportedException("No geometry handler for type available");
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#identifyTypeByName()
	 */
	@Override
	public boolean identifiesTypeByName() {

		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler#supportsType(eu.esdihumboldt.hale.common.schema.model.TypeDefinition)
	 */
	@Override
	public boolean supportsType(TypeDefinition type) {
		return handlers.containsKey(type.getName());
	}

}
