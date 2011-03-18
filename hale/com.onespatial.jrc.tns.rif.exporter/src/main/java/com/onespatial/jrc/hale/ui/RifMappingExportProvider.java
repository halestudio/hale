/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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

import com.onespatial.jrc.tns.oml_to_rif.HaleAlignment;
import com.onespatial.jrc.tns.oml_to_rif.AlignmentToRifTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportException;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportProvider;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;
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
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Templer / Fraunhofer IGD
 */
public class RifMappingExportProvider implements MappingExportProvider
{

    /**
	 * @see MappingExportProvider#export(Alignment, String, Collection, Collection)
	 */
	@Override
	public MappingExportReport export(Alignment al, String path,
			Collection<SchemaElement> sourceSchema,
			Collection<SchemaElement> targetSchema)
			throws MappingExportException {
		MappingExportReport report = new MappingExportReport();
		
		try
        {
			HaleAlignment hal = new HaleAlignment(al, sourceSchema, targetSchema);
            Document document = AlignmentToRifTranslator.getInstance(report).translate(hal);

            DOMSource source = new DOMSource(document);
            File newFile = new File(path);
            OutputStream stream = new FileOutputStream(newFile);

            StreamResult result = new StreamResult(stream);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
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
        
        return report;
	}

}
