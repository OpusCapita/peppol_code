*** Settings ***
Documentation   Tests if Inbound module can receive and process EHF_profile-bii05_invoice
...             \n  -   Given Inbound flow runs
...             \n  -   When Send EHF_profile-bii05_invoice to inbound
...             \n  -   Then Inbound tansmissionId should be found

Library   OperatingSystem
Library   Process
Library   String

Suite Setup   Prepare files

*** Variables ***
${source_file}=   resources/EHF_profile-bii05_invoice_OC.xml
*** Test Cases ***
Inbound positive test EHF_profile-bii05_invoice
    Given Inbound flow runs
    When Send EHF_profile-bii05_invoice to inbound
    Then Inbound tansmissionId should be found

*** Keywords ***
Prepare files
#    Copy File   ${source_file}    ${work_file}
    ${document_id} =  Generate Random String  20  [NUMBERS]
    Log     document_id: ${document_id}     console=true
    ${work_file}=     Get File   ${source_file}
    Log   work file: ${work_file}
    ${work_file}=     Replace String    ${work_file}    {{ document_id }}     ${document_id}
    Log   work file: ${work_file}
#-------------------------------------------------------------
Inbound flow runs
    ${inbound_status}=     Run Process   docker service ps peppol_inbound \--filter desired-state\=Running   shell=true    stdout=inbound_status
    Should Contain    ${inbound_status.stdout}    peppol_inbound    msg=Inbound instance is not running

Send EHF_profile-bii05_invoice to inbound
    ${inbound_result}=     Run Process    docker run -i --network peppol_back -v /tmp:/tmp -v /peppol:/peppol d-l-tools.ocnet.local:443/peppol2.0/inbound:latest ./send-to-peppol.sh -f /tmp/EHF_profile-bii05_invoice_OC.xml -u http://inbound:8080/peppol-ap-inbound/as2    shell=true    stdout=inbound_log
    Log   Inbound output: ${inbound_result.stdout}
    Set Global Variable   ${inbound_log}    ${inbound_result.stdout}
    Log   Inbound log variable: ${inbound_log}

Inbound tansmissionId should be found
#    Set Global Variable   ${inbound_log}    ${EMPTY}
    Log   Inbound output: ${inbound_log}
    ${search_result}=   Get Lines Containing String   ${inbound_log}    transmissionId
    Log   transmissionId line: ${search_result}   console=false
    ${transmissionId}=    Get Substring   ${search_result}    -36
    Should Not Be Empty   ${transmissionId}   msg=transmissionId was not found
    Log   transmissionId: ${transmissionId}   console=true
#-------------------------------------------------------------
