/*
 * Copyright (c) 2012 Fraunhofer IGD
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.xpath;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Filter to XPath visitor.
 * 
 * @author Kai Schwierczek
 */
public class GeotoolsFilterToXPath implements ExpressionVisitor, FilterVisitor {

	private static ALogger log = ALoggerFactory.getLogger(GeotoolsFilterToXPath.class);

	/**
	 * The main type definition.
	 */
	private final TypeDefinition typeDef;

	/**
	 * The parent type definition for property filters.
	 */
	private final TypeDefinition parentType;

	private final NamespaceContext namespaceContext;

	private final boolean propertyFilter;

	/**
	 * Converts type filters to XPath.
	 * 
	 * @param definition the base type
	 * @param namespaceContext the namespace context
	 */
	private GeotoolsFilterToXPath(TypeDefinition definition, NamespaceContext namespaceContext) {
		this.typeDef = definition;
		this.namespaceContext = namespaceContext;
		propertyFilter = false;
		parentType = null;
	}

	/**
	 * Converts property filters to XPath.
	 * 
	 * @param definition the base type
	 * @param namespaceContext the namespace context
	 */
	private GeotoolsFilterToXPath(PropertyDefinition definition, NamespaceContext namespaceContext) {
		this.typeDef = definition.getPropertyType();
		this.namespaceContext = namespaceContext;
		propertyFilter = true;
		parentType = definition.getParentType();
	}

	/**
	 * Transforms the given filter of the given type to a XPath query.
	 * Namespaces are transformed with the given mapping.
	 * 
	 * @param definition the type
	 * @param namespaceContext the namespace context
	 * @param filter the filter to transform
	 * @return the XPath query representing the given filter
	 */
	public static String toXPath(TypeDefinition definition, NamespaceContext namespaceContext,
			Filter filter) {
		return ((StringBuffer) filter.accept(
				new GeotoolsFilterToXPath(definition, namespaceContext), null)).toString();
	}

	/**
	 * Transforms the given filter of the given type to a XPath query.
	 * Namespaces are transformed with the given mapping.
	 * 
	 * @param definition the property definition
	 * @param namespaceContext the namespace context
	 * @param filter the property filter to transform
	 * @return the XPath query representing the given filter
	 */
	public static String toXPath(PropertyDefinition definition, NamespaceContext namespaceContext,
			Filter filter) {
		return ((StringBuffer) filter.accept(
				new GeotoolsFilterToXPath(definition, namespaceContext), null)).toString();
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.ExcludeFilter,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(ExcludeFilter filter, Object buffer) {
//		System.out.println("ExcludeFilter");
		StringBuffer output = asStringBuffer(buffer);

		output.append("false()");

		return output;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.IncludeFilter,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(IncludeFilter arg0, Object buffer) {
//		System.out.println("IncludeFilter");
		StringBuffer output = asStringBuffer(buffer);

		output.append("true()");

		return output;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.And,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(And filter, Object buffer) {
//		System.out.println("And");
		StringBuffer output = asStringBuffer(buffer);

		output.append("(");
		for (Iterator<Filter> i = filter.getChildren().iterator(); i.hasNext();) {
			Filter child = i.next();
			child.accept(this, output);
			if (i.hasNext())
				output.append(" and ");
		}
		output.append(")");

		return output;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.Id,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Id arg0, Object buffer) {
		throw new UnsupportedOperationException("Id filter not implemented");
		// TODO is there a way to implement this in XPath?
		// It's about FeatureIds/GeometricIds/...
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.Not,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Not filter, Object buffer) {
//		System.out.println("Not");
		StringBuffer output = asStringBuffer(buffer);

		output.append("not(");
		filter.getFilter().accept(this, output);
		output.append(')');

		return output;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.Or,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Or filter, Object buffer) {
//		System.out.println("Or");
		StringBuffer output = asStringBuffer(buffer);

		output.append("(");
		for (Iterator<Filter> i = filter.getChildren().iterator(); i.hasNext();) {
			Filter child = i.next();
			child.accept(this, output);
			if (i.hasNext())
				output.append(" or ");
		}
		output.append(")");

		return output;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsBetween,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsBetween filter, Object buffer) {
//		System.out.println("PropertyIsBetween");

		StringBuffer output = asStringBuffer(buffer);

		output.append("(").append(filter.getExpression().accept(this, output)).append(" >= ")
				.append(filter.getLowerBoundary().accept(this, output)).append(" and ")
				.append(filter.getExpression().accept(this, output)).append(" <= ")
				.append(filter.getUpperBoundary().accept(this, output))
				.append(filter.accept(this, output)).append(')');

		return output;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsEqualTo,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsEqualTo filter, Object buffer) {
//		System.out.println("PropertyIsEqualTo");

		// XXX isMatchingCase is currently ignored.
		// Would somehow need to determine whether a number or string is
		// compared? (And then use lower-case(string) on both sides.)
		StringBuffer result = asStringBuffer(buffer);
		filter.getExpression1().accept(this, result);
		result.append(" = ");
		filter.getExpression2().accept(this, result);

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsNotEqualTo,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsNotEqualTo filter, Object buffer) {
//		System.out.println("PropertyIsNotEqualTo");

		// XXX isMatchingCase is currently ignored. See IsEqual.
		StringBuffer result = asStringBuffer(buffer);
		filter.getExpression1().accept(this, result);
		result.append(" != ");
		filter.getExpression2().accept(this, result);

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsGreaterThan,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsGreaterThan filter, Object buffer) {
//		System.out.println("PropertyIsGreaterThan");

		StringBuffer result = asStringBuffer(buffer);
		filter.getExpression1().accept(this, result);
		result.append(" > ");
		filter.getExpression2().accept(this, result);

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsGreaterThanOrEqualTo,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object buffer) {
//		System.out.println("PropertyIsGreaterThanOrEqualTo");

		StringBuffer result = asStringBuffer(buffer);
		filter.getExpression1().accept(this, result);
		result.append(" >= ");
		filter.getExpression2().accept(this, result);

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsLessThan,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsLessThan filter, Object buffer) {
//		System.out.println("PropertyIsLessThan");

		StringBuffer result = asStringBuffer(buffer);
		filter.getExpression1().accept(this, result);
		result.append(" < ");
		filter.getExpression2().accept(this, result);

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsLessThanOrEqualTo,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsLessThanOrEqualTo filter, Object buffer) {
//		System.out.println("PropertyIsLessThanOrEqualTo");

		StringBuffer result = asStringBuffer(buffer);
		filter.getExpression1().accept(this, result);
		result.append(" <= ");
		filter.getExpression2().accept(this, result);

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsLike,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsLike filter, Object buffer) {
		StringBuffer result = asStringBuffer(buffer);

		String escape = filter.getEscape();
		String single = filter.getSingleChar();
		String multi = filter.getWildCard();
		String escapedEscape = escapeXPathRegexString(escape);
		String escapedSingle = escapeXPathRegexString(single);
		String escapedMulti = escapeXPathRegexString(multi);

		String pattern = filter.getLiteral();
		boolean caseSensitive = filter.isMatchingCase();

		result.append("matches(");
		filter.getExpression().accept(this, result);
		result.append(", '^");

		int pos = 0;
		boolean escaped = false;
		while (pos < pattern.length()) {
			if (pattern.regionMatches(pos, escape, 0, escape.length())) {
				// escape char
				if (escaped) {
					result.append(escapedEscape);
					escaped = false;
				}
				else
					escaped = true;
				pos += escape.length();
			}
			else if (pattern.regionMatches(pos, single, 0, single.length())) {
				// single wild card
				if (escaped) {
					result.append(escapedSingle);
					escaped = false;
				}
				else
					result.append('.');
				pos += single.length();
			}
			else if (pattern.regionMatches(pos, multi, 0, multi.length())) {
				// multi wild card
				if (escaped) {
					result.append(escapedMulti);
					escaped = false;
				}
				else
					result.append(".*");
				pos += multi.length();
			}
			else {
				// nothing special
				if (escaped)
					throw new IllegalArgumentException(
							"After the escape symbol of a LIKE pattern does not follow a special character.");
				char c = pattern.charAt(pos);
				if (isXPathRegexEscapeChar(c))
					result.append('\\');
				result.append(c);
				pos++;
			}
		}

		result.append("$', 's"); // s "dot-all" mode (. matches \n, too)
		if (!caseSensitive)
			result.append('i');
		result.append("')");

		return result;
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.PropertyIsNull,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyIsNull filter, Object buffer) {
		StringBuffer result = asStringBuffer(buffer);

		// allow it to not exist at all, or to be nilled
		result.append("(not(");
		filter.accept(this, result);
		result.append(") or nilled(");
		filter.accept(this, result);
		result.append("))");

		return result;
	}

	// TODO Can these geometric filters easily be converted to XPath?

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.BBOX,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(BBOX arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter BBOX not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Beyond,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Beyond arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Beyond not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Contains,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Contains arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Contains not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Crosses,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Crosses arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Crosses not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Disjoint,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Disjoint arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Disjoint not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.DWithin,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(DWithin arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter DWithin not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Equals,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Equals filter, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Equals not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Intersects,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Intersects arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Intersects not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Overlaps,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Overlaps arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Overlaps not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Touches,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Touches arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Touches not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.spatial.Within,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Within arg0, Object buffer) {
		throw new UnsupportedOperationException("Geometric filter Within not implemented");
	}

	// TODO Can these time filters easily be converted to XPath?
	// FilterToCQL does not support them.

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.After,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(After arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter After not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.AnyInteracts,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(AnyInteracts arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter AnyInteracts not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Before,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Before arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter Before not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Begins,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Begins arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter Begins not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.BegunBy,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(BegunBy arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter BegunBy not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.During,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(During arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter During not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.EndedBy,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(EndedBy arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter EndedBy not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Ends,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Ends arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter Ends not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.Meets,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Meets arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter Meets not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.MetBy,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(MetBy arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter MetBy not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.OverlappedBy,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(OverlappedBy arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter OverlappedBy not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.TContains,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(TContains arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter TContains not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.TEquals,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(TEquals arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter TEquals not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visit(org.opengis.filter.temporal.TOverlaps,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(TOverlaps arg0, Object buffer) {
		throw new UnsupportedOperationException("Temporal filter TOverlaps not implemented");
	}

	/**
	 * @see org.opengis.filter.FilterVisitor#visitNullFilter(java.lang.Object)
	 */
	@Override
	public Object visitNullFilter(Object buffer) {
		throw new UnsupportedOperationException("nullFilter not implemented");
		// TODO What is this about? FilterToCQL says
		// "Cannot encode null as a Filter"
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.NilExpression,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(NilExpression arg0, Object buffer) {
		throw new UnsupportedOperationException("nil not implemented");
		// TODO How to represent nil?
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.Function,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Function arg0, Object buffer) {
		log.error("Function filter present. Name: " + arg0.getName());
		throw new UnsupportedOperationException("Function filter not implemented");
		// TODO How to export functions? Is there a list of available functions?
		// If so, create a mapping from them to XPath!?
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.Literal,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Literal expression, Object buffer) {
		StringBuffer result = asStringBuffer(buffer);

		Object literal = expression.getValue();
//        if (literal instanceof Geometry) {
//            Geometry geometry = (Geometry) literal;
//            WKTWriter writer = new WKTWriter();
//            String wkt = writer.write( geometry );
//            output.append( wkt );
//        }
//        else if( literal instanceof Number ){
//                // don't convert to string
//                output.append( literal );
//        }
//        else if (literal instanceof Date ) {
//            return date( (Date) literal, output );
//        }
//        else {

		// TODO Needs suitable escaping!
		// a split+concat at ' and at the end replacing " with &quot; ?
		if (literal instanceof Number)
			result.append(literal);
		else
			result.append('\'').append(literal).append('\'');
		return result;
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.Add,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Add expression, Object buffer) {
		StringBuffer output = asStringBuffer(buffer);

		expression.getExpression1().accept(this, output);
		output.append(" + ");
		expression.getExpression2().accept(this, output);

		return output;
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.Divide,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Divide expression, Object buffer) {
		StringBuffer output = asStringBuffer(buffer);

		expression.getExpression1().accept(this, output);
		output.append(" div ");
		expression.getExpression2().accept(this, output);

		return output;
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.Multiply,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Multiply expression, Object buffer) {
		StringBuffer output = asStringBuffer(buffer);

		expression.getExpression1().accept(this, output);
		output.append(" * ");
		expression.getExpression2().accept(this, output);

		return output;
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.Subtract,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(Subtract expression, Object buffer) {
		StringBuffer output = asStringBuffer(buffer);

		expression.getExpression1().accept(this, output);
		output.append(" - ");
		expression.getExpression2().accept(this, output);

		return output;
	}

	/**
	 * @see org.opengis.filter.expression.ExpressionVisitor#visit(org.opengis.filter.expression.PropertyName,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(PropertyName expression, Object buffer) {
		StringBuffer result = asStringBuffer(buffer);

		TypeDefinition rootType = typeDef;
		String path = expression.getPropertyName();

		boolean propertyModeValue = true;
		if (propertyFilter) {
			/*
			 * If we are processing property filters, expressions either start
			 * with value or test.
			 */

			// value directly referenced
			if ("value".equals(path)) {
				// means we reference the current context
				result.append('.'); // XXX is this correct?
				return result;
			}
			else if (path.startsWith("parent/")) {
				rootType = parentType;
				propertyModeValue = false;
				// remove prefix from path
				path = path.substring(7);
			}
			else if (path.startsWith("value/")) {
				// remove prefix from path
				path = path.substring(6);
			}
		}

		List<List<QName>> paths = PropertyResolver.getQueryPaths(rootType, DataSet.SOURCE, path);

		// TODO what about no / multiple paths

		List<QName> qnames = paths.get(0);

		if (propertyFilter && !propertyModeValue) {
			// parent mode, we have to prepend the reference to the parent
			result.append("../");
		}

		Definition<?> parent = rootType;
		boolean first = true;
		for (int i = 0; i < qnames.size(); i++) {
			// get the element qualified name
			QName segment = qnames.get(i);
			// get the associated definition
			ChildDefinition<?> def = DefinitionUtil.getChild(parent, segment);
			if (def.asProperty() != null) {
				// groups are ignored

				if (first) {
					first = false;
				}
				else {
					result.append('/');
				}
				if (def.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					// attributes need to be marked w/ @
					result.append('@');
				}
				// add the qualified name
				result.append(qNameToXPathSegment(segment));
			}

			parent = def;
		}

		return result;
	}

	/**
	 * Process the possibly user supplied buffer parameter into a StringBuffer.
	 * 
	 * @param buffer the current buffer argument
	 * @return a StringBuffer for appending the result
	 */
	protected StringBuffer asStringBuffer(Object buffer) {
		if (buffer instanceof StringBuffer)
			return (StringBuffer) buffer;
		else
			return new StringBuffer();
	}

	/**
	 * Returns the given QName in a format for XPath.
	 * 
	 * @param segment the QName to format
	 * @return the XPath segment
	 */
	protected String qNameToXPathSegment(QName segment) {
		// XXX What if the namespace isn't specified in the map?
		// Check for NULL_NS.
		if (segment.getNamespaceURI().isEmpty())
			return segment.getLocalPart();
		else
			return namespaceContext.getPrefix(segment.getNamespaceURI()) + ':'
					+ segment.getLocalPart();
	}

	/**
	 * Returns true, if the given char is a char which may get escaped in XPath
	 * regular expressions.
	 * 
	 * @param c the char to test
	 * @return true, is the given char may be escaped in XPath
	 */
	private boolean isXPathRegexEscapeChar(char c) {
		return (c == '\\') || (c == '|') || (c == '.') || (c == '?') || (c == '*') || (c == '+')
				|| (c == '(') || (c == ')') || (c == '{') || (c == '}') || (c == '$') || (c == '-')
				|| (c == '[') || (c == ']') || (c == '^');
	}

	/**
	 * Escapes special XPath regex chars.
	 * 
	 * @param s the String to be escaped
	 * @return the escaped string
	 */
	private String escapeXPathRegexString(String s) {
		StringBuffer result = new StringBuffer(s.length());

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (isXPathRegexEscapeChar(c))
				result.append('\\' + c);
			else
				result.append(c);
		}

		return result.toString();
	}
}
