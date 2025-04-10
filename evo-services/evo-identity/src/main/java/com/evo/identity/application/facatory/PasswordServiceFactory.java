//package com.evo.identity.application.facatory;
//
//import com.evo.iam.application.service.PasswordCmdService;
//import com.evo.iam.application.service.impl.command.KeycloakPasswordCmdServiceImpl;
//import com.evo.iam.application.service.impl.command.SystemPasswordCmdServiceImpl;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PasswordServiceFactory {
//    @Value("${keycloak.enabled}")
//    private boolean isKeycloakEnabled;
//
//    private final SystemPasswordCmdServiceImpl changeSystemPasswordService;
//    private final KeycloakPasswordCmdServiceImpl changeKeycloakPasswordService;
//
//    public PasswordServiceFactory(SystemPasswordCmdServiceImpl changeSystemPasswordService,
//                                  KeycloakPasswordCmdServiceImpl changeKeycloakPasswordService) {
//        this.changeSystemPasswordService = changeSystemPasswordService;
//        this.changeKeycloakPasswordService = changeKeycloakPasswordService;
//    }
//
//    public PasswordCmdService getChangePasswordService() {
//        if (isKeycloakEnabled) {
//            return changeKeycloakPasswordService;
//        } else {
//            return changeSystemPasswordService;
//        }
//    }
//}
