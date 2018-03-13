package core.model

import core.ConfigItem
import util.Constants
import util.Labels

class Attribute extends ConfigItem {

    String description

    String helpText

    Integer order = 1

    String metadata

    AttributeType type

    String manifest

    DataModel dataModel

    static belongsTo = DataModel

    static constraints = {
        name blank: false,
             maxSize: 500,
             matches: "^(?!.*__.*)[a-zA-Z](?:\\w*[a-zA-Z0-9])?\$",
             validator: { String val, Attribute obj ->
                 if (obj.dataModel) {
                     Set<Attribute> attrs = obj.dataModel.attributes.findAll { it.name == val } as Set
                     if (attrs.size() > 1) {
                         return ['attribute.name.existsInDataModel']
                     }
                 }
                 if (obj.id != null && obj.isDirty('name') && obj.dirtyPropertyNames.size() > 0) {
                     return ['attribute.name.editable']
                 }

                 if (val == Constants.TEXT_SEARCH_FIELD) {
                     return ['attribute.name.notAllowed']
                 }
             }
        title blank: false, maxSize: 500
        description nullable: true
        helpText nullable: true
        order nullable: false
        type nullable: false, bindable: true
        metadata nullable: true
        manifest nullable: true
        dataModel nullable: true
    }

    static mapping = {
        dataModel lazy: false
    }

    def beforeInsert() {
        this.beforeUpdate()
    }

    def beforeUpdate() {
        if (!this.name) {
            this.name = this.title.encodeAsSlug()
        }
    }

    DataModel getDataModel() {
        if (!this.dataModel) {
            String query = "MATCH (n${Labels.getLabelsString(DataModel)})-[:ATTRIBUTES]->(m${Labels.getLabelsString(Attribute)}) " +
                           "WHERE m.`${Constants.ID_FIELD}` = {id} " +
                           "RETURN n"
            this.dataModel = DataModel.find(query, [id: this.id])
        }
        return this.dataModel
    }

    void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel
    }
}