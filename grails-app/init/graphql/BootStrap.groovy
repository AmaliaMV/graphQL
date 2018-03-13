package graphql

import core.DataSet
import core.model.Attribute
import core.model.DataModel
import core.model.EmailAttributeType
import core.model.NumberAttributeType
import core.model.TextAttributeType
import core.model.URLAttributeType

class BootStrap {

    def init = { servletContext ->

        DataModel.withNewSession {
            DataModel.withNewTransaction {

                DataModel employee = new DataModel(name: 'employee', title: 'Employee')
                employee.save(failOnError: true)

                // add attributes
                Attribute firstName = new Attribute(name: 'firstName',
                                                    title: 'First Name',
                                                    type: new TextAttributeType(30)).save(failOnError: true)

                Attribute lastName = new Attribute(name: 'lastName',
                                                   title: 'Last Name',
                                                   type: new TextAttributeType(30)).save(failOnError: true)

                Attribute email = new Attribute(name: 'email',
                                                title: 'Email',
                                                type: new EmailAttributeType(40)).save(failOnError: true)

                Attribute age = new Attribute(name: 'age',
                                              title: 'Age',
                                              type: new NumberAttributeType(length: 3, decimalPlaces: 0)).save(failOnError: true)

                Attribute homePage = new Attribute(name: 'homePage',
                                                   title: 'Personal Profile',
                                                   type: new URLAttributeType(50, '"http://myprofile.com"')).save(failOnError: true)

                employee.addToAttributes(firstName)
                employee.addToAttributes(lastName)
                employee.addToAttributes(email)
                employee.addToAttributes(age)
                employee.addToAttributes(homePage)

                employee.save(failOnError: true)

                DataSet mvinas = new DataSet(employee)
                mvinas.firstName = "Mauro"
                mvinas.lastName = "Vinas"
                mvinas.email = "mauro@example.com"
                mvinas.age = 32
                mvinas.save(failOnError: true)

            }
        }
    }
    def destroy = {
    }
}
