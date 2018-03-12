package core

import grails.compiler.GrailsCompileStatic
import grails.databinding.BindUsing
import grails.databinding.DataBindingSource

import org.grails.datastore.gorm.neo4j.GraphPersistentEntity

import com.brinqa.platform.core.model.DataModel

import core.model.DataModel

@GrailsCompileStatic
class Ticket extends DataSet {

    static mapWith = "neo4j"

    String summary
    String status
    String consolidationUid

    List<DataSet> sources = []

    static hasMany = [sources: DataSet]

    static constraints = {
        summary nullable: false
        status nullable: true
        consolidationUid nullable: true
    }

    static mapping = {
        labels "__Ticket__", { GraphPersistentEntity pe, Ticket instance ->
            if (instance.dataModel) {
                buildLabels(instance.dataModel)
            }
        }

        summary index: true
    }

    Ticket(DataModel dataModel) {
        super(dataModel)
    }

    @SuppressWarnings("UnnecessaryOverridingMethod")
    def propertyMissing(String name) {
        super.propertyMissing(name)
    }

    @SuppressWarnings("UnnecessaryOverridingMethod")
    def propertyMissing(String name, val) {
        super.propertyMissing(name, val)
    }

}
