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
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType.AND;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType.NOT;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.LogicalType.OR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3._2007.rif.And;
import org.w3._2007.rif.ArgsExprType;
import org.w3._2007.rif.ArgsUNITERMType;
import org.w3._2007.rif.Assert;
import org.w3._2007.rif.Atom;
import org.w3._2007.rif.Const;
import org.w3._2007.rif.ContentFORMULAType;
import org.w3._2007.rif.ContentTERMType;
import org.w3._2007.rif.Declare;
import org.w3._2007.rif.Do;
import org.w3._2007.rif.Document;
import org.w3._2007.rif.Equal;
import org.w3._2007.rif.Exists;
import org.w3._2007.rif.Expr;
import org.w3._2007.rif.ExternalFORMULAType;
import org.w3._2007.rif.ExternalTERMType;
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
import com.onespatial.jrc.tns.oml_to_rif.model.rif.CentroidMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ComparisonType;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ConcatenationMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.IdentifierMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifDocument;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelRifMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.ModelSentence;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.PropertyMapping;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.StaticAssignment;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;
import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable.Type;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.cst.corefunctions.ConcatenationOfAttributesFunction;

/**
 * Translates a collection of {@link ModelSentence} instances into a collection
 * of {@link Sentence} instances. NB {@link Cell}s and {@link Sentence}s do not
 * necessarily (nor usually) align one-to-one.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Susanne Reinwarth / TU Dresden
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
        
        ThenPart then = factory.createThenPart();
        implies.setThen(then);
        Do do1 = factory.createDo();
        then.setDo(do1);
        
        //special treatment for filter IS NULL (NOT_EXISTS), filter supported for only one "IS NULL"-attribute
        if (s.isAttributeFilterSentence() && s.getMappingConditions().get(0).getOperator().equals(NOT_EXISTS))
        {
        	s = insertINegExists(s, if1, exists, do1);
        }
        //special treatment for filter IS NOT NULL (EXISTS), filter supported for only one "IS NOT NULL"-attribute
        else if (s.isAttributeFilterSentence() && s.getMappingConditions().get(0).getOperator().equals(EXISTS))
        {
        	if1.setExists(exists);
        	s.getMappingConditions().clear();
        }
        else
        {
        	if1.setExists(exists);
        }
        
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
        for (StaticAssignment staticAssignment : s.getStaticAssignments())
        {
            RifVariable contextVariable = staticAssignment.getTarget().getContextVariable();
            Frame match = map.get(contextVariable);
            if (match == null)
            {
                map.put(contextVariable, initialiseFrame(contextVariable));
            }
        }
        for (IdentifierMapping mapping : s.getIdentifierMappings())
        {
        	RifVariable contextVariable = mapping.getTarget().getContextVariable();
        	Frame match = map.get(contextVariable);
        	if (match == null)
        	{
        		map.put(contextVariable, initialiseFrame(contextVariable));
        	}
        }
        for (PropertyMapping mapping : s.getPropertyMappings())
        {
            RifVariable contextVariable = mapping.getTarget().getContextVariable();
            Frame match = map.get(contextVariable);
            if (match == null)
            {
                map.put(contextVariable, initialiseFrame(contextVariable));
            }
        }
        for (ConcatenationMapping mapping : s.getConcatenationMappings())
        {
        	RifVariable contextVariable = mapping.getTarget().getContextVariable();
        	Frame match = map.get(contextVariable);
        	if (match == null)
        	{
        		map.put(contextVariable, initialiseFrame(contextVariable));
        	}
        }
        for (CentroidMapping mapping : s.getCentroidMappings())
        {
        	RifVariable contextVariable = mapping.getTarget().getContextVariable();
        	Frame match = map.get(contextVariable);
        	if (match == null)
        	{
        		map.put(contextVariable, initialiseFrame(contextVariable));
        	}
        }
        
        for (StaticAssignment mapping : s.getStaticAssignments())
        {
            Frame frame = map.get(mapping.getTarget().getContextVariable());
            createStaticAssignmentSlot(mapping, frame);
        }
        for (IdentifierMapping mapping : s.getIdentifierMappings())
        {
        	createIdentifierAssert(exists, do1, s, mapping);
        	
        	Frame frame = map.get(mapping.getTarget().getContextVariable());
        	mapping.setSource(mapping.getTarget());
        	createAssignmentSlot(mapping, frame);
        }       
        for (PropertyMapping mapping : s.getPropertyMappings())
        {
            Frame frame = map.get(mapping.getTarget().getContextVariable());
            createAssignmentSlot(mapping, frame);
        }
        for (ConcatenationMapping mapping : s.getConcatenationMappings())
        {
        	Frame frame = map.get(mapping.getTarget().getContextVariable());
        	createConcatenationSlot(mapping, frame);
        }
        for (CentroidMapping mapping : s.getCentroidMappings())
        {
        	Frame frame = map.get(mapping.getTarget().getContextVariable());
        	createCentroidSlot(mapping, frame);
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
	
	private ModelSentence insertINegExists(ModelSentence s, If if1, Exists exists, Do do1)
    {
    	//create specific sentence structure for attributive filter IS NULL (negated EXISTS)
		log.fine("Creating comparative filter (IS NOT NULL)");
    	final Exists negExists = factory.createExists();
		Formula negFormula = factory.createFormula();
		negFormula.setExists(negExists);
		INeg neg = factory.createINeg();
		neg.setFormula(negFormula);
		Formula andFormula1 = factory.createFormula();
		andFormula1.setINeg(neg);
		And negAnd = factory.createAnd();    			
		negAnd.getFormula().add(andFormula1);
		Formula andFormula2 = factory.createFormula();
		andFormula2.setExists(exists);
		negAnd.getFormula().add(andFormula2);
		if1.setAnd(negAnd);
		
		Map<String, RifVariable> varMap = new LinkedHashMap<String, RifVariable>();
		varMap.putAll(s.getVariablesMap());
		Map<String, RifVariable> varMapNeg = new LinkedHashMap<String, RifVariable>();
		varMapNeg.putAll(s.getVariablesMap());			
		String negVar = s.getMappingConditions().get(0).getLeft().getName();
		varMap.keySet().remove(negVar);
		varMapNeg.keySet().removeAll(varMap.keySet());
		
		List<RifVariable> instVar = s.getVariables(Type.INSTANCE);
		for(RifVariable rifVar : instVar)
		{
			varMap.put(rifVar.getName(), rifVar);
		}
		
		s.getMappingConditions().clear();
		s.getVariablesMap().clear();
		s.getVariablesMap().putAll(varMapNeg);
		s.getVariablesMap().put(s.getSourceClass().getName(), s.getSourceClass());
		processChildren(s, negExists, do1);
					
		s.getVariablesMap().clear();
		s.getVariablesMap().putAll(varMap);
		
		return s;
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
        log.fine("Creating logical filter");
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
            log.fine("Filter is a NOT filter");
        }
        else
        {
            if (mappingCondition.getLogicalType().equals(AND))
            {
                And and1 = factory.createAnd();
                logicFilterFormula.setAnd(and1);
                createChildFilters(mappingCondition, and1.getFormula());
                log.fine("Filter is an AND filter");

            }
            else if (mappingCondition.getLogicalType().equals(OR))
            {
                Or or = factory.createOr();
                logicFilterFormula.setOr(or);
                createChildFilters(mappingCondition, or.getFormula());
                log.fine("Filter is an OR filter");
            }
        }
        list.add(logicFilterFormula);
    }

    private void createComparativeFilter(List<Formula> list,
            ModelRifMappingCondition mappingCondition)
    {
        log.fine("Creating comparative filter");
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
        else if (mappingCondition.getOperator().equals(ComparisonType.EXISTS)) {
        	//nothing else to do here
        }
        else if (mappingCondition.getOperator().equals(ComparisonType.NOT_EXISTS)) {
        	throw new UnsupportedOperationException(
        			"Comparison type not yet supported for feature types: "
        			+ mappingCondition.getOperator().toString());
        }else
        {
            throw new UnsupportedOperationException("Comparison type is not supported: "
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
        opConst.setType("rif:iri");
        opConst.getContent().add(mappingCondition.getOperator().getRifPredicate());
        op.setConst(opConst);

        atom.setArgs(args);
        args.setOrdered("yes");
        Var var = factory.createVar();
        var.getContent().add(mappingCondition.getLeft().getName());
        args.getTERM().add(var);
        Const argsConst = factory.createConst();
        argsConst.setType(getLiteralTypeFor(mappingCondition.getLiteralClass()));
        String literalValue = mappingCondition.getLiteralValue().toString();
        // remove any wildcards
        if (mappingCondition.getOperator().equals(ComparisonType.STRING_CONTAINS))
        {
            literalValue = literalValue.replaceAll("%", "");
        }
        argsConst.getContent().add(literalValue);
        args.getTERM().add(argsConst);
        log.fine("Filter is a " + mappingCondition.getOperator().toString() + " filter");
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
        log.fine("Filter is a " + mappingCondition.getOperator().toString() + " filter");
    }

    private String getLiteralTypeFor(Class<?> literalClass)
    {
        if (Long.class.isAssignableFrom(literalClass))
        {
            return "http://www.w3.org/2001/XMLSchema#integer";
        }
        if (Double.class.isAssignableFrom(literalClass))
        {
            return "http://www.w3.org/2001/XMLSchema#double";
        }
        return "http://www.w3.org/2001/XMLSchema#string";
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
        SlotFrameType slot = createSlotPartTargetVariable(staticAssignment.getTarget().getPropertyName());
        Const const2 = factory.createConst();
        const2.setType(getLiteralTypeFor(String.class));
        const2.getContent().add(staticAssignment.getContent());
        slot.getContent().add(const2);
        frame.getSlot().add(slot);
    }

    private void createAssignmentSlot(PropertyMapping mapping, Frame frame)
    {
        SlotFrameType slot = createSlotPartTargetVariable(mapping.getTarget().getPropertyName());
        Var var1 = factory.createVar();
        var1.getContent().add(mapping.getSource().getName());
        slot.getContent().add(var1);
        frame.getSlot().add(slot);
    }
    
    private void createCentroidSlot(CentroidMapping mapping, Frame frame)
    {
    	//create target
    	SlotFrameType slot = createSlotPartTargetVariable(mapping.getTarget().getPropertyName());
    	
    	// create an <External>/<content>/<Expr>/ element hierarchy
    	ArgsExprType args = factory.createArgsExprType();
    	args.setOrdered("yes");
    	Var var = factory.createVar();
		var.getContent().add(mapping.getSource().getName());
		args.getConstOrVarOrExternal().add(var);
		Const constOp = factory.createConst();
    	constOp.setType("rif:iri");
    	constOp.getContent().add(mapping.getCentroidIri());
    	Op op = factory.createOp();
    	op.setConst(constOp);
    	Expr expr = factory.createExpr();
    	expr.setOp(op);
    	expr.setArgs(args);
    	ContentTERMType content = factory.createContentTERMType();
    	content.setExpr(expr);
    	ExternalTERMType external = factory.createExternalTERMType();
    	external.setContent(content);
    	
    	JAXBElement<ExternalTERMType> slotPartExternal = factory.createSlotFrameTypeExternal(external);
    	slot.getContent().add(slotPartExternal);    	
    	frame.getSlot().add(slot);
    }
    
    private void createConcatenationSlot(ConcatenationMapping mapping, Frame frame)
    {
    	/*
    	 * Annotation to RIF-DTB-function "string-join"
    	 * --------------------------------------------
    	 * Returns an xs:string created by concatenating the members of the input-sequence
    	 * using the last member as a separator. If the last member is the zero-length
    	 * string (""), then the members of the sequence are concatenated without a separator.
    	 * In contrast to OML no separator will be set at the end of the concatenation string.
    	 */
    	
    	//create target
    	SlotFrameType slot = createSlotPartTargetVariable(mapping.getTarget().getPropertyName());
    	
    	//sort sources according to the order in concatString
    	List<RifVariable> sources = sortConcatenationSources(mapping);
    	
    	// create an <External>/<content>/<Expr>/ element hierarchy
    	ArgsExprType args = factory.createArgsExprType();
    	args.setOrdered("yes");
    	for (RifVariable source : sources)
    	{
    		Var var = factory.createVar();
    		var.getContent().add(source.getName());
    		args.getConstOrVarOrExternal().add(var);
    	}
    	Const constSeparator = factory.createConst();
    	constSeparator.setType(getLiteralTypeFor(String.class));
    	constSeparator.getContent().add(mapping.getSeparator());
    	args.getConstOrVarOrExternal().add(constSeparator);
    	Const constOp = factory.createConst();
    	constOp.setType("rif:iri");
    	constOp.getContent().add("http://www.w3.org/2007/rif-builtin-function#string-join");
    	Op op = factory.createOp();
    	op.setConst(constOp);
    	Expr expr = factory.createExpr();
    	expr.setOp(op);
    	expr.setArgs(args);
    	ContentTERMType content = factory.createContentTERMType();
    	content.setExpr(expr);
    	ExternalTERMType external = factory.createExternalTERMType();
    	external.setContent(content);
    	
    	JAXBElement<ExternalTERMType> slotPartExternal = factory.createSlotFrameTypeExternal(external);
    	slot.getContent().add(slotPartExternal);    	
    	frame.getSlot().add(slot);
    }
    
    private void createIdentifierAssert(Exists exists, Do do1, ModelSentence s, IdentifierMapping mapping)
    {
    	String namespaceBaseTypes = "urn:x-inspire:specification:gmlas:BaseTypes:3.2:";
    	String identifier = "Identifier";
    	String localId = "localId";
    	String namespace = "namespace";
    	String versionId = "versionId";
    	String versionNilReason = "identifiertype-versionId";
    	String nilReason = "nilReason";
    	
    	mapping.getTarget().setType(Type.INSTANCE);
    	mapping.getTarget().setClassName(namespaceBaseTypes + identifier);
    	recurseChildren(exists, do1, mapping.getTarget(), s, false);
    	
    	Frame frame = factory.createFrame();
    	
    	Var var = factory.createVar();
    	var.getContent().add(mapping.getTarget().getName());
    	org.w3._2007.rif.Object frameObject = factory.createObject();
    	frameObject.setVar(var);
        frame.setObject(frameObject);
    	
    	SlotFrameType slotId = createSlotPartTargetVariable(namespaceBaseTypes + localId);
        Var varId = factory.createVar();
        varId.getContent().add(mapping.getSource().getName());
        slotId.getContent().add(varId);
        frame.getSlot().add(slotId);
        
        SlotFrameType slotNamespace = createSlotPartTargetVariable(namespaceBaseTypes + namespace);
        slotNamespace.setOrdered("yes");
        Const constNamespace = factory.createConst();
        constNamespace.setType(getLiteralTypeFor(String.class));
        constNamespace.getContent().add(mapping.getNamespace());
        slotNamespace.getContent().add(constNamespace);
        frame.getSlot().add(slotNamespace);
        
        if (mapping.getVersionId() != "")
        {
        	SlotFrameType slotVersionId = createSlotPartTargetVariable(namespaceBaseTypes + versionId);
        	slotVersionId.setOrdered("yes");
        	Const constVersionId = factory.createConst();
        	constVersionId.setType(getLiteralTypeFor(String.class));
        	constVersionId.getContent().add(mapping.getVersionId());
        	slotVersionId.getContent().add(constVersionId);
        	frame.getSlot().add(slotVersionId);
        }
        else if (mapping.getVersionNilReason() != "")
        {
        	Frame frameVersionNilReason = factory.createFrame();
        	
        	Var varVersionId = factory.createVar();
        	varVersionId.getContent().add(versionNilReason);
        	org.w3._2007.rif.Object frameObject2 = factory.createObject();
        	frameObject2.setVar(varVersionId);
            frameVersionNilReason.setObject(frameObject2);
        	
        	SlotFrameType slotVersionNilReason = createSlotPartTargetVariable(namespaceBaseTypes + nilReason);
        	slotVersionNilReason.setOrdered("yes");
        	Const constVersionNilReason = factory.createConst();
        	constVersionNilReason.setType(getLiteralTypeFor(String.class));
        	constVersionNilReason.getContent().add(mapping.getVersionNilReason());
        	slotVersionNilReason.getContent().add(constVersionNilReason);
        	frameVersionNilReason.getSlot().add(slotVersionNilReason);
        	
        	Target target2 = factory.createAssertTarget();
        	target2.setFrame(frameVersionNilReason);
        	Assert assert2 = factory.createAssert();
        	assert2.setTarget(target2);
        	do1.getActions().getACTION().add(assert2);
        	
        	SlotFrameType slotVersionId = createSlotPartTargetVariable(namespaceBaseTypes + versionId);
        	slotVersionId.setOrdered("yes");
        	Var varVersionId2 = factory.createVar();
        	varVersionId2.getContent().add(versionNilReason);
        	slotVersionId.getContent().add(varVersionId2);
        	frame.getSlot().add(slotVersionId);
        }
    	
        Target target = factory.createAssertTarget();
        target.setFrame(frame);
    	Assert assert1 = factory.createAssert();  
    	assert1.setTarget(target);
    	do1.getActions().getACTION().add(assert1);
    }
    
    private SlotFrameType createSlotPartTargetVariable(String targetPropertyName)
    {
    	SlotFrameType slot = factory.createSlotFrameType();
    	slot.setOrdered("yes");
    	Const const1 = factory.createConst();
    	const1.getContent().add(targetPropertyName);
    	const1.setType("rif:iri");
    	slot.getContent().add(const1);
		return slot;
    }

    private void recurseChildren(final Exists exists, Do do1, RifVariable variable,
            ModelSentence sentence, boolean isSource)
    {
        List<RifVariable> children = sentence.findChildren(variable);
        if (isSource)
        {
        	if (variable.getType().equals(Type.INSTANCE))
        	{
        		if (!sentence.isAttributeFilterSentence())
        		{
        			exists.getDeclare().add(createSourceDeclare(variable));
        			exists.getFormula().getAnd().getFormula().add(
        			createSourceInstanceMembershipFormula(sentence, variable));
        		}
        	}
        	else
        	{
        		exists.getDeclare().add(createSourceDeclare(variable));
        	}
        }
        else
        {
            // if (!children.isEmpty()) // problem?

            if (variable.getType() == Type.INSTANCE)
            {
            	if (sentence.isAttributeFilterSentence())
            	{
            		exists.getDeclare().add(createSourceDeclare(variable));
        			exists.getFormula().getAnd().getFormula().add(
        			createSourceInstanceMembershipFormula(sentence, variable));
            	}
            	else
            	{
            		do1.getActionVar().add(createTargetVariableDeclare(variable));
            		do1.getActions().getACTION().add(createTargetInstanceMembershipFormula(
            				do1.getActions(), sentence, variable));
            	}
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
        String name;
        if (!sentence.isAttributeFilterSentence())
        {
        	name = sentence.getSourceClass().getName();
        }
        else
        {
        	name = sentence.getTargetClass().getName();
        }
        var.getContent().add(name);
        Const const1 = factory.createConst();
        const1.setType("rif:iri");
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
        const1.setType("rif:iri");
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
        slot.setOrdered("yes");
        Const const1 = factory.createConst();
        const1.getContent().add(child.getPropertyName());
        const1.setType("rif:iri");
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
            targetInstanceActionVar.setNew(createElement("New"));
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
    
    private List<RifVariable> sortConcatenationSources(ConcatenationMapping mapping)
    {
    	//sort sources according to the order in concatString
    	String concatString = mapping.getConcatString();
    	String[] concat1 = concatString.split(ConcatenationOfAttributesFunction.INTERNALSEPERATOR);
    	List<String> concat2 = new ArrayList<String>();
    	for (String element : concat1)
    	{
    		String[] elementsOfElement = element.split(";");
    		String lastElement = elementsOfElement[elementsOfElement.length-1];
    		concat2.add(lastElement);
    	}
    	List<RifVariable> sources1 = mapping.getSources();
    	List<RifVariable> sources2 = new ArrayList<RifVariable>();
    	for (String element : concat2)
    	{
    		for (RifVariable source : sources1)
    		{
    			if (source.getPropertyName().endsWith(element))
    			{
    				sources2.add(source);
    			}
    		}
    	}
    	return sources2;
    }
}