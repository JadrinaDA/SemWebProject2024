package semweb_project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
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
                             "  BIND (((?lat - 49.89)*110.574)*((?lat - 49.89)*110.574) +  ((?long - 2.26)*111.32*0.6442571239197948)*((?long - 2.26)*111.32*0.6442571239197948) as ?dist)";
        String allMatchQS = queryString + "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) >= userMinute) )" +
                "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) <= userMinute) )" +
                "  FILTER (?dist  < disSq)" +
                "  FILTER (?price <= userLim)";
        String timeDistMatchQS = queryString + "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) >= userMinute) )" +
                "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) <= userMinute) )" +
                "  FILTER (?dist  < disSq)";
        String timePriceMatchQS = queryString + "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) >= userMinute) )" +
                "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) <= userMinute) )" +
                "  FILTER (?price <= userLim)";
        String distPriceMatchQS = queryString +
                "  FILTER (?dist  < disSq)" +
                "  FILTER (?price <= userLim)";
        String justTimeMatchQS = queryString + "  FILTER (?dayOfWeek = \"userDayOfWeek\") " +
                "  FILTER(hours(?closes) > userHour || (hours(?closes) = userHour && minutes(?closes) >= userMinute) )" +
                "  FILTER(hours(?opens) < userHour || (hours(?opens) = userHour && minutes(?opens) <= userMinute) )";
        String justDistMatchQS = queryString +
                "  FILTER (?dist  < disSq)";
        String justPriceMatchQS = queryString +
                "  FILTER (?price <= userLim)";
        String[] queryStrings = {allMatchQS, timeDistMatchQS, timePriceMatchQS, distPriceMatchQS, justTimeMatchQS, justDistMatchQS, justPriceMatchQS};
        for (int j= 0; j < queryStrings.length; j++)  
        {
        	if (args[1].equals("price"))
			{
			
        	queryStrings[j] +=  "} ORDER BY ?price";	
			}
	        else if (args[1].equals("distance"))
			{
	        	queryStrings[j] +=  "} ORDER BY ?distance";
			}
			else
			{
				queryStrings[j] += "} ORDER BY ?storeName";
			}
        }
        
		String datasetURL = "http://localhost:3030/coopcycle_dataset";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		
		Double userLat;
		Double userLon;
		String userDayOfWeek;
		String userHour;
		String userMinute;
		String userLim;
		Integer radius;
		if (args[2].equals("manual"))
		{
			Scanner scan = new Scanner(System.in);  // Create a Scanner object
		    System.out.println("Enter day of week as number:");

		    String day = scan.nextLine();  // Read user input
		    System.out.println("Enter time (hh:mm)");
		    String time = scan.nextLine();
		    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
			userDayOfWeek = daysOfWeek[Integer.parseInt(day)];
			userHour = time.split(":")[0];
			userMinute = time.split(":")[1];
			System.out.println("Looking up restaurant for: " + userDayOfWeek + " at " + time);  // Output user input
			
			System.out.println("Enter your latitude (float)");
		    String templat = scan.nextLine();
		    System.out.println("Enter your longitude (float)");
		    String templon = scan.nextLine();
			userLat = Double.parseDouble(templat);
			userLon = Double.parseDouble(templon);
			// We made the following assumptions based on an internet search and testing on google maps
			// 1 degree of lat = 110.547
			// 1 degree of lon = 111.32 * cos(lat)
			System.out.println("Enter maximum distance/radius");
		    String temprad = scan.nextLine();
			radius = Integer.parseInt(temprad);
			System.out.println("Enter maximum price");
			userLim = scan.nextLine();
			scan.close();
		}
		else
		{
			Scanner scan = new Scanner(System.in);  // Create a Scanner object
		    System.out.println("Enter your username:");
		    String userName = scan.nextLine();  // Read user input
			readpref readPref = new readpref("user#"+userName);
			String[] list = readPref.getPreferences(args[2]);
			userLat = Double.parseDouble(list[0]);
			userLon = Double.parseDouble(list[1]);
			radius = Integer.parseInt(list[2]);
			userLim = list[3];
			userHour = list[4].split(":")[0];
			userMinute = list[4].split(":")[1];
			userDayOfWeek = list[5];
		}
		Double cos = Math.cos(userLat * Math.PI /180);
		
		List<String> results = new ArrayList<>();
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
				// Set the user input as parameters in the query
				for (int j = 0; j < queryStrings.length; j++)
				{
					queryStrings[j] = queryStrings[j].replaceAll("userDayOfWeek", userDayOfWeek);
					queryStrings[j]= queryStrings[j].replaceAll("userHour", userHour);
					queryStrings[j] = queryStrings[j].replaceAll("userMinute", userMinute);
					queryStrings[j] = queryStrings[j].replaceAll("userLat", Double.toString(userLat));
					queryStrings[j] = queryStrings[j].replaceAll("userLon", Double.toString(userLon));
					queryStrings[j] = queryStrings[j].replaceAll("cos", Double.toString(cos));
					queryStrings[j] = queryStrings[j].replaceAll("disSq", Integer.toString(radius*radius));
					queryStrings[j] = queryStrings[j].replaceAll("userLim", userLim);
		            System.out.println(queryStrings[j]);
		            QueryExecution qExec = conn.query(queryStrings[j]) ;
					ResultSet rs = qExec.execSelect() ;
					while(rs.hasNext()) {
					  QuerySolution qs = rs.next() ;
					  Literal subject = qs.getLiteral("storeName") ;
					  if (!results.contains(subject.toString()))
					  {
						  results.add(subject.toString());
					  }
					}
					qExec.close() ;
					
				}
				conn.close() ;
		for (int k = 0; k < results.size(); k++)
		{
			System.out.println(results.get(k));
		}

	}

}
