package semweb_project2;

import org.apache.jena.graph.Graph;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.sparql.core.DatasetGraphFactory;

public class Validator {
	
	private String SHAPES;

	public Validator(String shapes) {
		this.SHAPES = shapes;
	};
	
	public boolean IsValid(Graph dataGraph)
	{

        Graph shapesGraph = RDFDataMgr.loadGraph(this.SHAPES);
        //Graph dataGraph = DatasetGraphFactory.createTxnMem().getDefaultGraph();
        //RDFDataMgr.read(dataGraph, DATA);
        Shapes shapes = Shapes.parse(shapesGraph);

        // Check initially valid.
        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
        //ShLib.printReport(report);
        if ( ! report.conforms() ) {
            System.out.println("** Initial data does not validate");
            return false;
        }
        return true;
	}
	
//	public static void main(String[] args)
//	{
//		Validator validator = new Validator("shapes.ttl");
//		System.out.println(validator.IsValid("test.ttl"));
//	}

}
