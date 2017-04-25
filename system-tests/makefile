#Spike tests
spike-test-outbound-s:
	ansible-playbook -i stage.hosts -K spike-test-outbound.yml
spike-test-inbound-s:
	ansible-playbook -i stage.hosts -K spike-test-inbound.yml
spike-test-outbound-l:
	ansible-playbook -i local.hosts -K spike-test-outbound.yml
spike-test-inbound-l:
	ansible-playbook -i local.hosts -K spike-test-inbound.yml
#------------------------------------------------------------
#System tests
inbound-tests-s:
	ansible-playbook -i stage.hosts -K inbound-tests.yml
inbound-tests-l:
	ansible-playbook -i local.hosts -K inbound-tests.yml
outbound-tests-s:
	ansible-playbook -i stage.hosts -K outbound-tests.yml
outbound-tests-l:
	ansible-playbook -i local.hosts -K outbound-tests.yml
#------------------------------------------------------------

