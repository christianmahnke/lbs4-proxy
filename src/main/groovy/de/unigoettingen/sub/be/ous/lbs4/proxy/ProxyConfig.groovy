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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable

/**
 * Created by cmahnke on 23.04.15.
 */
@Configurable
class ProxyConfig {
    @Autowired
    List<Filter> filters = new ArrayList<Filter>()
    @Autowired
    String baseUrl

    ProxyConfig () {

    }

    public void init () {
        //baseUrl = "${scheme}${host}:${port}${application_start}"
        addFilters()
    }

    void addFilters() {
        if (filters.get(0) instanceof FilterContainer) {
            FilterContainer fc = filters.get(0)
            //Hook up additional filters fpr debugging here

        } else {
            throw new IllegalStateException('FilterContainer not configured')
        }


        /*
        CssFilter cf = new CssFilter('\'^http://xgoe.gbv.de:9090.*\\.css$')
        ufc.addFilter(cf)

        DIV.loginBox
        ﻿top: 149px; left: 768px;
        RegExContentFilter gcf4 = new RegExContentFilter('^.*lbsData.*\\.css$', '//xgoe\\.gbv\\.de:9600/servlet/lbsInfo', '//xgoe\\.gbv\\.de:9090/servlet/lbsInfo')
        */

    }
}
