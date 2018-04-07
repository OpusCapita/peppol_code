#file-prep
file-prep-l:
	ansible-playbook -i local.hosts -K file-prep.yml
#------------------------------------------------------------
#cleaner
cleaner-l:
	ansible-playbook -i local.hosts -K cleaner.yml
cleaner-s:
	ansible-playbook -i stage.hosts -K cleaner.yml
#------------------------------------------------------------
#Load tests
small-load-tests-l:
	ansible-playbook -i local.hosts -K small-load-tests.yml
small-load-tests-s:
	ansible-playbook -i stage.hosts -K small-load-tests.yml
average-load-tests-l:
	ansible-playbook -i local.hosts -K average-load-tests.yml
average-load-tests-s:
	ansible-playbook -i stage.hosts -K average-load-tests.yml
large-load-tests-l:
	ansible-playbook -i local.hosts -K large-load-tests.yml
large-load-tests-s:
	ansible-playbook -i stage.hosts -K large-load-tests.yml

#------------------------------------------------------------
#Spike tests
spike-test-outbound-s:
	ansible-playbook -i stage.hosts -K spike-test-outbound.yml
spike-test-inbound-s:
	ansible-playbook -i stage.hosts -K spike-test-inbound.yml

spike-test-outbound-l:
	ansible-playbook -i local.hosts -K spike-test-outbound.yml
spike-test-inbound-l:
	ansible-playbook -i local.hosts -K spike-test-inbound.yml

spike-test-outbound-t:
	ansible-playbook -i test.hosts -K spike-test-outbound.yml
spike-test-inbound-t:
	ansible-playbook -i test.hosts -K spike-test-inbound.yml

#------------------------------------------------------------
#System tests
inbound-tests-s:
	ansible-playbook -i stage.hosts inbound-tests.yml -K
inbound-tests-l:
	ansible-playbook -i local.hosts inbound-tests.yml -K
outbound-tests-s:
	ansible-playbook -i stage.hosts -K outbound-tests.yml
outbound-tests-l:
	ansible-playbook -i local.hosts -K outbound-tests.yml
inbound-invalid-tests-s:
	ansible-playbook -i stage.hosts inbound-invalid-tests.yml
inbound-invalid-tests-l:
	ansible-playbook -i local.hosts inbound-invalid-tests.yml -K
outbound-invalid-tests-s:
	ansible-playbook -i stage.hosts -K outbound-invalid-tests.yml
outbound-invalid-tests-l:
	ansible-playbook -i local.hosts -K outbound-invalid-tests.yml
mlr-tests-s:
	ansible-playbook -i stage.hosts -K mlr-tests.yml
mlr-tests-l:
	ansible-playbook -i local.hosts -K mlr-tests.yml


inbound-tests-t:
	ansible-playbook -i test.hosts inbound-tests.yml
outbound-tests-t:
	ansible-playbook -i test.hosts -K outbound-tests.yml
inbound-invalid-tests-t:
	ansible-playbook -i test.hosts inbound-invalid-tests.yml
outbound-invalid-tests-t:
	ansible-playbook -i test.hosts -K outbound-invalid-tests.yml
send-to-outbound-stage-prod-l:
	ansible-playbook -i local.hosts -K send-to-outbound-stage-prod.yml
send-to-outbound-stage-prod-s:
	ansible-playbook -i stage.hosts -K send-to-outbound-stage-prod.yml

#------------------------------------------------------------
#run system tests: inbound-tests on just container that simulates real conditions as running this on d-l-tools
inbound-tests-c:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.3-peppol ansible-playbook -i stage.hosts --user $$USER inbound-tests.yml -e 'report_path=$$PWD/reports' -K
outbound-tests-c:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.3-peppol ansible-playbook -i stage.hosts --user $$USER outbound-tests.yml -e 'report_path=$$PWD/reports' -K
inbound-invalid-tests-c:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.3-peppol ansible-playbook -i stage.hosts --user $$USER inbound-invalid-tests.yml -e 'report_path=$$PWD/reports' -K
outbound-invalid-tests-c:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.3-peppol ansible-playbook -i stage.hosts --user $$USER outbound-invalid-tests.yml -e 'report_path=$$PWD/reports' -K

inbound-tests-ct:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.3-peppol ansible-playbook -i test.hosts --user $$USER inbound-tests.yml -e 'report_path=$$PWD/reports'
outbound-tests-ct:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.2-peppol ansible-playbook -i test.hosts --user $$USER outbound-tests.yml -e 'report_path=$$PWD/reports'
inbound-invalid-tests-ct:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.2-peppol ansible-playbook -i test.hosts --user $$USER inbound-invalid-tests.yml -e 'report_path=$$PWD/reports'
outbound-invalid-tests-ct:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.2-peppol ansible-playbook -i test.hosts --user $$USER outbound-invalid-tests.yml -e 'report_path=$$PWD/reports'

#------------------------------------------------------------
#run validator-tests
validator-tests-l:
	ansible-playbook -i local.hosts validator-tests.yml
validator-tests-s:
	ansible-playbook -i stage.hosts validator-tests.yml
validator-tests-c:
	docker run -it -v $$PWD:/system-tests -v ~/.ssh:/var/jenkins_home/.ssh -w /system-tests -e ANSIBLE_HOST_KEY_CHECKING=False d-l-tools.ocnet.local:443/jenkins:2.46.3-peppol ansible-playbook -i stage.hosts --user $$USER validator-tests.yml -e 'report_path=$$PWD/reports' -K
