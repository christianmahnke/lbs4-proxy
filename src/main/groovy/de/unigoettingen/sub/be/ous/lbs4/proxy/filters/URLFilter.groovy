package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import io.netty.handler.codec.http.HttpRequest

/**
 * Created by cmahnke on 23.02.15.
 */
@CompileStatic
interface URLFilter extends Filter {

    public String filter (final HttpRequest request)

}