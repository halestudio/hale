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

package eu.esdihumboldt.cst.transformer;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.cst.transformer.impl.BoundingBoxFunction;
import eu.esdihumboldt.cst.transformer.impl.CentroidFunction;
import eu.esdihumboldt.cst.transformer.impl.ClassificationMappingFunction;
import eu.esdihumboldt.cst.transformer.impl.GenericMathFunction;
import eu.esdihumboldt.cst.transformer.impl.NetworkExpansionFunction;
import eu.esdihumboldt.cst.transformer.impl.RenameAttributeFunction;
import eu.esdihumboldt.cst.transformer.impl.RenameFeatureFunction;
import eu.esdihumboldt.cst.transformer.impl.SpatialTypeConversionFunction;
import eu.esdihumboldt.cst.transformer.impl.inspire.IdentifierFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;

/**
 * A class managing instantiation of CstFunction objects.
 * 
 * @author Jan Jezek, Anna Pitaev, Thorsten Reitz
 * @version $Id$ 
 */
public class CstFunctionFactory {
	
	public static final String ENTITY_1_NAME = "ENTITY_1_NAME";
	public static final String ENTITY_2_NAME = "ENTITY_2_NAME";
	
	private static Map<String, Class<? extends CstFunction>> functions = 
		new HashMap<String, Class<? extends CstFunction>>();

	private static CstFunctionFactory functionFactory = null;
	
	/**
	 * Hidden constructor
	 */
	private CstFunctionFactory() {
		initConfiguration();
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
			throw new RuntimeException("The Service element of the passed " +
					"Transformation has not been initialized correctly with " +
					"a Resource.");
		}
		String operation = transformation.getService().getLocation();
		
		Class<?> tclass = functions.get(operation);
		if ( tclass != null) {
			CstFunction t;
			try {
				t = (CstFunction) tclass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("This Function " + operation 
						+ " could not be instantiated by this CST: ", e);
			}				
			
			// configure using cell first; if false is returned, proceed with manual param configuration
			if (!t.configure(cell)) {
				Map<String, String> paramMap = convertToMap(
						transformation.getParameters());
				paramMap.put(ENTITY_1_NAME, cell.getEntity1().getAbout().getAbout());
				paramMap.put(ENTITY_2_NAME, cell.getEntity2().getAbout().getAbout());
				t.configure(paramMap);
			}
			return t;
		}
		else {
			throw new RuntimeException("This Function " + operation 
					+ " is not known to this CST.");
		}

	}	
	
	/**
	 * Returns an Augmentation {@link CstFunction} if one was defined in the 
	 * {@link Cell}.
	 * @param cell the {@link Cell} to analyse.
	 * @return a {@link CstFunction} if a Transformation was defined on Entity2.
	 */
	public CstFunction getCstAugmentationFunction(ICell cell) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 
	 * @return Map of available CST Functions
	 */
	public Map<String, Class<? extends CstFunction>> getRegisteredFunctions(){
		final Map<String, Class<? extends CstFunction>> func = 
			new HashMap<String, Class<? extends CstFunction>>(CstFunctionFactory.functions);
		return  func;
	}
	
	/**
	 * TODO: replace with parsing a configuration file.
	 */
	protected void initConfiguration() {
		Set<String> functionNames = new HashSet<String>();
		functionNames.add(CentroidFunction.class.getName());
		functionNames.add(NetworkExpansionFunction.class.getName());
	    functionNames.add(RenameAttributeFunction.class.getName());
		functionNames.add(RenameFeatureFunction.class.getName());
		functionNames.add(GenericMathFunction.class.getName());
		functionNames.add(ClassificationMappingFunction.class.getName());
		functionNames.add(BoundingBoxFunction.class.getName());
		functionNames.add(SpatialTypeConversionFunction.class.getName());
		functionNames.add(IdentifierFunction.class.getName());
		
		for (String name : functionNames) {
			Class<?> tclass = null;
			try {
				tclass = Class.forName(name);
				tclass.newInstance();
				
			} catch (Exception e) {
				throw new RuntimeException("A CstFunction class could not be " +
						"instantiated for name = " + name + " : ", e);
			} 
			if (tclass != null) {
				CstFunctionFactory.functions.put(
						name, (Class<? extends CstFunction>) tclass);
			}
		}
	}
	
	protected Map<String, String> convertToMap(List<IParameter> params){
		Map<String, String> paramMap = new HashMap<String, String>();
		for (Iterator<IParameter> i = params.iterator(); i.hasNext();){
			IParameter p = i.next();
			
			paramMap.put(p.getName(), p.getValue());
		}
		return paramMap;
	}
	
}
