package org.abigballofmud.gmall.mock.exceptions;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * <p>
 * description
 * </p>
 *
 * @author abigballofmud 2019/11/25 10:11
 * @since 1.0
 */
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // ignore do nothing
    }
}
