package core.model

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class URLAttributeType extends TextAttributeType {

    static mapWith = "neo4j"
    static graphql = true

    static constraints = {
        defaultValue nullable: true
    }

    URLAttributeType() {
        super()
    }

    URLAttributeType(Integer length, String defaultValue) {
        super(length, defaultValue)
    }
}
