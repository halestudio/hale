/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.hale.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.onespatial.jrc.tns.oml_to_rif.AlignmentToRifTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportException;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportProvider;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * The RIF mapping export provider is an Eclipse RCP plugin that extends the
 * functionality of the Humboldt Alignment Editor (HALE) in order to enable HALE
 * schema mapping definition documents to be exported in W3C Rule Interchange
 * Format - Production Rule Dialect (RIF-PRD). <p/> Here is an example of how
 * this plugin may be integrated into HALE. The example here shows extension
 * using a plugin fragment which is hosted by HALE.
 * 
 * <pre>
 * &lt;fragment&gt;
 *        &lt;extension
 *              point=&quot;eu.esdihumboldt.hale.MappingExport&quot;&gt;
 *           &lt;MappingExportFactory
 *                 extension=&quot;*.rif&quot;
 *                 name=&quot;RIF-PRD&quot;
 *                 providerClass=&quot;com.onespatial.jrc.hale.ui.RifMappingExportProvider&quot;&gt;
 *           &lt;/MappingExportFactory&gt;
 *        &lt;/extension&gt;
 *     &lt;/fragment&gt;
 * </pre>
 * 
 * @author simonp
 */
public class RifMappingExportProvider implements MappingExportProvider
{

    /**
	 * @see MappingExportProvider#export(Alignment, String, Collection, Collection)
	 */
	@Override
	public void export(Alignment al, String path,
			Collection<SchemaElement> sourceSchema,
			Collection<SchemaElement> targetSchema)
			throws MappingExportException {
		try
        {
            Document document = AlignmentToRifTranslator.getInstance().translate(al);

            DOMSource source = new DOMSource(document);
            File newFile = new File(path);
            OutputStream stream = new FileOutputStream(newFile);

            StreamResult result = new StreamResult(stream);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
        }
        catch (TranslationException e)
        {
            throw new MappingExportException(e.getMessage(), e);
        }
        catch (FileNotFoundException e)
        {
            throw new MappingExportException(e.getMessage(), e);
        }
        catch (TransformerException e)
        {
            throw new MappingExportException(e.getMessage(), e);
        }
	}

}
