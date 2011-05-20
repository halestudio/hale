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

package eu.esdihumboldt.cst.transformer.service;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.configuration.ReflectionHelper;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * A class managing instantiation of CstFunction objects.
 * 
 * @author Thorsten Reitz, Jan Jezek, Anna Pitaev
 * @version $Id$ 
 */
public class CstFunctionFactory {
	
	public static final String ENTITY_1_NAME = "ENTITY_1_NAME"; //$NON-NLS-1$
	public static final String ENTITY_2_NAME = "ENTITY_2_NAME"; //$NON-NLS-1$
	
	private static Map<String, Class<? extends CstFunction>> functions = 
		new HashMap<String, Class<? extends CstFunction>>();

	private static CstFunctionFactory functionFactory = null;
	
	/**
	 * Hidden constructor
	 */
	private CstFunctionFactory() {
		functions.put(RenameFeatureFunction.class.getName(), RenameFeatureFunction.class);
	}
	
	/**
	 * Singleton access method.
	 * @return the only instance of a {@link CstFunctionFactory}.
	 */
	public static CstFunctionFactory getInstance() {
		if (functionFactory == null) {
			functionFactory = new CstFunctionFactory();
		}
		return functionFactory;
	}
	
	/**
	 * @param cell the {@link Cell} to analyze.
	 * @return a {@link CstFunction} for the given Cell, complete with all
	 * required {@link Parameter}s set.
	 */
	public CstFunction getCstFunction(ICell cell) {
		ITransformation transformation = cell.getEntity1().getTransformation();
		if (transformation == null || transformation.getService() == null) {
			throw new RuntimeException("The Service element of the passed " + //$NON-NLS-1$
					"Transformation has not been initialized correctly with " + //$NON-NLS-1$
					"a Resource."); //$NON-NLS-1$
		}
		return this.getFunction(transformation.getService().getLocation(), cell);
	}	
	
	
	/**
	 * Returns an Augmentation {@link CstFunction} if one was defined in the 
	 * {@link Cell}.
	 * @param cell the {@link Cell} to analyse.
	 * @return a {@link CstFunction} if a Transformation was defined on Entity2.
	 */
	public CstFunction getCstAugmentationFunction(ICell cell) {
		ITransformation transformation = cell.getEntity2().getTransformation();
		if (transformation == null || transformation.getService() == null) {
			throw new RuntimeException("The Service element of the passed " + //$NON-NLS-1$
					"Transformation has not been initialized correctly with " + //$NON-NLS-1$
					"a Resource."); //$NON-NLS-1$
		}
		return this.getFunction(transformation.getService().getLocation(), cell);
	}
	
	private CstFunction getFunction(String operation, ICell cell) {
		Class<?> tclass = functions.get(operation);
		if ( tclass != null) {
			CstFunction t;
			try {
				t = (CstFunction) tclass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("This Function " + operation  //$NON-NLS-1$
						+ " could not be instantiated by this CST: ", e); //$NON-NLS-1$
			}				
			t.configure(cell);
			return t;
		}
		else {
			throw new RuntimeException("This Function " + operation  //$NON-NLS-1$
					+ " is not known to this CST."); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @return Map of available CST Functions
	 */
	public Map<String, Class<? extends CstFunction>> getRegisteredFunctions(){
		final Map<String, Class<? extends CstFunction>> func = 
			new HashMap<String, Class<? extends CstFunction>>(
					CstFunctionFactory.functions);
		return  func;
	}
	
	/**
	 * Reads {@link CstFunction} implementations from the given package and 
	 * registers them with this {@link CstService}.
	 * @param functionPackage
	 */
	public void registerCstPackage(String functionPackage) {
		this.loadFunctionClasses(functionPackage);
	}
	
    /**
     * This method loads all {@link CstFunction}s from a specified package, 
     * identified by it's name. 
     * @param functionPackage the name of the package containing the 
     * {@link CstFunction}s
     * @throws RuntimeException if the package could not be initialized
     */
    private void loadFunctionClasses(String functionPackage) {
        // first, get a list of all classes in the functionPackage.
    	File[] files;
		try {
			files = ReflectionHelper.getFilesFromPackage(functionPackage);
		} catch (IOException e) {
			throw new RuntimeException("Could not load CstFunctions from function package: ", e); //$NON-NLS-1$
		}
        
		List<Class<? extends CstFunction>> result = null;
        if (files != null) {
            result =  this.readPackage(files, functionPackage);
        }

        for (Class<? extends CstFunction> cstclass : result) {
        	CstFunctionFactory.functions.put(
        			cstclass.getName(), cstclass);
        }
        
    }
    
    /**
     * 
     */
	private List<Class<? extends CstFunction>> readPackage(File[] files,
			String functionPackage) {
		List<Class<? extends CstFunction>> result =
        	new ArrayList<Class<? extends CstFunction>>();
        
        String package_path = functionPackage.replaceAll("\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
        
        String fileString;
        for (int i = 0; i < files.length; i++) {
        	fileString = files[i].toURI().toString();
            if (!fileString.matches(".*\\.class")) { //$NON-NLS-1$
            	continue;
            }

        	// cut the classname from the file
            int dppp = fileString.indexOf(package_path);
            if (dppp != -1) {
            	//try to preserve sub-directories
            	fileString = fileString.substring(dppp + package_path.length() + 1);
            	fileString = fileString.replaceAll("\\/", "."); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
            	fileString = fileString.replaceAll("(.*/)*", ""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            fileString = fileString.replaceAll("\\.class$", ""); //$NON-NLS-1$ //$NON-NLS-2$
            
            //skip unit tests
            if (fileString.endsWith("Test")) { //$NON-NLS-1$
            	continue;
            }

            try {
            	Class<?> c = Class.forName(functionPackage + "." + fileString); //$NON-NLS-1$
                if (c.isInterface() || Modifier.isAbstract(c.getModifiers()) ||
                	c.isLocalClass() || c.isAnonymousClass() || c.isMemberClass()) {
                	continue;
                }

                if (CstFunction.class.isAssignableFrom(c)) {
                	c.newInstance();
                	result.add((Class<? extends CstFunction>)c);
                } 
            } catch (Exception e) {
                throw new RuntimeException("CstFunction could not be " + //$NON-NLS-1$
                		"instantiated: " + e.getMessage(), e); //$NON-NLS-1$
            }
        }
    
        return result;
    }
	
}
