


/*
 * Copyright (c) 2013 Fraunhofer IGD
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

package eu.esdihumboldt.hale.io.xslt.transformations.property.regexanalysis;

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.cst.functions.string.RegexAnalysisFunction
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation


/**
 * XSLT representation of the Regex Analysis function.
 * 
 * @author Andrea Antonello
 */
class XslRegexAnalysis extends AbstractFunctionTransformation implements RegexAnalysisFunction {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
			XsltGenerationContext context, Cell typeCell) {

		def sourceString = variables.get(null)[0].XPath

		// get the parameters
		def regexPattern = CellUtil.getFirstParameter(cell, PARAMETER_REGEX_PATTERN).as(String)
		if (!regexPattern) {
			// original text if no expression
			return sourceString
		}
		def ouputFormat = CellUtil.getFirstParameter(cell, PARAMETER_OUTPUT_FORMAT).as(String)
		if (!ouputFormat) {
			// original text if no expression
			return sourceString
		}

		//		Pattern pattern = Pattern.compile(regexPattern);

		List<String> pieces = new ArrayList<String>();
		int indexOf;
		while( (indexOf = ouputFormat.indexOf('{')) != -1 ) {
			String pre = ouputFormat.substring(0, indexOf);
			int indexClose = ouputFormat.indexOf('}');
			if (indexClose == -1) {
				throw new IllegalAccessException();
			} else {
				indexClose++;
			}
			String group = ouputFormat.substring(indexOf, indexClose);

			if (pre.trim().length() > 0)
				pieces.add(pre);
			if (group.trim().length() > 0)
				pieces.add(group);

			ouputFormat = ouputFormat.substring(indexClose);
		}
		if (ouputFormat.trim().length() != 0) {
			pieces.add(ouputFormat);
		}

		/*
		 * Because curly braces are used in XSLT stylesheets to show which parts 
		 * of an attribute value template are expressions to be evaluated, be 
		 * careful when using these in a regular expression specified in an attribute 
		 * value: escape the curly braces by repeating them (ex, {{4}}) to tell an 
		 * XSLT 2.0 processor not to treat the curly braces as attribute value 
		 * template expression delimiters.
		 */
		regexPattern = regexPattern.replaceAll("\\{", "{{");
		regexPattern = regexPattern.replaceAll("\\}", "}}");

		StringBuilder sb = new StringBuilder();
		sb.append("<xsl:analyze-string select='").append(sourceString).append("' regex=\"").append(regexPattern).append("\">\n");
		sb.append("\t<xsl:matching-substring>\n");
		for( String piece : pieces ) {
			if (piece.startsWith("{")) {
				sb.append("\t\t<xsl:value-of select=\"regex-group(");
				String groupNumber = piece.substring(1, piece.length() - 1);
				sb.append(groupNumber);
				sb.append(")\" />\n");
			} else {
				sb.append("\t\t<xsl:text>");
				sb.append(piece);
				sb.append("</xsl:text>\n");
			}
		}
		sb.append("\t</xsl:matching-substring>\n");
		sb.append("</xsl:analyze-string>\n");

		def finalExpression = sb.toString();
	}
}
