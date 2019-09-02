package com.zdw.web.controller.exception;

//import org.apache.catalina.servlet4preview.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

//    Logger logger = LoggerFactory.getLogger(DataPackaging.class);
//    @ExceptionHandler(value = Exception.class)
//    public String defaultErrorHandler(HttpServletRequest req, Exception e, Model model) throws Exception {
//
//        logger.error("异常：",e);
//
//        model.addAttribute("url",req.getRequestURI());
//        model.addAttribute("exception",e);
//
//        if(e instanceof InvalidLogonStatusException){
//            model.addAttribute("targetUrl","/login");
//        }
//        return "error";
//    }
}