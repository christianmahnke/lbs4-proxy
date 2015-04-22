package de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util

import com.google.javascript.jscomp.AbstractCompiler
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.RhinoErrorReporter
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.jscomp.parsing.Config
import com.google.javascript.jscomp.parsing.ParserRunner
import com.google.javascript.rhino.Node
import com.google.javascript.rhino.head.ErrorReporter
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by cmahnke on 25.02.15.
 */
@TypeChecked
@CompileStatic
class ClosureCompilerUtils {
    static Pattern commentPattern = Pattern.compile( '/\\*.*?\\*/', Pattern.DOTALL)


    public static Node parse(String js) {
        SourceFile sf = getSource('internal.js', js)
        return parse(sf)
    }

    public static SourceFile getSource (String name, String content) {
        //Strip comments they can confuse the parser since it tries to find JSDoc strings
        Matcher m = commentPattern.matcher(content)
        content =  m.replaceAll('')

        InputStream is = new ByteArrayInputStream(content.bytes)
        SourceFile.fromInputStream(name, is)
    }

    public static SourceFile getSource (String name, InputStream is) {
        SourceFile.fromInputStream(name, is)
    }

    public static Node parse (SourceFile sf) {
        Compiler compiler = new Compiler()
        CompilerOptions options = new CompilerOptions()
        options.prettyPrint = true

        ErrorReporter er = RhinoErrorReporter.forNewRhino(compiler)
        Config c = ParserRunner.createConfig(false, Config.LanguageMode.ECMASCRIPT3, false, null)
        //Disable IDE mode to avoid parsing of comments
        //Config c = ParserRunner.createConfig(true, Config.LanguageMode.ECMASCRIPT3, false, null)

        Logger logger = Logger.getLogger(ClosureCompilerUtils.class.getName())
        logger.setLevel(Level.FINEST)
        return ParserRunner.parse(sf, sf.getCode(), c, er, logger)
    }

    public static String toSource(Node node) {
        Compiler compiler = new Compiler() {
            public String generateCode(Node n) {
                CompilerOptions options = new CompilerOptions()
                options.prettyPrint = true
                initOptions(options)
                return toSource(n)
            }
        }
        return compiler.generateCode(node)
    }

    public static AbstractCompiler getCompiler() {
        Compiler compiler = new Compiler() {
            public String generateCode(Node node) {
                return toSource(node)
            }
        }
        return compiler
    }

}
