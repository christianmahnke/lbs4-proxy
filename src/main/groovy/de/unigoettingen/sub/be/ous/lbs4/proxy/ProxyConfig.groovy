package de.unigoettingen.sub.be.ous.lbs4.proxy

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.CookieHttpResponseFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.CssFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.Filter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.FilterContainer
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.JavaScriptContentFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.RegExContentFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.RequestRewriteFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.AddEventListenerRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.WindowEventRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.WindowNavigateRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.lbs4.LanguageFilter

/**
 * Created by cmahnke on 23.04.15.
 */
class ProxyConfig {
    String scheme = 'http://'
    String host = 'xgoe.gbv.de'
    String port = '9090'
    String application_start = '/lbs4/'
    List<Filter> filters = new ArrayList<Filter>()
    String baseUrl

    ProxyConfig () {
        baseUrl = "${scheme}${host}:${port}${application_start}"
        init()
    }

    //TODO: Move this out off here -> Spring
    void init() {
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

        JavaScriptContentFilter jscf = new JavaScriptContentFilter('^.*\\.js$')
        jscf.addRewite(new WindowNavigateRewrite())
        jscf.addRewite(new AddEventListenerRewrite())
        jscf.addRewite(new WindowEventRewrite())
        ufc.addFilter(jscf)

        ufc.addFilter(new LanguageFilter('en', 'de'))


        filters.add(ufc)


    }
}
