"""Humboldt IOBridge process"""

from pywps.Process import WPSProcess
from eu.esdihumboldt.cst.iobridge.impl import DefaultCstServiceBridge
import types
import os,urllib,sys

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
            self.gmlout = self.addComplexOutput(identifier="gml",
                            title="Output GML file",
                            formats = [{"mimeType":"text/xml"}])

        def execute(self):
            dcsb = DefaultCstServiceBridge()
            schemaPath = os.path.abspath(self.schema.getValue())
            omlPath = os.path.abspath(self.oml.getValue())
            gmlPath = os.path.abspath(self.gmlin.getValue())
            schemaUrl = urllib.basejoin("file:",schemaPath)
            omlUrl = urllib.basejoin("file:",omlPath)
            gmlUrl = urllib.basejoin("file:",gmlPath)
            outFile = open("output.gml","w")
            outFile.close()
            outUrl = urllib.basejoin("file:",os.path.join(os.path.abspath(outFile.name)))
            self.status.set("Transforming input GML")

            tempDir = "tmp"
            omlFile = os.path.basename(self.oml.getValue())
            gmlFile = os.path.basename(self.gmlin.getValue())

            import java.lang.NullPointerException
            import java.lang.RuntimeException
            try:
                dcsb.transform(schemaUrl, omlUrl, gmlUrl,outUrl)
            except java.lang.Exception,e:
                return "Could not transform GML, got java.lang.RuntimeException: %s" % e

            self.gmlout.setValue(outFile.name)

    return IOBridgeProcess()
