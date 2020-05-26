package ca.gc.justice.rdftagger.triplestore;

import java.net.URI;

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

}
