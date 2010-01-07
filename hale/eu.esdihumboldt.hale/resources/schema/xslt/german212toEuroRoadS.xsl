<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:de="http://www.esdi-humboldt.org/data/germany"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

<!-- 7-Aug-2008 mdv, adapted to gml 2.1.2 input; put quotes around the OBJART values (because now string in schema); 
     changed featuretype name to lowercase; changed encoding to ISO-8859-1 ; removed linebreak in 'Null or No value' -->

    <xsl:output encoding="ISO-8859-1"/>

    <!-- create the root node "er:EuroRoadSDataset", which is the base collection for complete transfer (not for updates) of  streetdata -->
    <xsl:template match="/*">
        <er:EuroRoadSDataset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.euroroads.org ../euroroads_3.1/EuroRoadS.xsd"
            xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink"
            xmlns:er="http://www.euroroads.org">

            <xsl:copy-of select="gml:boundedBy" />

            <!-- Find elements sharing the same ObjNr - build one complex Roadnet Elements "ER_AggregatedWay" and several RoadLinks -->
            <xsl:for-each-group select="//de:ver01_l"
                group-by="de:OBJNR">
                <!-- complex Roadnet Element TODO
            <xsl:value-of select="de:OBJNR"></xsl:value-of>
            -->
                <xsl:for-each select="current-group()">
                    <xsl:element name="gml:featureMember">
                        <xsl:element name="er:RoadLink">
                            <!--xsl:attribute name="gml:id" select="@gml:id"/-->
                            <xsl:attribute name="fid" select="@fid"/>

                            <xsl:element name="er:id">
                                <xsl:element name="er:permanentId">
                                    <xsl:value-of select="de:OB"/>
                                </xsl:element>
                            </xsl:element>
                            <xsl:element name="er:attributes">
                                <!-- validity is undefined for all attributes -->
                                <!-- **************************************************************************************************************************************************** -->
                                <!-- START RoadName -->
                                <!-- the road names are undefined for 3105 (Strassenkörper) und 3106 (Fahrbahn) and cannot be restored from complex Street
                                 For 3101 (Strasse) and 3102(way) there could be up to 3 names be defined - or marked wit NNNN
                            -->
                                <xsl:if test="de:OBJART ='3102' or de:OBJART ='3101'">
                                    <xsl:element name="er:RoadName">
                                        <xsl:element name="er:roadName1">
                                            <xsl:choose>
                                                <xsl:when test="de:GN='NNNN'">Null or No value</xsl:when>
                                                <xsl:otherwise>
                                                  <xsl:value-of select="de:GN"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:element>
                                        <xsl:element name="er:roadName2">
                                            <xsl:choose>
                                                <xsl:when test="de:KN='NNNN'">Null or No value</xsl:when>
                                                <xsl:otherwise>
                                                  <xsl:value-of select="de:KN"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:element>
                                        <xsl:if test="de:ZN != null">
                                            <xsl:element name="er:alternativeName">
                                                <xsl:value-of select="de:ZN"/>
                                            </xsl:element>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:if>
                                <!-- END RoadName -->
                                <!-- **************************************************************************************************************************************************** -->
                                <!-- **************************************************************************************************************************************************** -->
                                <!-- Start  RoadSurface -->
                                <!-- all roads are paved, there is only the possibility for ways (3102) 
                            surprisingly, this attribute does not occure
                            -->
                                <xsl:element name="er:RoadSurface">
                                    <xsl:element name="er:roadSurface">
                                        <xsl:choose>
                                            <xsl:when
                                                test="de:OBJART ='3102' and de:BEF!='1000' "
                                                >Unpaved</xsl:when>
                                            <xsl:otherwise>Paved</xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:element>
                                </xsl:element>
                                <!-- END RoadSurface -->
                                <!-- **************************************************************************************************************************************************** -->
                                <!-- **************************************************************************************************************************************************** -->
                                <!-- Start  RoadWidth -->
                                <!-- The wisth is stored either a measurement in dm or offered as width class
                                This will result in different acuuracies
                            -->
                                <xsl:choose>
                                    <xsl:when
                                        test="de:BRF&gt;0 and de:BRF&lt;9998">
                                        <xsl:element name="er:RoadWidth">
                                            <xsl:element name="er:roadWidth">
                                                <xsl:attribute name="uom">m</xsl:attribute>
                                                <xsl:value-of select="de:BRF*0.1"/>
                                            </xsl:element>
                                        </xsl:element>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:choose>
                                            <xsl:when test="de:BRV=6">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>6</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=9">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>9</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=12">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>12</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=15">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>15</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=18">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>18</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=21">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>21</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=24">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>24</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=27">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>27</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                            <xsl:when test="de:BRV=30">
                                                <xsl:element name="er:RoadWidth">
                                                  <xsl:element name="er:roadWidth"><xsl:attribute
                                                  name="uom"
                                                  >m</xsl:attribute>30</xsl:element>
                                                </xsl:element>
                                            </xsl:when>
                                        </xsl:choose>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <!-- END RoadWidth -->
                                <!-- **************************************************************************************************************************************************** -->

                                <!-- **************************************************************************************************************************************************** -->
                                <!-- Start  NumberOf Lanes -->
                                <!-- number of lanes is only partly provided
                                -->
                                <xsl:if test="de:FSZ&gt;0 and de:FSZ&lt;9997">
                                    <xsl:element name="er:NumberOfLanes">
                                        <xsl:element name="er:numberOfLanes">
                                            <xsl:element name="er:numberOfLanes">
                                                <xsl:value-of select="de:FSZ"/>
                                            </xsl:element>
                                            <!-- Note, there is a mistake on Min Occurence in the D65FeatureCatalogue Schema -->
                                            <xsl:element name="er:minMaxNumberOfLanes"
                                            >Average</xsl:element>
                                        </xsl:element>
                                    </xsl:element>
                                </xsl:if>
                                <!-- END Number Of Lanes -->
                                <!-- **************************************************************************************************************************************************** -->

                            </xsl:element>
                            <!-- END Attributes -->
                            <!-- **************************************************************************************************************************************************** -->

                            <!-- **************************************************************************************************************************************************** -->
                            <!-- Start  The Geometry -->
                            <!-- Here is a mistake in the source data; it is not allowed to have splitted geometry; so we ignore the differnt input LineStrings and build one
                                TODO: Check exactly
                            -->
                            <xsl:element name="er:curve">
                                <xsl:variable name="srs"
                                    select="de:the_geom/gml:MultiLineString/attribute::srsName"/>
                                <xsl:element name="gml:LineString">
                                    <xsl:attribute name="srsName">
                                        <xsl:value-of select="$srs"/>
                                    </xsl:attribute>

                                    <xsl:element name="gml:coordinates">
                                    <!-- TO FIX when multilinestring the leaf linestrings are concatenated, 
                                         but is too simple (assumes good order, and keeps double vertices in) -->
                                    <xsl:for-each
                                        select="de:the_geom/gml:MultiLineString/gml:lineStringMember/gml:LineString/gml:coordinates">

                                        <xsl:value-of select="concat(., ' ')" />  


                                    </xsl:for-each>
						</xsl:element>

                                </xsl:element>
                            </xsl:element>

                            <!-- END The Geometry -->
                            <!-- **************************************************************************************************************************************************** -->

                            <!-- START formOfWay -->
                            <xsl:element name="er:formOfWay">
                                <!-- classification is complicate and somehow arbritrary:
                                      OBJ-Art: 3102 (Weg)
                                      -> FKT: 1703 -> Walkway
                                      -> FKT: 1706 -> Bicycle
                                      -> FKT: 1710 -> Bicycle (in fact bicycle and pedestrians)
                                      -> all other FKT -> Tractor
                                  -->
                                <xsl:choose>
                                    <xsl:when test="de:OBJART ='3102'">
                                        <xsl:choose>
                                            <xsl:when test="de:FKT=1703">Walkway</xsl:when>
                                            <xsl:when test="de:FKT=1706">Bicycle</xsl:when>
                                            <xsl:when test="de:FKT=1710">Bicycle</xsl:when>
                                            <xsl:otherwise>Tractor</xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>

                                    <!-- classification is complicate and somehow arbritrary:
                                          OBJ-Art: 3101 (Weg)
                                          -> FKT: 1808 -> Pedestrian Zone
                                          -> all other FKT 
                                              -> WDM (Widmung):  1301 (bundesautobahn) -> Freeway
                                              -> WDM (Widmung): other SingleCarriageWay (cannot be differntiated according to Euroroads)
                                      -->
                                    <xsl:when test="de:OBJART ='3101'">
                                        <xsl:choose>
                                            <xsl:when test="de:FKT=1801">PedestrianZone</xsl:when>
                                            <xsl:otherwise>
                                                <xsl:choose>
                                                  <xsl:when test="de:WDM=1301">Freeway</xsl:when>
                                                  <xsl:otherwise>SingleCarriageway</xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>

                                    <!-- classification is complicate and somehow arbritrary:
                                          OBJ-Art: 3105 (Strassenkoerper) is used similar to street axis for splitted carriages
                                          -> WDM (Widmung):  1301 (bundesautobahn) -> Freeway
                                          -> WDM (Widmung): other SingleCarriageWay (cannot be differntiated according to Euroroads)
                                          NOTE: The freeway is currently double, because the combination of centerline and carriage way is droped during export. 
                                          Originally it is made with the complex Street type 3104
                                      -->
                                    <xsl:when test="de:OBJART =3105">
                                        <xsl:choose>
                                            <xsl:when test="de:WDM=1301">Freeway</xsl:when>
                                            <xsl:otherwise>DualCarriageway</xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>

                                    <!-- classification is complicate and somehow arbritrary:
                                          OBJ-Art: 3106 (Fahrbahn) is similar to carriages and only used for splitted carriages
                                          There is no possibility to define weather this is a freeway or a dual carriage way - so as default dualCarriageWay is choosen
                                      -->
                                    <xsl:when test="de:OBJART =3106"
                                    >DualCarriageway</xsl:when>
                                </xsl:choose>
                            </xsl:element>
                            <!-- / END formOfWay -->
                            <!-- **************************************************************************************************************************************************** -->
                            <!-- **************************************************************************************************************************************************** -->
                            <!-- Start functional Road Class -->
                            <xsl:element name="er:functionalRoadClass">
                                <xsl:choose>
                                    <!-- Streets or Carriage ways are classified according to their "Widmung" WDM, alternatively it could be made by their 
                                        international, national and local relevance IBD, BDU, BDI
                                        -->
                                    <xsl:when test="de:OBJART='3101' or de:OBJART='3105'">
                                        <xsl:choose>
                                            <xsl:when test="de:WDM=1301">MainRoad</xsl:when>
                                            <xsl:when test="de:WDM=1303">FirstClass</xsl:when>
                                            <xsl:when test="de:WDM=1305">SecondClass</xsl:when>
                                            <xsl:when test="de:WDM=1306">ThirdClass</xsl:when>
                                            <xsl:when test="de:WDM=1307">FourthClass</xsl:when>
                                            <xsl:otherwise>Unknown</xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <!-- simple ways are classified according to their Function, where the relevance of ways are generally lower than the streets -->
                                    <xsl:when test="de:OBJART='3102'">
                                        <xsl:choose>
                                            <xsl:when test="de:FKT=1701">SixthClass</xsl:when>
                                            <xsl:when test="de:FKT=1702">SeventhClass</xsl:when>
                                            <xsl:otherwise>EighthClass</xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <!-- For the carriage way, it is not possible to define the road class, this would be defined in the complex street type, which relation is lost -->
                                    <xsl:when test="de:OBJART=3106">Unknown</xsl:when>
                                </xsl:choose>
                            </xsl:element>
                            <!-- / END functional Road Class -->
                            <!-- **************************************************************************************************************************************************** -->
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:for-each-group>
            
            <xsl:element name="er:properties">
                <xsl:element name="er:id">TODO</xsl:element>
                <xsl:element name="er:timestamp">
                    <xsl:element name="gml:timePosition">2008-07-18</xsl:element>
                </xsl:element>
            </xsl:element>
        </er:EuroRoadSDataset>
    </xsl:template>


</xsl:stylesheet>
