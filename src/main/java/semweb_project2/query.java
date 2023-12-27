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
                             "schema:location [" +
                             "                 schema:longitude ?long;" +
                             "                 schema:latitude ?lat;" +
                             "             ];" +
                             "            schema:name ?storeName ;" +
                             "            schema:priceRange ?price ." +
                             "  BIND (((?lat - 49.89)*110.574)*((?lat - 49.89)*110.574) +  ((?long - 2.26)*111.32*0.6442571239197948)*((?long - 2.26)*111.32*0.6442571239197948) as ?dist)" +
                             "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                             "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) >= userMinute) )" +
                             "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) <= userMinute) )" +
                             "  FILTER (?dist  < disSq)" +
                             "  FILTER (?price <= userLim)";
        if (args[1].equals("price"))
			{
			  queryString +=  "} ORDER BY ?price";	
			}
        else if (args[1].equals("distance"))
		{
			queryString +=  "} ORDER BY ?distance";
		}
		else
		{
			 queryString += "} ORDER BY ?storeName";
		}
		String datasetURL = "http://localhost:3030/coopcycle_dataset";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		
		Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter day of week as number:");

	    String day = scan.nextLine();  // Read user input
	    System.out.println("Enter time (hh:mm)");
	    String time = scan.nextLine();
	    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		String userDayOfWeek = daysOfWeek[Integer.parseInt(day)];
		String userHour = time.split(":")[0];
		String userMinute = time.split(":")[1];
		System.out.println("Looking up restaurant for: " + userDayOfWeek + " at " + time);  // Output user input
		
		System.out.println("Enter your latitude (float)");
	    String templat = scan.nextLine();
	    System.out.println("Enter your longitude (float)");
	    String templon = scan.nextLine();
		Double userLat = Double.parseDouble(templat);
		Double userLon = Double.parseDouble(templon);
		// We made the following assumptions based on an internet search and testing on google maps
		// 1 degree of lat = 110.547
		// 1 degree of lon = 111.32 * cos(lat)
		System.out.println("Enter maximum distance/radius");
	    String temprad = scan.nextLine();
		Integer radius = Integer.parseInt(temprad);
		Double cos = Math.cos(userLat * Math.PI /180);
		System.out.println(cos);
		System.out.println("Enter maximum price");
		String userLim = scan.nextLine();
		scan.close();
		
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
				// Set the user input as parameters in the query
	            queryString = queryString.replaceAll("userDayOfWeek", userDayOfWeek);
	            queryString = queryString.replaceAll("userHour", userHour);
	            queryString = queryString.replaceAll("userMinute", userMinute);
	            queryString = queryString.replaceAll("userLat", Double.toString(userLat));
	            queryString = queryString.replaceAll("userLon", Double.toString(userLon));
	            queryString = queryString.replaceAll("cos", Double.toString(cos));
	            queryString = queryString.replaceAll("disSq", Integer.toString(radius*radius));
	            queryString = queryString.replaceAll("userLim", userLim);
	            System.out.println(queryString);
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
