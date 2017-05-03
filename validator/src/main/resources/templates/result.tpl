layout 'layouts/main.tpl',
    pageTitle: 'Peppol Standalone Validator',
    root: root,
    mainBody: contents {
      div() {
                        span('Validation status: '+status, id: 'validationStatus')
                        span() {
                            a(href: "$root", class: 'btn-success', style: 'padding: 5px;margin-left: 15px;', 'New validation')
                        }
                    }
                    if(!status) {
                        div() {
                            span('Validation errors:'+errors.size())
                            for(Object error: errors) {
                                div(style: 'font-color: red;', error.source)
                                div(error.message)
                                hr('')
                            }
                        }
                    }
                    div() {
                        span('Validation warnings:'+warnings.size())
                        for(Object warning: warnings) {
                            div(style: 'font-color: brown;', warning.source)
                            div(warning.message)
                            hr('')
                        }
                    }
    }