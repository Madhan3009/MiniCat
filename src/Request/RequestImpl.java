package Request;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestImpl implements Request{
    private final Map<String,String[]> params = new HashMap<>();

    private InputStream inputStream =
    @Override
    public String getParameters() {

        return "";
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Map.of();
    }

    @Override
    public String getHeader(String name) {
        return "";
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public String getProtocol() {
        return "";
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }
}
