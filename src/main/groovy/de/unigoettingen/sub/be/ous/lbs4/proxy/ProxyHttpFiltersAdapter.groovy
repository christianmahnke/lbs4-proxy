package de.unigoettingen.sub.be.ous.lbs4.proxy

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.Filter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.HttpResponseFilter

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.URLFilter

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import org.littleshoot.proxy.HttpFiltersAdapter

/**
 * Created by cmahnke on 17.02.15.
 */
@Log4j
@CompileStatic
class ProxyHttpFiltersAdapter extends HttpFiltersAdapter {

    //Configuration
    List<Filter> filters

    ProxyHttpFiltersAdapter(HttpRequest originalRequest, List<Filter> filters) {
        super(originalRequest)
        this.filters = filters
    }

    @Override
    public HttpResponse requestPre(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            String newUri = filter(httpObject)
            if (newUri != null) {
                httpObject.setUri(newUri)
                log.trace("URI rewritten to ${newUri}")
            }
        } else {
            throw new IllegalStateException('Unknown request type ' + httpObject.class)
        }
        return null
    }

    @Override
    public HttpResponse requestPost(HttpObject httpObject) {
        return null;
    }

    @Override
    public HttpObject responsePre(HttpObject httpObject) {
        if (httpObject instanceof FullHttpResponse) {
            log.info('[responsePre] Got full response')
            if (httpObject instanceof DefaultFullHttpResponse) {
                log.info(httpObject.toString())
            }
            return filter(originalRequest, httpObject)

        } else {
            throw new IllegalStateException('Unknown request type ' + httpObject.class)
        }
    }

    @Override
    public HttpObject responsePost(HttpObject httpObject) {
        return httpObject;
    }


    private String getContext() {
        return "${originalRequest.getMethod().name()} ${originalRequest.getUri()}"
        //ctx
    }

    protected String filter(HttpRequest req) {
        for (f in filters) {
            if (f instanceof URLFilter && f.matches(req)) {
                return ((URLFilter) f).filter(req)
            }
        }
    }

    protected FullHttpResponse filter(final HttpRequest req, final FullHttpResponse fhr) {
        FullHttpResponse resp = fhr
        for (f in filters) {
            if (f instanceof HttpResponseFilter && f.matches(req)) {
                log.trace("Appling filter ${f}")
                resp = ((HttpResponseFilter) f).filter(req, resp)
            }
        }
        return resp
    }

}
