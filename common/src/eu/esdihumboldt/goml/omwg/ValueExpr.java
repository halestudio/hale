/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.goml.omwg;

//import java.util.List;

/**
 * A {@link ValueExpr} is used to deal with literal values and ranges. It 
 * represents the goml:ValueExprType.
 * FIXME not clear yet how to deal with other kinds of 'value'
 * 
 * @author Marian de Vries 
 * @partner 08 / Delft University of Technology
 * @version $Id$ 
 */
public class ValueExpr {

    private String literal;
    private String min;
    private String max;

    //private Property property;
    //private Relation relation;

    private Function apply;


}
