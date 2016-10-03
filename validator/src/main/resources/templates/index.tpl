yieldUnescaped '<!DOCTYPE html>'
html() {
    head() {
        title('Peppol Standalone Validator')
    }
    body() {
        form(method: 'post', enctype: 'multipart/form-data') {
            input(type: 'file', name: 'datafile', id: 'datafile')
            input(type: 'submit', value: 'Go')
        }
        div(id: 'result')
    }
}