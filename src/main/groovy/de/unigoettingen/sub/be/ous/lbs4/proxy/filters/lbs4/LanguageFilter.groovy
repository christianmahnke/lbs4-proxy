package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.lbs4

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.AbstractFilter
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.URLFilter
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import io.netty.handler.codec.http.HttpRequest

import java.util.regex.Matcher

/**
 * Created by cmahnke on 24.02.15.
 */
@CompileStatic
@Log4j
class LanguageFilter extends AbstractFilter implements URLFilter {
    static String PREFIX = '^(.*/lbs4/general/html/)'
    static String SUFFIX = '(/.*)$'

    String from
    String to
    String replacement


    LanguageFilter(String from, String to) {
        super(PREFIX + from + SUFFIX)
        this.from = from
        this.to = to
        this.replacement = '$1' + to + '$2'
    }

    @Override
    String filter(HttpRequest request) {
        String reqUri = request.getUri()
        log.trace("Checking URI ${reqUri}")
        Matcher m = urlPattern.matcher(reqUri)
        String result =  m.replaceAll(replacement)
        log.trace("Result of replacement (${replacement}): ${result}")
        return result
    }
}
