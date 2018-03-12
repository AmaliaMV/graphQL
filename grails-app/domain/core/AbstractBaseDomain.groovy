package core

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
abstract class AbstractBaseDomain implements Serializable {

    static mapWith = "neo4j"

    Long version = 0L

    Date dateCreated
    Date lastUpdated

    static constraints = {
        dateCreated nullable: true, bindable: false
        lastUpdated nullable: true, bindable: false
    }

    static mapping = {
        dateCreated index: true
        lastUpdated index: true
    }
}
