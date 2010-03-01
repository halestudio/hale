Ext.BLANK_IMAGE_URL = 'js/ext-3.1.1/resources/images/default/s.gif';

Ext.onReady(function(){

    // create CST IOBridge form
    //
    var omlField = new Ext.form.Field({
        xtype:"textfield",
        cls: 'x-form-file',
        inputType:"file",
        name:"oml",
        width: 100,
        fieldLabel:"OML File"
    });

    var gmlField = new Ext.form.Field({
        xtype:"textfield",
        cls: 'x-form-file',
        inputType:"file",
        name:"gml",
        width: 100,
        fieldLabel:"GML File"
    });

    Ext.Ajax.method = "GET";
    var schemaStore = new Ext.data.XmlStore({
            autoDestroy: true,
            storeId: 'myStore',
            url:'IOBridgeServlet.py?service=wps&version=1.0.0&request=describeprocess&identifier=iobridge', 
            // reader configs

            record: 'ows:Value', // records will have an "ows:Value" tag
            fields: [
                // set up the fields mapping into the xml doc

                // The first needs mapping, the others are very basic

                {name: 'identifier', convert: function(v,record) {
                return record.firstChild.nodeValue}}
            ]
        });

    var schemaCombo = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        //lazyRender:true,
        fieldLabel: "Schema file",
        //mode: 'local',
        store: schemaStore,
        valueField: 'identifier',
        displayField: 'identifier'
    });



    var requestForm = new Ext.form.FormPanel({
        renderTo: Ext.get("body"),
        fileUpload: true,
        width: 500,
        frame: true,
        title: 'Humboldt CST IOBridge web client',
        autoHeight: true,
        renderTo:"wps-div",
        items: [
            omlField,
            gmlField,
            schemaCombo
        ],
        buttons: [{
            text: 'Execute',
            handler: function(){
                if(requestForm.getForm().isValid()){
                        requestForm.getForm().submit({
                            url: 'FileBackServlet.py',
                            waitMsg: 'Retrieving OML file',
                            success: function(fp, o){
                                console.log(o.result.msg);
                            }
                        });
                }
            }
        },{
            text: 'Reset',
            handler: function(){
                fp.getForm().reset();
            }
        }]
    });

});

// HACK HACK HACK
Ext.DomQuery.select = function(record, root) {
    //console.log(record,root,"##");
    return root.getElementsByTagName(record);
};
