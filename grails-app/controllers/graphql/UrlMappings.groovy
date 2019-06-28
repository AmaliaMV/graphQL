package graphql

class UrlMappings {

    static mappings = {
        "/graphql/$action?"(controller: 'graphql')


        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
