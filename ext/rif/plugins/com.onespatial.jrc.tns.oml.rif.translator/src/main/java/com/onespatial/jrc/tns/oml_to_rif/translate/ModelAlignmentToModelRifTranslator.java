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
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package com.onespatial.jrc.tns.oml_to_rif.translate;

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
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.NOT_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.OR_NODE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.opengis.feature.type.Name;

import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAttributeMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelClassMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelStaticAssignmentCell;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifDocument;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelSentence;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.PropertyMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.StaticAssignment;
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
            result.getSentences().add(translateClassMapping(alignment, c));
        }

        return result;
    }

    private ModelSentence translateClassMapping(ModelAlignment alignment,
            ModelClassMappingCell classMapping)
    {

        // determine which attribute mappings are applicable to this class
        // mapping.
        List<ModelAttributeMappingCell> applicableAttributeMappings = filter(alignment
                .getAttributeMappings(), classMapping.getSourceClass(), classMapping
                .getTargetClass());

        List<ModelStaticAssignmentCell> applicableStaticAssignments = filter(alignment
                .getStaticAssignments(), classMapping.getTargetClass());

        return buildModelSentance(classMapping, applicableAttributeMappings,
                applicableStaticAssignments);

    }

    private ModelSentence buildModelSentance(ModelClassMappingCell classMapping,
            List<ModelAttributeMappingCell> attributeMappings,
            List<ModelStaticAssignmentCell> staticAssignments)
    {

        ModelSentence sentence = new ModelSentence();

        sentence.setSourceClass(
                classMapping.getSourceClass().getElementName().getLocalPart().toLowerCase() + "-instance", //$NON-NLS-1$
                getName(classMapping.getSourceClass().getElementName()));
        sentence.setTargetClass(
                classMapping.getTargetClass().getElementName().getLocalPart().toLowerCase() + "-instance", //$NON-NLS-1$
                getName(classMapping.getTargetClass().getElementName()));

        for (ModelMappingCondition condition : classMapping.getMappingConditions())
        {
            sentence.addMappingCondition(buildRifMappingCondition(sentence,
                    (AbstractFilterNode) condition.getRoot()));
        }

        for (ModelAttributeMappingCell attributeMapping : attributeMappings)
        {
            buildPropertyMapping(sentence, attributeMapping);
        }

        for (ModelStaticAssignmentCell staticAssigment : staticAssignments)
        {
            buildStaticAssignment(sentence, staticAssigment);
        }

        return sentence;
    }

    private void buildStaticAssignment(ModelSentence sentence,
            ModelStaticAssignmentCell staticAssigment)
    {
        RifVariable targetVariable = descendGmlAttributePath(sentence, staticAssigment.getTarget(),
                false);
        sentence.addStaticAssigment(new StaticAssignment(targetVariable, staticAssigment
                .getContent()));
    }

    private void buildPropertyMapping(ModelSentence sentence,
            ModelAttributeMappingCell attributeMapping)
    {

        RifVariable sourceVariable = descendGmlAttributePath(sentence, attributeMapping
                .getSourceAttribute(), true);

        RifVariable targetVariable = descendGmlAttributePath(sentence, attributeMapping
                .getTargetAttribute(), false);

        sentence.addPropertyMapping(new PropertyMapping(sourceVariable, targetVariable));
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

                rifCondition.setLeft(getContents(sentence, cnode.getLeft()));
                rifCondition.setLiteralClass(cnode.getRight().getLiteralValue().getValueClass());
                rifCondition.setLiteralValue(cnode.getRight().getLiteralValue().toString());
                rifCondition.setRight(getContents(sentence, cnode.getRight()));

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
                .length())
                + "-" + leaf.getPropertyName().toLowerCase() + "-filter"; //$NON-NLS-1$ //$NON-NLS-2$
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

        String propertyName = fragment.getDefinition().getName(); //XXX what name to use here? //getName(fragment.getAttributeElement());
        RifVariable child = sentence.findChildAttribute(current, propertyName);
        if (child == null)
        {
            String variableName = fragment.getDefinition().getDeclaringType().getName().getLocalPart() + "-" //$NON-NLS-1$
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
            if (canBeSubstitutedBy(sourceElement, sourceClass)
                    && canBeSubstitutedBy(targetElement, targetClass))
            {
                applicableMappings.add(candidate);
            }
            // also test attributes.
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
        return element.getNamespaceURI() + ":" + element.getLocalPart(); //$NON-NLS-1$
    }

}
