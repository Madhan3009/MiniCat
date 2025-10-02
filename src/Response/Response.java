package Response;

import java.io.OutputStream;
import java.io.PrintWriter;

public interface Response {
    void setStatus(int statusCode);
    void setHeader(String name, String value);
    void addHeader(String name, String value);
    OutputStream getOutputStream();
    PrintWriter getWriter();
    void setContentType(String type);
    void setContentLength(int length);
    void flushBuffer();
}
