package core.model

import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

@EqualsAndHashCode(includes = 'defaultValue')
@Slf4j
@GrailsCompileStatic
abstract class SimpleAttributeType extends AttributeType {

    String defaultValue

    static constraints = {
        defaultValue nullable: true
    }

    abstract TypeToken getClazzType()
}
