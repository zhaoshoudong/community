package com.my.community.controller.advice;

import com.my.community.util.CommunityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//声明通知类型为controller,可以传一个controller也可以传多个
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器异常!" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }
        String header = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(header)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtils.getJSONString(1,"服务器异常,请联系管理员!"));
        } else {
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
