yieldUnescaped '<!DOCTYPE html>'
html(class: 'no-js') {
    head() {
        meta(charset: 'utf-8')
        meta('http-equiv': 'X-UA-Compatible', 'content': 'IE=edge')
        meta(name: 'viewport', content: 'width=device-width, initial-scale=1')
        link(rel: 'icon', href: "$root/favicon.ico")
        title('Peppol Standalone Validator')
        link(rel:'stylesheet', href:"$root/css/bootstrap.min.css")
        link(rel:'stylesheet', href:"$root/css/oc-style.css")
        link(rel:'stylesheet', href:"$root/css/main.css")
    }
    body() {
        div(id: 'wrap', style: 'height: 100vh') {
            div(id: 'header') {
                div(class: 'wrap') {
                    a(href: '$root/', id: 'logo') {
                        img(src: "$root/images/logo.png", '')
                    }
                }
                span(id: 'header_title', 'PEPPOL AP VALIDATOR')
                div(id: 'header_menu_bar') {
                    span(class: 'glyphicon glyphicon-user','')
                    i('Welcome, user :)')
                    a(href: "$root#logout", 'Sign out(not working anyway)')
                }
            }
            div(id: 'main') {
                div(class: 'content') {
                    form(method: 'post', enctype: 'multipart/form-data') {
                        input(type: 'file', name: 'datafile', id: 'datafile')
                        input(type: 'submit', value: 'Go', id: 'submit')
                    }
                    div(id: 'result')
                }
            }
        }
        footer(id: 'footer') {
            div(class: 'wrap') {
                span(class: 'right') {
                    a(href: 'http://www.itella.com', target: '_blank', 'Part of Itella Group')
                }
                section(class: 'copy') {
                    span('© OpusCapita 2016')
                }
            }
        }

    }
}

