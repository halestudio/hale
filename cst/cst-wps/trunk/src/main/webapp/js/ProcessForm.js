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
            ],
        // hack, which will enable to look after namespaced elements, e.g.
        // ows:Value
        listeners : {
                    beforeload:function() {
                        this._tempSelect = Ext.DomQuery.select;
                        Ext.DomQuery.select = function(record, root) {
                            return root.getElementsByTagName(record);
                        };
                    },
                    load: function() {
                        Ext.DomQuery.select = this._tempSelect ;
                    },
                    scope: this
                }

        });

    this.schemaCombo = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        //lazyRender:true,
        fieldLabel: "Schema file",
        //mode: 'local',
        store: this.schemaStore,
        valueField: 'identifier',
        displayField: 'identifier'    });

    // configure the form
    config.fileUpload =  true;
    config.buttonAlign = "right";
    config.items = [
            this.omlField,
            this.gmlField,
            this.schemaCombo
        ];
    config.buttons =  [{
            text: 'Execute',
            scope: this,
            handler: this.uploadAndExecute 
        },
        {
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
        if(this.getForm().isValid()){
                this.getForm().submit({
                    url: this.uploadUrl,
                    waitMsg: 'Storing GML and OML files.',
                    scope: this,
                    success: function(fp, o){
                        
                        this.omlUrl = o.result.omlFile;
                        this.omlUrl = o.result.gmlFile;
                        try {
                            this.execute();
                        }catch(e) {
                            if (window.console) {
                                console.log(e);
                            }
                            alert(e);
                        }
                    }
                });
        }
    },

    /**
     * Files are uploaded, call the execute request
     */
    execute: function() {
        // define the WPS instance
        this.wps = new OpenLayers.WPS(this.wpsUrl,{onSucceeded: this.onExecuted,
                                                   onFailed: this.onFailed});

        // define inputs and outputs
        var schemaInput = new OpenLayers.WPS.LiteralPut({identifier:"schema",
                value: this.schemaCombo.getValue()});
        var omlInput = new OpenLayers.WPS.ComplexPut({identifier:"oml",
                value: this.omlUrl});
        var gmlInput = new OpenLayers.WPS.ComplexPut({identifier:"gml",
                value: this.gmlUrl});

        var gmlOutput = new OpenLayers.WPS.ComplexPut({identifier:"gml",
                value: this.gmlUrl,
                asReference: true});

        // define the iobridge process
        var ioBridgeProcess = new OpenLayers.WPS.Process({identifier:"iobridge",
                inputs: [schemaInput, omlInput, gmlInput],
                outputs: [gmlOutput]});

        // register process
        this.wps.addProcess(ioBridgeProcess);
        
        this.showMessage();
        // execute process
        this.wps.execute(ioBridgeProcess.identifier);
    },

    /**
     * Called when WPS successfully finished
     */
    onExecuted: function(process) {
        Ext.MessageBox.hide();
        var gmlUrl = process.outputs[0].getValue();
        document.getElementById("wps-results").innerHTML = gmlUrl;
    },

    /**
     * Called when WPS failed
     */
    onFailed: function(process) {
        Ext.MessageBox.hide();
        document.getElementById("wps-results").innerHTML = "<b>"+process.exception.code+": </b>"+
            process.exception.text;
    },

    /**
     * Show progress message box
     */
    showMessage: function() {
        Ext.MessageBox.show({
            msg: 'Transforming GML file',
            progressText: 'Transforming...',
            width:300,
            wait:true,
            waitConfig: {interval:200}//,
            //icon:'ext-mb-download' //, //custom class in msg-box.html
            //animEl: 'mb7'
        });
    }

});
