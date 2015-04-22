package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic

import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest

/**
 * Created by cmahnke on 24.02.15.
 */
@CompileStatic
interface HttpResponseFilter extends Filter {
    public FullHttpResponse filter(final HttpRequest req, final FullHttpResponse resp)

}