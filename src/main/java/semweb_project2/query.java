package semweb_project2;

import org.apache.jena.rdfconnection.RDFConnectionFactory;

public class query {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String datasetURL = "http://localhost:3030/coopcycle_dataset";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
		QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI,
				query);
		ResultSet results = q.execSelect();

		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			RDFNode x = soln.get("x");
			System.out.println(x);
		}
		
		try ( RDFConnection conn = RDFConnection.connect(datasetURL) ) {
		    ResultSet safeCopy =
		        Txn.execReadReturn(conn, () -> {
		            // Process results by row:
		            conn.querySelect("SELECT DISTINCT ?s { ?s ?p ?o }", (qs) -> {
		                Resource subject = qs.getResource("s") ;
		                System.out.println("Subject: "+subject) ;
		            }) ;
		            ResultSet rs = conn.query("SELECT * { ?s ?p ?o }").execSelect() ;
		            return ResultSetFactory.copyResults(rs) ;
		        }) ;
		}

	}

}
