/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * Abstract implementation of
 * {@link com.onespatial.jrc.tns.oml_to_rif.schema.SchemaBrowser}.
 * 
 * @author richards
 */
public abstract class AbstractGmlSchemaBrowser implements SchemaBrowser
{

    private static final Logger LOGGER = Logger.getLogger(AbstractGmlSchemaBrowser.class
            .getCanonicalName());

    private transient URL applicationSchemaUrl;

    private transient XSSchemaSet cachedSchema;

    private transient Proxy proxy;

    private static final int PART_COUNT = 3;

    /**
     * Returns the GML namespace for the version of GML applicable to the
     * extending class.
     * 
     * @return String
     */
    protected abstract String getGmlNamespace();

    /**
     * Returns the GML abstract feature element name for the version of GML
     * applicable to the extending class.
     * 
     * @return String
     */
    protected abstract String getAbstractFeatureElementName();

    /**
     * @param appSchemaUrl
     *            String
     * @param httpProxyHost
     *            String
     * @param httpProxyPort
     *            int
     * @throws MalformedURLException
     *             if the application schema URL is unable to be parsed
     */
    public AbstractGmlSchemaBrowser(String appSchemaUrl, String httpProxyHost,
            final int httpProxyPort) throws MalformedURLException
    {
        this(appSchemaUrl, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyHost,
                httpProxyPort)));
    }

    /**
     * @param sourceXsdUrl
     *            String
     * @throws MalformedURLException
     *             if the application schema URL is unable to be parsed
     */
    public AbstractGmlSchemaBrowser(final String sourceXsdUrl) throws MalformedURLException
    {
        this(sourceXsdUrl, null);
    }

    private AbstractGmlSchemaBrowser(final String appSchemaUrl, Proxy proxy)
            throws MalformedURLException
    {
        this.applicationSchemaUrl = new URL(appSchemaUrl);
        this.proxy = proxy;
    }

    /**
     * This method overrides the following parent class method.
     * 
     * @see com.onespatial.jrc.tns.oml_to_rif.schema.SchemaBrowser#getFeatureClassNames()
     *      .
     * @return List&lt;{@link XSElementDecl}&gt;
     * @throws SAXException
     *             if unable to parse application schema
     */
    @Override
    public List<XSElementDecl> getFeatureClassNames() throws SAXException
    {
        XSSchemaSet xschema = lazyLoadSchema();

        XSElementDecl type = xschema.getElementDecl(getGmlNamespace(),
                getAbstractFeatureElementName());

        if (type == null)
        {
            throw new IllegalStateException("Failed to find " + getAbstractFeatureElementName());
        }

        List<XSElementDecl> featureTypes = new ArrayList<XSElementDecl>();
        for (XSElementDecl sub : type.getSubstitutables())
        {
            if (!getGmlNamespace().equals(sub.getTargetNamespace()))
            {
                featureTypes.add(sub);
            }
        }

        Collections.sort(featureTypes, new Comparator<XSElementDecl>()
        {
            @Override
            public int compare(XSElementDecl o1, XSElementDecl o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return featureTypes;
    }

    /**
     * @see com.onespatial.jrc.tns.oml_to_rif.schema.SchemaBrowser#findAttributes(XSElementDecl)
     *      which this overrides.
     * @param featureClass
     *            {@link XSElementDecl} the class containing the sought-for
     *            attributes
     * @return List&lt;{@link GmlAttribute}&gt;
     */
    @Override
    public List<GmlAttribute> findAttributes(XSElementDecl featureClass)
    {
        ArrayList<GmlAttribute> result = new ArrayList<GmlAttribute>();

        for (XSParticle child : featureClass.getType().asComplexType().getContentType()
                .asParticle().getTerm().asModelGroup().getChildren())
        {
            for (XSParticle grandchild : child.getTerm().asModelGroup().getChildren())
            {
                XSElementDecl decl = grandchild.getTerm().asElementDecl();
                if (decl != null)
                {
                    result.add(new GmlAttribute(featureClass, decl, grandchild.getMinOccurs(),
                            grandchild.getMaxOccurs()));
                }
            }
        }

        Collections.sort(result);

        return result;
    }

    /**
     * Decompose a slash separated string list of attributes.
     * 
     * @param attributePath
     *            the path to decompose.
     * @return an object representing the decomposition.
     * @throws SAXException
     *             if failed to process application schema.
     * @throws TranslationException
     *             if unable to split attribute path by a given regex (which is
     *             hardcoded to a semi-colon in this method)
     */
    public GmlAttributePath decomposeHaleAttributePath(String attributePath) throws SAXException,
            TranslationException
    {

        XSSchemaSet schema = lazyLoadSchema();
        GmlAttributePath binding = new GmlAttributePath();

        List<String> nestedParts = split(attributePath, ";", 1);

        GmlAttribute currentAttribute = findRootAttribute(attributePath, schema, nestedParts);

        binding.add(currentAttribute);

        Iterator<String> ni = nestedParts.iterator();
        ni.next(); // skip initial top level attribute and handle only nested
        // parts.
        while (ni.hasNext())
        {
            String innerClassName = ni.next();
            String innerAttributeName = ni.next();

            currentAttribute = createGmlAttribute(currentAttribute.getAttributeElement(),
                    innerClassName, innerAttributeName);
            binding.add(currentAttribute);
        }

        return binding;
    }

    private GmlAttribute findRootAttribute(String attributePath, XSSchemaSet schema,
            List<String> nestedParts) throws TranslationException
    {
        String primaryAttributePart = nestedParts.get(0);

        List<String> parts = splitIntoParts(schema, primaryAttributePart);

        if (parts.size() != PART_COUNT)
        {
            throw new IllegalArgumentException("Found more parts to expression that expected "
                    + attributePath + " expected 3, but found " + parts);
        }

        String namespaceURI = parts.get(0);
        String className = parts.get(1);
        String attributeName = parts.get(2);

        XSElementDecl declaringElement = schema.getElementDecl(namespaceURI, className);
        LOGGER.info(namespaceURI + ":" + className);

        if (!declaringElement.getType().isComplexType())
        {
            throw new IllegalStateException("AttributeDecomposition only works with ComplexTypes.");
        }

        XSContentType content = declaringElement.getType().asComplexType().getContentType();

        XSParticle particle = findElementDecl(content.asParticle(), attributeName);
        if (particle == null)
        {
            throw new TranslationException("Attribute not found in schema: " + attributeName);
        }
        XSElementDecl attributeTypeDefinition = particle.getTerm().asElementDecl();
        int minOccurs = particle.getMinOccurs();
        int maxOccurs = particle.getMaxOccurs();

        GmlAttribute currentAttribute = new GmlAttribute(declaringElement, attributeTypeDefinition,
                minOccurs, maxOccurs);
        return currentAttribute;
    }

    private GmlAttribute createGmlAttribute(XSElementDecl originalAttribute,
            String objectElementName, String attributeElementName)
    {
        XSParticle objectParticle = findElementDecl(originalAttribute.getType().asComplexType()
                .getContentType().asParticle(), objectElementName);

        XSElementDecl objectElement = objectParticle.getTerm().asElementDecl();

        XSParticle attributeParticule = findElementDecl(objectElement.getType().asComplexType()
                .getContentType().asParticle(), attributeElementName);

        XSElementDecl attributeElement = attributeParticule.getTerm().asElementDecl();

        return new GmlAttribute(objectElement, attributeElement, attributeParticule.getMinOccurs(),
                attributeParticule.getMaxOccurs());

    }

    private List<String> split(String stringToSplit, String regex, int mimimumExpected)
            throws TranslationException
    {

        String[] parts = stringToSplit.split(regex);
        List<String> result = new ArrayList<String>(parts.length);
        for (String part : parts)
        {
            part = part.trim();
            if (!part.isEmpty())
            {
                result.add(part);
            }
        }

        if (result.size() < mimimumExpected)
        {
            throw new TranslationException("Failed to spilt '" + stringToSplit
                    + "' into enough parts. Expected at least " + mimimumExpected + " but found "
                    + result.size() + " parts");
        }
        return result;
    }

    private XSParticle findElementDecl(XSParticle particle, String elementName)
    {
        XSTerm term = particle.getTerm();

        XSParticle result = null;
        if (term.isElementDecl())
        {
            if (term.asElementDecl().getName().equals(elementName))
            {
                result = particle;
            }
        }
        else if (term.isModelGroup())
        {
            for (XSParticle child : term.asModelGroup().getChildren())
            {
                result = findElementDecl(child, elementName);
                if (result != null)
                {
                    break;
                }
            }
        }
        return result;

    }

    private final List<String> splitIntoParts(XSSchemaSet schemas, String attributePath)
    {

        List<String> parts = null;
        for (XSSchema schema : schemas.getSchemas())
        {
            if (attributePath.startsWith(schema.getTargetNamespace()))
            {
                parts = new ArrayList<String>();
                parts.add(schema.getTargetNamespace());
                String tns = schema.getTargetNamespace().trim();
                for (String part : attributePath.substring(tns.length())
                        .split("/"))
                {
                    part = part.trim();
                    if (part.length() > 0)
                    {
                        parts.add(part);
                    }
                }
                break;
            }
        }

        if (parts == null)
        {
            throw new IllegalStateException("Could not find a schema with a target namespace "
                    + "that matched the beginning of the following attribute references. "
                    + attributePath);
        }

        return parts;

    }

    private XSSchemaSet lazyLoadSchema() throws SAXException
    {
        if (cachedSchema == null)
        {
            XSOMParser parser = new XSOMParser();
            List<Exception> exceptions = new ArrayList<Exception>();
            parser.setErrorHandler(new LoggingErrorHandler(exceptions));
            parser.setEntityResolver(new ResolverImpl(proxy));
            parser.parse(applicationSchemaUrl);
            cachedSchema = parser.getResult();
        }
        return cachedSchema;
    }

    /**
     * @return {@link Logger}
     */
    Logger getLogger()
    {
        return LOGGER;
    }

    class ResolverImpl implements EntityResolver
    {
        private Proxy resolverProxy;

        private List<String> excludedDomains = Arrays.asList(".1spatial.local");

        public ResolverImpl(Proxy proxy)
        {
            resolverProxy = proxy;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
                IOException
        {
            InputStream stream = null;
            URL systemUrl = null;
            boolean directConnection = true;
            try
            {
                systemUrl = new URL(systemId);
                directConnection = resolverProxy == null;
                directConnection |= systemId.startsWith("jar");

                if (!directConnection)
                {
                    String host = systemUrl.getHost();
                    for (String suffix : excludedDomains)
                    {
                        if (host.endsWith(suffix))
                        {
                            directConnection = true;
                            break;
                        }
                    }

                    if (!directConnection)
                    {
                        // direct connect if no domain specified
                        directConnection = host.indexOf('.') == -1;
                    }
                }

                stream = attemptConnection(directConnection, systemUrl);
            }
            catch (IOException e)
            {
                // if one approach failed, try the other one.
                try
                {
                    stream = attemptConnection(!directConnection, systemUrl);
                }
                catch (IOException e2)
                {
                    // if the other approach also failed, log it, but throw
                    // the original exception.
                    getLogger().severe(
                            "Failed to connect to " + systemId + " directConnect="
                                    + !directConnection + " " + e2.getLocalizedMessage());

                    getLogger().severe(
                            "Failed to connect to " + systemId + " directConnect="
                                    + directConnection + " " + e.getLocalizedMessage());
                    throw e;
                }

            }

            InputSource source = new InputSource(stream);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;
        }

        private InputStream attemptConnection(boolean directConnection, URL systemUrl)
                throws IOException
        {
            InputStream stream;
            if (directConnection)
            {
                stream = systemUrl.openStream();
            }
            else
            {
                stream = systemUrl.openConnection(resolverProxy).getInputStream();
            }
            return stream;
        }
    }

}
