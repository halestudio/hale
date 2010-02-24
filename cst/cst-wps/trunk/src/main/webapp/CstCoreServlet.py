"""PyWPS Jython (Java) servlet implementation

.. moduleauthor: Jachym Cepicky 
"""

from java.io import *
from javax.servlet.http import HttpServlet 

import pywps
from pywps.Exceptions import *
import coreFunctions
import traceback

from pywps import config
import os

class CstCoreServlet(HttpServlet):

    def doGet(self,request,response):

        inputQuery = request.getQueryString()
        if not inputQuery:
            e = NoApplicableCode("Missing request value")
            pywps.response.response(e,response)
            return
        self.doPywps(request, response, inputQuery, pywps.METHOD_GET)

    def doPost(self,request,response):

        inputQuery = request.getQueryString()
        self.doPywps(request, response, inputQuery, pywps.METHOD_POST)

    def doPywps(self,request, response, inputQuery,method):

        here = os.path.abspath(os.path.join(os.path.dirname(__file__)))
        config.loadConfiguration(os.path.join(here,"pywpscore.cfg"))
        os.environ["PYWPS_TEMPLATES"] = os.path.join(here,"Templates")

        cstProcesses = []
        cstProcesses = coreFunctions.createCstCoreFunctionProcesses()

        # create the WPS object
        try:
            wps = pywps.Pywps(method)
            if wps.parseRequest(inputQuery):
                pywps.debug(wps.inputs)
                wpsresponse = wps.performRequest(processes=cstProcesses)
                if wpsresponse:
                    pywps.response.response(wps.response, response, wps.parser.isSoap)
        except WPSException,e:
            pywps.response.response(e, response)

