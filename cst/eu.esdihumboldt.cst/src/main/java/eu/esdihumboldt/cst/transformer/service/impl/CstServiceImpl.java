/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : cst
 * 	 
 * Classname    : eu.esdihumboldt.cst.transformer/TransformerService.java 
 * 
 * Author       : schneidersb
 * 
 * Created on   : Aug 13, 2009 -- 3:19:53 PM
 *
 */
package eu.esdihumboldt.cst.transformer.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.capabilities.CstServiceCapabilities;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.CstServiceCapabilitiesImpl;
import eu.esdihumboldt.cst.transformer.capabilities.impl.FunctionDescriptionImpl;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

/**
 * Simple CstService implementation which applies all necessary Transformers on
 * a FeatureCollection or Feature.
 */
public class CstServiceImpl 
	implements CstService {

	private CstFunctionFactory transformerFactory;

	CstServiceCapabilities tCapabilities;

	/**
	 * Default {@link CstService} constructor.
	 */
	public CstServiceImpl() {
		transformerFactory = CstFunctionFactory.getInstance();
		List<FunctionDescription> odList = new ArrayList<FunctionDescription>();

		try {
			Map<String, Class<? extends CstFunction>> transformers = transformerFactory
					.getRegisteredFunctions();
			for (Iterator<String> i = transformers.keySet().iterator(); i.hasNext();) {
				String transName = i.next();
				Class<?> tclass = Class.forName(transName);
				CstFunction t = (CstFunction) tclass.newInstance();

				/**
				 * TODO - clarify what URL in FunctionDescription mean.
				 */
				FunctionDescription od = new FunctionDescriptionImpl(new URL(
						"file://" + transName), t.getParameterTypes());
				odList.add(od);
			}
			tCapabilities = new CstServiceCapabilitiesImpl(odList);
		} catch (Exception e) {
			throw new RuntimeException("Initialising the CstServiceImpl failed: " + e);
		}

	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc,
			IAlignment alignment, Set<FeatureType> targetSchema) {
		TargetSchemaProvider.getInstance().addTypes(targetSchema);
		SchemaTranslationController stc = new SchemaTranslationController(alignment);
		FeatureCollection result = stc.translate((FeatureCollection) fc);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Feature transform(Feature f, ICell c) {
		CstFunctionFactory transformerFactory = CstFunctionFactory
				.getInstance();

		// The transformed feature
		Feature result = null;

		try {
			// Get the transformer which could execute the given
			// operation name.
			CstFunction function = transformerFactory.getCstFunction(c);

			// Execute the transformer
			result = function.transform(f, f);
		} catch (Exception e) {
			throw new RuntimeException("Transformation failed: " + e);
		}

		return result;
	}

	public CstServiceCapabilities getCapabilities() {
		return tCapabilities;

	}
}
