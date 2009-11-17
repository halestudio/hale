package test.eu.esdihumboldt.goml;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.esdihumboldt.cst.align.ext.IValueExpression;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;


public class ValueConditionsTest {
	
	
	private final String sourceLocalname = "FT1";
	private final String sourceLocalnamePropertyA = "PropertyA";
	private final String sourceNamespace = "http://esdi-humboldt.eu";
	
	private final String targetLocalname = "FT2";
	private final String targetLocalnamePropertyD = "PropertyB";
	private final String targetNamespace = "http://xsdi.org";
	
	@Test
	public void testValueConditionGeneration()throws Exception{
	Alignment a = new Alignment();
	a.setAbout(new About("lala"));
	a.setSchema1(new Schema(
			sourceNamespace, new Formalism(
					"GML", new URI("http://schemas.opengis.org/gml"))));
	a.setSchema2(new Schema(
			targetNamespace, new Formalism(
					"GML", new URI("http://schemas.opengis.org/gml"))));
	
	Cell cell = new Cell();
	
	BigInteger sequenceId = new BigInteger("1");
	
	Property entity1 = new Property(
			new About(this.sourceNamespace, this.sourceLocalname, 
					this.sourceLocalnamePropertyA));
	
	List<IValueExpression> valueExpressions = new ArrayList<IValueExpression>();
	valueExpressions.add(new ValueExpression("2"));
	valueExpressions.add(new ValueExpression("3"));
	valueExpressions.add(new ValueExpression("4"));
	valueExpressions.add(new ValueExpression("70"));
	valueExpressions.add(new ValueExpression("71"));
	valueExpressions.add(new ValueExpression("72"));
	valueExpressions.add(new ValueExpression("73"));
	valueExpressions.add(new ValueExpression("74"));
	Restriction r = new Restriction(valueExpressions);
	r.setSeq(sequenceId);
	r.setComparator(ComparatorType.ONE_OF);
	
	List<Restriction> valueConditions = new ArrayList<Restriction>();
	valueConditions.add(r);
	entity1.setValueCondition(valueConditions);
	
	Property entity2 = new Property(
			new About(this.targetNamespace, this.targetLocalname, 
					this.targetLocalnamePropertyD));
	
	
	List<IValueExpression> valueExpressions2 = new ArrayList<IValueExpression>();
	valueExpressions2.add(new ValueExpression("Fluss, Bach"));
	Restriction r2 = new Restriction(valueExpressions2);
	r2.setSeq(sequenceId); // key link to other Seq Identifier
	r2.setComparator(ComparatorType.ONE_OF);
	
	List<Restriction> valueConditions2 = new ArrayList<Restriction>();
	valueConditions2.add(r2);
	entity2.setValueCondition(valueConditions2);
	
	cell.setEntity1(entity1);
	cell.setEntity2(entity2);
	
	a.getMap().add(cell);
	
	OmlRdfGenerator org = new OmlRdfGenerator();
	org.write(a, "res/schema/test_value_conditions_generated.xml");
	

}
}
