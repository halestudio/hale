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

package eu.esdihumboldt.hale.io.gml.geometry;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.reflection.ReflectionHelper;

/**
 * Manages geometry handlers.
 * @author Simon Templer
 */
public class Geometries implements GeometryHandler {
	
	private static final ALogger log = ALoggerFactory.getLogger(Geometries.class);

	private static Geometries instance;
	
	/**
	 * Get the geometries instance.
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
	
	/**
	 * Default constructor
	 */
	private Geometries() {
		// register default geometry handlers from handler package
		try {
			List<Class<?>> classes = ReflectionHelper.getClassesFromPackage(
					getClass().getPackage().getName() + ".handler", 
					getClass().getClassLoader());
			
			for (Class<?> clazz : classes) {
				try {
					if (!Modifier.isAbstract(clazz.getModifiers()) &&
							GeometryHandler.class.isAssignableFrom(clazz)) {
						GeometryHandler handler = (GeometryHandler) clazz.newInstance();
						register(handler);
					}
				} catch (Exception e) {
					log.error("Error registering geometry handler "
							+ clazz.getSimpleName(), e);
				}
			}
		} catch (IOException e) {
			log.error("Failed to retrieve classes from package, skipping registering default geometry handlers", e);
		}
	}
	
	/**
	 * Register a geometry handler.
	 * @param handler the geometry handler
	 */
	public void register(GeometryHandler handler) {
		synchronized (handlers) {
			for (QName name : handler.getSupportedTypes()) {
				handlers.put(name, handler);
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
		synchronized (handlers) {
			for (GeometryHandler handler : handlers.get(type.getName())) {
				try {
					return handler.getTypeConstraints(type);
				} catch (Throwable e) {
					// ignore
				}
			}
		}
		
		throw new GeometryNotSupportedException("No geometry handler for type available");
	}

	/**
	 * @see GeometryHandler#createGeometry(Instance)
	 */
	@Override
	public Object createGeometry(Instance instance)
			throws GeometryNotSupportedException {
		//TODO support retrieving handler from a constraint?
		
		synchronized (handlers) {
			for (GeometryHandler handler : handlers.get(instance.getDefinition().getName())) {
				try {
					return handler.createGeometry(instance);
				} catch (Throwable e) {
					// ignore
				}
			}
		}
		
		throw new GeometryNotSupportedException("No geometry handler for type available");
	}
	
}
