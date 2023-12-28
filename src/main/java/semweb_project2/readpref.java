package semweb_project2;


import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.query.*;

import java.util.Scanner;

public class readpref {

    public static UserProfile getUserPreferences(String sparqlEndpoint, String sparqlUpdate, String graphStore) {       
    	RDFConnection connection = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
//        Scanner scan = new Scanner(System.in);  // Create a Scanner object
//	    System.out.println("Welcome back. Please, indicate the number corresponding your user [1. Antoine , 2. Ignacio , 3. Jadrina , 4. Victor]:");
//	    String userNumber = scan.nextLine();  // Read user input
//		scan.close();
//		
//		String[] demoUsers = {"Antoine", "Ignacio", "Jadrina", "Victor"};
//		String userName = demoUsers[(Integer.parseInt(userNumber)-1)];
    	
    	String userName = "Victor";
		
        UserProfile userInfo = null;
        
        // SPARQL query to extract the information	
		String queryString = 
	            "PREFIX schema: <https://schema.org/> " +
	            "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
	            "SELECT ?userName ?latitude ?longitude ?radius ?postalCode ?maxPrice " +
	            "WHERE { " +
	            "  ?user schema:seeks [ " +
	            "    schema:availableAtOrFrom [ " +
	            "      schema:geoWithin [ " +
	            "        schema:geoMidpoint [ " +
	            "          schema:longitude ?longitude ; " +
	            "          schema:latitude ?latitude " +
	            "        ] ; " +
	            "        schema:geoRadius ?radius " +
	            "      ] " +
	            "    ] ; " +
	            "    schema:priceSpecification [ " +
	            "      schema:maxPrice ?maxPrice " +
	            "    ] " +
	            "  ];" +
	            "  schema:address [ " +
	            "    schema:postalCode ?postalCode" +
	            "  ]; " +
	            "  schema:name ?userName . " +
	            "  FILTER regex(str(?userName), \"userNameSelected\")" +
	            "} " +
	            "LIMIT 1";
        
     // Set the user input as parameters in the query
 		queryString = queryString.replaceAll("userNameSelected", userName);
        QueryExecution qExec = connection.query(queryString) ;
        ResultSet results = qExec.execSelect();
        if (results.hasNext()) {
             QuerySolution soln = results.nextSolution();
             double latitude = soln.getLiteral("latitude").getDouble();
             double longitude = soln.getLiteral("longitude").getDouble();
             int radius = soln.getLiteral("radius").getInt();
             int postalCode = soln.getLiteral("postalCode").getInt();
             double maxPrice = soln.getLiteral("maxPrice").getDouble();
             userName = soln.getLiteral("userName").getString();

             userInfo = new UserProfile(userName, latitude, longitude, radius, postalCode, maxPrice);
         }
        
        qExec.close() ;
 		connection.close() ;
        
        return userInfo;
    }
    
}
