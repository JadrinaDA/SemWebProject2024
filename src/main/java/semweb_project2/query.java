package semweb_project2;

import java.util.Scanner;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

public class query {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Create a query string
        String queryString = "PREFIX schema: <https://schema.org/> " +
                             "SELECT ?storeName " +
                             "WHERE {" +
                             "  ?storeUri a schema:ProfessionalService ;" +
                             "            schema:openingHoursSpecification [ " +
                             "              a schema:OpeningHoursSpecification ;" +
                             "              schema:opens ?opens ;" +
                             "              schema:closes ?closes ;" +
                             "              schema:dayOfWeek ?dayOfWeek" +
                             "            ] ;" +
                             "            schema:location [" +
                             "                 schema:longitude ?long;" +
                             "                 schema:latitude ?lat;" +
                             "            ] ;" +
                             "            schema:name ?storeName ;" +
                             "            schema:priceRange ?price ." +
                             "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                             "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) > userMinute) )" +
                             "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) < userMinute) )" +
                             "  FILTER (((?lat - userLat)*110.574)*((?lat - userLat)*110.574) + ((?long - userLon)*111.32*cos)*((?long - userLon)*111.32*0.75)  < disSq)" +
                             "  FILTER (?price <= userLim)" +
                             "} ORDER BY (((?lat - 49.40391)*110.574)*((?lat - 49.40391)*110.574) +  ((?long - 2.42788)*111.32*0.6507224012742214)*((?long - 2.42788)*111.32*0.75))";
		
		String datasetURL = "http://localhost:3030/coopcycle_dataset";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		
		UserProfile user = readpref.getUserPreferences(sparqlEndpoint,sparqlUpdate,graphStore);
		if (user == null) {
			System.err.println("user = null");
			return;
		}
		
		Scanner scan = new Scanner(System.in); 
		System.out.println("Welcome back " + user.getUserName() + "!");
	    System.out.println("Please, enter day of week as number:");
    	String day = scan.nextLine();  
	    System.out.println("Enter time (hh:mm)");
	    String time = scan.nextLine();
	    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		String userDayOfWeek = daysOfWeek[(Integer.parseInt(day)-1)];
		String userHour = time.split(":")[0];
		String userMinute = time.split(":")[1];
		System.out.println("Looking up restaurant for: " + userDayOfWeek + " at " + time);  // Output user input
		// We made the following assumptions based on an internet search and testing on google maps
		// 1 degree of lat = 110.547
		// 1 degree of lon = 111.32 * cos(lat)
		Double cos = Math.cos(user.getLatitude() * Math.PI /180);
		scan.close();
		
		// Set the user input as parameters in the query
        queryString = queryString.replaceAll("userDayOfWeek", userDayOfWeek);
        queryString = queryString.replaceAll("userHour", userHour);
        queryString = queryString.replaceAll("userMinute", userMinute);
        queryString = queryString.replaceAll("userLat", Double.toString(user.getLatitude()));
        queryString = queryString.replaceAll("userLon", Double.toString(user.getLongitude()));
        queryString = queryString.replaceAll("cos", Double.toString(cos));
        queryString = queryString.replaceAll("disSq", Double.toString(Math.pow(user.getGeoRadius(),2)));
        queryString = queryString.replaceAll("userLim", Double.toString(user.getMaxPrice()));
        RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
        QueryExecution qExec = conn.query(queryString) ;
		ResultSet rs = qExec.execSelect() ;
		while(rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
	        String storeName = soln.getLiteral("storeName").getString();
	        System.out.println("Store open: " + storeName);
		}
		qExec.close() ;
		conn.close() ;

	}
	
	

}
