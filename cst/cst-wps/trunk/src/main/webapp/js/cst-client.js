Ext.BLANK_IMAGE_URL = 'js/ext-3.1.1/resources/images/default/s.gif';
Ext.namespace("Humboldt");
Ext.Ajax.method = "GET";

Ext.onReady(function(){


    // create CST IOBridge form
    //
    var processForm = new Humboldt.ProcessForm({
        wpsUrl:"IOBridgeServlet.py",
        uploadUrl:"upload",
        width: 500,
        frame: true,
        title: 'Humboldt CST IOBridge web client',
        autoHeight: true,
        renderTo:"wps-div",
    });
});

