"""Humboldt processes"""

from pywps.Process import WPSProcess
from eu.esdihumboldt.cst.transformer import service
from eu.esdihumboldt.cst.transformer.capabilities.impl import CstServiceCapabilitiesImpl;

def createCstCoreFunctionProcesses():

    processes = []
        
    service.CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions");
    myService = CstServiceCapabilitiesImpl();

    for function in myService.getFunctionDescriptions():
        identifier = str(function.getFunctionId()).replace("http://java/","")
        humboldtProcess = WPSProcess(identifier=identifier,
                                     title = identifier)
        processes.append(humboldtProcess)

    return processes
