layout 'layouts/main.tpl',
    pageTitle: 'Peppol Standalone Validator',
    root: root,
    mainBody: contents {
      form(method: 'post', enctype: 'multipart/form-data') {
                        span() {
                            input(type: 'file', name: 'datafile', id: 'datafile')
                            span(class: 'label label-warning', 'Provide only XML files with "xml" extension and having document wrapped into SBDH')
                        }
                        p('')
                        div() {
                            input(type: 'submit', value: 'Go', id: 'submit')
                        }
                    }
                    div(id: 'result')
    }