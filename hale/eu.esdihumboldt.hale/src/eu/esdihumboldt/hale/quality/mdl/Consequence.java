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
package eu.esdihumboldt.hale.quality.mdl;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Consequence} object describes the impact a mismatch has in terms of 
 * {@link DataQualityElement}s and a defined {@link MismatchContext}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class Consequence {
	
	private MismatchContext context;
	
	private List<DataQualityElement> impact;
	
	public Consequence() {
		this.context = null;
		this.impact = new ArrayList<DataQualityElement>();
	}

	public MismatchContext getContext() {
		return context;
	}

	public void setContext(MismatchContext context) {
		this.context = context;
	}

	public List<DataQualityElement> getImpact() {
		return impact;
	}

	public void setImpact(List<DataQualityElement> impact) {
		this.impact = impact;
	}

}
