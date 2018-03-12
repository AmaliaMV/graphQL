package core.model

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class EmailAttributeType extends TextAttributeType {

    static constraints = {
        defaultValue nullable: true
    }

    EmailAttributeType() {
        super()
    }

    EmailAttributeType(Integer length, String defaultValue) {
        super(length, defaultValue)
    }

}
