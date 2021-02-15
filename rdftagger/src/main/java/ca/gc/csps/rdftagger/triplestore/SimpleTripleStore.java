package ca.gc.csps.rdftagger.triplestore;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author jturner
 */
public class SimpleTripleStore implements ITripleStore {

    private HashSet<RDFTriple> triples = new HashSet<>();

    @Override
    public void put(RDFTriple triple) {
        triples.add(triple);
    }

    @Override
    public Collection<RDFTriple> get(URI subject, URI predicate, Object objekt) {
        HashSet<RDFTriple> returnble = new HashSet<>();
        for (RDFTriple triple : triples) {
            if ((subject != null && !subject.equals(triple.getSubject()))
                    || (predicate != null && !predicate.equals(triple.getPredicate()))
                    || (objekt != null && !objekt.equals(triple.getObjekt()))) {
            } else {
                returnble.add(triple);
            }

        }
        return returnble;
    }

    @Override
    public void removeAll(URI subject, URI predicate, Object objekt) {
        triples.removeAll(get(subject, predicate, objekt));
    }

}
