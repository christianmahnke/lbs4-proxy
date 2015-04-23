package de.unigoettingen.sub.be.ous.lbs4.proxy

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest

import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created by cmahnke on 12.02.15.
 *
 */
@Log4j
@CompileStatic
class LBS4Proxy {
    static String NO_SCRIPTOR_STRING = '?SCRIPT=0'
    static String SCRIPTOR_STRING = '?SCRIPT=1'
    static ProxyConfig pc

    static void main(args) {
        //Use Spring to set up filters
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml")
        pc = (ProxyConfig) context.getBean('proxyConfigBean')

        log.info('Starting Proxy server')
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(8080)
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new ProxyHttpFiltersAdapter(originalRequest, pc.getFilters())
                    }

                    //This is needed to avoid fragmented HTTP packets
                    @Override
                    public int getMaximumRequestBufferSizeInBytes() {
                        return 1024 * 1024
                    }

                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return 1024 * 1024
                    }

                })
                        .start()
    }

}
