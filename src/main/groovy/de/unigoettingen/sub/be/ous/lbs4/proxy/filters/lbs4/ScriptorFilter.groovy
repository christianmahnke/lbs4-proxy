package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.lbs4

import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.URLFilter
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.handler.codec.http.HttpRequest

/**
 * Created by cmahnke on 23.02.15.
 */
@Log4j
@CompileStatic
@Deprecated
class ScriptorFilter implements URLFilter {
    static String SCRIPTOR_PREFIX = '?SCRIPT='
    static String NO_SCRIPTOR_STRING = SCRIPTOR_PREFIX + '0'
    static String SCRIPTOR_STRING = SCRIPTOR_PREFIX + '1'

    @Override
    Boolean matches(HttpRequest request) {
        String reqUri = request.getUri()
        log.trace("Checking if URL ${reqUri} contains Scriptor")
        return reqUri.contains(SCRIPTOR_STRING)
    }

    @Override
    String getMatchIdentifier() {
        return SCRIPTOR_PREFIX
    }

    @Override
    String filter(HttpRequest request) {
        String reqUri = request.getUri()
        String newUri = reqUri.toString().replace(SCRIPTOR_STRING, NO_SCRIPTOR_STRING)
        log.trace("Rewitten ${reqUri} to ${newUri}")
        return newUri
    }
}
