package util

import grails.compiler.GrailsCompileStatic
import grails.util.Holders

import org.grails.datastore.gorm.neo4j.GraphPersistentEntity
import org.grails.datastore.gorm.neo4j.Neo4jDatastore

@GrailsCompileStatic
class Labels {

    static Set<String> getLabels(Class klass) {
        Set<String> labels = []

        if (klass) {
            GraphPersistentEntity persistentEntity = (GraphPersistentEntity) neo4jDatastore.mappingContext?.getPersistentEntity(klass.name)

            persistentEntity?.getLabels()?.each { String label ->
                labels.add(label)
            }
        }

        return labels
    }

    static toString(Set<String> labels) {
        StringBuilder builder = new StringBuilder()
        labels.each { String label ->
            builder.append(':`')
            builder.append(label)
            builder.append('`')
        }
        return builder.toString()
    }

    static String getLabelsString(Class klass) {
        return toString(getLabels(klass))
    }

    static Neo4jDatastore getNeo4jDatastore() {
        return (Neo4jDatastore) Holders.grailsApplication.mainContext.getBean(Neo4jDatastore)
    }
}
