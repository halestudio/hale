from java.io import *
from javax.servlet.http import HttpServlet 
from java.lang import System


class Test(HttpServlet):

    def doGet(self,request,response):
                 
        from eu.esdihumboldt.cst.transformer.service.rename import  RenameFeatureFunction       
        response.setContentType("text/plain")
        toClient = response.getWriter()             
        toClient.println("%s"% repr(RenameFeatureFunction.PARAMETER_INSTANCE_MERGE_CONDITION))       
    def doPost(self,request,response):
        pass