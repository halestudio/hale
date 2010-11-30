/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.digest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAttributeMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelClassMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelStaticAssignmentCell;
import com.onespatial.jrc.tns.oml_to_rif.schema.Gml311SchemaBrowser;
import com.onespatial.jrc.tns.oml_to_rif.schema.Gml321SchemaBrowser;
import com.onespatial.jrc.tns.oml_to_rif.schema.SchemaBrowser;
import com.sun.xml.xsom.XSElementDecl;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * Alignment to model alignment digester.
 * 
 * @author simonp
 */
public class AlignmentToModelAlignmentDigester extends
        AbstractFollowableTranslator<Alignment, ModelAlignment>
{

    private static final String PROXY;
    private static final int PORT;

    static
    {
        String env = System.getenv("http_proxy");
        if (env == null)
        {
            env = "http://relay:8080";
        }

        try
        {
            URL url = new URL(env);
            PROXY = url.getHost();
            PORT = url.getPort();
        }
        catch (MalformedURLException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Translates a HALE {@link Alignment} into an intermediate
     * {@link ModelAlignment} format.
     * 
     * @param source
     *            {@link Alignment}
     * @return {@link ModelAlignment}
     * @throws TranslationException
     *             if any problems occurred during translation
     */
    @Override
    public ModelAlignment translate(Alignment source) throws TranslationException
    {
        List<ModelClassMappingCell> classMappings = new ArrayList<ModelClassMappingCell>();
        List<ModelAttributeMappingCell> attributeMappings = new ArrayList<ModelAttributeMappingCell>();
        ArrayList<ModelStaticAssignmentCell> staticAssigments = new ArrayList<ModelStaticAssignmentCell>();

        SchemaBrowser sourceBrowser = getSourceBrowser(source);
        Map<String, XSElementDecl> sourceFeatures = buildFeatureMap(sourceBrowser);

        SchemaBrowser targetBrowser = getTargetBrowser(source);
        Map<String, XSElementDecl> targetFeatures = buildFeatureMap(targetBrowser);

        for (ICell cell : source.getMap())
        {
            IEntity sourceEntity = cell.getEntity1();
            IEntity targetEntity = cell.getEntity2();

            boolean sourceIsFeatureClass = sourceEntity instanceof FeatureClass;
            boolean targetIsFeatureClass = targetEntity instanceof FeatureClass;

            if (sourceIsFeatureClass && targetIsFeatureClass)
            {
                classMappings.add(createCell((FeatureClass) sourceEntity,
                        (FeatureClass) targetEntity, sourceFeatures, targetFeatures));
            }
            else if (!sourceIsFeatureClass && !targetIsFeatureClass)
            {
                attributeMappings.add(createAttribute((Property) sourceEntity,
                        (Property) targetEntity, sourceBrowser, targetBrowser));
            }
            else if (sourceIsFeatureClass && !targetIsFeatureClass)
            {
                staticAssigments
                        .add(createStaticAssignment((Property) targetEntity, targetBrowser));
            }
            else
            {
                throw new TranslationException("Unhandled combination");
            }

        }

        return new ModelAlignment(sourceBrowser, targetBrowser, classMappings, attributeMappings,
                staticAssigments);

    }

    private ModelStaticAssignmentCell createStaticAssignment(Property targetEntity,
            SchemaBrowser targetBrowser) throws TranslationException
    {
        try
        {
            String content = null;
            for (IParameter param : targetEntity.getTransformation().getParameters())
            {
                if (param.getName().equals("defaultValue"))
                {
                    content = param.getValue();
                    break;
                }
            }
            return new ModelStaticAssignmentCell(targetBrowser
                    .decomposeHaleAttributePath(targetEntity.getAbout().getAbout()), content);
        }
        catch (SAXException e)
        {
            throw new TranslationException(e);
        }
    }

    private ModelAttributeMappingCell createAttribute(Property sourceEntity, Property targetEntity,
            SchemaBrowser sourceBrowser, SchemaBrowser targetBrowser) throws TranslationException
    {
        try
        {
            return new ModelAttributeMappingCell(sourceBrowser
                    .decomposeHaleAttributePath(sourceEntity.getAbout().getAbout()), targetBrowser
                    .decomposeHaleAttributePath(targetEntity.getAbout().getAbout()));
        }
        catch (SAXException e)
        {
            throw new TranslationException(e);
        }
    }

    private Map<String, XSElementDecl> buildFeatureMap(SchemaBrowser sourceBrowser)
            throws TranslationException
    {
        try
        {
            List<XSElementDecl> sourceFeatureDecls = sourceBrowser.getFeatureClassNames();
            Map<String, XSElementDecl> sourceFeatures = new LinkedHashMap<String, XSElementDecl>();
            for (XSElementDecl featureDecl : sourceFeatureDecls)
            {
                sourceFeatures.put(featureDecl.getTargetNamespace() + "/" + featureDecl.getName(),
                        featureDecl);
            }
            return sourceFeatures;
        }
        catch (SAXException e)
        {
            throw new TranslationException(e);
        }
    }

    private SchemaBrowser getTargetBrowser(Alignment source) throws TranslationException
    {
        return getBrowser(source.getSchema2().getLocation(), false);
    }

    private SchemaBrowser getSourceBrowser(Alignment source) throws TranslationException
    {
        return getBrowser(source.getSchema1().getLocation(), true);
    }

    private SchemaBrowser getBrowser(String location, boolean isGml311) throws TranslationException
    {
        SchemaBrowser result = null;
        try
        {
            if (isGml311)
            {
                result = new Gml311SchemaBrowser(location);
            }
            else
            {
                result = new Gml321SchemaBrowser(location, PROXY, PORT);
            }
        }
        catch (MalformedURLException e)
        {
            throw new TranslationException(e);
        }

        return result;
    }

    private ModelClassMappingCell createCell(FeatureClass sourceEntity, FeatureClass targetEntity,
            Map<String, XSElementDecl> sourceFeatures, Map<String, XSElementDecl> targetFeatures)
            throws TranslationException
    {
        return new ModelClassMappingCell(findFeatureElementDecl(sourceEntity, sourceFeatures),
                findFeatureElementDecl(targetEntity, targetFeatures), findFilter(sourceEntity));
    }

    private List<Restriction> findFilter(FeatureClass sourceEntity)
    {
        return sourceEntity.getAttributeValueCondition();
    }

    private XSElementDecl findFeatureElementDecl(FeatureClass entity,
            Map<String, XSElementDecl> nameToFeatureMap)
    {
        String featureName = entity.getAbout().getAbout();
        XSElementDecl result = nameToFeatureMap.get(featureName);
        return result;
    }

}
