yieldUnescaped '<!DOCTYPE html>'
html() {
    head() {
        title('Peppol Standalone Validator')
    }
    body() {
        form() {
            input(type: 'file', name: 'datafile', id: 'datafile')
            input(type: 'submit', value: 'Go')
        }
        div(id: 'result')
    }
}