package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.handler.codec.http.DefaultHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpVersion

import java.nio.charset.Charset

/**
 * Created by cmahnke on 23.02.15.
 */
@Log4j
@CompileStatic
class FilterContainer extends AbstractHttpResponseFilter implements URLFilter, HttpResponseFilter {
    String scheme
    String host

    List<Filter> filters = []
    //this might be need if we have multiple filters for the same pattern
    //Map<String, List<Filter>> filterIdentifier = [:]

    FilterContainer (String url) {
        super(url)
        URI u = new URL(url).toURI()
        this.scheme = u.getScheme()
        this.host = u.getHost()
    }

    /*
    FilterContainer(String scheme, String host, String port, String path) {
        this.scheme = scheme
        this.host = host
        this.port = port
        this.path = path
        log.trace("Set up filtering container for ${scheme}${host}:${port}${path}")
    }

    FilterContainer(String scheme, String host) {
        this.scheme = scheme
        this.host = host
        log.trace("Set up filtering container for ${scheme}${host}")
    }
    */


    public void addFilter(Filter f) {
        this.filters.add(f)
    }

    public List<Filter> getFilters() {
        return filters
    }

    @Override
    Boolean matches(final HttpRequest request) {
        String requestUri = request.getUri()
        log.trace("Checking request for ${requestUri}")
        if (!requestUri.startsWith("${scheme}://${host}")) {
            return false
        }
        /*
        else {
            //Check for optional port and path settings
            if (port != null && path != null && !request.getUri().startsWith("${scheme}${host}:${port}${path}")) {
                return false
            } else if (port == null && path != null && !request.getUri().startsWith("${scheme}${host}:${path}")) {
                return false
            } else if (port != null && path == null && !request.getUri().startsWith("${scheme}${host}:${port}")) {
                return false
            }
        }
        */
        log.trace('Match found, trying other filters')
        //Try the filters from the internal list
        for (f in filters) {
            if (f.matches(request)) {
                return true
            }
        }
    }

    @Override
    String getMatchIdentifier() {
        return '*'
    }

    @Override
    String filter(final HttpRequest request) {
        log.trace('FilterContainer looping over URLFilters')
        HttpRequest hr = request
        String result
        for (f in filters) {
            if (f instanceof URLFilter && f.matches(request)) {
                log.trace("Got match for ${f.getMatchIdentifier()}, returning")
                result = ((URLFilter) f).filter(hr)
                hr = updateHttpRequest(hr, result)
            }
        }
        return result
    }

    @Override
    FullHttpResponse filter(final HttpRequest request, final FullHttpResponse fhr) {
        log.trace("FilterContainer looping over HttpResponseFilters for ${request.getUri()}")
        FullHttpResponse response = fhr
        for (f in filters) {
            if (f instanceof HttpResponseFilter && f.matches(request)) {
                log.trace("Got match for ${f.getMatchIdentifier()} - Filter ${f.getClass().getName()}")
                response = ((HttpResponseFilter) f).filter(request, response)
                //log.trace("New Content: ${getContent(response)}")
            }
        }
        return response
    }

    @Override
    String filter (String content) {
        throw new IllegalStateException('Not implemented')
    }


}