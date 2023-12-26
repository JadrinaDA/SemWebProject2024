package semweb_project2;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.RDFDataMgr;

public class readpref {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final String url = "https://www.emse.fr/~zimmermann/Teaching/SemWeb/Project/pref-charpenay.ttl";
        final Model model = ModelFactory.createDefaultModel();
        model.read(url);
        model.write(System.out);
        
        
        //System.out.print(userLon.getURI());


	}

}
