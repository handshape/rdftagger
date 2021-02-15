package ca.gc.csps.rdftagger.triplestore;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author jturner
 */
public class JenaTripleStore implements ITripleStore {

    private Model model = ModelFactory.createDefaultModel();

    @Override
    public void put(RDFTriple triple) {
        Resource res = model.createResource(triple.getSubject().toASCIIString());
        Property prop = model.getProperty(triple.getPredicate().toASCIIString());
        if (prop == null) {
            prop = model.createProperty(triple.getPredicate().toASCIIString());
        }
        String objectString = String.valueOf(triple.getObjekt());
        try {
            URI obj = new URI(objectString);
            res.addProperty(prop, model.createResource(obj.toASCIIString()));
        } catch (URISyntaxException ex) {
            res.addLiteral(prop, triple.getObjekt());
        }
    }

    @Override
    public Collection<RDFTriple> get(URI subject, URI predicate, Object objekt) {
        HashSet<RDFTriple> returnable = new HashSet<>();
        String objectString = null;
        if (objekt != null) {
            objectString = String.valueOf(objekt);
        }
        Resource res = null;
        if (subject != null) {
            res = model.getResource(subject.toASCIIString());
        }
        Property p = null;
        if (predicate != null) {
            p = model.getProperty(predicate.toASCIIString());
        }
        StmtIterator iter;
        if (objectString == null) {
            iter = model.listStatements(res, p, (RDFNode) null);
        } else {
            try {
                URI obj = new URI(objectString);
                iter = model.listStatements(res, p, model.createResource(obj.toASCIIString()));
            } catch (URISyntaxException ex) {
                iter = model.listStatements(res, p, objectString);
            }
        }
        iter.forEachRemaining((Statement stmt) -> {
            try {
                if (stmt.getObject().isURIResource()) {
                    returnable.add(new RDFTriple(new URI(stmt.getSubject().getURI()), new URI(stmt.getPredicate().getURI()), stmt.getObject().asResource().getURI()));
                } else {
                    returnable.add(new RDFTriple(new URI(stmt.getSubject().getURI()), new URI(stmt.getPredicate().getURI()), stmt.getObject().asLiteral().getValue()));
                }
            } catch (URISyntaxException | RDFTripleException ex) {
                Logger.getLogger(JenaTripleStore.class.getName()).log(Level.SEVERE, "Failed to convert Jena statement: " + stmt.asTriple().toString(), ex);
            }
        });
        return returnable;
    }

    @Override

    public void removeAll(URI subject, URI predicate, Object objekt) {
        String objectString = null;
        if (objekt != null) {
            objectString = String.valueOf(objekt);
        }
        Resource res = null;
        if (subject != null) {
            res = model.getResource(subject.toASCIIString());
        }
        Property p = null;
        if (predicate != null) {
            p = model.getProperty(predicate.toASCIIString());
        }
        StmtIterator iter;
        if (objectString == null) {
            iter = model.listStatements(res, p, (RDFNode) null);
        } else {
            try {
                URI obj = new URI(objectString);
                iter = model.listStatements(res, p, model.createResource(obj.toASCIIString()));
            } catch (URISyntaxException ex) {
                iter = model.listStatements(res, p, objectString);
            }
        }
        model.remove(iter);
    }

    public void save(Path path) {
        try (OutputStream out = Files.newOutputStream(path.toAbsolutePath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            model.write(out, "TTL");
            out.flush();
            //TODO: notify the user of success...
        } catch (IOException ex) {
            Logger.getLogger(JenaTripleStore.class.getName()).log(Level.SEVERE, null, ex);
            //TODO: notify the user.
        }
    }

}
