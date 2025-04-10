//package com.evo.client;
//
//import com.evo.UserAuthority;
//import com.evo.enums.ServiceUnavailableError;
//import com.evo.exception.ForwardInnerAlertException;
//import com.evo.exception.ResponseException;
//import com.evo.response.Response;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.openfeign.FallbackFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class IamClientFallback implements FallbackFactory<IamClient> {
//    @Override
//    public IamClient create(Throwable cause) {
//        return new FallbackWithFactory(cause);
//    }
//
//    @Slf4j
//    static class FallbackWithFactory implements IamClient {
//        private final Throwable cause;
//
//        FallbackWithFactory(Throwable cause) {
//            this.cause = cause;
//        }
//
//        @Override
//        public Response<UserAuthority> getUserAuthority(UUID userId) {
//            if (cause instanceof ForwardInnerAlertException) {
//                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, (RuntimeException) cause);
//            }
//            return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR,
//                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
//        }
//
//        @Override
//        public Response<UserAuthority> getUserAuthority(String username) {
//            if (cause instanceof ForwardInnerAlertException) {
//                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, (RuntimeException) cause);
//            }
//            return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR,
//                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
//        }
//    }
//}
