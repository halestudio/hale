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
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;

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
	private final String description;
	private final Map<String, Class<?>> parameters = new TreeMap<String, Class<?>>();
	
	public FunctionDescriptionImpl(CstFunction function) {
		
		// create Identifier for function
		try {
			this.url = new URL("file://java/" + function.getClass().getName()); //$NON-NLS-1$
			this.description=function.getDescription();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		// set parameters from cell configuration
		inspectCell(function.getParameters());
	}
	
	public FunctionDescriptionImpl(ICell cell) {
		
		// create Identifier for function
		try {
			String cstfname = ""; //$NON-NLS-1$
			if (cell.getEntity1() != null 
					&& cell.getEntity1().getTransformation() != null) {
				cstfname = cell.getEntity1().getTransformation().getService().getLocation();
			}
			if (cell.getEntity2() != null 
					&& cell.getEntity2().getTransformation() != null) {
				cstfname = cell.getEntity2().getTransformation().getService().getLocation();
			}
			this.url = new URL("file://java/" + cstfname); //$NON-NLS-1$
			this.description = ""; //$NON-NLS-1$
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		// set parameters from cell configuration
		inspectCell(cell);
	}
	
	private void inspectCell(ICell parameter) {
		try {
			if (parameter.getEntity1() != null) {
				parameters.put("entity1", parameter.getEntity1().getClass()); //$NON-NLS-1$
				inspectObject("entity1", parameter.getEntity1()); //$NON-NLS-1$
			}
			if (parameter.getEntity2() != null) {
				parameters.put("entity2", parameter.getEntity2().getClass()); //$NON-NLS-1$
				inspectObject("entity2", parameter.getEntity2()); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new RuntimeException("Translating a parameter Cell to KV failed: ", e); //$NON-NLS-1$
		}
	}

	private void inspectObject(String parentname, Object parameter) 
		throws IllegalArgumentException, IllegalAccessException 
	{
		if (parameter.getClass().isPrimitive() 
				|| Number.class.isAssignableFrom(parameter.getClass())
				|| String.class.isAssignableFrom(parameter.getClass())) {
			if (parentname.matches(".*typeCondition\\[.*?\\]$")) { //$NON-NLS-1$
				// special case: when encountering a type condition, use it's value instead of it's type.
				Class<?> typeConditionClass = String.class;
				try {
					typeConditionClass = Class.forName(parameter.toString());
				} catch (ClassNotFoundException e) {}
				parameters.put(parentname, typeConditionClass);
			}
			else {
				parameters.put(parentname, parameter.getClass());
			}
    		return;
    	}
		
		for (Field field : this.getFields(parameter.getClass())) {
			
			if (parentname.contains(field.getName() ) 
					|| Modifier.isStatic(field.getModifiers())
					|| field.getName().equals("count") //$NON-NLS-1$
					|| field.getName().equals("hash") //$NON-NLS-1$
					|| field.getName().equals("offset") //$NON-NLS-1$
					|| field.getName().equals("serialPersistentFields")) { //$NON-NLS-1$
				continue;
			}
			field.setAccessible(true);
			if (field.get(parameter) != null) {
				if (field.getType().isPrimitive() 
						|| Number.class.isAssignableFrom(field.getType())
						|| String.class.isAssignableFrom(field.getType())) {
	        		parameters.put(parentname + "." + field.getName(), field.getType()); //$NON-NLS-1$
	        	}
				else if (Collection.class.isAssignableFrom(field.getType())) {
					parameters.put(parentname + "." + field.getName(), field.getType()); //$NON-NLS-1$
					int i = 0;
					for (Object o : (Collection<?>)field.get(parameter)) {
						String name = parentname + "." + field.getName() + "[" + i++ + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						inspectObject(name, o);
					}
				}
	        	else {
	        		parameters.put(parentname + "." + field.getName(), field.getType()); //$NON-NLS-1$
	        		inspectObject(parentname + "." + field.getName(), field.get(parameter)); //$NON-NLS-1$
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
	
	public String getFunctionDescription() {
		return description;
	}

	public Map<String, Class<?>> getParameterConfiguration() {
		return parameters;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String key : this.parameters.keySet()) {
			String name = this.parameters.get(key).getName();
			String shortName = name.substring(name.lastIndexOf('.') + 1);
			sb.append(key + ": " + shortName + ", "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sb.toString();
	}

}
