/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.sun.xml.xsom.XSElementDecl;

/**
 * Test unit for {@link Gml311SchemaBrowser}. These tests refer to the INSPIRE
 * XML Schemas version 3.0.
 * 
 * @author richards
 */
public class Gml321SchemaBrowserTest extends AbstractSchemaBrowserTest
{
    private Log log = LogFactory.getLog(Gml321SchemaBrowserTest.class);

    /**
     * Tests reading a list of transportation data theme feature classes.
     * 
     * @throws MalformedURLException
     *             if the URL of the XML Schema is invalid
     * @throws SAXException
     *             if any errors occurred reading the list of feature classes
     */
    @Test
    public final void testGettingTransportFeatureClasses() throws MalformedURLException,
            SAXException
    {

        URL url = new URL("http://server1/inspire/RailwayTransportNetwork.xsd");
        assertNotNull(url);
        log.info(url);

        String schemaUrl = url.toExternalForm();

        List<String> expectedNames = Arrays.asList("AccessRestriction", "ConditionOfFacility",
                "CrossReference", "DesignSpeed", "ERoad", "FormOfWay", "FunctionalRoadClass",
                "GeneralisedLink", "GradeSeparatedCrossing", "Link", "LinkSequence", "LinkSet",
                "MaintenanceAuthority", "MarkerPost", "NamedPlace", "Network", "NetworkArea",
                "NetworkConnection", "NetworkElement", "NetworkProperty", "Node",
                "NominalTrackGauge", "NumberOfLanes", "NumberOfTracks", "OwnerAuthority",
                "RailwayArea", "RailwayElectrification", "RailwayLine", "RailwayLink",
                "RailwayLinkSequence", "RailwayNode", "RailwayStationArea", "RailwayStationCode",
                "RailwayStationNode", "RailwayType", "RailwayUse", "RailwayYardArea",
                "RailwayYardNode", "RestrictionForVehicles", "Road", "RoadArea", "RoadLink",
                "RoadLinkSequence", "RoadName", "RoadNode", "RoadServiceArea", "RoadServiceType",
                "RoadSurfaceCategory", "RoadWidth", "SpatialDataSet", "SpeedLimit",
                "TrafficFlowDirection", "TransportArea", "TransportLink", "TransportLinkSequence",
                "TransportLinkSet", "TransportNetwork", "TransportNode", "TransportPoint",
                "TransportProperty", "VehicleTrafficArea", "VerticalPosition");

        testFeatureClassNames(schemaUrl, expectedNames);
    }

    /**
     * Tests reading a list of cadastral parcel data theme feature classes.
     * 
     * @throws MalformedURLException
     *             if the URL of the XML Schema is invalid
     * @throws SAXException
     *             if any errors occurred reading the list of feature classes
     */
    @Test
    public final void testGettingCadastralFeatureClasses() throws MalformedURLException,
            SAXException
    {

        URL url = new URL("http://server1/inspire/CadastralParcels.xsd");
        assertNotNull(url);
        log.info(url);

        String schemaUrl = url.toExternalForm();

        List<String> expectedNames = Arrays.asList("AdministrativeBoundary", "AdministrativeUnit",
                "BasicPropertyUnit", "CadastralBoundary", "CadastralParcel", "CadastralZoning",
                "Condominium", "NUTSRegion", "NamedPlace", "SpatialDataSet");

        testFeatureClassNames(schemaUrl, expectedNames);
    }

    /**
     * Tests that it is possible to read a list of top-level attributes.
     * 
     * @throws MalformedURLException
     *             if the URL of the XML Schema is invalid
     * @throws SAXException
     *             if any errors occurred reading the list of feature classes
     * @throws TranslationException
     *             if unable to decompose the HALE attribute path
     */
    @Test
    public final void canGetTopLevelAttributes() throws MalformedURLException, SAXException,
            TranslationException
    {

        URL url = new URL("http://server1/inspire/CadastralParcels.xsd");
        SchemaBrowser browser = getBrowser(url.toExternalForm());

        GmlAttributePath result = browser
                .decomposeHaleAttributePath("urn:x-inspire:specification:gmlas:CadastralParcels:"
                        + "3.0/CadastralParcel/geometry");
        assertNotNull(result);
        assertThat(result.size(), is(1));
        GmlAttribute rootAttribute = result.get(0);
        assertThat(rootAttribute.getObjectElement().getTargetNamespace(),
                is("urn:x-inspire:specification:gmlas:CadastralParcels:3.0"));
        assertThat(rootAttribute.getObjectElement().getName(), is("CadastralParcel"));
        assertThat(rootAttribute.getAttributeElement().getTargetNamespace(),
                is("urn:x-inspire:specification:gmlas:CadastralParcels:3.0"));
        assertThat(rootAttribute.getAttributeElement().getName(), is("geometry"));

    }

    /**
     * Tests that it is possible to read nested element attributes.
     * 
     * @throws MalformedURLException
     *             if the URL of the XML Schema is invalid
     * @throws SAXException
     *             if any errors occurred reading the list of feature classes
     * @throws TranslationException
     *             if unable to decompose the HALE attribute path
     */
    @Test
    public final void canGetNestedElementAttribute() throws MalformedURLException, SAXException,
            TranslationException
    {

        URL url = new URL("http://server1/inspire/CadastralParcels.xsd");
        SchemaBrowser browser = getBrowser(url.toExternalForm());

        GmlAttributePath result = browser
                .decomposeHaleAttributePath("urn:x-inspire:specification:gmlas:CadastralParcels:3.0"
                        + "/CadastralParcel/inspireId;Identifier;localId");
        assertNotNull(result);

        assertNotNull(result);
        assertThat(result.size(), is(2));
        GmlAttribute rootAttribute = result.get(0);
        assertThat(rootAttribute.getObjectElement().getTargetNamespace(),
                is("urn:x-inspire:specification:gmlas:CadastralParcels:3.0"));
        assertThat(rootAttribute.getObjectElement().getName(), is("CadastralParcel"));
        assertThat(rootAttribute.getAttributeElement().getTargetNamespace(),
                is("urn:x-inspire:specification:gmlas:CadastralParcels:3.0"));
        assertThat(rootAttribute.getAttributeElement().getName(), is("inspireId"));

        GmlAttribute nestedAttribute = result.get(1);
        assertThat(nestedAttribute.getObjectElement().getTargetNamespace(),
                is("urn:x-inspire:specification:gmlas:BaseTypes:3.2"));

        assertThat(nestedAttribute.getObjectElement().getName(), is("Identifier"));
        assertThat(nestedAttribute.getAttributeElement().getTargetNamespace(),
                is("urn:x-inspire:specification:gmlas:BaseTypes:3.2"));
        assertThat(nestedAttribute.getAttributeElement().getName(), is("localId"));

    }

    /**
     * Tests that it is possible to find the attributes of a feature.
     * 
     * @throws MalformedURLException
     *             if the URL of the XML Schema is invalid
     * @throws SAXException
     *             if any errors occurred reading the feature class names from
     *             the schem browser
     */
    @Test
    public final void canFindTheAttributesOfAFeature() throws MalformedURLException, SAXException
    {
        URL url = new URL("http://server1/inspire/CadastralParcels.xsd");
        SchemaBrowser browser = getBrowser(url.toExternalForm());
        XSElementDecl featureClass = browser.getFeatureClassNames().get(0);

        List<GmlAttribute> result = browser.findAttributes(featureClass);
        assertNotNull(result);
        // CHECKSTYLE:OFF
        assertThat(result.size(), is(9));
        // CHECKSTYLE:ON

        List<String> attributeNames = new ArrayList<String>();
        for (GmlAttribute attribute : result)
        {
            attributeNames.add(attribute.getAttributeElement().getName());
        }

        assertThat(attributeNames, is(Arrays.asList("admUnit", "beginLifespanVersion", "country",
                "endLifespanVersion", "geometry", "inspireId", "legalStatus", "nationalLevel",
                "technicalStatus")));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SchemaBrowser getBrowser(String schemaUrl) throws MalformedURLException
    {
        // CHECKSTYLE:OFF
        return new Gml321SchemaBrowser(schemaUrl, "local.server.proxy", 8080);
        // CHECKSTYLE:ON
    }
}
