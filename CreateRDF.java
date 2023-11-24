package semweb_project;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;


public class CreateRDF {
	
	public static void main(String args[])
	{
		JSONArray o;
		Model model = ModelFactory.createDefaultModel();
		String schema = "https://schema.org/";
		
		try {
			o = (JSONArray) new JSONParser().parse(new FileReader("coopcycle.json"));
			for (int i = 0; i < o.size(); i++)
			{
				JSONObject service = (JSONObject) o.get(i);
				JSONObject text = (JSONObject) service.get("text");
				Resource address = model.createResource()
                        .addProperty(RDF.type, model.createResource(schema+"PostalAddress"))
                        .addProperty(model.createProperty(schema+"addressLocality"), (String) service.get("city"))
                        .addProperty(model.createProperty(schema+"addressCountry"), ((String) service.get("country")).toUpperCase());
				Resource location = model.createResource()
						                 .addProperty(RDF.type, model.createResource(schema+"Place"))
						                 .addProperty(model.createProperty(schema+"address"), address)
						                 .addProperty(model.createProperty(schema+"latitude"), model.createTypedLiteral((Double)service.get("latitude")))
						                 .addProperty(model.createProperty(schema+"longitude"),model.createTypedLiteral((Double) service.get("longitude")));
				
				Resource serv = model.createResource((String) service.get("coopcycle_url"))
						             .addProperty(RDF.type, model.createResource(schema+"ProfessionalService"))
						             .addProperty(model.createProperty(schema+"location"), location)
						             .addProperty(model.createProperty(schema+"name"), (String) service.get("name"));
						             
				if (service.get("mail") != null)
				{
					serv.addProperty(model.createProperty(schema+"email"), (String) service.get("mail"));
				}
				if (text != null)
				{
					Iterator<String> keys = text.keySet().iterator();
					
					while(keys.hasNext())
					{
						String key = keys.next();
						serv.addLiteral(model.createProperty(schema+"description"), model.createLiteral((String)text.get(key), key));
					}
				}
				Iterator<String> keys = service.keySet().iterator();
				while (keys.hasNext())
				{
					String key = keys.next();
					if (key.contains("url") && !key.contains("coopcycle"))
					{
						serv.addProperty(model.createProperty(schema+"url"), (String) service.get(key));
					}
				}
				System.out.println(text);
									 
									 
				                     //.addProperty(model.createProperty(schema+"url"), (String) service.get("url"))
				                     //.addProperty(model.createProperty(schema+"url"), (String) service.get("facebook_url"));
				
			}
			
	        model.setNsPrefix("schema", schema);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.write(System.out, "Turtle") ;


		
	}

}
