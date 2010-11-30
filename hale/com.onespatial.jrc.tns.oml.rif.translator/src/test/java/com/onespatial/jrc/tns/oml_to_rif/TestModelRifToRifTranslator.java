/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.w3._2007.rif.And;
import org.w3._2007.rif.Assert;
import org.w3._2007.rif.Declare;
import org.w3._2007.rif.Do;
import org.w3._2007.rif.Document;
import org.w3._2007.rif.Formula;
import org.w3._2007.rif.Member;
import org.w3._2007.rif.Sentence;
import org.w3._2007.rif.Do.ActionVar;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.AlignmentToModelAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester;
import com.onespatial.jrc.tns.oml_to_rif.fixture.DomBasedUnitTest;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelAlignmentToModelRifTranslator;
import com.onespatial.jrc.tns.oml_to_rif.translate.ModelRifToRifTranslator;

/**
 * Tests for the translation of "Model RIF" (the internal proto-format of RIF)
 * into W3C RIF-PRD.
 * 
 * @author simonp
 */
public class TestModelRifToRifTranslator extends DomBasedUnitTest
{

    private Translator<URL, Document> translator;

    /**
     * Test-level initialisation.
     */
    @Before
    public void setUp()
    {
        translator = new UrlToAlignmentDigester().connect(new AlignmentToModelAlignmentDigester()
                .connect(new ModelAlignmentToModelRifTranslator()
                        .connect(new ModelRifToRifTranslator())));
    }

    /**
     * Tests that it is possible to translate the example 3 CP source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateExample3CP() throws TranslationException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/oml_to_rif/alignments/example3_cp.goml");
        org.w3._2007.rif.Document doc = translator.translate(url);
        assertNotNull(doc);
        assertNotNull(doc.getPayload());
        assertNotNull(doc.getPayload().getGroup());
        assertNotNull(doc.getPayload().getGroup().getSentence());
        assertThat(doc.getPayload().getGroup().getSentence().size(), is(1));
        assertNotNull(doc.getPayload().getGroup().getSentence().get(0));
        checkJavaBindings(doc);
    }

    /**
     * Tests that it is possible to translate the example 3 CP source dataset,
     * including a simple predicate filter.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to write out a DOM document containing the RIF-PRD
     */
    @Test
    public void testTranslateExample3CPSimpleFilter() throws TranslationException, JAXBException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_cp_filter.goml");
        org.w3._2007.rif.Document doc = translator.translate(url);
        assertNotNull(doc);
        assertNotNull(doc.getPayload());
        assertNotNull(doc.getPayload().getGroup());
        assertNotNull(doc.getPayload().getGroup().getSentence());
        assertThat(doc.getPayload().getGroup().getSentence().size(), is(1));
        assertNotNull(doc.getPayload().getGroup().getSentence().get(0));
        checkJavaBindings(doc);
        writeDom(getDomFromRif(doc), System.out);
    }

    /**
     * Tests that it is possible to translate the example 3 CP source dataset,
     * including a slightly more complex predicate filter.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to write out a DOM document containing the RIF-PRD
     */
    @Test
    public void testTranslateExample3CPComplexFilter() throws TranslationException, JAXBException
    {
        URL url = getClass()
                .getClassLoader()
                .getResource(
                        "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example3_complex_logical_filter.goml");
        org.w3._2007.rif.Document doc = translator.translate(url);
        assertNotNull(doc);
        assertNotNull(doc.getPayload());
        assertNotNull(doc.getPayload().getGroup());
        assertNotNull(doc.getPayload().getGroup().getSentence());
        assertThat(doc.getPayload().getGroup().getSentence().size(), is(1));
        assertNotNull(doc.getPayload().getGroup().getSentence().get(0));
        checkJavaBindings(doc);
        writeDom(getDomFromRif(doc), System.out);
    }

    /**
     * Tests that it is possible to translate the example 3 CP source dataset,
     * including a logical negation predicate filter.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws JAXBException
     *             if unable to write out a DOM document containing the RIF-PRD
     */
    @Test
    public void testTranslateExample1NegationFilter() throws TranslationException, JAXBException
    {
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn_road.goml");
        org.w3._2007.rif.Document doc = translator.translate(url);
        assertNotNull(doc);
        assertNotNull(doc.getPayload());
        assertNotNull(doc.getPayload().getGroup());
        assertNotNull(doc.getPayload().getGroup().getSentence());
        // CHECKSTYLE:OFF
        assertThat(doc.getPayload().getGroup().getSentence().size(), is(3));
        // CHECKSTYLE:ON
        assertNotNull(doc.getPayload().getGroup().getSentence().get(0));
        writeDom(getDomFromRif(doc), System.out);
    }

    private Sentence checkJavaBindings(Document result)
    {
        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertNotNull(result.getPayload().getGroup());
        assertNotNull(result.getPayload().getGroup().getSentence());
        assertThat(result.getPayload().getGroup().getSentence().size(), is(1));

        Sentence actualSentence = result.getPayload().getGroup().getSentence().get(0);
        assertNotNull(actualSentence.getImplies());
        assertNotNull(actualSentence.getImplies().getIf());
        assertNotNull(actualSentence.getImplies().getIf().getExists());
        assertNotNull(actualSentence.getImplies().getThen());
        assertNotNull(actualSentence.getImplies().getThen().getDo());
        checkDoElements(actualSentence.getImplies().getThen().getDo());

        // check contents of if
        And and = checkDeclareElement(actualSentence);
        checkAndChildren(and);

        return actualSentence;
    }

    private void checkDoElements(Do do1)
    {
        assertNotNull(do1.getActionVar());
        assertThat(do1.getActionVar().size(), is(greaterThanOrEqualTo(1)));
        // count number of news and frames in the collection
        int numNews = 0;
        int numFrames = 0;
        for (ActionVar v : do1.getActionVar())
        {
            assertNotNull(v);
            if (v.getNew() != null)
            {
                numNews++;
                checkActionVarTypeNew(v);
            }
            if (v.getFrame() != null)
            {
                numFrames++;
                checkActionVarTypeFrame(v);
            }
        }
        assertThat(numNews, is(equalTo(1)));

        int numAsserts = 0;
        for (Object action : do1.getActions().getACTION())
        {
            assertTrue(action instanceof org.w3._2007.rif.Assert);
            numAsserts++;
            org.w3._2007.rif.Assert a = (org.w3._2007.rif.Assert) action;
            assertNotNull(a);
            checkAssertTarget(a);
        }
        // CHECKSTYLE:OFF
        assertThat(numAsserts, is(equalTo(3)));
        // CHECKSTYLE:ON

    }

    private void checkAssertTarget(Assert a)
    {
        assertNotNull(a.getTarget());
        boolean hasContents = false;
        if (a.getTarget().getMember() != null)
        {
            hasContents = true;
            assertNotNull(a.getTarget().getMember().getInstance());
            assertNotNull(a.getTarget().getMember().getClazz());
            // add more checks
        }
        else if (a.getTarget().getFrame() != null)
        {
            hasContents = true;
            assertNotNull(a.getTarget().getFrame().getObject());
            assertNotNull(a.getTarget().getFrame().getSlot());
            // assertThat(a.getTarget().getFrame().getSlot().size(),
            // is(equalTo(6)));
            // add more checks
        }
        assertTrue("No contents found in Assert", hasContents);

    }

    private void checkActionVarTypeNew(ActionVar v)
    {
        assertNotNull(v.getVar());
        assertNotNull(v.getNew());
        // add more checks
    }

    private void checkActionVarTypeFrame(ActionVar v)
    {
        assertNotNull(v.getVar());
        assertNotNull(v.getFrame());
        assertNotNull(v.getFrame().getObject());
        assertNotNull(v.getFrame().getSlot());
        assertThat(v.getFrame().getSlot().size(), is(equalTo(1)));
        // add more checks
    }

    private And checkDeclareElement(Sentence actualSentence)
    {
        List<Declare> declareList = actualSentence.getImplies().getIf().getExists().getDeclare();
        assertNotNull(declareList);
        assertThat(declareList.size(), is(greaterThanOrEqualTo(1)));
        for (Declare d : declareList)
        {
            assertNotNull(d);
            assertNotNull(d.getVar());
            assertNotNull(d.getVar().getContent());
            assertThat(d.getVar().getContent().size(), is(equalTo(1)));
            assertNotNull(d.getVar().getContent().get(0));
        }
        assertNotNull(actualSentence.getImplies().getIf().getExists().getFormula());
        And and = actualSentence.getImplies().getIf().getExists().getFormula().getAnd();
        assertNotNull(and);
        return and;
    }

    private void checkAndChildren(And and)
    {
        assertNotNull(and.getFormula());
        assertThat(and.getFormula().size(), is(greaterThanOrEqualTo(1)));
        for (Formula formula : and.getFormula())
        {
            if (formula.getMember() != null)
            {
                checkMemberElement(formula.getMember());
            }
            else if (formula.getFrame() != null)
            {
                // checkPropertyFrame(formula.getFrame());
            }
        }

    }

    private void checkMemberElement(Member m)
    {
        assertNotNull(m);
        assertNotNull(m.getInstance());
        assertNotNull(m.getInstance().getVar());
        assertNotNull(m.getInstance().getVar().getContent());
        assertThat(m.getInstance().getVar().getContent().size(), is(equalTo(1)));
        assertNotNull(m.getInstance().getVar().getContent().get(0));
        assertThat((String) m.getInstance().getVar().getContent().get(0),
                is(equalTo("parcelarea-instance")));
        assertNotNull(m.getClazz());
        assertNotNull(m.getClazz().getConst());
        assertThat(m.getClazz().getConst().getType(), is(equalTo("rif:iri")));
        assertNotNull(m.getClazz().getConst().getContent());
        assertThat(m.getClazz().getConst().getContent().size(), is(equalTo(1)));
        assertNotNull(m.getClazz().getConst().getContent().get(0));
        assertThat((String) m.getClazz().getConst().getContent().get(0),
                is(equalTo("http://jrc.onespatial.com/cp/example3:ParcelArea")));
        assertNotNull(m.getInstance());
    }
}
