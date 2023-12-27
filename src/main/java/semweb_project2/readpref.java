package semweb_project2;

import java.io.File;

import org.apache.jena.rdf.model.*;
import org.apache.jena.query.*;
import org.apache.jena.riot.RDFDataMgr;

import java.net.URL;

import org.apache.commons.io.FileUtils;

public class readpref {

    public static void main(String[] args) {
        String url = "https://www.emse.fr/~zimmermann/Teaching/SemWeb/Project/pref-charpenay.ttl";
        String outputFile = "user_preferences.ttl";

        try {
            URL rdfUrl = new URL(url);
            File outputfile = new File(outputFile);

            // Fetch RDF information from the URL and save it into a file
            FileUtils.copyURLToFile(rdfUrl, outputfile);

            System.out.println("RDF information fetched and saved to: " + outputfile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Model model = RDFDataMgr.loadModel(outputFile);
//        model.write(System.out, "TURTLE");
        
     // SPARQL query to extract the information
        String queryString = 
            "PREFIX schema: <http://schema.org/> " +
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
            "  schema:name ?userName" +
            "} ";

        // Execute the query
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                double latitude = soln.getLiteral("latitude").getDouble();
                double longitude = soln.getLiteral("longitude").getDouble();
                int radius = soln.getLiteral("radius").getInt();
                int postalCode = soln.getLiteral("postalCode").getInt();
                double maxPrice = soln.getLiteral("maxPrice").getDouble();
                String userName = soln.getLiteral("userName").getString();

                // Process the extracted values as needed
                System.out.println("User Name: " + userName);
                System.out.println("Latitude: " + latitude);
                System.out.println("Longitude: " + longitude);
                System.out.println("Geo Radius: " + radius);
                System.out.println("Postal Code: " + postalCode);
                System.out.println("Max Price: " + maxPrice);
            }
        }
    }
    
}
