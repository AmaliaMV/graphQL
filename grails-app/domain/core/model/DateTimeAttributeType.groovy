package core.model

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class DateTimeAttributeType extends SimpleAttributeType {

    TypeToken getClazzType() {
        return TypeToken.of(Date)
    }
}
