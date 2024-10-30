package a.b.c;

import javax.servlet.http.*;

public class BasicTest extends HttpServlet {
  
  @Override
  public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    final var param = req.getParameter("param");
    // This makes the output unsafe.
    resp.setHeader("content-type", "text/html");
    var param1 = cleanParam(param);
    int x = 2*2;
    resp.getWriter().println(param.repeat(x));    
    resp.getWriter().println(param1.repeat(x));
  }


  public String cleanParam(String x){ return "";}

}
