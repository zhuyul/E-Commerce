package cs137;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;


public class DestroyListener implements HttpSessionListener {
    String mutex="";
    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        
        if (se.getSession().getAttribute("lastVisitedProductId") != null){
            if (((HashMap<String, Integer>) se.getSession().getServletContext().getAttribute("viewcounter")).containsKey((String)se.getSession().getAttribute("lastVisitedProductId")))
            {
                synchronized(mutex){
                    Integer count1 = ((HashMap<String, Integer>) se.getSession().getServletContext().getAttribute("viewcounter")).get((String)se.getSession().getAttribute("lastVisitedProductId"));
                    ((HashMap<String, Integer>) se.getSession().getServletContext().getAttribute("viewcounter")).put((String)se.getSession().getAttribute("lastVisitedProductId"),count1-1);
                }
            }
            se.getSession().setAttribute("lastVisitedProductId",null);
        }
    }
}
