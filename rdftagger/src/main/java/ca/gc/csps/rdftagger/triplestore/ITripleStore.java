package ca.gc.csps.rdftagger.triplestore;

import java.net.URI;
import java.util.Collection;

/**
 *
 * @author jturner
 */
public interface ITripleStore {

    public void put(RDFTriple triple);

    public Collection<RDFTriple> get(URI subject, URI predicate, Object objekt);

    public void removeAll(URI subject, URI predicate, Object objekt);
    
}
