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

package eu.esdihumboldt.cst.corefunctions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opengis.feature.Feature;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IValueExpression;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.DetailedAbout;
import eu.esdihumboldt.goml.rdf.IDetailedAbout;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * The {@link ClassificationMappingFunction} allows to map values of an
 * attribute to a different classification system.
 * 
 * @author Thorsten Reitz, Mark Doyle
 * @version $Id$ 
 */
public class ClassificationMappingFunction extends AbstractCstFunction {
	
	/**
	 * The source property that has a mapping configured.
	 */
	private IDetailedAbout sourceProperty;
	
	private Set<String> allSourceValues = new HashSet<String>();
	
	/**
	 * The mapping restrictions associated to the {@link #sourceProperty}
	 */
	private List<Restriction> sourceRestrictions = null;
	
	/**
	 * The target property that has a mapping configured.
	 */
	private IDetailedAbout targetProperty;
	
	/**
	 * The mapping restrictions associated to the {@link #targetProperty} 
	 */
	private Map<Integer, Restriction> targetRestrictions = null;
	

	/**
	 * @see CstFunction#configure(ICell)
	 */
	public boolean configure(ICell cell) {
		sourceProperty = DetailedAbout.getDetailedAbout(((Property) cell.getEntity1()).getAbout(), true);
		sourceRestrictions = ((Property)cell.getEntity1()).getValueCondition();
		
		if (sourceRestrictions != null) {
			for (Restriction r : this.sourceRestrictions) {
				for (IValueExpression ive : r.getValue()) {
					this.allSourceValues.add(ive.getLiteral());
				}
			}
		}
		
		targetProperty = DetailedAbout.getDetailedAbout(((Property) cell.getEntity2()).getAbout(), true);
		targetRestrictions = new HashMap<Integer, Restriction>();
		for (Restriction r : ((Property)cell.getEntity2()).getValueCondition()) {
			targetRestrictions.put(r.getSeq().intValue(), r);
		}
		
		if(sourceRestrictions == null || targetRestrictions == null || sourceProperty == null || targetProperty == null) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * @see CstFunction#transform(Feature, Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		org.opengis.feature.Property source_property = FeatureInspector.getProperty(source, sourceProperty, true);

		for(Restriction restriction : this.sourceRestrictions) {
			ComparatorType comparator = restriction.getComparator();
			List<IValueExpression> sourceValues = restriction.getValue();
			// If there is a match...
			if(match(comparator, sourceValues, source_property)) {
				// ...adjust the target property defined by the targetProperty field to the same Property in 
				// the targetRestrictions
				String targetClassValue =  targetRestrictions.get(
						restriction.getSeq().intValue()).getValue().get(0).getLiteral();
				Class<?> targetBinding = FeatureInspector.getProperty(target, targetProperty, true).getType().getBinding();
				if (Collection.class.isAssignableFrom(targetBinding)) {
					Collection<String> c = new ArrayList<String>();
					c.add(targetClassValue);
					FeatureInspector.setPropertyValue(target, targetProperty, c);
				}
				else {
					FeatureInspector.setPropertyValue(target, targetProperty, targetClassValue);
				}
			}
		}
		return target;
	}

	/**
	 * Checks a {@link org.opengis.feature.Property} value for matches against a list of sourceValues.
	 * 
	 * @param comparator The comparison type to use
	 * @param sourceValues The list of values used to compare with the source {@link Feature} {@link org.opengis.feature.Property}
	 * @param sourceProp The property from the source Feature being compared
	 * 
	 * @return if the property value matches the restrictions
	 */
	private boolean match(ComparatorType comparator, List<IValueExpression> sourceValues, org.opengis.feature.Property sourceProp) {
		boolean result = false;
		Object sourcePropValue = sourceProp.getValue();
		switch(comparator) {
			case ONE_OF:
				for(IValueExpression value : sourceValues) {
					// TODO Should we be testing the equivalence of the source property value against the literal?
					// Is literal always populated?  I would have assumed the equal() would have worked on the value object
					if(sourcePropValue.toString().equals(value.getLiteral())) {
						// We have found one of the values so we can break and return true;
						result = true;
						break;
					}
				}
				break;
			case BETWEEN:
				throw new RuntimeException(comparator + " not yet supported");
			case COLLECTION_CONTAINS:
				throw new RuntimeException(comparator + " not yet supported");
			case CONTAINS:
				throw new RuntimeException(comparator + " not yet supported");
			case EMPTY:
				throw new RuntimeException(comparator + " not yet supported");
			case ENDS_WITH:
				throw new RuntimeException(comparator + " not yet supported");
			case EQUAL:
				throw new RuntimeException(comparator + " not yet supported");
			case GREATER_THAN:
				throw new RuntimeException(comparator + " not yet supported");
			case GREATER_THAN_OR_EQUAL:
				throw new RuntimeException(comparator + " not yet supported");
			case INCLUDES:
				throw new RuntimeException(comparator + " not yet supported");
			case INCLUDES_STRICTLY:
				throw new RuntimeException(comparator + " not yet supported");
			case LESS_THAN:
				throw new RuntimeException(comparator + " not yet supported");
			case LESS_THAN_OR_EQUAL:
				throw new RuntimeException(comparator + " not yet supported");
			case MATCHES:
				throw new RuntimeException(comparator + " not yet supported");
			case NOT_EQUAL:
				throw new RuntimeException(comparator + " not yet supported");
			case OTHERWISE:
				if (!this.allSourceValues.contains(sourcePropValue)) {
					// Value is in none of the other classes so we can break and return true;
					result = true;
					break;
				}
			case STARTS_WITH:
				throw new RuntimeException(comparator + " not yet supported");
			default:
				throw new RuntimeException(comparator + " is an unrecognised ComparatorType");
		}
		return result;
	}
	
	/**
	 * Sample parameter configuration using a cell instead of KV pairs.
	 * @return a {@link Cell} that provides information on the required 
	 * parameter structure for this function. In this case, it returns a cell 
	 * that maps two properties, each of which contains a list of restrictions 
	 * with a list of value expressions.
	 */
	public Cell getParameters() {
		Cell parameterCell = new Cell();
		Property entity1 = new Property(new About(""));
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(String.class.getName());
		entityTypes.add(Number.class.getName());
		entityTypes.add(Boolean.class.getName());
		entityTypes.add(Collection.class.getName());
		entity1.setTypeCondition(entityTypes);

		List<IValueExpression> valueExpressions = new ArrayList<IValueExpression>();
		Restriction r = new Restriction(valueExpressions);
		r.setSeq(new BigInteger("1"));

		List<Restriction> valueConditions = new ArrayList<Restriction>();
		valueConditions.add(r);
		entity1.setValueCondition(valueConditions);

		Property entity2 = new Property(new About(""));

		// Setting of type condition for entity2
			// 	entity2 has same type conditions as entity1
		entity2.setTypeCondition(entityTypes);
		
		List<IValueExpression> valueExpressions2 = new ArrayList<IValueExpression>();
		Restriction r2 = new Restriction(valueExpressions2);
		r2.setSeq(new BigInteger("1"));
		
		List<Restriction> valueConditions2 = new ArrayList<Restriction>();
		valueConditions2.add(r2);
		entity2.setValueCondition(valueConditions2);
		
		parameterCell.setEntity1(entity1);
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

}
