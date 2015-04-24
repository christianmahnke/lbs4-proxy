package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest


/**
 * Created by cmahnke on 19.04.15.
 */
@CompileStatic
@Log4j
//TODO: Replace this with Header
@Deprecated
class XFrameHttpResponseFilter extends AbstractFilter implements HttpResponseFilter  {
    String allow

    XFrameHttpResponseFilter (String u, String allow) {
        super(u)
        this.allow = allow
    }

    @Override
    FullHttpResponse filter(HttpRequest req, FullHttpResponse resp) {
        log.info("Adding X-Frame-Options for ${allow}")
        resp.headers().set('X-Frame-Options', 'ALLOW-FROM ' + allow)
        return resp
    }


}
