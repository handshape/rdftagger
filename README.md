# rdftagger

A simple JavaFX app illustrating how RDF triples can be authored with as few user gestures as possible.

Build requires Maven and a Java 17 JDK to build and a Java 17 runtime. Azul Zulu is recommended, although just about any compliant JDK will work -- be aware of licensing restrictions! : 

https://www.azul.com/downloads/zulu-community/

## To build:
```
cd rdftagger
mvn clean install
```

## Usage:
```
java -jar target/rdftagger-*-bin.jar [subject file] [predicate file] [object file]
```

The subject, predicate, and object files are UTF-8 encoded tab-separated values 
files or CSV files, in which the first column is the URI of the subject, predicate, or 
object, and the optional second column is a human-readable label for the given 
URI.

The subjects are treated as URLs, and used to load content into the viewing pane.

Authors: Joshua Turner
