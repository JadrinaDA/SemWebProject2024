package semweb_project2;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class readpref2 {

	public static void main(String[] args) {
        // Specify the RDF file path or URL
        String rdfFilePath = "https://www.emse.fr/~zimmermann/Teaching/SemWeb/Project/";

        // Create an RDF model
        Model model = ModelFactory.createDefaultModel();

     // Specify the URL of the web page
        String url = "https://www.emse.fr/~zimmermann/Teaching/SemWeb/Project/pref-charpenay.ttl";

        try {
            // Connect to the web page and fetch its HTML content
            Document document = Jsoup.connect(url).get();
            System.out.println(document.body());

            // Select the <body><pre> element
            Element preElement = document.select("pre").first();

            // Extract text content from <pre> element
            String rdfData = preElement.text();

            // Print the RDF data
            System.out.println(rdfData);

            // Now you can parse the RDF data using regular expressions or any RDF parsing library
            // Extract latitude, longitude, geoRadius, postal code, and maxPrice using regex or other methods
            // For simplicity, let's print the raw RDF data for now

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Read the RDF data from the specified file or URL
        try (InputStream in = FileManager.get().open(rdfFilePath)) {
            if (in == null) {
                throw new IllegalArgumentException("File not found: " + rdfFilePath);
            }
            
            System.out.println(in);

            // Read the RDF data into the model
//            model.read(in, null, "TURTLE");
//            model.write(System.out, "TURTLE");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fetch information from the RDF model
        Resource me = model.getResource("#me");

        // Get latitude, longitude, geoRadius, postal code, and maxPrice
        String latitude = me.getProperty(model.getProperty("http://schema.org/geoMidpoint/schema:latitude")).getString();
        String longitude = me.getProperty(model.getProperty("http://schema.org/geoMidpoint/schema:longitude")).getString();
        String geoRadius = me.getProperty(model.getProperty("http://schema.org/geoRadius")).getString();
        String postalCode = me.getProperty(model.getProperty("http://schema.org/postalCode")).getString();
        String maxPrice = me.getProperty(model.getProperty("http://schema.org/maxPrice")).getString();

        // Print the fetched information
        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);
        System.out.println("Geo Radius: " + geoRadius);
        System.out.println("Postal Code: " + postalCode);
        System.out.println("Max Price: " + maxPrice);
    }

}
