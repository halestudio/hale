/**
 * Humboldt CST-WPS input process form
 */
Humboldt.ProcessForm = function(config) {

    config = config ? config : {};

    if (!config.wpsUrl) {
        throw new Exception("Config parameter wpsUrl not set");
    }

    if (!config.uploadUrl) {
        throw new Exception("Config parameter uploadUrl not set");
    }

    this.omlField = new Ext.form.Field({
        xtype:"textfield",
        cls: 'x-form-file',
        inputType:"file",
        name:"oml",
        width: 100,
        fieldLabel:"OML File"
    });

    this.gmlField = new Ext.form.Field({
        xtype:"textfield",
        cls: 'x-form-file',
        inputType:"file",
        name:"gml",
        width: 100,
        fieldLabel:"GML File"
    });

    this.schemaStore = new Ext.data.XmlStore({
            autoDestroy: true,
            storeId: 'myStore',
            url: Ext.urlAppend(config.wpsUrl,"service=wps&version=1.0.0&request=describeprocess&identifier=iobridge"), 
            // reader configs

            record: 'ows:Value', // records will have an "ows:Value" tag
            fields: [
                // set up the fields mapping into the xml doc

                // The first needs mapping, the others are very basic

                {name: 'identifier', convert: function(v,record) {
                return record.firstChild.nodeValue}}
            ]
        });

    this.schemaCombo = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        //lazyRender:true,
        fieldLabel: "Schema file",
        //mode: 'local',
        store: this.schemaStore,
        valueField: 'identifier',
        displayField: 'identifier'
    });

    // configure the form
    config.fileUpload =  true;
    config.items = [
            this.omlField,
            this.gmlField,
            this.schemaCombo
        ];
    config.buttons =  [{
            text: 'Execute',
            scope: this,
            handler: this.uploadAndExecute 
        },{
            text: 'Reset',
            handler: function(){
                fp.getForm().reset();
            }
        }];

    //
    Humboldt.ProcessForm.superclass.constructor.call(this, config);
};

Ext.extend(Humboldt.ProcessForm, Ext.form.FormPanel, {  
   
    omlField: null,
    gmlField: null,
    schemaStore: null,
    schemaCombo: null, 

    omlUrl: null,
    gmlUrl: null,

    wps: null,
    wpsUrl: null,
    uploadUrl: null,

    /**
     * Upload all files and call the execute request
     */
    uploadAndExecute: function() {
        if(requestForm.getForm().isValid()){
                requestForm.getForm().submit({
                    url: this.uploadUrl,
                    waitMsg: 'Storing GML and OML files.',
                    success: function(fp, o){
                        this.omlUrl = o.result.omlFile;
                        this.omlUrl = o.result.gmlFile;
                        this.execute()
                    }
                });
        }
    },

    /**
     * Files are uploaded, call the execute request
     */
    execute: function() {
        // define the WPS instance
        this.wps = new OpenLayers.WPS(this.wpsUrl);

        // define inputs and outputs
        var schemaInput = new OpenLayes.WPS.LiteralPut({identifier:"schema",
                value: this.schemaCombo.getValue()});
        var omlInput = new OpenLayes.WPS.ComplexPut({identifier:"oml",
                value: this.omlUrl});
        var gmlInput = new OpenLayes.WPS.ComplexPut({identifier:"gml",
                value: this.gmlUrl});

        var gmlOutput = new OpenLayes.WPS.ComplexPut({identifier:"gml",
                value: this.gmlUrl,
                asReference: true});

        // define the iobridge process
        var ioBridgeProcess = new OpenLayes.WPS.Process({identifier:"iobridge",
                inputs: [schemaInput, omlInput, gmlInput],
                outputs: [gmlOutput]});

        // register process
        this.wps.addProcess(ioBridgeProcess);
        
        // execute process
        this.wps.execute();
    }
});
