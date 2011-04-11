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
package com.onespatial.jrc.tns.oml_to_rif.translate;

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.EXISTS;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.NOT_EXISTS;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.NUMBER_EQUALS;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.NUMBER_GREATER_THAN;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.NUMBER_LESS_THAN;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.STRING_CONTAINS;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType.STRING_EQUALS;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.AND_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.EQUAL_TO_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.GREATER_THAN_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.LESS_THAN_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.LIKE_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.IS_NOT_NULL_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.IS_NULL_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.NOT_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.OR_NODE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.opengis.feature.type.Name;

import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.AbstractModelFilter;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.GeometryType;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAttributeMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelCentroidCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelClassMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelIdentifierCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelStaticAssignmentCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelConcatenationOfAttributesCell;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.CentroidMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.IdentifierMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifDocument;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelSentence;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.PropertyMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.StaticAssignment;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ConcatenationMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.AbstractFilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.AbstractComparisonNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.EqualToNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LeafNode;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttribute;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable.Type;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Susanne Reinwarth / TU Dresden
 */
public class ModelAlignmentToModelRifTranslator extends
        AbstractFollowableTranslator<ModelAlignment, ModelRifDocument>
{

    /**
     * @see com.onespatial.jrc.tns.oml_to_rif.api.Translator#translate(Object)
     *      which this implements.
     * @param alignment
     *            {@link ModelAlignment}
     * @return {@link ModelRifDocument}
     * @throws TranslationException
     *             if any exceptions are thrown during translation
     */
    @Override
    public ModelRifDocument translate(ModelAlignment alignment) throws TranslationException
    {
        ModelRifDocument result = new ModelRifDocument();

        // loop over class mappings, & for each of them see if each of their
        // attribute mappings is found in the attribute mappings that are
        // possible for the given elementdecl for the source class
        for (ModelClassMappingCell c : alignment.getClassMappings())
        {
            result.getSentences().addAll(translateClassMapping(alignment, c));
        }

        return result;
    }

    private List<ModelSentence> translateClassMapping(ModelAlignment alignment,
            ModelClassMappingCell classMapping)
    {
        // determine which attribute mappings are applicable to this class
        // mapping.
        List<ModelAttributeMappingCell> applicableAttributeMappings = filter(alignment
                .getAttributeMappings(), classMapping.getSourceClass(), classMapping
                .getTargetClass());

        List<ModelStaticAssignmentCell> applicableStaticAssignments = filter(alignment
                .getStaticAssignments(), classMapping.getTargetClass());
        
        List<ModelConcatenationOfAttributesCell> applicableConcatenationMappings = filterConcatenation(alignment
        		.getConcatenationMappings(), classMapping.getSourceClass(), classMapping
        		.getTargetClass());
        
        List<ModelCentroidCell> applicableCentroidMappings = filterCentroid(alignment
        		.getCentroidMappings(), classMapping.getSourceClass(), classMapping
        		.getTargetClass());
        
        List<ModelIdentifierCell> applicableIdentifierMappings = filterIdentifier(alignment
        		.getIdentifierMappings(), classMapping.getSourceClass(), classMapping.getTargetClass());

        return buildModelSentences(classMapping, applicableAttributeMappings,
                applicableStaticAssignments, applicableConcatenationMappings,
                applicableCentroidMappings, applicableIdentifierMappings);
    }

	private List<ModelSentence> buildModelSentences(ModelClassMappingCell classMapping,
            List<ModelAttributeMappingCell> attributeMappings,
            List<ModelStaticAssignmentCell> staticAssignments,
            List<ModelConcatenationOfAttributesCell> concatenationMappings,
            List<ModelCentroidCell> centroidMappings,
            List<ModelIdentifierCell> identifierMappings)
    {
    	// sentence containing class mapping and all non-filter attribute mappings
    	ModelSentence mainSentence = new ModelSentence();
    	setSourceAndTargetClass(mainSentence, classMapping);
    	
        for (ModelMappingCondition condition : classMapping.getMappingConditions())
        {
            mainSentence.addMappingCondition(buildRifMappingCondition(mainSentence,
                    (AbstractFilterNode) condition.getRoot()));
        }
        
        // contains a sentence for each filter attribute mapping
    	List<ModelSentence> filterSentences = new ArrayList<ModelSentence>();
        
        for (ModelAttributeMappingCell attributeMapping : attributeMappings)
        {
        	if (attributeMapping.getMappingConditions().isEmpty()) {
        		buildPropertyMapping(mainSentence, attributeMapping);
        	}
        	else {
        		ModelSentence filterSentence = buildModelFilterSentence(classMapping, attributeMapping);
        		buildPropertyMapping(filterSentence, attributeMapping);
        		filterSentences.add(filterSentence);
        	}
        }
        
        //HALE does not allow filters for static assignments (ConstantValueFunction)
        for (ModelStaticAssignmentCell staticAssignment : staticAssignments)
        {
        	buildStaticAssignment(mainSentence, staticAssignment);
        }
        
        for (ModelConcatenationOfAttributesCell concatenationMapping : concatenationMappings)
        {
        	if (concatenationMapping.getMappingConditions().isEmpty()) {
        		buildConcatenationMapping(mainSentence, concatenationMapping);
        	}
        	else {
        		ModelSentence filterSentence = buildModelFilterSentence(classMapping, concatenationMapping);
        		buildConcatenationMapping(filterSentence, concatenationMapping);
        		filterSentences.add(filterSentence);
        	}
        }
        
        for (ModelCentroidCell centroidMapping : centroidMappings)
        {
        	if (centroidMapping.getMappingConditions().isEmpty()) {
        		buildCentroidMapping(mainSentence, centroidMapping);
        	}
        	else {
        		ModelSentence filterSentence = buildModelFilterSentence(classMapping, centroidMapping);
        		buildCentroidMapping(filterSentence, centroidMapping);
        		filterSentences.add(filterSentence);
        	}
        }
        
        for (ModelIdentifierCell identifierMapping : identifierMappings)
        {
        	if (identifierMapping.getMappingConditions().isEmpty()) {
        		buildIdentifierMapping(mainSentence, identifierMapping);
        	}
        	else {
        		ModelSentence filterSentence = buildModelFilterSentence(classMapping, identifierMapping);
        		buildIdentifierMapping(filterSentence, identifierMapping);
        		filterSentences.add(filterSentence);
        	}
        }
        
        List<ModelSentence> allSentences = new ArrayList<ModelSentence>();
        allSentences.add(mainSentence);
        allSentences.addAll(filterSentences);
        return allSentences;
    }

    private void setSourceAndTargetClass(ModelSentence sentence,
			ModelClassMappingCell classMapping) {
    	sentence.setSourceClass(
                classMapping.getSourceClass().getElementName().getLocalPart().toLowerCase() + "-instance",
                getName(classMapping.getSourceClass().getElementName()));
        sentence.setTargetClass(
                classMapping.getTargetClass().getElementName().getLocalPart().toLowerCase() + "-instance",
                getName(classMapping.getTargetClass().getElementName()));
	}

    private ModelSentence buildModelFilterSentence (ModelClassMappingCell classMapping, 
    		AbstractModelFilter mapping)
    {	
    	ModelSentence sentence = new ModelSentence();
    	setSourceAndTargetClass(sentence, classMapping);
    	sentence.setAttributeFilterSentence(true);
		for (ModelMappingCondition condition : mapping.getMappingConditions())
        {
            sentence.addMappingCondition(buildRifMappingCondition(sentence,
                    (AbstractFilterNode) condition.getRoot()));
        }
		return sentence;
	}

	private void buildStaticAssignment (ModelSentence sentence,
            ModelStaticAssignmentCell staticAssignment)
    {
        RifVariable targetVariable = descendGmlAttributePath(sentence, staticAssignment.getTarget(),
                false);
       
        if (staticAssignment.isNilReason())
        {
        	//create additional property mapping for variable created in static nil reason assignment
        	RifVariable sourceVar = targetVariable.getContextVariable();
        	sourceVar.setIsActionVar(true);
        	sourceVar.setIsNew(true);
        	RifVariable targetVar = new RifVariable();
        	targetVar.setType(Type.ATTRIBUTE);
        	targetVar.setName(staticAssignment.getTarget().get(0).getDefinition().getName());
        	targetVar.setContextVariable(targetVariable.getContextVariable().getContextVariable());
        	targetVar.setPropertyName(targetVariable.getContextVariable().getPropertyName());
        	sentence.addPropertyMapping(new PropertyMapping(sourceVar, targetVar));
        	targetVariable.setPropertyName("urn:x-inspire:specification:gmlas:BaseTypes:3.2:nilReason");
        }
        
        sentence.addStaticAssigment(new StaticAssignment(targetVariable, staticAssignment
                .getContent()));
    }
	
	private void buildIdentifierMapping (ModelSentence sentence,
			ModelIdentifierCell identifierMapping)
	{
		RifVariable sourceVariable = descendGmlAttributePath(sentence, identifierMapping
				.getSourceAttribute(), true);
		
		RifVariable targetVariable = descendGmlAttributePath(sentence, identifierMapping
				.getTargetAttribute(), false);
		
		sentence.addIdentifierMapping(new IdentifierMapping(sourceVariable, targetVariable,
				identifierMapping.getNamespace(),
				identifierMapping.getVersionId(),
				identifierMapping.getVersionNilReason()));
	}

    private void buildPropertyMapping (ModelSentence sentence,
            ModelAttributeMappingCell attributeMapping)
    {
    	RifVariable sourceVariable = descendGmlAttributePath(sentence, attributeMapping
                .getSourceAttribute(), true);
        
        RifVariable targetVariable = descendGmlAttributePath(sentence, attributeMapping
                .getTargetAttribute(), false);

        sentence.addPropertyMapping(new PropertyMapping(sourceVariable, targetVariable));
    }
    
    private void buildCentroidMapping (ModelSentence sentence, ModelCentroidCell centroidMapping)
    {
    	 RifVariable sourceVariable = descendGmlAttributePath(sentence, centroidMapping
                 .getSourceAttribute(), true);
         
         RifVariable targetVariable = descendGmlAttributePath(sentence, centroidMapping
                 .getTargetAttribute(), false);
         
         GeometryType gT = centroidMapping.getGeometryType();

         sentence.addCentroidMapping(new CentroidMapping(sourceVariable, targetVariable, gT));
    }
    
    private void buildConcatenationMapping(ModelSentence sentence,
    		ModelConcatenationOfAttributesCell concatenationMapping)
    {
    	List<RifVariable> sourceVariables = new ArrayList<RifVariable>();
    	List<GmlAttributePath> sourceAttributes = concatenationMapping.getSourceAttributes();
    	for (GmlAttributePath sourceAttribute : sourceAttributes)
    	{
    		sourceVariables.add(descendGmlAttributePath(sentence, sourceAttribute, true));
    	}
    	
    	RifVariable targetVariable = descendGmlAttributePath(sentence, concatenationMapping
    			.getTargetAttribute(), false);
    	
    	String separator = concatenationMapping.getSeparator();
    	String concatString = concatenationMapping.getConcatString();
    	
    	sentence.addConcatenationMapping(new ConcatenationMapping(sourceVariables, targetVariable, separator, concatString));
    }

    private ModelRifMappingCondition buildRifMappingCondition(ModelSentence sentence,
            AbstractFilterNode node)
    {
        ModelRifMappingCondition rifCondition = new ModelRifMappingCondition();
        if (node != null)
        {
            // is it logical, comparison or geometric?

            // logical ones
            if (node.isLogical())
            {
                for (FilterNode child : node.getChildren())
                {
                    AbstractFilterNode childNode = (AbstractFilterNode) child;
                    rifCondition.addChild(buildRifMappingCondition(sentence, childNode));
                }
                if (node.getNodeType().equals(AND_NODE))
                {
                    rifCondition.setLogicalType(LogicalType.AND);
                }
                else if (node.getNodeType().equals(OR_NODE))
                {
                    rifCondition.setLogicalType(LogicalType.OR);
                }
                else if (node.getNodeType().equals(NOT_NODE))
                {
                    rifCondition.setLogicalType(LogicalType.NOT);
                    rifCondition.setNegated(true);
                }
            }
            // comparison ones
            else if (node.isComparison())
            {
                AbstractComparisonNode cnode = (AbstractComparisonNode) node;
                if (node.getNodeType().equals(EQUAL_TO_NODE))
                {
                    EqualToNode equalNode = (EqualToNode) node;
                    // work out if it's a string or a numeric equality test
                    rifCondition.setOperator(STRING_EQUALS);
                    if (equalNode.getRight().isNumeric())
                    {
                        rifCondition.setOperator(NUMBER_EQUALS);
                    }
                }
                // we assume numeric comparison for the greater-than and
                // less-than comparisons
                else if (node.getNodeType().equals(GREATER_THAN_NODE))
                {
                    rifCondition.setOperator(NUMBER_GREATER_THAN);
                }
                else if (node.getNodeType().equals(LESS_THAN_NODE))
                {
                    rifCondition.setOperator(NUMBER_LESS_THAN);
                }
                else if (node.getNodeType().equals(LIKE_NODE))
                {
                    rifCondition.setOperator(STRING_CONTAINS);
                }
                else if (node.getNodeType().equals(IS_NULL_NODE))
                {
                	rifCondition.setOperator(NOT_EXISTS);
                }
                else if (node.getNodeType().equals(IS_NOT_NULL_NODE))
                {
                	rifCondition.setOperator(EXISTS);
                }

                rifCondition.setLeft(getContents(sentence, cnode.getLeft()));
                if (!node.getNodeType().equals(IS_NULL_NODE) && !node.getNodeType().equals(IS_NOT_NULL_NODE))
                {
                	rifCondition.setLiteralClass(cnode.getRight().getLiteralValue().getValueClass());
                    rifCondition.setLiteralValue(cnode.getRight().getLiteralValue().toString());
                    rifCondition.setRight(getContents(sentence, cnode.getRight()));
                }
            }
            // geometric ones
            else if (node.isGeometric())
            {
                // TODO add this
            }
        }
        return rifCondition;
    }

    private RifVariable getContents(ModelSentence sentence, LeafNode leaf)
    {
        // it's either a property or a literal
        if (leaf.getPropertyName() == null)
        {
            // we don't need a variable
            return null;
        }
        RifVariable contextVariable = sentence.getSourceClass();
        String className = contextVariable.getClassName();
        String variableName = className.substring(className.lastIndexOf(':') + 1, className
                .length()).toLowerCase()
                + "-" + leaf.getPropertyName().toLowerCase() + "-filter";
        RifVariable variable = sentence.createVariable(variableName);
        variable.setContextVariable(sentence.getSourceClass());

        variable.setName(variableName.toLowerCase());
        variable.setPropertyName(className.substring(0, className.lastIndexOf(':') + 1)
                + leaf.getPropertyName());
        variable.setType(Type.ATTRIBUTE);
        return variable;
    }

    private RifVariable descendGmlAttributePath(ModelSentence sentence,
            GmlAttributePath gmlAttributePath, boolean isSource)
    {

        RifVariable variable;
        if (isSource)
        {
            variable = sentence.getSourceClass();
        }
        else
        {
            variable = sentence.getTargetClass();
        }

        for (GmlAttribute fragment : gmlAttributePath)
        {
            variable = lazyCreate(variable, fragment, sentence, isSource);
        }
        return variable;
    }

    private RifVariable lazyCreate(RifVariable current, GmlAttribute fragment,
            ModelSentence sentence, boolean isSource)
    {
    	String propertyName = fragment.getDefinition().getNamespace().concat(":").
    			concat(fragment.getDefinition().getName());
    	
    	RifVariable child = sentence.findChildAttribute(current, propertyName);
        if (child == null)
        {
            String variableName = fragment.getDefinition().getDeclaringType().getName().getLocalPart() + "-"
                    + fragment.getDefinition().getName();
            variableName = variableName.toLowerCase();
            if (isSource)
            {
                child = sentence.createVariable(variableName);
            }
            else
            {
                child = sentence.createActionVariable(variableName, false);
            }
            child.setType(Type.ATTRIBUTE);
            child.setPropertyName(propertyName);
            child.setContextVariable(current);
        }

        return child;
    }

    /**
     * Filter {@link ModelStaticAssignmentCell}s to leave only those that can be
     * applied to the specified target class.
     * 
     * @param staticAssignments
     *            the assignments to filter.
     * @param targetClass
     *            target class that assignment must apply to.
     * @return filtered list of assignments.
     */
    private List<ModelStaticAssignmentCell> filter(
            List<ModelStaticAssignmentCell> staticAssignments, SchemaElement targetClass)
    {

        List<ModelStaticAssignmentCell> applicableAssigments = new ArrayList<ModelStaticAssignmentCell>();
        for (ModelStaticAssignmentCell candidate : staticAssignments)
        {

            TypeDefinition targetElement = candidate.getTarget().get(0).getDefinition().getDeclaringType();
            if (canBeSubstitutedBy(targetElement, targetClass))
            {
                applicableAssigments.add(candidate);
            }
            // also test attributes.
        }

        return applicableAssigments;
    }
    
    /**
     * Filter {@link ModelIdentifierCell}s to leave only those that can be
     * applied to the specified source class.
     * 
     * @param identiferMappings
     *            the assignments to filter.
     * @param sourceClass
     *            source class that assignment must apply to.
     * @return filtered list of assignments.
     */
    private List<ModelIdentifierCell> filterIdentifier(
            List<ModelIdentifierCell> identifierMappings, SchemaElement sourceClass, SchemaElement targetClass)
    {

        List<ModelIdentifierCell> applicableAssigments = new ArrayList<ModelIdentifierCell>();
        for (ModelIdentifierCell candidate : identifierMappings)
        {
            TypeDefinition sourceElement = candidate.getSourceAttribute().get(0).getDefinition().getDeclaringType();
            TypeDefinition targetElement = candidate.getTargetAttribute().get(0).getDefinition().getDeclaringType();
            if (canBeSubstitutedBy(sourceElement, sourceClass) && canBeSubstitutedBy(targetElement, targetClass))
            {
                applicableAssigments.add(candidate);
            }
            // also test attributes.
        }

        return applicableAssigments;
    }

    /**
     * Filter {@link ModelAttributeMappingCell}s to leave only those that can
     * target the specified source and target classes.
     * 
     * @param attributeMappings
     *            cells to filter.
     * @param sourceClass
     *            source class that mapping must apply to.
     * @param targetClass
     *            target class that mapping must apply to.
     * @return filtered list of mappings cells.
     */
    private static List<ModelAttributeMappingCell> filter(
            List<ModelAttributeMappingCell> attributeMappings, SchemaElement sourceClass,
            SchemaElement targetClass)
    {
        List<ModelAttributeMappingCell> applicableMappings = new ArrayList<ModelAttributeMappingCell>();
        for (ModelAttributeMappingCell candidate : attributeMappings)
        {
            TypeDefinition sourceElement = candidate.getSourceAttribute().get(0).getDefinition().getDeclaringType();
            TypeDefinition targetElement = candidate.getTargetAttribute().get(0).getDefinition().getDeclaringType();
            if (canBeSubstitutedBy(sourceElement, sourceClass) && canBeSubstitutedBy(targetElement, targetClass))
            {
                applicableMappings.add(candidate);
            }
            // also test attributes.
        }

        return applicableMappings;
    }
    
    /**
     * Filter {@link ModelCentroidCell}s to leave only those that can
     * target the specified source and target classes.
     * 
     * @param centroidMappings
     * 				cells to filter.
     * @param sourceClass
     * 				source class that mapping must apply to.
     * @param targetClass
     * 				target class that mapping must apply to.
     * @return filtered list of mapping cells.
     */
    private static List<ModelCentroidCell> filterCentroid(
			List<ModelCentroidCell> centroidMappings, SchemaElement sourceClass,
			SchemaElement targetClass)
	{
		List<ModelCentroidCell> applicableMappings = new ArrayList<ModelCentroidCell>();
		for (ModelCentroidCell candidate : centroidMappings)
	    {
			TypeDefinition sourceElement = candidate.getSourceAttribute().get(0).getDefinition().getDeclaringType();
			TypeDefinition targetElement = candidate.getTargetAttribute().get(0).getDefinition().getDeclaringType();
	        if (canBeSubstitutedBy(sourceElement, sourceClass) && canBeSubstitutedBy(targetElement, targetClass))
	        {
	        	applicableMappings.add(candidate);
	        }
	        // also test attributes.
	    }
		return applicableMappings;
	}
    
    /**
     * Filter {@link ModelConcatenationOfAttributesCell}s to leave only those that can
     * target the specified source and target classes.
     * 
     * @param concatenationMappings
     *            cells to filter.
     * @param sourceClass
     *            source class that mapping must apply to.
     * @param targetClass
     *            target class that mapping must apply to.
     * @return filtered list of mappings cells.
     */
    private static List<ModelConcatenationOfAttributesCell> filterConcatenation(
            List<ModelConcatenationOfAttributesCell> concatenationMappings,
            SchemaElement sourceClass, SchemaElement targetClass)
    {
    	List<ModelConcatenationOfAttributesCell> applicableMappings = new ArrayList<ModelConcatenationOfAttributesCell>();
        for (ModelConcatenationOfAttributesCell candidate : concatenationMappings)
        {
            TypeDefinition targetElement = candidate.getTargetAttribute().get(0).getDefinition().getDeclaringType();
            boolean belongsToTargetClass = canBeSubstitutedBy(targetElement, targetClass);
            
            boolean belongsToSourceClass = true;
            List<GmlAttributePath> sourceAttributes = candidate.getSourceAttributes();
            for (GmlAttributePath sourceAttribute : sourceAttributes)
            {
            	TypeDefinition sourceElement = sourceAttribute.get(0).getDefinition().getDeclaringType();
            	 if (!canBeSubstitutedBy(sourceElement, sourceClass))
            	 {
            		 belongsToSourceClass = false;
            		 break;
            	 }
            }
            
            if (belongsToSourceClass && belongsToTargetClass)
            {
            	 applicableMappings.add(candidate);
            }
        }

        return applicableMappings;
    }
    
    /**
     * Determines if the given type can be substituted by the given element
     * 
	 * @param type the type definition
	 * @param element the element
	 * @return if the type can be substituted by the given element
	 */
	private static boolean canBeSubstitutedBy(TypeDefinition type,
			SchemaElement element) {
		Queue<TypeDefinition> toTest = new LinkedList<TypeDefinition>();
		toTest.add(type);
		
		while (!toTest.isEmpty()) {
			TypeDefinition test = toTest.poll();
			if (test.getDeclaringElements().contains(element)) return true;
			
			toTest.addAll(test.getSubTypes());
		}
		
		return false;
	}

	private String getName(Name element)
    {
        return element.getNamespaceURI() + ":" + element.getLocalPart();
    }

}
