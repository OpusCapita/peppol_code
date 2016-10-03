package com.opuscapita.peppol

import geb.Browser
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.hidetake.groovy.ssh.Ssh

public class DirectoryTest {
    @Test
    void DirectoryTest() {
        class App {
            static void main(String[] args) {
                def ssh = Ssh.newService()
                ssh.remotes { ... }
                ssh.run { ... }
            }
        }
    }
}


