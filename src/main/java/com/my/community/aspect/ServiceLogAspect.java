package com.my.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.my.community.service.*.*(..))")
    public void pointCut() {

    }

    /**
     * 在切点 pointCut 执行逻辑(记录日志)
     */
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequest.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获取 切入点的类名、方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + joinPoint.getSignature().getName();
        log.info(String.format("用户[%s],在[%s],访问了[%s]", ip, now, target));
    }

}
