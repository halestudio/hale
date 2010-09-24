"""Humboldt IOBridge process"""

from pywps.Process import WPSProcess
from eu.esdihumboldt.cst.iobridge.impl import DefaultCstServiceBridge
import types
import os,urllib,sys
import traceback,pywps

def createIOBridgeProcess():

    class IOBridgeProcess(WPSProcess):
        def __init__(self):
            WPSProcess.__init__(self, identifier="iobridge",
                    title = "Humboldt CST IOBridge",
                    abstract = """IOBridge process for the Humbold CST.
                    Process acceptes schema file, oml file and input gml
                    file and provides the transformation""",
                    storeSupported = True,
                    statusSupported = True)

            self.schema = self.addComplexInput(identifier="schema",
                            title="Schema file",
                            formats = [{"mimeType":"text/xml"}])
            self.oml = self.addComplexInput(identifier="oml",
                            title="Ontology mapping file",
                            formats = [{"mimeType":"text/xml"}])
            self.gmlin = self.addComplexInput(identifier="gml",
                            title="Input GML file",
                            formats = [{"mimeType":"text/xml"}])
            self.sourceschema = self.addComplexInput(identifier="sourceschema",
                            title="Input Source Schema file",
                            formats = [{"mimeType":"text/xml"}],
                            minOccurs = 0)
            self.gmlversion = self.addLiteralInput(identifier="gmlversion",
                            title="GML Version",
                            type = types.StringType,
                            allowedValues = ["GML2","GML3","GML3_2"],
                            minOccurs = 0)
            self.gmlout = self.addComplexOutput(identifier="gml",
                            title="Output GML file",
                            formats = [{"mimeType":"text/xml"}])

        def execute(self):

            # import cst
            dcsb = DefaultCstServiceBridge()

            # make urls from file names
            schemaPath = os.path.abspath(self.schema.getValue())
            omlPath = os.path.abspath(self.oml.getValue())
            gmlPath = os.path.abspath(self.gmlin.getValue())

            schemaUrl = urllib.basejoin("file:",schemaPath)
            omlUrl = urllib.basejoin("file:",omlPath)
            gmlUrl = urllib.basejoin("file:",gmlPath)


            if self.sourceschema.getValue():
                sourceschemaPath = os.path.abspath(self.sourceschema.getValue())
                sourceschemaUrl = urllib.basejoin("file:",sourceschemaPath)
            else:
                sourceschemaUrl = None

            # stup gml version
            gmlversion = self.gmlversion.getValue()
            if gmlversion:
                from eu.esdihumboldt.hale.gmlparser import ConfigurationType
                gmlversion = ConfigurationType.valueOf(gmlversion)

            # create empty output file
            outFile = open("output.gml","w")
            outFile.close()
            outUrl = urllib.basejoin("file:",os.path.join(os.path.abspath(outFile.name)))

            self.status.set("Transforming input GML")

            import java.lang.NullPointerException
            import java.lang.RuntimeException
            try:
                dcsb.transform(schemaUrl, omlUrl, gmlUrl,outUrl, sourceschemaUrl, gmlversion)
            except java.lang.Exception,e:
                traceback.print_stack(file=pywps.logFile)
                return "Could not transform GML, got java.lang.Exception: %s" % e

            self.gmlout.setValue(outFile.name)

    return IOBridgeProcess()
