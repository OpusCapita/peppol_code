package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.validator.validations.common.TestCommon;

/**
 * Created by bambr on 16.7.10.
 */
//@SuppressWarnings("SpringJavaAutowiredMembersInspection")
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = DifiTestConfig.class)
//@EnableConfigurationProperties
public class DifiValidationControllerTest extends TestCommon {
//    private String[] documentProfilesToBeTested = { "svefaktura1", "difi", "simpler_invoicing" };
//
//    @Autowired
//    private DifiValidationController validationController;
//
//    @Autowired
//    private DocumentLoader documentLoader;
//
//    @Test
//    public void validateSvefaktura1Files() {
//        Arrays.stream(documentProfilesToBeTested).forEach(this::testDocumentProfileValidation);
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    private void testDocumentProfileValidation(final String documentProfile) {
//        Consumer<? super File> consumer = (File file) -> {
//            if (!file.getAbsolutePath().contains("Valids-D.56980-BEL2449A5F29E6311E7A4D3371AB1B8DE82.xml")) {
//                //return;
//            }
//            try {
//                ContainerMessage containerMessage = ContainerMessageTestLoader.createContainerMessageFromFile(documentLoader, file);
//                if (containerMessage == null) {
//                    System.out.println("Failed to create ContainerMessage for file: " + file.getAbsolutePath());
//                    return;
//                } else {
//                    System.out.println("Successfully created ContainerMessage from file: " + file.getAbsolutePath());
//                }
//
//                ValidationResult result = ValidationResult.fromContainerMessage(validationController.validate(containerMessage));
//                System.out.println("result: " + result.isPassed() + " on " + file.getName());
//                result.getErrors().forEach(System.out::println);
//                if ((result.isPassed() && file.getName().contains("invalid"))
//                        || (!result.isPassed() && file.getName().contains("Valid")
//                        && !file.getName().contains("invalid"))) {
//                    fail("Failed on expected validation result: " + result.isPassed() + " on " + file.getName() + " [" + documentProfile + "]");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                fail("Failed with exception: " + e.getMessage());
//            }
//        };
//        try {
//            runTestsOnDocumentProfile(documentProfile, consumer);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            fail(documentProfile + " -> " + e.getMessage());
//        }
//    }

}
