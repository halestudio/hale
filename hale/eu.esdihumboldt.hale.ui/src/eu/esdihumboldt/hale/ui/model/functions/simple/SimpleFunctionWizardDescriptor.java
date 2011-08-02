/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.model.functions.simple;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.ComposedFeatureClass;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.hale.ui.model.functions.AbstractFunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.model.functions.SchemaSelectionInfo;
import eu.esdihumboldt.hale.ui.model.schema.NullSchemaItem;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.specification.cst.CstFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * Simple function wizard descriptor for functions with no parameters.
 * No wizard will be displayed, instead the cell is created and added to the
 * alignment.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleFunctionWizardDescriptor extends
		AbstractFunctionWizardDescriptor {
	
	/**
	 * Simple wizard factory (that actually doesn't create a wizard)
	 */
	private class SimpleWizardFactory implements FunctionWizardFactory {

		/**
		 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
		 */
		@Override
		public FunctionWizard createWizard(AlignmentInfo selection) {
			// create transformation
			Transformation t = new Transformation();
			String name = getFunctionClassName();
			t.setLabel(name);
			t.setService(new Resource(name));
			
			Entity entity2 = createEntity(selection.getTargetItems());
			
			// create entities
			Entity entity1;
			if (isAugmentation()) {
				entity1 = Entity.NULL_ENTITY;
				entity2.setTransformation(t);
			}
			else {
				entity1 = createEntity(selection.getSourceItems()); 
				entity1.setTransformation(t);
			}
			
			// create cell
			Cell cell = new Cell();
			cell.setEntity1(entity1);
			cell.setEntity2(entity2);
			
			// add cell to alignment
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
//FIXME			as.addOrUpdateCell(cell);
			
//			log.userInfo("Added cell");
			
			return null;
		}

		/**
		 * Create an entity from the given schema items
		 * 
		 * @param items the schema items, must be all of the same type (property/attribute or type)
		 * @return the entity
		 */
		private Entity createEntity(Collection<SchemaItem> items) {
			if (items.size() == 0) {
				return Entity.NULL_ENTITY;
			}
			else {
				return SchemaSelectionInfo.determineEntity(items);
			}
		}

		/**
		 * @see FunctionWizardFactory#supports(AlignmentInfo)
		 */
		@Override
		public boolean supports(AlignmentInfo selection) {
			boolean ok = true;
			
			ICell cell = null;
			if (!isAugmentation()) {
				// check source entity
				ok = checkEntity(parameterCell.getEntity1(), selection.getSourceItems());
				
				if (ok) {
					// get augmentation cell
					cell = selection.getAlignment(Collections.singleton(NullSchemaItem.INSTANCE), selection.getTargetItems());
				}
			}
			
			if (ok) {
				// check target entity
				ok = checkEntity(parameterCell.getEntity2(), selection.getTargetItems());
				
				if (ok) {
					cell = selection.getAlignment(selection.getSourceItems(), selection.getTargetItems());
				}
			}
			
			//TODO check existing cell
			if (ok && cell != null) {
				// don't allow editing any cell
				return false;
			}
			
			return ok;
		}

		/**
		 * Check if the given entity is compatible to the given schema items
		 * 
		 * @param entity the entity
		 * @param items the schema items
		 * @return if they are compatible
		 */
		private boolean checkEntity(IEntity entity,
				Collection<SchemaItem> items) {
			if (entity instanceof ComposedProperty) {
				// composed property
				// size check
				if (items.size() < 1) return false;
				
				// check properties
				ComposedProperty p = (ComposedProperty) entity;
				List<Property> properties = p.getCollection();
				for (Property property : properties) {
					boolean ok = checkEntity(property, items);
					if (!ok) {
						return false;
					}
				}
				
				return true;
			}
			else if (entity instanceof ComposedFeatureClass) {
				// composed feature
				// size check
				if (items.size() < 1) return false;
				
				ComposedFeatureClass p = (ComposedFeatureClass) entity;
				List<FeatureClass> features = p.getCollection();
				for (FeatureClass feature : features) {
					boolean ok = checkEntity(feature, items);
					if (!ok) {
						return false;
					}
				}
				
				return true;
			}
			else if (entity instanceof Property || entity instanceof FeatureClass) {
				// feature
				// size check
				if (items.size() != 1) return false;
				
				return checkItems(entity, items);
			}
			
			return false;
		}

		/**
		 * Check if the given schema items are compatible with the given entity
		 * 
		 * @param entity the entity, may not be composed
		 * @param items the schema items
		 * 
		 * @return if the items are compatible
		 */
		private boolean checkItems(IEntity entity,
				Collection<SchemaItem> items) {
			for (SchemaItem item : items) {
				// property check
				if (entity instanceof Property && !item.isAttribute()) {
					return false;
				}
				
				// type check
				if (entity instanceof FeatureClass && !item.isType()) {
					return false;
				}
				
				// check type conditions
				if (entity instanceof Property) {
					List<String> typeConditions = ((Property) entity).getTypeCondition();
					
					Class<?> binding = item.getPropertyType().getBinding();
					
					boolean matches = false;
					Iterator<String> it = typeConditions.iterator();
					while (!matches && it.hasNext()) {
						String typeName = it.next();
						try {
							Class<?> type = getFunctionClassLoader().loadClass(typeName);
							if (type.isAssignableFrom(binding)) {
								matches = true;
							}
						} catch (ClassNotFoundException e) {
							log.warn("Class for function type condition not found", e); //$NON-NLS-1$
						}
					}
					
					if (!matches) {
						return false;
					}
				}
				
				//TODO more to check?
			}
			
			return true;
		}

	}
	
	private static final ALogger log = ALoggerFactory.getLogger(SimpleFunctionWizardDescriptor.class);
	
	private final ICell parameterCell;
	
	private final SimpleWizardFactory factory;

	private ClassLoader functionClassLoader;

	/**
	 * @see AbstractFunctionWizardDescriptor#AbstractFunctionWizardDescriptor(IConfigurationElement)
	 */
	public SimpleFunctionWizardDescriptor(IConfigurationElement conf) {
		super(conf);
		
		String functionClass = conf.getAttribute("function"); //$NON-NLS-1$
//		Map<String, Class<? extends CstFunction>> functions = CstFunctionFactory.getInstance().getRegisteredFunctions();
//		Class<? extends CstFunction> function = functions.get(functionClass);
//		
//		if (function == null) {
			throw new RuntimeException("Function " + functionClass + " not available in the CST"); //$NON-NLS-1$ //$NON-NLS-2$
//		}
//		
//		parameterCell = createFunction().getParameters();
//		
//		factory = new SimpleWizardFactory();
	}
	
	/**
	 * Get the function class name
	 * 
	 * @return the function class name
	 */
	protected String getFunctionClassName() {
		return conf.getAttribute("function"); //$NON-NLS-1$
	}
	
	/**
	 * Get the function class loader
	 * 
	 * @return the function class loader
	 */
	protected ClassLoader getFunctionClassLoader() {
		if (functionClassLoader == null) {
			functionClassLoader = createFunction().getClass().getClassLoader();
		}
		return functionClassLoader;
	}

	/**
	 * Create a function instance
	 * 
	 * @return the function instance or <code>null</code>
	 */
	protected CstFunction createFunction() {
		try {
			return (CstFunction) conf.createExecutableExtension("function"); //$NON-NLS-1$
		} catch (CoreException e) {
			log.error("Error creating function", e); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @see FunctionWizardDescriptor#getFactory()
	 */
	@Override
	public FunctionWizardFactory getFactory() {
		return factory;
	}

	/**
	 * @see FunctionWizardDescriptor#isAugmentation()
	 */
	@Override
	public boolean isAugmentation() {
		if (parameterCell.getEntity2().getTransformation() != null) {
			return true;
		}
		
		return false;
	}

}
