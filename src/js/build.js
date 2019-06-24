var fs = require("fs");

// IN TEST FILE 
// EXECUTE Clazz.ClassFilesLoaded.sort();  (test_EntryPoint.html)
var files = ["java/applet/AppletContext.js","java/applet/AppletStub.js","java/applet/JSApplet.js","java/awt/AWTEvent.js","java/awt/AWTKeyStroke.js","java/awt/ActiveEvent.js","java/awt/BorderLayout.js","java/awt/Color.js","java/awt/Component.js","java/awt/ComponentOrientation.js","java/awt/Container.js","java/awt/ContainerOrderFocusTraversalPolicy.js","java/awt/Cursor.js","java/awt/DefaultFocusTraversalPolicy.js","java/awt/DefaultKeyboardFocusManager.js","java/awt/Dimension.js","java/awt/EventDispatchThread.js","java/awt/EventFilter.js","java/awt/EventQueue.js","java/awt/EventQueueItem.js","java/awt/FlowLayout.js","java/awt/FocusTraversalPolicy.js","java/awt/Font.js","java/awt/GraphicsConfiguration.js","java/awt/GraphicsDevice.js","java/awt/GraphicsEnvironment.js","java/awt/Insets.js","java/awt/JSComponent.js","java/awt/JSPanel.js","java/awt/KeyEventDispatcher.js","java/awt/KeyEventPostProcessor.js","java/awt/KeyboardFocusManager.js","java/awt/LayoutManager.js","java/awt/LayoutManager2.js","java/awt/LightweightDispatcher.js","java/awt/Paint.js","java/awt/Queue.js","java/awt/Rectangle.js","java/awt/Shape.js","java/awt/Toolkit.js","java/awt/Transparency.js","java/awt/dnd/peer/DropTargetPeer.js","java/awt/event/AWTEventListener.js","java/awt/event/ActionListener.js","java/awt/event/InvocationEvent.js","java/awt/geom/Dimension2D.js","java/awt/geom/Rectangle2D.js","java/awt/geom/RectangularShape.js","java/awt/image/ImageObserver.js","java/awt/peer/ComponentPeer.js","java/awt/peer/ContainerPeer.js","java/awt/peer/KeyboardFocusManagerPeer.js","java/awt/peer/LightweightPeer.js","java/beans/ChangeListenerMap.js","java/beans/PropertyChangeEvent.js","java/beans/PropertyChangeListener.js","java/beans/PropertyChangeSupport.js","java/io/BufferedReader.js","java/io/Closeable.js","java/io/Reader.js","java/io/StringReader.js","java/lang/AbstractStringBuilder.js","java/lang/AutoCloseable.js","java/lang/Class.js","java/lang/Iterable.js","java/lang/Readable.js","java/lang/StringBuffer.js","java/lang/StringBuilder.js","java/lang/Thread.js","java/lang/ThreadGroup.js","java/lang/reflect/Constructor.js","java/math/BigInteger.js","java/net/URL.js","java/net/URLStreamHandlerFactory.js","java/util/AbstractCollection.js","java/util/AbstractList.js","java/util/AbstractMap.js","java/util/AbstractSequentialList.js","java/util/AbstractSet.js","java/util/ArrayList.js","java/util/Arrays.js","java/util/Collection.js","java/util/Collections.js","java/util/Deque.js","java/util/Dictionary.js","java/util/Enumeration.js","java/util/EventListener.js","java/util/EventObject.js","java/util/HashMap.js","java/util/HashSet.js","java/util/Hashtable.js","java/util/IdentityHashMap.js","java/util/Iterator.js","java/util/LinkedHashMap.js","java/util/LinkedList.js","java/util/List.js","java/util/Locale.js","java/util/Map.js","java/util/Objects.js","java/util/Queue.js","java/util/RandomAccess.js","java/util/Set.js","java/util/Stack.js","java/util/Vector.js","javajs/util/AjaxURLStreamHandlerFactory.js","javajs/util/JSThread.js","javajs/util/Lst.js","javax/swing/ArrayTable.js","javax/swing/JApplet.js","javax/swing/JComponent.js","javax/swing/JLayeredPane.js","javax/swing/JPanel.js","javax/swing/JRootPane.js","javax/swing/LookAndFeel.js","javax/swing/RepaintManager.js","javax/swing/RootPaneContainer.js","javax/swing/SwingConstants.js","javax/swing/SwingPaintEventDispatcher.js","javax/swing/SwingUtilities.js","javax/swing/UIDefaults.js","javax/swing/UIManager.js","javax/swing/border/AbstractBorder.js","javax/swing/border/Border.js","javax/swing/border/EmptyBorder.js","javax/swing/border/EtchedBorder.js","javax/swing/event/ChangeListener.js","javax/swing/event/EventListenerList.js","javax/swing/plaf/BorderUIResource.js","javax/swing/plaf/ColorUIResource.js","javax/swing/plaf/ComponentUI.js","javax/swing/plaf/DimensionUIResource.js","javax/swing/plaf/FontUIResource.js","javax/swing/plaf/InsetsUIResource.js","javax/swing/plaf/UIResource.js","oscript/DefaultParser.js","oscript/NodeEvaluatorFactory.js","oscript/OscriptBuiltins.js","oscript/OscriptInterpreter.js","oscript/Parser.js","oscript/data/BasicScope.js","oscript/data/BuiltinType.js","oscript/data/Function.js","oscript/data/FunctionScope.js","oscript/data/GlobalScope.js","oscript/data/JavaBridge.js","oscript/data/OArray.js","oscript/data/OBoolean.js","oscript/data/OExactNumber.js","oscript/data/OException.js","oscript/data/OIllegalArgumentException.js","oscript/data/OInexactInterface.js","oscript/data/OInexactNumber.js","oscript/data/OJavaException.js","oscript/data/ONoSuchMemberException.js","oscript/data/ONullReferenceException.js","oscript/data/OObject.js","oscript/data/OSpecial.js","oscript/data/OString.js","oscript/data/OUnsupportedOperationException.js","oscript/data/Proxy.js","oscript/data/Reference.js","oscript/data/Scope.js","oscript/data/Symbol.js","oscript/data/Symbols.js","oscript/data/Type.js","oscript/data/Value.js","oscript/exceptions/PackagedScriptObjectException.js","oscript/interpreter/InterpretedNodeEvaluatorFactory.js","oscript/parser/ASCII_UCodeESC_CharStream.js","oscript/parser/BitHacker.js","oscript/parser/Int64.js","oscript/parser/JTBToolkit.js","oscript/parser/OscriptParser.js","oscript/parser/OscriptParserConstants.js","oscript/parser/OscriptParserTokenManager.js","oscript/parser/ParseException.js","oscript/parser/Token.js","oscript/syntaxtree/Literal.js","oscript/syntaxtree/MultiplicativeExpression.js","oscript/syntaxtree/Node.js","oscript/syntaxtree/NodeChoice.js","oscript/syntaxtree/NodeListInterface.js","oscript/syntaxtree/NodeListOptional.js","oscript/syntaxtree/NodeOptional.js","oscript/syntaxtree/NodeSequence.js","oscript/syntaxtree/NodeToken.js","oscript/syntaxtree/PostfixExpression.js","oscript/syntaxtree/PrimaryExpression.js","oscript/syntaxtree/PrimaryPrefix.js","oscript/syntaxtree/PrimaryPrefixNotFunction.js","oscript/syntaxtree/TypeExpression.js","oscript/syntaxtree/UnaryExpression.js","oscript/util/MemberTable.js","oscript/util/OpenHashSymbolTable.js","oscript/util/Primes.js","oscript/util/SymbolTable.js","server/ValueConvertor.js","sun/awt/AWTAccessor.js","sun/awt/AWTAutoShutdown.js","sun/awt/AppContext.js","sun/awt/ComponentFactory.js","sun/awt/KeyboardFocusManagerPeerProvider.js","sun/awt/MostRecentKeyValue.js","sun/awt/MostRecentThreadAppContext.js","sun/awt/PaintEventDispatcher.js","sun/awt/PostEventQueue.js","sun/awt/RequestFocusController.js","sun/awt/SunToolkit.js","sun/awt/WindowClosingListener.js","sun/awt/WindowClosingSupport.js","sun/swing/SwingLazyValue.js","swingjs/JSApp.js","swingjs/JSApplet.js","swingjs/JSAppletThread.js","swingjs/JSAppletViewer.js","swingjs/JSFocusPeer.js","swingjs/JSFrameViewer.js","swingjs/JSGraphicsConfiguration.js","swingjs/JSGraphicsEnvironment.js","swingjs/JSScreenDevice.js","swingjs/JSThreadGroup.js","swingjs/JSToolkit.js","swingjs/JSUtil.js","swingjs/api/Interface.js","swingjs/api/js/DOMNode.js","swingjs/api/js/JSInterface.js","swingjs/plaf/HTML5LookAndFeel.js","swingjs/plaf/JSAppletUI.js","swingjs/plaf/JSComponentUI.js","swingjs/plaf/JSEventHandler.js","swingjs/plaf/JSLayeredPaneUI.js","swingjs/plaf/JSLightweightUI.js","swingjs/plaf/JSPanelUI.js","swingjs/plaf/JSRootPaneUI.js","test/EntryPoint.js"];

function rec(d) {
	for (var e of fs.readdirSync("site/swingjs/j2s/"+d)) {
		var stats = fs.lstatSync("site/swingjs/j2s/"+d+"/"+e);
		if (stats.isDirectory()) 
			rec(d+"/"+e);
		else if (e.endsWith(".js")) {
			var k = d+"/"+e;
			if (files.indexOf(k) < 0)  {
				//console.log("!!! "+k);
				files.push(k);
			}
		}
	}
}
// extra
//-------------------------------------------------
rec("oscript/syntaxtree");
rec("oscript/parser");
rec("oscript/translator");
rec("javajs/util");
rec("org/apache/harmony/luni/util");
rec("oscript/interpreter");
files.push("swingjs/JSNullComponentPeer.js");
files.push("oscript/NodeEvaluator");
files.push("oscript/util/StackFrame");
files.push("oscript/visitor/ObjectVisitor");
files.push("java/util/GregorianCalendar");
files.push("java/util/Calendar");
files.push("java/util/TimeZone");
files.push("sun/util/calendar/ZoneInfo");
files.push("sun/util/resources/LocaleData");
files.push("java/util/ResourceBundle");
files.push("java/io/ByteArrayInputStream");
files.push("java/io/InputStream");
files.push("java/util/PropertyResourceBundle");
files.push("java/util/Properties");
files.push("sun/util/calendar/CalendarSystem");
files.push("sun/util/calendar/Gregorian");
files.push("sun/util/calendar/BaseCalendar");
files.push("sun/util/calendar/AbstractCalendar");
files.push("sun/util/calendar/CalendarDate");
files.push("sun/util/calendar/CalendarUtils");
files.push("java/util/TreeMap");
files.push("java/util/NavigableMap");
files.push("java/util/SortedMap");
rec("oscript/data");
rec("bridge");
rec("server");
rec("oscript/varray");
files.push("java/util/NavigableSet");
files.push("java/util/SortedSet");
//-------------------------------------------------
console.log(" >>> BUILDING visionr-oscript-js...");
var p=[];
for (var e of files) {
	var f = "./site/swingjs/j2s/"+e;
	p.push("case '/"+e+"' : require('"+f+"');");
}
var a=[
	"require('./src/js/emul')(global,function(path){ switch(path) {\n"+p.join("\n")+"\n}});",
	"require('./target/swingjs2.js');",
	"var info = {code: null,main: 'test.EntryPoint',core: 'NONE',readyFunction: null,width:0,height:0,j2sPath: '',console:console,allowjavascript: true};",
	"SwingJS.getApplet('testApplet', info);"
];
fs.writeFileSync("index.js", a.join("\n"), 'utf8');
fs.writeFileSync("site/swingjs/j2s/bridge/bridge.js", fs.readFileSync("src/js/bridge.js","utf8"), "utf8");
var pre = "var document=function(){};document.location={href : ''};document.createTextNode=document.createElement=function(){return {style:{},childNodes:[],appendChild:function(){}}};document.body={appendChild:function(){},parentNode:{ }};\n";
fs.writeFileSync("target/swingjs2.js",pre+fs.readFileSync("site/swingjs/swingjs2.js","utf8"), "utf8");




