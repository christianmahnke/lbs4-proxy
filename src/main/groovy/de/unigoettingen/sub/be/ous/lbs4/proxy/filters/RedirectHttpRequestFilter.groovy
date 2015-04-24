package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion

/**
 * Created by cmahnke on 16.04.15.
 */
@Log4j
@CompileStatic
class RedirectHttpRequestFilter extends AbstractHttpResponseFilter implements HttpResponseFilter {

    String to

    RedirectHttpRequestFilter(String url) {
        super(url)
    }

    RedirectHttpRequestFilter(String url, String to) {
        super(url)
        this.to = to
    }

    @Override
    FullHttpResponse filter(HttpRequest req, FullHttpResponse resp) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, to);
        log.info("Redirecting to ${to}")
        return response
    }

    @Override
    String filter (String content) {
        throw new IllegalStateException('Not implemented')
    }

}
