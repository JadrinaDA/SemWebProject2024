package semweb_project2;

import java.util.Scanner;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

public class query {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Create a query string
        String queryString = "PREFIX schema: <https://schema.org/> " +
                             "SELECT ?storeUri ?storeName " +
                             "WHERE {" +
                             "  ?storeUri a schema:ProfessionalService ;" +
                             "            schema:openingHoursSpecification [ " +
                             "              a schema:OpeningHoursSpecification ;" +
                             "              schema:opens ?opens ;" +
                             "              schema:closes ?closes ;" +
                             "              schema:dayOfWeek ?dayOfWeek" +
                             "            ] ;" +
                             "            schema:name ?storeName ." +
                             "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                             "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) > userMinute) )" +
                             "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) < userMinute) )" +
                             "} ORDER BY ?storeName";
		
		String datasetURL = "http://localhost:3030/coopcycle_dataset";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		
		Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter day of week as number:");

	    String day = scan.nextLine();  // Read user input
	    System.out.println("Enter time (hh:mm)");
	    String time = scan.nextLine();
	    scan.close();
	    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		String userDayOfWeek = daysOfWeek[Integer.parseInt(day)];
		String userHour = time.split(":")[0];
		String userMinute = time.split(":")[1];
		System.out.println("Looking up restaurant for: " + userDayOfWeek + " at " + time);  // Output user input
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
				// Set the user input as parameters in the query
	            queryString = queryString.replaceAll("userDayOfWeek", userDayOfWeek);
	            queryString = queryString.replaceAll("userHour", userHour);
	            queryString = queryString.replaceAll("userMinute", userMinute);
	            QueryExecution qExec = conn.query(queryString) ;
				ResultSet rs = qExec.execSelect() ;
				while(rs.hasNext()) {
				  QuerySolution qs = rs.next() ;
				  Resource subject = qs.getResource("storeUri") ;
				  System.out.println("Subject: " + subject) ;
				}
				qExec.close() ;
				conn.close() ;

	}

}
