package Request;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

public interface Request {
    public String getParameters();
    public Map<String,String[]> getParameterMap();
    public String getHeader(String name);
    public Enumeration<String> getHeaderNames();
    public InputStream getInputStream();
    public String getProtocol();
    public Object getAttribute(String name);
    public void setAttribute(String name, Object value);
}
