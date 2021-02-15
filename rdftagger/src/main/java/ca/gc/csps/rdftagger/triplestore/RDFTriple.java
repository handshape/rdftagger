package ca.gc.csps.rdftagger.triplestore;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author jturner
 */
public class RDFTriple {

    private final URI subject;
    private final URI predicate;
    private final Object objekt;

    public RDFTriple(URI subject, URI predicate, Object objekt) throws RDFTripleException {
        if (subject == null || predicate == null || objekt == null) {
            throw new RDFTripleException("Triples cannot have null values.");
        }
        this.subject = subject;
        this.predicate = predicate;
        this.objekt = objekt;
    }

    public static RDFTriple parse(String in) throws URISyntaxException, RDFTripleException {
        String[] parts = in.split("\t");
        return new RDFTriple(new URI(parts[0]), new URI(parts[1]), parts[2]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RDFTriple)) {
            return false;
        }
        RDFTriple other = (RDFTriple) obj;
        return other.subject.equals(subject) && other.predicate.equals(predicate) && other.objekt.equals(objekt);
    }

    @Override
    public int hashCode() {
        return String.valueOf(subject).hashCode() * String.valueOf(predicate).hashCode() * String.valueOf(objekt).hashCode();
    }

    /**
     * @return the subject
     */
    public URI getSubject() {
        return subject;
    }

    /**
     * @return the predicate
     */
    public URI getPredicate() {
        return predicate;
    }

    /**
     * @return the objekt
     */
    public Object getObjekt() {
        return objekt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSubject().toASCIIString());
        sb.append("\t");
        sb.append(getPredicate().toASCIIString());
        sb.append("\t");
        sb.append(String.valueOf(getObjekt()));
        return sb.toString();
    }

}
