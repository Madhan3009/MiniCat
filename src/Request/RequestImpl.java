package Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
//
public class RequestImpl implements Request{
    private Map<String,String> params = new HashMap<>();
    private Map<String,String> headers = new HashMap<>();
    private ServerSocket socket = new ServerSocket(5000);
    private BufferedReader bufferedReader;
    private Map<String,Object> attribute = new HashMap<>();
    private String method;
    private String path;
    private String version;
    private String pathParams;
    private String queryString;


    public RequestImpl(BufferedReader bufferedReader1) throws IOException {
        this.bufferedReader = bufferedReader1;
        String requestLine = bufferedReader.readLine();
        if(requestLine == null || requestLine.isEmpty()) {
            System.err.println("The Request is incomplete");
            return;
        }
        String[] parts = requestLine.split(" ");
        method = parts[0];
        path = parts[1];
        version = parts[2];
        if(path.contains("?")){
            String[] split = path.split("\\?",2);
            path = split[0];
            queryString = split[1];
            for(String pair: queryString.split("&")){
                String[] res = pair.split("=",2);
                params.put(res[0],res.length>1?res[1]:"");
            }
        }
        String line;
        while((line = bufferedReader.readLine())!=null){
            if(line.isEmpty())break;
            String head[] = line.split(":");
            headers.put(head[0],head[1]);
        }
        int length = Integer.parseInt(headers.get("Content-Length"));

    }

    @Override
    public String getParameters(String name) {
        if(params.containsKey(name))return params.get(name);
        return null;
    }

    @Override
    public Map<String, String> getParameterMap() {
        Map<String,String> res = new HashMap<>(params);
        return res;
    }

    @Override
    public String getHeader(String name) {
        if(headers.containsKey(name))return headers.get(name);
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Enumeration<String> names = Collections.enumeration(headers.keySet());
        if(names.hasMoreElements())return names;
        return null;
    }

    @Override
    public InputStream getInputStream() {
        //This would be filled with inputStream object
        //got from ServerSocket for working with request body
        return null;
    }

    @Override
    public String getProtocol() {
        if(version.isEmpty())return null;
        return version;
    }

    @Override
    public Object getAttribute(String name) {
        if(attribute.containsKey(name))return attribute.get(name);
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {
        attribute.put(name,value);
    }
}
