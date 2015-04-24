package de.unigoettingen.sub.be.ous.lbs4.proxy

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.CssFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.Filter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.FilterContainer

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

    ProxyConfig() {

    }

    public void init() {
        //baseUrl = "${scheme}${host}:${port}${application_start}"
        addFilters()
    }

    void addFilters() {
        if (filters.get(0) instanceof FilterContainer) {
            FilterContainer fc = filters.get(0)
            //Hook up additional filters fpr debugging here
            /* Example:
            CssFilter cf = new CssFilter('^http://xgoe.gbv.de:9090.*\\.css$')
            fc.addFilter(cf)
            */
        } else {
            throw new IllegalStateException('FilterContainer not configured')
        }
    }
}
