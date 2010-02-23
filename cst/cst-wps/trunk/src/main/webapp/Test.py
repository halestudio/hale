from java.io import *
from javax.servlet.http import HttpServlet 
from java.lang import System


class Test(HttpServlet):

    def doGet(self,request,response):
          
        from org.geotools.referencing import CRS

        response.setContentType("text/plain")
        toClient = response.getWriter()             
        toClient.println("%s"% repr(CRS.decode("epsg:2065")))

    def doPost(self,request,response):
        pass