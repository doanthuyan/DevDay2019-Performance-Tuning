package com.axonactive.devdayapp.test.cucumber;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: jacobs
 * Date: 6/11/15
 * Time: 11:03 AM
 */
public class ResponseResults
{
    private final ClientHttpResponse theResponse;
    private final String body;

    protected ResponseResults(final ClientHttpResponse response) throws IOException
    {
        this.theResponse = response;
        final InputStream bodyInputStream = response.getBody();
        if (null == bodyInputStream)
        {
            this.body = "{}";
        }
        else
        {
            //final StringWriter stringWriter = new StringWriter();
            this.body = StreamUtils.copyToString(bodyInputStream, Charset.forName("UTF-8"));
            //this.body = stringWriter.toString();
        }
    }

    public ClientHttpResponse getTheResponse()
    {
        return theResponse;
    }

    public String getBody()
    {
        return body;
    }
}
