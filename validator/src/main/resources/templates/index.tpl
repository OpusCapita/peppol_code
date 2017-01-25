layout 'layouts/main.tpl',
    pageTitle: 'Peppol Standalone Validator',
    root: root,
    mainBody: contents {
      form(method: 'post', enctype: 'multipart/form-data') {
                        input(type: 'file', name: 'datafile', id: 'datafile')
                        input(type: 'submit', value: 'Go', id: 'submit')
                    }
                    div(id: 'result')
    }