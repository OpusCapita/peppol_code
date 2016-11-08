yieldUnescaped '<!DOCTYPE html>'
html(class: 'no-js') {
    head() {
        meta(charset: 'utf-8')
        meta('http-equiv': 'X-UA-Compatible', 'content': 'IE=edge')
        meta(name: 'viewport', content: 'width=device-width, initial-scale=1')
        link(rel: 'icon', href: '/favicon.ico')
        title('Peppol Standalone Validator')
        link(rel:'stylesheet', href:'/css/bootstrap.min.css')
        link(rel:'stylesheet', href:'/css/oc-style.css')
        link(rel:'stylesheet', href:'/css/main.css')
    }
    body() {
        div(id: 'wrap') {
            div(id: 'header') {
                div(class: 'wrap') {
                    a(href: '/', id: 'logo') {
                        img(src: '/images/logo.png', '')
                    }
                }
                span(id: 'header_title', 'PEPPOL AP VALIDATION UI')
                div(id: 'header_menu_bar') {
                    span(class: 'glyphicon glyphicon-user','')
                    i('Welcome, user :)')
                    a(href: '#logout', 'Sign out(not working anyway)')
                }
            }
            div(id: 'main') {
                div(class: 'content') {
                    div() {
                        span('Validation status: '+status)
                        span() {
                            a(href: '/', class: 'btn-success', style: 'padding: 5px;margin-left: 15px;', 'New validation')
                        }
                    }
                    if(!status) {
                        div() {
                            span('Validation errors:'+errors.size())
                            for(Object error: errors) {
                                div(error.title)
                                div(error.details)
                                hr('')
                            }
                        }
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
}