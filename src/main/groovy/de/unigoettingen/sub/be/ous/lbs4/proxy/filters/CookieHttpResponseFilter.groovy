package de.unigoettingen.sub.be.ous.lbs4.proxy.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpRequest

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by cmahnke on 22.04.15.
 */
@CompileStatic
@Log4j
class CookieHttpResponseFilter extends AbstractFilter implements HttpResponseFilter {
    Tuple2<String, String> rewrite


    CookieHttpResponseFilter(String url, Tuple2<String, String> rewrite) {
        super(url)
        this.rewrite = rewrite
    }

    @Override
    FullHttpResponse filter(HttpRequest req, FullHttpResponse resp) {
        if(resp.headers().contains(HttpHeaders.Names.SET_COOKIE)) {
            log.trace('This is Cookie Monster')
            List<String> cookies = []
            //Find all cookies
            for (String c : resp.headers().getAll(HttpHeaders.Names.SET_COOKIE)) {
                log.trace("Got cookie ${c}")
                if (c.matches(rewrite.first)) {
                    //Rewrite matching cookie
                    Pattern p = Pattern.compile(rewrite.first, Pattern.DOTALL)
                    Matcher m = p.matcher(c)
                    cookies.add(m.replaceAll(rewrite.second))
                    log.trace("Rewrote ${c} to ${rewrite.second} \n ${MATCH}")
                } else {
                    cookies.add(c)
                }
            }
            //Remove all Cookies
            resp.headers().remove(HttpHeaders.Names.SET_COOKIE)
            //Readd all cookies
            for (String c : cookies) {
                resp.headers().add(HttpHeaders.Names.SET_COOKIE, c)
            }
        }
        return resp
    }

    static String MATCH = '                .---. .---. \n' +
            '               :     : o   :    me want cookie!\n' +
            '           _..-:   o :     :-.._    /\n' +
            '       .-\'\'  \'  `---\' `---\' "   ``-.    \n' +
            '     .\'   "   \'  "  .    "  . \'  "  `.  \n' +
            '    :   \'.---.,,.,...,.,.,.,..---.  \' ;\n' +
            '    `. " `.                     .\' " .\'\n' +
            '     `.  \'`.                   .\' \' .\'\n' +
            '      `.    `-._           _.-\' "  .\'  .----.\n' +
            '        `. "    \'"--...--"\'  . \' .\'  .\'  o   `.\n' +
            '        .\'`-._\'    " .     " _.-\'`. :       o  :\n' +
            '      .\'      ```--.....--\'\'\'    \' `:_ o       :\n' +
            '    .\'    "     \'         "     "   ; `.;";";";\'\n' +
            '   ;         \'       "       \'     . ; .\' ; ; ;\n' +
            '  ;     \'         \'       \'   "    .\'      .-\'\n' +
            '  \'  "     "   \'      "           "    _.-\'\n' +
            'Taken from http://www.chris.com/ascii/joan/www.geocities.com/SoHo/7373/sesame.html'


}
