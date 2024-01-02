package semweb_project2;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;

public class readpref {
	private String userName;
	
	public readpref(String userName)
	{
		this.userName = userName;
	}

	public String[] getPreferences(String url) {
		// TODO Auto-generated method stub
		//final String url = "https://www.emse.fr/~zimmermann/Teaching/SemWeb/Project/pref-charpenay.ttl";
        final Model model = ModelFactory.createDefaultModel();
        model.read(url);
        //model.write(System.out);
        
        // Fetch information from the RDF model
        Resource me = model.getResource(url+"#me");
        String schema = "http://schema.org/";

        // Get latitude, longitude, geoRadius, postal code, and maxPrice
        Resource seeks = me.getPropertyResourceValue(model.createProperty(schema + "seeks"));
        Resource priceSpec = seeks.getPropertyResourceValue(model.createProperty(schema+"priceSpecification"));
        Resource aAOF =  seeks.getPropertyResourceValue(model.createProperty(schema+"availableAtOrFrom"));
        Resource geoWithin = aAOF.getPropertyResourceValue(model.createProperty(schema+"geoWithin"));
        Resource geoMidpoint = geoWithin.getPropertyResourceValue(model.createProperty(schema+"geoMidpoint"));
        String latitude = geoMidpoint.getProperty(model.createProperty(schema + "latitude")).getString();
        String longitude = geoMidpoint.getProperty(model.createProperty(schema + "longitude")).getString();
        String geoRadius = geoWithin.getProperty(model.getProperty(schema+"geoRadius")).getString();
        String maxPrice = priceSpec.getProperty(model.getProperty(schema+"maxPrice")).getString();
        //String time = seeks.getProperty(model.createProperty(schema+"availabilityStarts")).getString();
        //String day = seeks.getProperty(model.createProperty(schema+"dayOfWeek")).getString();
        // Print the fetched information
        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);
        System.out.println("Geo Radius: " + geoRadius);
        System.out.println("Max Price: " + maxPrice);
        //System.out.println("Time: " + time);
        //System.out.println("Day: " + day);
        
        //System.out.print(userLon.getURI());
        String[] list = {latitude, longitude, geoRadius, maxPrice, "12:00", "Saturday"};
        return list;


	}

}
