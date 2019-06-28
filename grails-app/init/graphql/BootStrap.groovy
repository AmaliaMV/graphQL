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

                if (DataModel.findByName('employee') == null) {
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

//                    Attribute age = new Attribute(name: 'age',
//                                                  title: 'Age',
//                                                  type: new NumberAttributeType(length: 3, decimalPlaces: 0)).save(failOnError: true)

                    Attribute homePage = new Attribute(name: 'homePage',
                                                       title: 'Personal Profile',
                                                       type: new URLAttributeType(50, '"http://myprofile.com"')).save(failOnError: true)

                    employee.addToAttributes(firstName)
                    employee.addToAttributes(lastName)
                    employee.addToAttributes(email)
//                    employee.addToAttributes(age)
                    employee.addToAttributes(homePage)

                    employee.save(failOnError: true)

                    DataSet empleado1 = new DataSet(employee)
                    empleado1.firstName = "Empleado 0"
                    empleado1.lastName = "Apellido 0"
                    empleado1.email = "mauro@example.com"
//                    mvinas.age = 32
                    empleado1.save(failOnError: true)

                    empleado1 = new DataSet(employee)
                    empleado1.firstName = "Empleado 1"
                    empleado1.lastName = "Apellido 1"
                    empleado1.email = "mauro@example.com"
//                    mvinas.age = 32
                    empleado1.save(failOnError: true)

                    empleado1 = new DataSet(employee)
                    empleado1.firstName = "Empleado 2"
                    empleado1.lastName = "Apellido 2"
                    empleado1.email = "mauro@example.com"
//                    mvinas.age = 32
                    empleado1.save(failOnError: true)

                    empleado1 = new DataSet(employee)
                    empleado1.firstName = "Empleado 3"
                    empleado1.lastName = "Apellido 3"
                    empleado1.email = "mauro@example.com"
//                    mvinas.age = 32
                    empleado1.save(failOnError: true)
                }

                if (DataModel.findByName('host') == null) {
                    DataModel host = new DataModel(name: 'host', title: 'Host')
                    host.save(failOnError: true)

                    // add attributes
                    Attribute name = new Attribute(name: 'name',
                                                        title: 'Name',
                                                        type: new TextAttributeType(30)).save(failOnError: true)

                    Attribute ip = new Attribute(name: 'ip',
                                                       title: 'IP',
                                                       type: new TextAttributeType(30)).save(failOnError: true)

                    host.addToAttributes(name)
                    host.addToAttributes(ip)

                    host.save(failOnError: true)

                    DataSet hostData = new DataSet(host)
                    hostData.name = "Host-0"
                    hostData.ip = "192.168.252.13"
                    hostData.save(failOnError: true)

                    hostData = new DataSet(host)
                    hostData.name = "Host-1"
                    hostData.ip = "192.168.253.13"
                    hostData.save(failOnError: true)

                    hostData = new DataSet(host)
                    hostData.name = "Host-2"
                    hostData.ip = "192.168.252.16"
                    hostData.save(failOnError: true)
                }

            }
        }
    }
    def destroy = {
    }
}
