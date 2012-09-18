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

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType.AND;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType.NOT;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType.OR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3._2007.rif.And;
import org.w3._2007.rif.ArgsUNITERMType;
import org.w3._2007.rif.Assert;
import org.w3._2007.rif.Atom;
import org.w3._2007.rif.Const;
import org.w3._2007.rif.ContentFORMULAType;
import org.w3._2007.rif.Declare;
import org.w3._2007.rif.Do;
import org.w3._2007.rif.Document;
import org.w3._2007.rif.Equal;
import org.w3._2007.rif.Exists;
import org.w3._2007.rif.ExternalFORMULAType;
import org.w3._2007.rif.Formula;
import org.w3._2007.rif.Frame;
import org.w3._2007.rif.GroupContents;
import org.w3._2007.rif.INeg;
import org.w3._2007.rif.If;
import org.w3._2007.rif.Implies;
import org.w3._2007.rif.Instance;
import org.w3._2007.rif.Left;
import org.w3._2007.rif.Member;
import org.w3._2007.rif.ObjectFactory;
import org.w3._2007.rif.Op;
import org.w3._2007.rif.Or;
import org.w3._2007.rif.Payload;
import org.w3._2007.rif.Right;
import org.w3._2007.rif.Sentence;
import org.w3._2007.rif.SlotFrameType;
import org.w3._2007.rif.ThenPart;
import org.w3._2007.rif.Var;
import org.w3._2007.rif.Assert.Target;
import org.w3._2007.rif.Do.ActionVar;
import org.w3._2007.rif.Do.Actions;
import org.w3c.dom.Element;

import com.onespatial.jrc.tns.oml_to_rif.RifExportException;
import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifDocument;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelSentence;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.PropertyMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.StaticAssignment;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable.Type;

import eu.esdihumboldt.commons.goml.align.Cell;

/**
 * Translates a collection of {@link ModelSentence} instances into a collection
 * of {@link Sentence} instances. NB {@link Cell}s and {@link Sentence}s do not
 * necessarily (nor usually) align one-to-one.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class ModelRifToRifTranslator extends
        AbstractFollowableTranslator<ModelRifDocument, Document>
{
    private ObjectFactory factory;
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    private static final transient Logger log = Logger.getAnonymousLogger();

    /**
     * Default constructor.
     */
    public ModelRifToRifTranslator()
    {
        factory = new ObjectFactory();
    }

    /**
     * @see com.onespatial.jrc.tns.oml_to_rif.api.Translator#translate(Object)
     *      which this method implements.
     * @param source
     *            {@link ModelRifDocument}
     * @return {@link Document}
     * @throws TranslationException
     *             if any exceptions are thrown during translation.
     */
    @Override
    public Document translate(ModelRifDocument source) throws TranslationException
    {
        final Document document = factory.createDocument();
        Payload payload = factory.createPayload();
        document.setPayload(payload);
        GroupContents group = factory.createGroupContents();
        payload.setGroup(group);
        for (ModelSentence s : source.getSentences())
        {
            group.getSentence().add(buildSentence(s));
        }
        return document;
    }

    private Sentence buildSentence(ModelSentence s)
    {
        // sort variables within sentence

        final Sentence sentence = factory.createSentence();
        final Implies implies = factory.createImplies();
        sentence.setImplies(implies);
        final If if1 = factory.createIf();
        implies.setIf(if1);
        final Exists exists = factory.createExists();
        if1.setExists(exists);

        ThenPart then = factory.createThenPart();
        implies.setThen(then);
        Do do1 = factory.createDo();
        then.setDo(do1);

        processChildren(s, exists, do1);

        return sentence;
    }

    private void processChildren(ModelSentence s, final Exists exists, Do do1)
    {
        Formula existsFormula = factory.createFormula();
        And and = factory.createAnd();
        existsFormula.setAnd(and);
        exists.setFormula(existsFormula);
        Actions actions = factory.createDoActions();
        do1.setActions(actions);
        for (RifVariable instanceVariable : s.getVariables(Type.INSTANCE))
        {
            // source instance variables
            if (!instanceVariable.isActionVar())
            {
                recurseChildren(exists, do1, instanceVariable, s, true);
            }
            else
            // target instance variables
            {
                recurseChildren(exists, do1, instanceVariable, s, false);
            }
        }

        Map<RifVariable, Frame> map = new LinkedHashMap<RifVariable, Frame>();
        for (PropertyMapping mapping : s.getPropertyMappings())
        {
            RifVariable contextVariable = mapping.getTarget().getContextVariable();
            Frame match = map.get(contextVariable);
            if (match == null)
            {
                map.put(contextVariable, initialiseFrame(contextVariable));
            }
        }

        for (StaticAssignment staticAssignment : s.getStaticAssignments())
        {
            RifVariable contextVariable = staticAssignment.getTarget().getContextVariable();
            Frame match = map.get(contextVariable);
            if (match == null)
            {
                map.put(contextVariable, initialiseFrame(contextVariable));
            }
        }

        for (PropertyMapping mapping : s.getPropertyMappings())
        {
            Frame frame = map.get(mapping.getTarget().getContextVariable());
            createAssignmentSlot(mapping, frame);
        }

        for (StaticAssignment staticAssignment : s.getStaticAssignments())
        {
            Frame frame = map.get(staticAssignment.getTarget().getContextVariable());
            createStaticAssignmentSlot(staticAssignment, frame);
        }

        for (ModelRifMappingCondition mappingCondition : s.getMappingConditions())
        {
            createFilter(exists.getFormula().getAnd().getFormula(), mappingCondition);
        }

        for (Frame frame : map.values())
        {
            Assert assert1 = factory.createAssert();
            Target target = factory.createAssertTarget();
            target.setFrame(frame);
            assert1.setTarget(target);
            actions.getACTION().add(assert1);
        }

    }

    private void createFilter(List<Formula> list, ModelRifMappingCondition mappingCondition)
    {
        // if it's a logical filter
        if (mappingCondition.isLogical())
        {
            createLogicalFilter(list, mappingCondition);
        }
        else if (mappingCondition.isComparative())
        {
            createComparativeFilter(list, mappingCondition);
        }
        else if (mappingCondition.isGeometric())
        {
            // TODO complete this bit
        }
    }

    private void createLogicalFilter(List<Formula> list, ModelRifMappingCondition mappingCondition)
    {
        log.fine("Creating logical filter"); //$NON-NLS-1$
        Formula logicFilterFormula = factory.createFormula();
        if (mappingCondition.getLogicalType().equals(NOT))
        {
            INeg negation = factory.createINeg();
            logicFilterFormula.setINeg(negation);
            // Formula subNegationFormula = factory.createFormula();
            List<Formula> notList = new ArrayList<Formula>();
            // notList.add(subNegationFormula);
            createChildFilters(mappingCondition, notList);
            negation.setFormula(notList.get(0));
            log.fine("Filter is a NOT filter"); //$NON-NLS-1$
        }
        else
        {
            if (mappingCondition.getLogicalType().equals(AND))
            {
                And and1 = factory.createAnd();
                logicFilterFormula.setAnd(and1);
                createChildFilters(mappingCondition, and1.getFormula());
                log.fine("Filter is an AND filter"); //$NON-NLS-1$

            }
            else if (mappingCondition.getLogicalType().equals(OR))
            {
                Or or = factory.createOr();
                logicFilterFormula.setOr(or);
                createChildFilters(mappingCondition, or.getFormula());
                log.fine("Filter is an OR filter"); //$NON-NLS-1$

            }
        }
        list.add(logicFilterFormula);
    }

    private void createComparativeFilter(List<Formula> list,
            ModelRifMappingCondition mappingCondition)
    {
        log.fine("Creating comparative filter"); //$NON-NLS-1$
        Formula filterFormula = factory.createFormula();
        list.add(filterFormula);
        if (mappingCondition.getOperator().equals(ComparisonType.NUMBER_EQUALS)
                || mappingCondition.getOperator().equals(ComparisonType.STRING_EQUALS))
        {
            createEqualsFilter(mappingCondition, filterFormula);
        }
        else if (mappingCondition.getOperator().equals(ComparisonType.NUMBER_GREATER_THAN)
                || mappingCondition.getOperator().equals(ComparisonType.NUMBER_LESS_THAN)
                || mappingCondition.getOperator().equals(ComparisonType.STRING_CONTAINS))
        {
            createExternalPredicateFilter(mappingCondition, filterFormula);
        }
        else
        {
            throw new UnsupportedOperationException("Comparison type is not supported: " //$NON-NLS-1$
                    + mappingCondition.getOperator().toString());
        }
    }

    private void createExternalPredicateFilter(ModelRifMappingCondition mappingCondition,
            Formula filterFormula)
    {
        // create an <External>/<content>/<Atom> element hierarchy
        ExternalFORMULAType external = factory.createExternalFORMULAType();
        filterFormula.setExternal(external);
        ContentFORMULAType content = factory.createContentFORMULAType();
        external.setContent(content);
        Atom atom = factory.createAtom();
        content.setAtom(atom);
        Op op = factory.createOp();
        ArgsUNITERMType args = factory.createArgsUNITERMType();
        atom.setOp(op);
        Const opConst = factory.createConst();
        opConst.setType("rif:iri"); //$NON-NLS-1$
        opConst.getContent().add(mappingCondition.getOperator().getRifPredicate());
        op.setConst(opConst);

        atom.setArgs(args);
        args.setOrdered("yes"); //$NON-NLS-1$
        Var var = factory.createVar();
        var.getContent().add(mappingCondition.getLeft().getName());
        args.getTERM().add(var);
        Const argsConst = factory.createConst();
        argsConst.setType(getLiteralTypeFor(mappingCondition.getLiteralClass()));
        String literalValue = mappingCondition.getLiteralValue().toString();
        // remove any wildcards
        if (mappingCondition.getOperator().equals(ComparisonType.STRING_CONTAINS))
        {
            literalValue = literalValue.replaceAll("%", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        argsConst.getContent().add(literalValue);
        args.getTERM().add(argsConst);
        log.fine("Filter is a " + mappingCondition.getOperator().toString() + " filter"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void createEqualsFilter(ModelRifMappingCondition mappingCondition, Formula filterFormula)
    {
        // create an <Equals> element
        Equal equal = factory.createEqual();
        filterFormula.setEqual(equal);
        Left left = factory.createLeft();
        Right right = factory.createRight();
        equal.setLeft(left);
        Var var = factory.createVar();
        var.getContent().add(mappingCondition.getLeft().getName());
        left.setVar(var);
        equal.setRight(right);
        Const const1 = factory.createConst();
        const1.setType(getLiteralTypeFor(mappingCondition.getLiteralClass()));
        right.setConst(const1);
        const1.getContent().add(mappingCondition.getLiteralValue());
        log.fine("Filter is a " + mappingCondition.getOperator().toString() + " filter"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String getLiteralTypeFor(Class<?> literalClass)
    {
        if (Long.class.isAssignableFrom(literalClass))
        {
            return "http://www.w3.org/2001/XMLSchema#integer"; //$NON-NLS-1$
        }
        if (Double.class.isAssignableFrom(literalClass))
        {
            return "http://www.w3.org/2001/XMLSchema#double"; //$NON-NLS-1$
        }
        return "http://www.w3.org/2001/XMLSchema#string"; //$NON-NLS-1$
    }

    private void createChildFilters(ModelRifMappingCondition mappingCondition, List<Formula> list)
    {
        for (ModelRifMappingCondition child : mappingCondition.getChildren())
        {
            createFilter(list, child);
        }
    }

    private void createStaticAssignmentSlot(StaticAssignment staticAssignment, Frame frame)
    {
        SlotFrameType slot = factory.createSlotFrameType();
        slot.setOrdered("yes"); //$NON-NLS-1$
        Const const1 = factory.createConst();
        const1.getContent().add(staticAssignment.getTarget().getPropertyName());
        const1.setType("rif:iri"); //$NON-NLS-1$
        slot.getContent().add(const1);
        Const const2 = factory.createConst();
        const2.setType("http://www.w3.org/2001/XMLSchema#string"); //$NON-NLS-1$
        const2.getContent().add(staticAssignment.getContent());
        slot.getContent().add(const2);
        frame.getSlot().add(slot);
    }

    private void createAssignmentSlot(PropertyMapping mapping, Frame frame)
    {
        SlotFrameType slot = factory.createSlotFrameType();
        slot.setOrdered("yes"); //$NON-NLS-1$
        Const const1 = factory.createConst();
        const1.getContent().add(mapping.getTarget().getPropertyName());
        const1.setType("rif:iri"); //$NON-NLS-1$
        slot.getContent().add(const1);
        Var var1 = factory.createVar();
        var1.getContent().add(mapping.getSource().getName());
        slot.getContent().add(var1);
        frame.getSlot().add(slot);
    }

    private void recurseChildren(final Exists exists, Do do1, RifVariable variable,
            ModelSentence sentence, boolean isSource)
    {
        List<RifVariable> children = sentence.findChildren(variable);
        if (isSource)
        {
            exists.getDeclare().add(createSourceDeclare(variable));
            if (variable.getType().equals(Type.INSTANCE))
            {
                exists.getFormula().getAnd().getFormula().add(
                        createSourceInstanceMembershipFormula(sentence, variable));
            }
        }
        else
        {
            // if (!children.isEmpty()) // problem?

            do1.getActionVar().add(createTargetVariableDeclare(variable));
            if (variable.getType() == Type.INSTANCE)
            {
                do1.getActions().getACTION()
                        .add(
                                createTargetInstanceMembershipFormula(do1.getActions(), sentence,
                                        variable));
            }

        }
        if (!children.isEmpty())
        {
            if (isSource)
            {
                Frame frame = initialiseFrame(variable);
                for (RifVariable child : children)
                {
                    recurseChildren(exists, do1, child, sentence, isSource);
                    createBindingSlot(child, frame);
                }
                Formula frameFormula = factory.createFormula();
                frameFormula.setFrame(frame);
                exists.getFormula().getAnd().getFormula().add(frameFormula);
            }
            else
            {
                for (RifVariable child : children)
                {
                    recurseChildren(exists, do1, child, sentence, isSource);
                }
            }
        }
    }

    private Frame initialiseFrame(RifVariable contextVariable)
    {
        Frame frame = factory.createFrame();
        frame = factory.createFrame();
        org.w3._2007.rif.Object frameObject = factory.createObject();
        frame.setObject(frameObject);
        Var var = factory.createVar();
        var.getContent().add(contextVariable.getName());
        frameObject.setVar(var);
        return frame;
    }

    private Formula createSourceInstanceMembershipFormula(ModelSentence sentence,
            RifVariable instanceVariable)
    {
        Formula result = factory.createFormula();
        Member member = factory.createMember();
        Instance instance = factory.createInstance();
        Var var = factory.createVar();
        String name = sentence.getSourceClass().getName();
        var.getContent().add(name);
        Const const1 = factory.createConst();
        const1.setType("rif:iri"); //$NON-NLS-1$
        const1.getContent().add(instanceVariable.getClassName());
        org.w3._2007.rif.Class clazz = factory.createClass();
        instance.setVar(var);
        clazz.setConst(const1);
        member.setInstance(instance);
        member.setClazz(clazz);
        result.setMember(member);
        return result;
    }

    private Assert createTargetInstanceMembershipFormula(Actions actions, ModelSentence sentence,
            RifVariable instanceVariable)
    {
        Assert assert1 = factory.createAssert();
        Target target = factory.createAssertTarget();
        Member member = factory.createMember();
        Instance instance = factory.createInstance();
        Var var = factory.createVar();
        var.getContent().add(instanceVariable.getName());
        instance.setVar(var);
        member.setInstance(instance);
        org.w3._2007.rif.Class clazz = factory.createClass();
        Const const1 = factory.createConst();
        const1.setType("rif:iri"); //$NON-NLS-1$
        const1.getContent().add(instanceVariable.getClassName());
        clazz.setConst(const1);
        member.setClazz(clazz);
        target.setMember(member);
        assert1.setTarget(target);
        return assert1;
    }

    private void createBindingSlot(RifVariable child, Frame frame)
    {
        SlotFrameType slot = factory.createSlotFrameType();
        slot.setOrdered("yes"); //$NON-NLS-1$
        Const const1 = factory.createConst();
        const1.getContent().add(child.getPropertyName());
        const1.setType("rif:iri"); //$NON-NLS-1$
        slot.getContent().add(const1);
        Var var1 = factory.createVar();
        var1.getContent().add(child.getName());
        slot.getContent().add(var1);
        frame.getSlot().add(slot);
    }

    private ActionVar createTargetVariableDeclare(RifVariable variable)
    {
        ActionVar targetInstanceActionVar = factory.createDoActionVar();
        Var var = factory.createVar();
        var.getContent().add(variable.getName());
        targetInstanceActionVar.setVar(var);
        if (variable.getType() == Type.INSTANCE)
        {
            targetInstanceActionVar.setNew(createElement("New")); //$NON-NLS-1$
        }
        else
        {
            Frame frame = initialiseFrame(variable.getContextVariable());
            createBindingSlot(variable, frame);
            targetInstanceActionVar.setFrame(frame);

        }
        return targetInstanceActionVar;
    }

    private Declare createSourceDeclare(RifVariable variable)
    {
        Declare propertyDeclare = factory.createDeclare();
        Var var = factory.createVar();
        var.getContent().add(variable.getName());
        propertyDeclare.setVar(var);
        return propertyDeclare;
    }

    private Element createElement(String tagName)
    {
        DocumentBuilder docBuilder;
        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.newDocument();
            return doc.createElement(tagName);
        }
        catch (ParserConfigurationException e)
        {
            throw new RifExportException(e);
        }
    }
}
