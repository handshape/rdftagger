package ca.gc.csps.rdftagger.triplestore;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 *
 * @author jturner
 */
public class TrieTripleStore implements ITripleStore {

    private PatriciaTrie triples = new PatriciaTrie<>();

    @Override
    public void put(RDFTriple triple) {
        if (triple != null) {
            triples.put(triple.toString(), "");
        }
    }

    @Override
    public Collection<RDFTriple> get(URI subject, URI predicate, Object objekt) {
        HashSet<RDFTriple> returnble = new HashSet<>();
        for (Object key : triples.keySet()) {
            try {
                RDFTriple triple = RDFTriple.parse(String.valueOf(key));
                if ((subject != null && !subject.equals(triple.getSubject()))
                        || (predicate != null && !predicate.equals(triple.getPredicate()))
                        || (objekt != null && !objekt.equals(triple.getObjekt()))) {
                } else {
                    returnble.add(triple);
                }
            } catch (URISyntaxException | RDFTripleException ex) {
                Logger.getLogger(TrieTripleStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return returnble;
    }

    @Override
    public void removeAll(URI subject, URI predicate, Object objekt) {
        PatriciaTrie toDelete = new PatriciaTrie();
        for (Object key : triples.keySet()) {
            try {
                RDFTriple triple = RDFTriple.parse(String.valueOf(key));
                if ((subject != null && !subject.equals(triple.getSubject()))
                        || (predicate != null && !predicate.equals(triple.getPredicate()))
                        || (objekt != null && !objekt.equals(triple.getObjekt()))) {
                } else {
                    toDelete.put(String.valueOf(key), "");
                }
            } catch (URISyntaxException | RDFTripleException ex) {
                Logger.getLogger(TrieTripleStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (Object key : toDelete.keySet()) {
            triples.remove(key);
        }
    }

}
