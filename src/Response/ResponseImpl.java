package Response;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseImpl implements Response{

    @Override
    public void setStatus(int statusCode) {

    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public PrintWriter getWriter() {
        return null;
    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setContentLength(int length) {

    }

    @Override
    public void flushBuffer() {

    }
}
