//package com.evo.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.util.StringUtils;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@Slf4j
//public class FeignClientInterceptor implements RequestInterceptor {
//
//    private static final Logger logger = LoggerFactory.getLogger(FeignClientConfiguration.class);
//
//    @Override
//    public void apply(RequestTemplate requestTemplate) {
//        ServletRequestAttributes servletRequestAttributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        var authHeader = servletRequestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
//        logger.info("Token: {}", authHeader);
//
//        if (StringUtils.hasText(authHeader)) {
//            requestTemplate.header("Authorization", authHeader);
//        }
//    }
//}
