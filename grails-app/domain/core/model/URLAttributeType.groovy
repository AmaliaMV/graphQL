package core.model

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class URLAttributeType extends TextAttributeType {

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
