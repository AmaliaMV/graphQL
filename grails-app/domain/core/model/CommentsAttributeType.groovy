package core.model

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

import core.Comment

@GrailsCompileStatic
class CommentsAttributeType extends AttributeType {

    TypeToken getClazzType() {
        return new TypeToken<List<Comment>>() {
        }
    }
}
