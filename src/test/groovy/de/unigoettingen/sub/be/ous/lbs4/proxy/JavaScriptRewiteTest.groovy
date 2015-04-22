package de.unigoettingen.sub.be.ous.lbs4.proxy

import com.google.javascript.jscomp.AbstractCompiler
import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.rhino.Node
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.AddEventListenerRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.BaseRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.RewriteContainer
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.WindowEventRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.WindowNavigateRewrite
import de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.ClosureCompilerUtils
import org.junit.BeforeClass
import org.junit.Ignore

import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.*
import static de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.rewrite.JSConstants.*

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import org.junit.Test


/**
 * Created by cmahnke on 24.02.15.
 */
@Log4j
//@TypeChecked
class JavaScriptRewiteTest {

    static String JS_WINDOW_NAVIGATE = 'window.navigate("shiphrahscript:LoadEngine(\'shiphrah.Scriptor\', \'LBS4\')");'
    //static String JS_WINDOW_NAVIGATE = 'window.location.href = \'http://www.google.com\''

    static String JS = 'function setTextFieldFocus() {\n' +
            '   var oText;\n' +
            '   oText = window.event.srcElement.id + "_t";\n' +
            '   document.all(oText).className = "textFieldFocus";\n' +
            '}' +
            'function setTextFieldAndDSO(oField, oNode) {\n' +
            '   if (oField.dataLength && oNode) {\n' +
            '      oField.title = oNode.text;\n' +
            '      oNode.text = oNode.text.substring(0, parseInt(oField.dataLength));\n' +
            '   }\n' +
            '}'

    static Map<String, String> JS_ATTACH_EVENT_SNIPPETS = [
            'ROOT'    : 'document.attachEvent(\'onreadystatechange\',documentReadyStateChange);',
            'FUNCTION': 'function documentReadyStateChange() {\n' +
                    '    if (document.readyState=="complete") {\n' +
                    '        var allTxtAreas = document.getElementsByTagName("textarea");\n' +
                    '            for (var i = 0; i < allTxtAreas.length; i++) {\n' +
                    '                allTxtAreas[i].attachEvent(\'onkeydown\',processKeyDown);\n' +
                    '            }\n' +
                    '        }\n' +
                    '}']

    static Map<String, String> JS_WINDOW_EVENT_SNIPPETS = [
            'KEYCODES'               : 'function processKeyDown() {\n' +
                    '    if ((window.event.keyCode==13) && !(window.event.ctrlKey || window.event.altKey || window.event.shiftKey )) {\n' +
                    '        window.event.cancelBubble = true;\n' +
                    '        window.returnValue = false;\n' +
                    '        return;\n' +
                    '    }\n' +
                    '}',
            'WINDOW_EVENT'           : 'function setTextFieldFocus() {\n' +
                    '   var oText;\n' +
                    '   oText = window.event.srcElement.id + "_t";\n' +
                    '   document.all(oText).className = "textFieldFocus";\n' +
                    '}',
            'WINDOW_EVENT_SRCELEMENT': 'function gotoNextTextBox() {\n' +
                    '//AvM 26-07-2000\n' +
                    '//implementation of the enterkey\n' +
                    '\n' +
                    '   var el = window.event.srcElement;\n' +
                    '   if (el.enterKeyEnabled == 1) {\n' +
                    '      var tbl = document.all.tags("INPUT");\n' +
                    '      var found =false;\n' +
                    '      var i = 0;\n' +
                    '      while (! found && i < tbl.length) {\n' +
                    '         if (tbl(i).id == el.id)\n' +
                    '             found = true;\n' +
                    '         i++;\n' +
                    '      }\n' +
                    '\n' +
                    '      if (i <= tbl.length) {\n' +
                    '         try {\n' +
                    '            if (i == tbl.length) {\n' +
                    '               el.blur();\n' +
                    '               tbl(0).focus();\n' +
                    '            } else {\n' +
                    '               tbl(i).focus();\n' +
                    '            }\n' +
                    '         } catch(e) {\n' +
                    '            el.blur();\n' +
                    '            el.focus();\n' +
                    '         }\n' +
                    '      }\n' +
                    '      return true;\n' +
                    '   } else {\n' +
                    '      return false;\n' +
                    '   }\n' +
                    '}',
            'WINDOW_EVENT_IN_IF'     : 'function getFieldDescription(sField, sCode, bClearMsg) {\n' +
                    '   if (window.event) {\n' +
                    '         // clearmessages only in onchange event\n' +
                    '      if (bClearMsg == undefined && window.event.type == "change")\n' +
                    '         bClearMsg = true;\n' +
                    '   }\n' +
                    '\n' +
                    '   var oField = document.all(sField);\n' +
                    '   if (oField) {\n' +
                    '      if (!sCode || sCode === undefined)\n' +
                    '         sCode = oField.value;\n' +
                    '\n' +
                    '      var descField = document.all(sField + \'_d\');\n' +
                    '\n' +
                    '      if (sCode != \'\') {\n' +
                    '         if (oField.getDescription) {\n' +
                    '            var desc = oField.getDescription(connectionId, sCode);\n' +
                    '            desc = parseReturnXML(bClearMsg, desc, oField);\n' +
                    '            setTextField(descField, desc);\n' +
                    '         }\n' +
                    '      } else {\n' +
                    '         descField.innerText=\'\';\n' +
                    '      }\n' +
                    '   }\n' +
                    '}',
            'WINDOW_EVENT_ARG'       : 'function documentKeyDown() {\n' +
                    '   var key = window.event.keyCode, el = window.event.srcElement;\n' +
                    '   var ctrl = window.event.ctrlKey, alt = window.event.altKey;\n' +
                    '   var shift = window.event.shiftKey;\n' +
                    '   var button = "";\n' +
                    '   var shortcutExists;\n' +
                    '\n' +
                    '   if (key == 27 && window.dialogArguments) {      // Escape in popup window\n' +
                    '      window.returnValue = "";\n' +
                    '      window.close();\n' +
                    '      return;\n' +
                    '   }\n' +
                    '\n' +
                    '   var menuBar = window.top.frames.MenuBar;\n' +
                    '   if (window.dialogArguments) {\n' +
                    '      popupKeyDown();\n' +
                    '   }\n' +
                    '\n' +
                    '   if (!menuBar) return;\n' +
                    '\n' +
                    '   menuBar = menuBar.document;\n' +
                    '   var menu = window.top.frames.Menu;\n' +
                    '\n' +
                    '   if (key ==13) {                                                 // Enter\n' +
                    '      var sys = window.top.frames.Data.document.system;\n' +
                    '      if (sys == "ACQ") {\n' +
                    '         if (el.enterKeyEnabled != 1){\n' +
                    '            if(el.id != "searchTerm" && el.id != "search" && el.dataSrc != "#searchData") {\n' +
                    '               button="save"\n' +
                    '            }\n' +
                    '         }\n' +
                    '      }\n' +
                    '      if (window.gotoNextTextBox != undefined){\n' +
                    '         gotoNextTextBox();\n' +
                    '      }\n' +
                    '   }\n' +
                    '\n' +
                    '   if (ctrl && !alt && !shift && key == 83)           // Ctrl-S\n' +
                    '      button = "save";\n' +
                    '   if (!ctrl && alt && !shift && key == 115)          // Alt-F4\n' +
                    '      button = "exit";\n' +
                    '   if (ctrl && !alt && !shift && key == 80)           // Ctrl-P\n' +
                    '      button = "print";\n' +
                    '   if (ctrl && alt && !shift && key == 80)           // Ctrl-Alt-P\n' +
                    '      button = "print_local";\n' +
                    '   if (ctrl && !alt && !shift && key == 107)           // Ctrl-+\n' +
                    '      if (document.all.titlePrint) {\n' +
                    '         if (document.all.titlePrint[0].style.visibility == "visible") {\n' +
                    '            magnifyTitle();\n' +
                    '         }\n' +
                    '      }\n' +
                    '   if (ctrl && !alt && !shift && key == 86)           // Ctrl-V\n' +
                    '      button = "paste";\n' +
                    '   if (ctrl && !alt && !shift && key == 67)           // Ctrl-C\n' +
                    '      button = "copy";\n' +
                    '   if (ctrl && !alt && !shift && key == 120)          // Ctrl-F9\n' +
                    '      button = "copy_record";\n' +
                    '   if (ctrl && !alt && !shift && key == 121)          // Ctrl-F10\n' +
                    '      button = "paste_record";\n' +
                    '   if (ctrl && !alt && !shift && key == 88)           // Ctrl-X\n' +
                    '      button = "cut";\n' +
                    '   if (ctrl && !alt && !shift && key == 36)           // Ctrl-Home\n' +
                    '      button = "home";\n' +
                    '   if (ctrl && !alt && !shift && key == 87)           // Ctrl-W\n' +
                    '      button = "delall";\n' +
                    '   if (ctrl && !alt && !shift && key == 89)           // Ctrl-Y\n' +
                    '      button = "generate_shelfmark";\n' +
                    '   if (!ctrl && !alt && !shift && key == 112)         // F1\n' +
                    '      button = "help";\n' +
                    '   if (ctrl && !alt && !shift && key ==46 )           // Ctrl-Del\n' +
                    '      button = "delete";\n' +
                    '   if (ctrl && alt && !shift && key ==37 )            // Ctrl-Alt-left arrow\n' +
                    '      button = "prev";\n' +
                    '   if (ctrl && alt && !shift && key ==39 )            // Ctrl-Alt-right arrow\n' +
                    '      button = "next";\n' +
                    '   if (!ctrl && !alt && shift && key ==32 )           // Shift-Space\n' +
                    '         button = "searchbox";\n' +
                    '   if (ctrl && !alt && !shift && key ==74 )           // Ctrl-J\n' +
                    '            button = "newsession";\n' +
                    '\n' +
                    '   if (!ctrl && !alt && !shift && key == 27) {         // Escape\n' +
                    '      storeItem(\'clearContext\', \'false\');\n' +
                    '      menu.window.processPreviousScreen();\n' +
                    '   }\n' +
                    '\n' +
                    '   if (ctrl && !alt && shift) {\n' +
                    '      menu.window.processShortCutKey(key);\n' +
                    '      event.cancelBubble = true;\n' +
                    '      event.returnValue = false;\n' +
                    '   }\n' +
                    '   if (!ctrl && alt && shift) {\n' +
                    '      storeItem(\'clearContext\', \'true\');\n' +
                    '      clearContext();\n' +
                    '      window.top.frames.Menu.window.processShortCutKey(key);\n' +
                    '   }\n' +
                    '\n' +
                    '   if (ctrl && alt && !shift && key == 48) {       // Ctrl-Alt-0\n' +
                    '      var oItem = menu.window.getFocusMenu();\n' +
                    '      if (oItem != null) {\n' +
                    '         menu.window.processMenuItem(oItem.id);\n' +
                    '      }\n' +
                    '   }\n' +
                    '\n' +
                    '   if (ctrl && !alt && !shift && key == 65) {      // Ctrl-A\n' +
                    '      if (window.top.frames.Message) {\n' +
                    '         window.top.frames.Message.enlargeMessageWindow(window.event);\n' +
                    '         event.cancelBubble = true;\n' +
                    '         event.returnValue = false;\n' +
                    '         return;\n' +
                    '      }\n' +
                    '   }\n' +
                    '\n' +
                    '   if (button != "") {\n' +
                    '     if (isMenuButtonEnabled(button))\n' +
                    '         menuBar.all(button).click();\n' +
                    '         event.cancelBubble = true;\n' +
                    '         event.returnValue = false;\n' +
                    '         event.keyCode = 0;\n' +
                    '         return false;\n' +
                    '   } else {\n' +
                    '      if ((!ctrl && alt && !shift && key == 121) || (ctrl && !alt && !shift && key == 77)) {     // Alt-F10 OR Ctrl-M\n' +
                    '         menu.focus();\n' +
                    '         event.cancelBubble = true;\n' +
                    '         event.returnValue = false;\n' +
                    '         event.keyCode = 0;\n' +
                    '         return false;\n' +
                    '      } else {\n' +
                    '         try {\n' +
                    '            if (CTab_tabKeyPressed(el, key, ctrl, alt, shift))\n' +
                    '               return key;\n' +
                    '         } catch(e) {\n' +
                    '            try {\n' +
                    '               if (parent.CTab_tabKeyPressed(el, key, ctrl, alt, shift))\n' +
                    '                  return key;\n' +
                    '            } catch (e) {\n' +
                    '            };\n' +
                    '         };\n' +
                    '\n' +
                    '         try {\n' +
                    '           if (!checkInfoWindow(el, key, ctrl, alt, shift))\n' +
                    '              checkPopupList(el, key, ctrl, alt, shift);\n' +
                    '         } catch(e) {\n' +
                    '         }\n' +
                    '\n' +
                    '         try {\n' +
                    '           if (document.all.historyBox)\n' +
                    '              if (document.all.historyBox.style.visibility == "visible")\n' +
                    '                 document.all.historyBox.style.visibility = "hidden";\n' +
                    '         } catch (e) {\n' +
                    '         }\n' +
                    '         try {\n' +
                    '            parent.keyDown(window.event);\n' +
                    '         } catch (e) {\n' +
                    '         }\n' +
                    '      }\n' +
                    '   }\n' +
                    '}'

    ]

    //Test Resources
    static Map<URL, URL> URLS = [:]

    static {
        URLS.put(new URL('http://xgoe.gbv.de:9090/lbs4/general/html/script/lbsData.js'), new URL('file:///scripts/lbsData.js'))
        URLS.put(new URL('http://xgoe.gbv.de:9090/lbs4/general/html/script/messageDoc.js'), new URL('file:///scripts/messageDoc.js'))
        URLS.put(new URL('http://xgoe.gbv.de:9090/lbs4/general/html/script/buttonBar.js'), new URL('file:///scripts/buttonBar.js'))
        URLS.put(new URL('http://xgoe.gbv.de:9090/lbs4/general/html/script/buttonBarDoc.js'), new URL('file:///scripts/buttonBarDoc.js'))
        URLS.put(new URL('http://xgoe.gbv.de:9090/lbs4/general/html/script/jaguar.js'), new URL('file:///scripts/jaguar.js'))
    }

    @BeforeClass
    public static void init() {
        for (URL u : URLS.keySet()) {
            if (checkClassPath(URLS.get(u)) != null) {
                URLS.put(u, checkClassPath(URLS.get(u)))
            } else {
                URLS.put(u, null)
            }
        }
    }

    @Test
    public void testParse() {
        SourceFile sf = ClosureCompilerUtils.getSource('test.js', JS)
        Node node = ClosureCompilerUtils.parse(sf)

        sf = ClosureCompilerUtils.getSource('test.js', 'window.location.href = test')
        node = ClosureCompilerUtils.parse(sf)

        log.info('JS parsed')
    }

    @Test
    public void testGenerate() {
        log.info('------------------- Start of testGenerate() -------------------')
        log.info('Try to dump source')
        SourceFile sf = ClosureCompilerUtils.getSource('test.js', JS)
        log.info('Example function: ' + ClosureCompilerUtils.toSource(ClosureCompilerUtils.parse(sf)))
        log.info('Example line: ' + ClosureCompilerUtils.toSource(getEventNode()))
        log.info('------------------- End of testGenerate() -------------------')
    }

    @Test
    public void testAddEventListenerRewrite() {
        log.info('------------------- Start of testAddEventListenerRewrite() -------------------')
        for (String key : JS_ATTACH_EVENT_SNIPPETS.keySet()) {
            SourceFile sf = ClosureCompilerUtils.getSource('test.js', JS_ATTACH_EVENT_SNIPPETS.get(key))
            Node node = ClosureCompilerUtils.parse(sf)
            log.info('Original Source: ' + ClosureCompilerUtils.toSource(node))
            //NodeTraversal.traverse(ClosureCompilerUtils.getCompiler(), node, new AddEventListenerRewrite())
            NodeTraversal.traverse(ClosureCompilerUtils.getCompiler(), node, new AddEventListenerRewrite())
            log.info('Changed Source: ' + ClosureCompilerUtils.toSource(node))
        }
        log.info('------------------- End of testAddEventListenerRewrite() -------------------')

    }

    @Test
    public void testWindowNavigateRewrite() {
        log.info('------------------- Start of testWindowNavigateRewrite() -------------------')
        SourceFile sf = ClosureCompilerUtils.getSource('test.js', JS_WINDOW_NAVIGATE)
        Node node = ClosureCompilerUtils.parse(sf)
        log.info('Original Source: ' + ClosureCompilerUtils.toSource(node))
        NodeTraversal.traverse(ClosureCompilerUtils.getCompiler(), node, new WindowNavigateRewrite())
        log.info('Changed Source: ' + ClosureCompilerUtils.toSource(node))
        log.info('------------------- End of testWindowNavigateRewrite() -------------------')

    }


    @Test
    public void testWindowEventRewrite() {
        log.info('------------------- Start of testWindowEventRewrite() -------------------')
        for (String key : JS_WINDOW_EVENT_SNIPPETS.keySet()) {
            SourceFile sf = ClosureCompilerUtils.getSource('test.js', JS_WINDOW_EVENT_SNIPPETS.get(key))
            Node node = ClosureCompilerUtils.parse(sf)
            log.info("Original Source(${key}): " + ClosureCompilerUtils.toSource(node))
            NodeTraversal.traverse(ClosureCompilerUtils.getCompiler(), node, new WindowEventRewrite())
            log.info("Changed Source(${key}): " + ClosureCompilerUtils.toSource(node))
        }
        log.info('------------------- End of testWindowEventRewrite() -------------------')

    }

    @Test
    public void testDSL() {
        log.info('------------------- Start of testDSL() -------------------')

        Node windowEvent = getprop(name('window'), str('event'))
        log.info('Generated Source: ' + ClosureCompilerUtils.toSource(windowEvent))

        Node block = block(var(name('e', getprop(name('window'), str('event')))))
        Node condition = iF(not(name('e')), block)
        log.info('Generated Source: ' + ClosureCompilerUtils.toSource(condition))
        log.info('------------------- End of testDSL() -------------------')
    }


    @Test
    public void testDownload() {
        //TODO: Add a time out here
        for (URL u : URLS.keySet()) {
            log.info("Checking URL ${u}")
            InputStream is
            RewriteContainer rc = new RewriteContainer()
            rc.addRewite(new WindowNavigateRewrite())
            rc.addRewite(new AddEventListenerRewrite())
            rc.addRewite(new WindowEventRewrite())
            //Connection handling
            URLConnection uc = u.openConnection()
            uc.setConnectTimeout(10)
            try {
                is = uc.getInputStream()
            } catch (UnknownHostException uhe) {
                if (URLS.get(u) == null) {
                    log.trace("Can't open connection no local file given")
                    continue
                }
                log.trace("Can't open connection trying local file ${URLS.get(u)}")
                is = URLS.get(u).openStream()
            } catch (SocketTimeoutException ste) {
                log.error('TODO')
            }

            String result = rc.rewrite(is.text)
            log.info("Changed Source(${u}): " + result)
        }

    }

    @Ignore
    @Test
    public void testScratchRewrite() {
        log.info('------------------- Start of testScratchRewrite() -------------------')
        SourceFile sf = ClosureCompilerUtils.getSource('test.js', JS_WINDOW_EVENT_SNIPPETS.get('WINDOW_EVENT_SRCELEMENT'))
        Node node = ClosureCompilerUtils.parse(sf)
        log.info('Original Source: ' + ClosureCompilerUtils.toSource(node))
        //NodeTraversal.traverse(ClosureCompilerUtils.getCompiler(), node, new ScratchRewrite())
        log.info('Changed Source: ' + ClosureCompilerUtils.toSource(node))
        log.info('------------------- End of testScratchRewrite() -------------------')

    }

    public static URL checkClassPath (URL url) {
        if (JavaScriptRewiteTest.getClass().getResource(url.toString().substring(5))) {
            return JavaScriptRewiteTest.getClass().getResource(url.toString().substring(5))
        }
        return null
    }


/*
    static class ScratchListenerRewrite extends BaseRewrite implements NodeTraversal.Callback {
        static String METHOD = 'attachEvent'

        public void visit(NodeTraversal t, Node n, Node parent) {

            if (n.isString() && parent.isGetProp() && n.getString() == METHOD) {
                Node target = parent.getChildAtIndex(0)
                String method = n.getString()
                Node call = parent.getParent()
                String rawEvent = call.getChildAtIndex(1).getString()
                String event
                if (rawEvent.startsWith('on')) {
                    event = rawEvent.substring(2)
                } else {
                    event = rawEvent
                }
                Node callback = call.getChildAtIndex(2)
                if (!target.isGetElem()) {
                    log.info("JS: Found call for ${METHOD} on ${target.getString()} for event (on)${event}, callback ${callback.getString()}")
                    //rewrite call
                    Node getProp = getprop(name(target.getString()), str('addEventListener'))
                    Node newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.call(getProp, (Node) str(event), (Node) name(callback.getString()), nil())
                    parent.getParent().getParent().replaceChild(call, newCall)
                } else {
                    String targetDesc = target.firstChild.getString()
                    log.info("JS: Found call for ${METHOD} on element of array ${targetDesc} for event (on)${event}, callback ${callback.getString()}")
                    Node getProp = getprop(target.cloneNode(), str('addEventListener'))
                    Node newCall = de.unigoettingen.sub.be.ous.lbs4.proxy.filters.javascript.util.NodeBuilder.call(getProp, (Node) str(event), (Node) name(callback.getString()), nil())
                }
            }
        }
    }


    static class ScratchRewrite extends BaseRewrite implements NodeTraversal.Callback {
        static String OBJECT = 'window'
        static String METHOD = 'event'

        Node from = getprop(name(OBJECT), str(METHOD))

        ScratchRewrite() {

        }

        public void visit(NodeTraversal t, Node n, Node parent) {
            dumpNodes(n, parent)
            if (n.isEquivalentToTyped(from)) {

                log.info('found node: ' + n.toString())

                //Check if there are children
                Node newGetOpt
                if (parent.isGetProp()) {
                    //Check if srcElement needs to be rewritten to target
                    if (n.getNext().isString() && n.getNext().getString() == 'srcElement') {
                        log.trace('Also rewriting srcElement to target')
                        if (!parent.getParent().getParent().isVar()) {
                            Node child = parent.getParent().getChildAtIndex(1).cloneNode()
                            if (!child.isString()) {
                                throw new IllegalStateException('window.event rewrite for childs that are not strings is not implemented yet on line ' + n.getLineno())
                            }
                            newGetOpt = getprop(getprop(name('e'), str('target')), child)
                            parent.getParent().getParent().replaceChild(parent.getParent(), newGetOpt)
                        } else {
                            newGetOpt = getprop(name('e'), str('target'))
                            parent.getParent().replaceChild(parent, newGetOpt)
                        }
                    } else {
                        newGetOpt = getprop(name('e'), parent.getChildAtIndex(1).cloneNode())
                        parent.getParent().replaceChild(parent, newGetOpt)
                    }
                } else if (parent.isIf() || parent.isCall() || parent.isNot() || parent.isAssign()) {
                    if (n.getNext() != null && n.getNext().isString() && n.getNext().getString() == 'srcElement') {
                        throw new IllegalStateException('window.event.srcElement rewrite for parent If is not implemented yet on line ' + n.getLineno())
                    } else if (parent.getChildAtIndex(0).isEquivalentToTyped(from) || parent.getLastChild().isEquivalentToTyped(from)) {
                        parent.replaceChild(n, name('e'))
                    } else {
                        throw new IllegalStateException('window.event rewrite for parent If is not implemented yet for complex expressions on line ' + n.getLineno())
                    }
                } else {
                    throw new IllegalStateException('window.event rewrite for other parents than GetProp, Call and If is not implemented yet on line ' + n.getLineno())
                }
            }
        }
    }

*/


}
