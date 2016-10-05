package com.opuscapita.peppol

import geb.Browser
import geb.navigator.Navigator
import groovy.sql.Sql
import org.junit.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile

class SupportUIBrowserTest extends BaseTest {

    static WebDriver driver

    Browser browser
    Sql database

    @BeforeClass
    static void driverSetUp() {

        System.setProperty("webdriver.log.driver", "OFF")
        System.setProperty("webdriver.log.logfile", "javascript.log")
        System.setProperty("webdriver.firefox.logfile", "firefox.log")
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog")
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "OFF")
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "OFF")

        // driver = new ChromeDriver()
        // driver = new HtmlUnitDriver()

        FirefoxProfile firefoxProfile = new FirefoxProfile()
        firefoxProfile.setPreference("browser.startup.homepage_override.mston‌​e", "ignore")
        firefoxProfile.setPreference("browser.startup.homepage", "about:blank")
        firefoxProfile.setPreference("startup.homepage_welcome_url", "about:blank")
        firefoxProfile.setPreference("startup.homepage_welcome_url.additional", "about:blank")

        driver = new FirefoxDriver(firefoxProfile)

    }

    @AfterClass
    static void driverTearDown() {
        driver = null
    }

    @Before
    void createBrowser() {
        browser = new Browser()

    }

    @After
    void quitBrowser() {
        try {
            browser.quit()
            driver.close()
        } catch (Exception e) {
            println "Quit failed: ${e.message}"
        }
        browser = null
    }

    @Before
    void createDatabase() {
        database = Sql.newInstance(
                url: integrationProperties['remoteDbUrl'],
                user: integrationProperties['remoteDbUser'],
                password: integrationProperties['remoteDbPass'],
                driver: integrationProperties['remoteDbDriver']
        )
    }

    @After
    void disconnectDatabase() {
        database.close()
        database = null
    }

    @Test
    void login() {
        drive {
            go 'http://peppol.itella.net/login'
            assert $("h2").text() == "Please sign in"
            $("form", name: "log_in").with {
                username = integrationProperties['remoteUiUser']
                password = integrationProperties['remoteUiPass']
                $('button', type: 'submit').click()
            }
            waitFor {
                $("a", href: '/status').text() == "PEPPOL status"
            }
        }
    }


    @Test
    void data() {
        login()
        drive {
            waitFor {
                $('a', href: '/access_point').text() == 'Access Points'
            }
            $('a', href: '/access_point').click()
            waitFor(30) {
                !(dataTable.empty)
            }
            println "]]]]]]]" + dataTable.text()
            def firstRecord = database.firstRow('SELECT * FROM access_points AS a WHERE a.ap_name IS NOT NULL ORDER BY a.ap_name DESC')
            secondColumnFilter(dataTable).value(firstRecord.ap_name)
            waitFor(30) {
                firstRow(dataTable).children('td').first().next().children('span').text() == firstRecord.ap_name
            }
        }
    }

    void drive(@DelegatesTo(Browser) Closure cl) {
        browser.drive(cl)
    }

    static Navigator firstRow(Navigator dataTable) {
        dataTable.children('tbody').first().children('tr').first()
    }

    static Navigator secondColumnFilter(Navigator dataTable) {
        dataTable.children('thead').first().children('tr').first().next().children('th').first().next().$('input')
    }

    Navigator getDataTable() {
        browser.$('div', class: 'col-lg-10 ng-scope').children('div').first().next().children('table')
    }

}

