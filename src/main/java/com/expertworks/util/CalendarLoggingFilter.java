package com.expertworks.util;
 
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
 
import javax.servlet.http.HttpServletRequest;
 
   
@Configuration
public class CalendarLoggingFilter extends CommonsRequestLoggingFilter {
 
   
    private static long beforeTime;
    private static long afterTime;
 
   
 
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        beforeTime = System.currentTimeMillis();
       // MemoryLogUtil.logUsed("beforeRequest");//only shows at this moment in time
        super.beforeRequest(request, message);
        System.out.println("Hello beforeRequestbeforeRequestbeforeRequestbeforeRequest");
    }
 
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        afterTime = System.currentTimeMillis();
      
        super.afterRequest(request, message);
    }
}