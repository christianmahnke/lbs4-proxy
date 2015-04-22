package de.unigoettingen.sub.be.ous.lbs4.proxy

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.CookieHttpResponseFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.CssFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.Filter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.HttpResponseFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.JavaScriptContentFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.lbs4.LanguageFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.RegExContentFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.RequestRewriteFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.URLFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.FilterContainer
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.XFrameHttpResponseFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.AddEventListenerRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.WindowEventRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.WindowNavigateRewrite
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
class LBS4HttpFiltersAdapter extends HttpFiltersAdapter {

    //Configuration
    List<Filter> filters = []

    static String NO_SCRIPTOR_STRING = '?SCRIPT=0'
    static String SCRIPTOR_STRING = '?SCRIPT=1'
    static String scheme = 'http://'
    static String host = 'xgoe.gbv.de'
    static String port = '9090'
    static String application_start = '/lbs4/'
    String baseUrl = "${scheme}${host}:${port}${application_start}"

    //def filters = [{ if (((HttpRequest) it).getUri().matches("{host}:${port}${application_start}lbs4.html")) return true} : '']

    LBS4HttpFiltersAdapter(HttpRequest originalRequest) {
        super(originalRequest)
        init()
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

    //TODO: Move this out off here -> Spring
    public void init() {
        //Set up container
        FilterContainer ufc = new FilterContainer(baseUrl)
        //ScriptorFilter sf = new ScriptorFilter()

        //RedirectHttpResponseFilter rhrf = new RedirectHttpResponseFilter('^.*/lbs4/general/html/de/lbsMain_1024.htm?SCRIPT=1$', '/lbs4/general/html/de/lbsMain_1024.htm?SCRIPT=0')
        //ufc.addFilter(rhrf)

        RegExContentFilter gcf1 = new RegExContentFilter('^.*lbs4.html$', '^(.{340}).{240}(.*)$', '$1$2')
        RegExContentFilter gcf2 = new RegExContentFilter('^.*lbs4.html$', '^(.*)var lng = window.navigator.systemLanguage;(.*)$', '$1var lng = window.navigator.systemLanguage || window.navigator.language;$2')
        RegExContentFilter gcf3 = new RegExContentFilter('^.*login_.*$', '//xgoe\\.gbv\\.de:9600/servlet/lbsInfo', '//xgoe\\.gbv\\.de:9090/servlet/lbsInfo')

        ufc.addFilter(gcf1)
        ufc.addFilter(gcf2)
        ufc.addFilter(gcf3)

        RequestRewriteFilter rrf = new RequestRewriteFilter('^http://xgoe.gbv.de:9090/servlet.*$', '^http://xgoe.gbv.de:9090/servlet(.*)$', 'http://xgoe.gbv.de:9600/servlet$1')
        ufc.addFilter(rrf)

        CookieHttpResponseFilter chrf = new CookieHttpResponseFilter('^http://xgoe.gbv.de.*$', new Tuple2<String, String>('servletUrl=http%3A%2F%2Fxgoe.gbv.de%3A9600', 'servletUrl=http%3A%2F%2Fxgoe.gbv.de%3A9090'))
        ufc.addFilter(chrf)

        CssFilter cf = new CssFilter('\'^http://xgoe.gbv.de:9090.*\\.css$')
        ufc.addFilter(cf)

        /*
        DIV.loginBox
        ﻿top: 149px; left: 768px;
        RegExContentFilter gcf4 = new RegExContentFilter('^.*lbsData.*\\.css$', '//xgoe\\.gbv\\.de:9600/servlet/lbsInfo', '//xgoe\\.gbv\\.de:9090/servlet/lbsInfo')
        */

        //http://xgoe.gbv.de:9600
        XFrameHttpResponseFilter xftrf1 = new XFrameHttpResponseFilter('^http://xgoe.gbv.de:9090.*\\.htm\\??.*?$', 'http://xgoe.gbv.de:9600/')
        XFrameHttpResponseFilter xftrf2 = new XFrameHttpResponseFilter('^http://xgoe.gbv.de:9600/servlet.*$', 'http://xgoe.gbv.de:9090/')
        //ufc.addFilter(xftrf1)
        //ufc.addFilter(xftrf2)

        JavaScriptContentFilter jscf = new JavaScriptContentFilter('^.*\\.js$')
        jscf.addRewite(new WindowNavigateRewrite())
        jscf.addRewite(new AddEventListenerRewrite())
        jscf.addRewite(new WindowEventRewrite())
        ufc.addFilter(jscf)

        ufc.addFilter(new LanguageFilter('en', 'de'))
        this.filters.add(ufc)

    }


}
