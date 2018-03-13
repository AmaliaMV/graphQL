package core

import java.lang.reflect.Method

import org.springframework.util.ReflectionUtils

import grails.databinding.BindUsing
import grails.databinding.DataBindingSource

import org.grails.datastore.gorm.neo4j.GraphPersistentEntity
import org.grails.datastore.gorm.neo4j.Neo4jSession

import core.model.Attribute
import core.model.DataModel

class DataSet extends AbstractBaseDomain {

    static mapWith = "neo4j"

    @BindUsing({ DataSet object, DataBindingSource source ->
        if (source['dataModel']) {
            Long dataModelID = ((Map) source['dataModel']).id as Long
            return DataModel.get(dataModelID)
        }
    })
    DataModel dataModel

    Boolean active = true
    protected String displayValue

    static constraints = {
        dataModel nullable: true, dataSet: true
        active nullable: false
    }

    static mapping = {
        dynamicAssociations true
        labels(
            { GraphPersistentEntity pe, DataSet instance ->
                if (instance.dataModel) {
                    buildLabels(instance.dataModel)
                }
            }
        )
        dataModel lazy: false, fetch: "eager"
    }

    String getDisplayValueProperty() {
        return dataModel?.displayAttribute?.name
    }

    DataSet(DataModel dataModel) {
        this.dataModel = dataModel
    }

    def propertyMissing(String name, val) {
        if (val instanceof Date) {
            super.propertyMissing(name, val.time)
        }
        else {
            super.propertyMissing(name, val)
        }
    }

    String getDisplayValue() {
        if (!this.displayValue) {
            this.displayValue = dataSetService.getDisplayValue(this)
        }
        return this.displayValue
    }

    boolean containsProperty(String propertyName) {
        return getProperty(propertyName) || (this.dataModel?.getAttributeByName(propertyName) != null)
    }

    Object getPropertyValue(String propertyName) {
        Object ret

        Method method = ReflectionUtils.findMethod(DataSet, 'getProperty', String)

        if (method != null) {
            ret = ReflectionUtils.invokeMethod(method, this, propertyName)
        }
        else {
            method = ReflectionUtils.findMethod(DataSet, 'propertyMissing', String)
            ret = ReflectionUtils.invokeMethod(method, this, propertyName)
        }

        return ret
    }

    Set<String> getPropertyNames() {
        Set<String> properties = []
        properties.addAll((Collection<String>) this.getProperties().keySet())

        this.dataModel?.attributes?.each { Attribute attribute ->
            properties.add(attribute.name)
        }

        return properties
    }

    Map<String, Object> getAttributes() {
        Map<String, Object> map
        withSession { Neo4jSession session ->
            map = (Map<String, Object>) session.getAttribute(this, 'undeclared')
        }
        return map
    }

    protected static String buildLabels(DataModel dataModel) {
        StringBuilder labels = new StringBuilder()

        if (dataModel) {
            labels.append("`${dataModel.title}`")
            if (dataModel.parent) {
                labels.append(":")
                labels.append(buildLabels(dataModel.parent))
            }
        }

        return labels.toString()
    }
}
