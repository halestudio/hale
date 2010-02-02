/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.capabilities.impl;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.goml.align.Cell;

/**
 * This is an implementation of {@link FunctionDescription}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */ 
public class FunctionDescriptionImpl 
	implements FunctionDescription {

	private final URL url;
	private final Map<String, Class<?>> parameters = new TreeMap<String, Class<?>>();
	
	public FunctionDescriptionImpl(CstFunction function) {
		
		// create Identifier for function
		try {
			this.url = new URL("file://java/" + function.getClass().getName());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		// set parameters from cell configuration
		inspectCell(function.getParameters());
	}
	
	private void inspectCell(Cell parameter) {
		try {
			if (parameter.getEntity1() != null) {
				parameters.put("entity1", parameter.getEntity1().getClass());
				inspectObject("entity1", parameter.getEntity1());
			}
			if (parameter.getEntity2() != null) {
				parameters.put("entity2", parameter.getEntity2().getClass());
				inspectObject("entity2", parameter.getEntity2());
			}
		} catch (Exception e) {
			throw new RuntimeException("Translating a parameter Cell to KV failed: ", e);
		}
	}

	private void inspectObject(String parentname, Object parameter) 
		throws IllegalArgumentException, IllegalAccessException 
	{
		
		for (Field field : this.getFields(parameter.getClass())) {
			if (parentname.contains(field.getName() ) 
					|| field.getName().equals("NULL_ENTITY")
					|| field.getName().equals("CASE_INSENSITIVE_ORDER")
					|| field.getName().equals("count")
					|| field.getName().equals("hash")
					|| field.getName().equals("offset")
					|| field.getName().equals("serialPersistentFields")
					|| field.getName().equals("serialVersionUID")) {
				continue;
			}
			field.setAccessible(true);
			if (field.get(parameter) != null) {
				if (field.getType().isPrimitive() 
						|| Number.class.isAssignableFrom(field.getType())
						|| String.class.isAssignableFrom(field.getType())) {
	        		parameters.put(parentname + "." + field.getName(), field.getType());
	        	}
				else if (Collection.class.isAssignableFrom(field.getType())) {
					int i = 0;
					for (Object o : (Collection<?>)field.get(parameter)) {
						String name = parentname + "." + field.getName() + "[" + i++ + "]";
						parameters.put(name, field.getType());
						inspectObject(name, o);
					}
				}
	        	else {
	        		parameters.put(parentname + "." + field.getName(), field.getType());
	        		inspectObject(parentname + "." + field.getName(), field.get(parameter));
	        	}
			}
        }
	}
	
	private Set<Field> getFields(Class<?> currentType) {
		final Set<Field> result = new HashSet<Field>();
		while (!currentType.equals(Object.class)) {
			result.addAll(this.getAllFields(currentType));
			currentType = currentType.getSuperclass();
		}
		return result;
	}

	private Collection<? extends Field> getAllFields(Class<?> currentType) {
		return Arrays.asList(currentType.getDeclaredFields());
	}

	public URL getFunctionId() {
		return url;
	}


	public Map<String, Class<?>> getParameterConfiguration() {
		return parameters;
	}

}
