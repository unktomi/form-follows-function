#if !defined(__WIN32__) && !defined(_WIN32)
#include "org_f3_media_web_awesomium_Browser.h"
#include "Awesomium/WebCore.h"
#include "Awesomium/WebView.h"
#include "Awesomium/WebViewListener.h"
#include "Awesomium/BitmapSurface.h"
#include "Awesomium/STLHelpers.h"
#include <iostream>
#include <fstream>
#include <string.h>
#if defined(__WIN32__) || defined(_WIN32)
#include <windows.h>
#elif defined(__APPLE__)
#include <unistd.h>
#endif


using namespace Awesomium;

static WebString toWebString(JNIEnv *env, jstring value);
static jstring newString(JNIEnv *env, const WebString &str);
static void initMethodIds(JNIEnv *env);
static jobject fromJSValue(JNIEnv *env, const JSValue &v);
static jobject newJSArray(JNIEnv *env, const JSArray *value);
static jclass Integer = 0;
static jclass Double = 0;
static jclass Boolean = 0;
static jclass JSArrayClazz = 0;
static jclass JSObjectClazz = 0;
static jclass BrowserClazz = 0;
static jmethodID NewInteger = 0;
static jmethodID NewDouble = 0;
static jmethodID NewBoolean = 0;
static jmethodID NewJSObject = 0;
static jmethodID NewJSArray = 0;

static jmethodID MOnMethodCall = 0;
static jmethodID MOnMethodCallWithReturn = 0;

static Awesomium::WebCore *webCore = 0;
static Awesomium::WebSession *webSession = 0;
static void ensureWebCore() {
  if (webCore == 0) {
    Awesomium::WebConfig config;
    //config.setEnablePlugins(true);
    config.log_level = kLogLevel_Verbose;
    config.plugin_path = WSLit("/Library/Internet Plug-ins/");
    WebStringArray arr;
    arr.Push(WSLit("--allow-file_access-from-files"));
    config.additional_options = arr;
    webCore = Awesomium::WebCore::Initialize(config);
    WebPreferences prefs;
    prefs.enable_plugins = true;
    prefs.enable_web_gl = true;
    prefs.allow_universal_access_from_file_url = true;
    prefs.allow_file_access_from_file_url = true;
    webSession = webCore->CreateWebSession(WSLit(""), prefs);
    //webCore->set_surface_factory(new BitmapSurfaceFactory());
  }
}

static void updateAll() {
  ensureWebCore();
  webCore->Update();
}

class MyWebViewListener : public Awesomium::WebViewListener::View, Awesomium::WebViewListener::Load, Awesomium::JSMethodHandler 
{

public:

  Awesomium::WebView *webView;
  WebString currentURL;
  //  std::wstring currentFrameName;
  WebString currentTitle;
  bool resized;
  JavaVM *jvm;
  jobject target;

  void update() {
    //webCore->update();
  }

  ~MyWebViewListener() {
    webView->Destroy();
  }

  MyWebViewListener(JavaVM *jvm, jobject target)
  {
     webView = 0;
     resized = true;
     this->target = target;
     this->jvm = jvm;
  }

  int width, height;

  Awesomium::Cursor cursorType;

  JSObject *callbackObject;

  void setSize(int width, int height) {
    this->width = width;
    this->height = height;
    if (webView == 0) {
      webView = webCore->CreateWebView(width, height, webSession, kWebViewType_Offscreen);
      webView->set_view_listener(this);
      webView->set_load_listener(this);
      JSValue result = webView->CreateGlobalJavascriptObject(WSLit("f3"));
      callbackObject = &result.ToObject();
      webView->set_js_method_handler(this);
      callbackObject->SetCustomMethod(WSLit("handleEvent"), false);
      if (currentURL.length() > 0) {
        webView->LoadURL(WebURL(currentURL));
      }
    } else {
      resized = true;
    }
  }
  /// This event occurs when the page begins loading a frame.
  virtual void OnBeginLoadingFrame(Awesomium::WebView* caller,
                                   int64 frame_id,
                                   bool is_main_frame,
                                   const Awesomium::WebURL& url,
                                   bool is_error_page) 
  {
  }

  /// This event occurs when a frame fails to load. See error_desc
  /// for additional information.
  virtual void OnFailLoadingFrame(Awesomium::WebView* caller,
                                  int64 frame_id,
                                  bool is_main_frame,
                                  const Awesomium::WebURL& url,
                                  int error_code,
                                  const Awesomium::WebString& error_desc) 
  {
  }

  /// This event occurs when the page finishes loading a frame.
  /// The main frame always finishes loading last for a given page load.
  virtual void OnFinishLoadingFrame(Awesomium::WebView* caller,
                                    int64 frame_id,
                                    bool is_main_frame,
                                    const Awesomium::WebURL& url) 
  {
  }

  /// This event occurs when the DOM has finished parsing and the
  /// window object is available for JavaScript execution.
  virtual void OnDocumentReady(Awesomium::WebView* caller,
                               const Awesomium::WebURL& url)
  {
    currentURL = url.spec();
    //fprintf(stderr, "webView=%p, caller=%p\n", webView, caller);
    JSValue result = caller->ExecuteJavascriptWithResult(WSLit("window"), WSLit(""));
    fprintf(stderr, "window.isNull %d\n", result.IsNull());
    //fprintf(stderr, "thread=%p\n", pthread_self());
    webView->ExecuteJavascript(WSLit("document.addEventListener('mouseover', f3);"), WSLit(""));
  }


  /// This event occurs when the page title has changed.
  virtual void OnChangeTitle(Awesomium::WebView* caller,
                             const Awesomium::WebString& title)
  {
  }

  /// This event occurs when the page URL has changed.
  virtual void OnChangeAddressBar(Awesomium::WebView* caller,
                                  const Awesomium::WebURL& url) {
  }

  /// This event occurs when the tooltip text has changed. You
  /// should hide the tooltip when the text is empty.
  virtual void OnChangeTooltip(Awesomium::WebView* caller,
                               const Awesomium::WebString& tooltip) 
  {
  }

  /// This event occurs when the target URL has changed. This
  /// is usually the result of hovering over a link on a page.
  virtual void OnChangeTargetURL(Awesomium::WebView* caller,
                                 const Awesomium::WebURL& url) 
  {
  }

  /// This event occurs when the cursor has changed. This is
  /// is usually the result of hovering over different content.
  virtual void OnChangeCursor(Awesomium::WebView* caller,
                              Awesomium::Cursor cursor) 
  {
    cursorType = cursor;
  }

  /// This event occurs when the focused element changes on the page.
  /// This is usually the result of textbox being focused or some other
  /// user-interaction event.
  virtual void OnChangeFocus(Awesomium::WebView* caller,
                             Awesomium::FocusedElementType focused_type) 
  {
  }

  /// This event occurs when a message is added to the console on the page.
  /// This is usually the result of a JavaScript error being encountered
  /// on a page.
  virtual void OnAddConsoleMessage(Awesomium::WebView* caller,
                                   const Awesomium::WebString& message,
                                   int line_number,
                                   const Awesomium::WebString& source) 
  {
  }

  /// This event occurs when a WebView creates a new child WebView
  /// (usually the result of window.open or an external link). It
  /// is your responsibility to display this child WebView in your
  /// application. You should call Resize on the child WebView
  /// immediately after this event to make it match your container
  /// size.
  ///
  /// If this is a child of a Windowed WebView, you should call
  /// WebView::set_parent_window on the new view immediately within
  /// this event.
  ///
  virtual void OnShowCreatedWebView(Awesomium::WebView* caller,
                                    Awesomium::WebView* new_view,
                                    const Awesomium::WebURL& opener_url,
                                    const Awesomium::WebURL& target_url,
                                    const Awesomium::Rect& initial_pos,
                                    bool is_popup) 
  {
    fprintf(stderr, "POPUP\n");
  }

  virtual void OnMethodCall(Awesomium::WebView* caller,
                            unsigned int remote_object_id,
                            const Awesomium::WebString& method_name,
                            const Awesomium::JSArray& args) 
  {
    JNIEnv * g_env = 0;
    JavaVM * g_vm = jvm;
    // double check it's all ok
    int getEnvStat = g_vm->GetEnv((void **)&g_env, JNI_VERSION_1_6);
    bool wasAttached = true;
    if (getEnvStat == JNI_EDETACHED) {
      std::cout << "GetEnv: not attached" << std::endl;
      if (g_vm->AttachCurrentThread((void **) &g_env, NULL) != 0) {
        std::cout << "Failed to attach" << std::endl;
      }
      wasAttached = false;
    } else if (getEnvStat == JNI_OK) {
      //
    } else if (getEnvStat == JNI_EVERSION) {
      std::cout << "GetEnv: version not supported" << std::endl;
    }
    jstring methodName = newString(g_env, method_name);
    jobject local = g_env->NewGlobalRef(target);
    g_env->CallStaticVoidMethod(BrowserClazz,
                                MOnMethodCall,
                                local,
                                methodName,
                                newJSArray(g_env, new JSArray(args)));
    g_env->DeleteGlobalRef(local);
    if (g_env->ExceptionCheck()) {
      g_env->ExceptionDescribe();
    }
    if (!wasAttached) {
      g_vm->DetachCurrentThread();
    }
  }

  virtual Awesomium::JSValue 
  OnMethodCallWithReturnValue(Awesomium::WebView* caller,
                              unsigned int remote_object_id,
                              const Awesomium::WebString& method_name,
                              const Awesomium::JSArray& args) 
  {
    JNIEnv *env = 0;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    jobject result = env->CallObjectMethod(target, 
                                           MOnMethodCallWithReturn,
                                           newString(env, method_name),
                                           fromJSValue(env, JSValue(args)));
    return JSValue();
  }
};

/*
 * Class:     org_f3_media_web_Browser
 * Method:    updateAll
 * Signature: ()V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_updateAll
(JNIEnv *env, jclass cls) {
  updateAll();
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    create
 * Signature: (II)I
 */
extern "C" jlong JNICALL Java_org_f3_media_web_awesomium_Browser_create
(JNIEnv *env, jobject none, jobject target, jint w, jint h) {
  ensureWebCore();
  initMethodIds(env);
  JavaVM *jvm = 0;
  env->GetJavaVM(&jvm);
  std::cout << "target => " << target << std::endl;
  MyWebViewListener *p = new MyWebViewListener(jvm, env->NewGlobalRef(target)); 
  p->setSize(w, h);
  return (jlong)p;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    create
 * Signature: (II)I
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_resize
(JNIEnv *env, jclass clazz, jlong handle, jint w, jint h) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->setSize(w, h);
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    destroy
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_destroy
(JNIEnv *env, jclass clazz, jlong handle) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  if (p->target != 0) {
    env->DeleteGlobalRef(p->target);
  }
  delete p;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getCursor
 * Signature: (I)I
 */
extern "C" jint JNICALL Java_org_f3_media_web_awesomium_Browser_getCursor
(JNIEnv *env, jclass clazz, jlong handle) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  return (jint)(long)p->cursorType;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    render
 * Signature: (ILjava/nio/ByteBuffer;II[I)V
 */
extern "C" jboolean JNICALL Java_org_f3_media_web_awesomium_Browser_render
(JNIEnv *env, jclass clazz, jlong handle, jobject buffer, jint rowSpan, jint depth, jintArray rectArray) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  if (p == 0 || p->webView == 0) {
    return 0;
  }
  unsigned char *bufPtr = (unsigned char *)env->GetDirectBufferAddress(buffer);
  if (p->resized) {
    p->webView->Resize(p->width, p->height);
  }
  //std::cout << "updating" << std::endl;
  updateAll();
  //std::cout << "done updating" << std::endl;
  Awesomium::BitmapSurface *s = (Awesomium::BitmapSurface*) p->webView->surface();
  //fprintf(stderr, "surface %p\n", s);
  //fprintf(stderr, "bufPtr %p\n", bufPtr);
  if (s == 0) {
    return 0;
  }
  if (s->is_dirty()) {
    p->resized = false;
    if (bufPtr != 0) {
      //fprintf(stderr, "surface %d, %d\n", s->width(), s->height());
      //fprintf(stderr, "p %d, %d\n", p->width, p->height);
      if (s->width() == p->width && s->height() == p->height) {
        unsigned char *outPtr = bufPtr;
        s->CopyTo(outPtr, s->row_span(), 4, true, true);
        jint arr[4];
        arr[0] = 0;
        arr[1] = 0;
        arr[2] = p->width;
        arr[3] = p->height;
        env->SetIntArrayRegion(rectArray, 0, 4, (const jint*)arr);
        s->set_is_dirty(false);
        return 1;
      }
    }
    /*
    Awesomium::Rect rect = p->webView->getDirtyBounds();
    const Awesomium::RenderBuffer *buffer = p->webView->render();
    if (buffer != 0) {
        if (buffer->width == p->width && buffer->height == p->height) {
          unsigned char *outPtr = bufPtr;
          unsigned char *buf = buffer->buffer;
          jint arr[4];
          if (0) {
            //fprintf(stderr, "rect %d %d %d %d\n", rect.x, rect.y, rect.width, rect.height);
            for (int i = rect.height-1; i >= 0; i--) {
              int row = rect.y + i;
              for (int j = 0; j < rect.width; j++) {
                int col = rect.x + j;
                unsigned char *p = buf + row * rowSpan + (col * depth);
                unsigned char b = *p++;
                unsigned char g = *p++;
                unsigned char r = *p++;
                unsigned char a = *p++;
                *outPtr++ = r;
                *outPtr++ = g;
                *outPtr++ = b;
                *outPtr++ = a;
              }
            }
            arr[0] = rect.x;
            arr[1] = rect.y;
            arr[2] = rect.width;
            arr[3] = rect.height;
          } else {
            for (int i = buffer->height-1; i >= 0; i--) {
              int row = i;
              for (int j = 0; j < buffer->width; j++) {
                int col = j;
                unsigned char *p = buf + row * rowSpan + (col * depth);
                unsigned char b = *p++;
                unsigned char g = *p++;
                unsigned char r = *p++;
                unsigned char a = *p++;
                *outPtr++ = r;
                *outPtr++ = g;
                *outPtr++ = b;
                *outPtr++ = a;
              }
            }
            arr[0] = 0;
            arr[1] = 0;
            arr[2] = buffer->width;
            arr[3] = buffer->height;
          }
          env->SetIntArrayRegion(rectArray, 0, 4, (const jint*)arr);
          return 1;
        } else {
          std::cerr << "buffer mismatch " << buffer->width << " " << buffer->height << std::endl;
        }
    } else {
      std::cerr << "buffer from render is null" << std::endl;
    }
    */
  }
  return 0;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectMouseDown
 * Signature: (II)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectMouseDown
(JNIEnv *env, jclass, jlong handle, jint button) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  Awesomium::MouseButton mb = button == 1 ? Awesomium::kMouseButton_Left :
    button == 2 ? Awesomium::kMouseButton_Middle : Awesomium::kMouseButton_Right;
  p->webView->InjectMouseDown(mb);
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectMouseUp
 * Signature: (II)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectMouseUp
(JNIEnv *, jclass, jlong handle, jint button) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  Awesomium::MouseButton mb = button == 1 ? Awesomium::kMouseButton_Left :
    button == 2 ? Awesomium::kMouseButton_Middle : Awesomium::kMouseButton_Right;
  p->webView->InjectMouseUp(mb);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectMouseMove
 * Signature: (III)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectMouseMove
(JNIEnv *, jclass, jlong handle, jint x, jint y) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->InjectMouseMove(x, y);
}

extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectMouseWheel
(JNIEnv *, jclass, jlong handle, jint x, jint y) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->InjectMouseWheel(x, y);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectKeyDown
 * Signature: (III)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectKeyDown
(JNIEnv *, jclass, jlong handle, jint mods, jint code) {
  Awesomium::WebKeyboardEvent e;
  e.type = Awesomium::WebKeyboardEvent::kTypeKeyDown;
  e.modifiers = mods;
  e.virtual_key_code = code;
  e.native_key_code = 0;
  e.text[0] = 0;
  e.unmodified_text[0] = 0;
  e.key_identifier[0] = 0;
  e.is_system_key = 0;
  //Awesomium::getKeyIdentifierFromVirtualKeyCode(e.virtualKeyCode, (char**)&e.keyIdentifier);
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->InjectKeyboardEvent(e);
  
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectKeyUp
 * Signature: (III)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectKeyUp
(JNIEnv *, jclass, jlong handle, jint mods, jint code) {
  Awesomium::WebKeyboardEvent e;
  e.type = Awesomium::WebKeyboardEvent::kTypeKeyUp;
  e.modifiers = mods;
  e.virtual_key_code = code;
  e.native_key_code = 0;
  e.text[0] = 0;
  e.unmodified_text[0] = 0;
  e.key_identifier[0] = 0;
  e.is_system_key = 0;
  //Awesomium::getKeyIdentifierFromVirtualKeyCode(e.virtualKeyCode, (char**)&e.keyIdentifier);
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->InjectKeyboardEvent(e);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectKeyInput
 * Signature: (IIILjava/lang/String;)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectKeyInput
(JNIEnv *env, jclass, jlong handle, jint mods, jint code, jchar ch) {
  Awesomium::WebKeyboardEvent e;
  e.type = Awesomium::WebKeyboardEvent::kTypeChar;
  e.modifiers = mods;
  e.virtual_key_code = code;
  e.native_key_code = 0;
  e.text[0] = ch;
  e.text[1] = 0;
  e.unmodified_text[0] = ch;
  e.unmodified_text[1] = 0;
  e.key_identifier[0] = 0;
  e.is_system_key = 0;
  //  Awesomium::getKeyIdentifierFromVirtualKeyCode(e.virtualKeyCode, (char**)e.keyIdentifier);
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->InjectKeyboardEvent(e);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    setURL
 * Signature: (ILjava/lang/String;)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_setURL
(JNIEnv *env, jclass clazz, jlong handle, jstring url) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->LoadURL(WebURL(toWebString(env, url)));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    setURL
 * Signature: (ILjava/lang/String;)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_setContent
(JNIEnv *env, jclass clazz, jlong handle, jstring url) {
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getURL
 * Signature: (I)Ljava/lang/String;
 */
extern "C" jstring JNICALL Java_org_f3_media_web_awesomium_Browser_getURL
(JNIEnv *env, jclass, jlong handle) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  return newString(env, p->currentURL);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    focus
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_focus
(JNIEnv *, jclass, jlong handle) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->Focus();
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    unfocus
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_unfocus
(JNIEnv *, jclass, jlong handle) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->Unfocus();
}



/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    zoomIn
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_setZoom
                                                                 (JNIEnv *, jclass, jlong handle, jint percent) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->SetZoom(percent);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    resetZoom
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_resetZoom
(JNIEnv *, jclass, jlong handle) {
  MyWebViewListener *p = (MyWebViewListener*)handle;
  p->webView->ResetZoom();
}


void initMethodIds(JNIEnv *env) {
  if (Integer != 0) return;
  jclass cls = env->FindClass("java/lang/Integer");
  jmethodID methodId = env->GetMethodID(cls, "<init>", "(I)V");
  cls = (jclass)env->NewGlobalRef(cls);
  Integer = cls;
  NewInteger = methodId;
  cls = env->FindClass("java/lang/Double");
  cls = (jclass)env->NewGlobalRef(cls);
  methodId = env->GetMethodID(cls, "<init>", "(D)V");
  NewDouble = methodId;
  Double = cls;
  cls = env->FindClass("java/lang/Boolean");
  cls = (jclass)env->NewGlobalRef(cls);
  methodId = env->GetMethodID(cls, "<init>", "(Z)V");
  NewBoolean = methodId;
  Boolean = cls;
  cls = env->FindClass("org/f3/media/web/awesomium/JSArray");
  cls = (jclass)env->NewGlobalRef(cls);
  methodId = env->GetMethodID(cls, "<init>", "(J)V");
  NewJSArray =  methodId;
  JSArrayClazz = cls;
  cls = env->FindClass("org/f3/media/web/awesomium/JSObject");
  cls = (jclass)env->NewGlobalRef(cls);
  fprintf(stderr, "cls=%p\n", cls);
  methodId = env->GetStaticMethodID(cls, "createFromHandle", "(J)Lorg/f3/media/web/awesomium/JSObject;");
  NewJSObject = methodId;
  JSObjectClazz = cls;
  cls = env->FindClass("org/f3/media/web/awesomium/Browser");
  BrowserClazz = (jclass)env->NewGlobalRef(cls);
  methodId = env->GetStaticMethodID(cls, "onMethodCall", "(Ljava/lang/Object;Ljava/lang/String;Lorg/f3/media/web/awesomium/JSArray;)V");
  MOnMethodCall = methodId;
  methodId = env->GetMethodID(cls, "onMethodCallWithReturn", "(Ljava/lang/String;Lorg/f3/media/web/awesomium/JSArray;)Ljava/lang/Object;");
  MOnMethodCallWithReturn = methodId;
}

static jobject newInteger(JNIEnv *env, jint value) {
  return env->NewObject(Integer, NewInteger, value);
}

static jobject newDouble(JNIEnv *env, jdouble value) {
  return env->NewObject(Double, NewDouble, value);
}

static jobject newBoolean(JNIEnv *env, jboolean value) {
  return env->NewObject(Boolean, NewBoolean, value);
}

#if 1

static jobject newJSArray(JNIEnv *env, const JSArray *value) {
  return env->NewObject(JSArrayClazz, NewJSArray, (long)value);
}

static jobject newJSObject(JNIEnv *env, const JSObject *value) {
  //fprintf(stderr, "newJSObject clss=%p meth=%p val=%p\n", JSObjectClazz, NewJSObject, value);
  return env->CallStaticObjectMethod(JSObjectClazz, NewJSObject, (long)value);
}

static jstring newString(JNIEnv *env, const WebString &str) {
    unsigned int size = str.ToUTF8(0, 0);
    char *chs = new char[size+1];
    chs[size] = 0;
    str.ToUTF8(chs, size);
    jstring result = env->NewStringUTF(chs);
    delete chs;
    return result;
}  

static WebString toWebString(JNIEnv *env, jstring value) {
  jboolean iscopy;
  const char *chs = env->GetStringUTFChars(value, &iscopy);
  //fprintf(stderr, "to web string %s\n", chs);
  WebString str = WebString::CreateFromUTF8(chs, strlen(chs));
  env->ReleaseStringUTFChars(value, (const char*)chs);
  return str;
}

static jobject fromJSValue(JNIEnv *env, const JSValue &v) {
    if (v.IsNull()) {
        return 0;
    } else if (v.IsBoolean()) {
        return newBoolean(env, v.ToBoolean());
    } else if (v.IsInteger()) {
        return newInteger(env, v.ToInteger());
    } else if (v.IsDouble()) {
        return newDouble(env, v.ToDouble());
    } else if (v.IsArray()) {
        return newJSArray(env, new JSArray(v.ToArray()));
    } else if (v.IsString()) {
        return newString(env, v.ToString());
    } else if (v.IsObject()) {
        return newJSObject(env, new JSObject(v.ToObject()));
    } 
    return 0;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    execute_js
 * Signature: (JLjava/lang/String;)J
 */
JNIEXPORT jobject JNICALL Java_org_f3_media_web_awesomium_Browser_execute_1js
  (JNIEnv *env, jclass, jlong h, jstring script)
{
  MyWebViewListener *l = (MyWebViewListener*)h;
  fprintf(stderr, "handle=%p\n", (void*)h);
  if (l->webView == 0) {
    return 0;
  }
  fprintf(stderr, "thread=%p\n", pthread_self());
  fprintf(stderr, "webview=%p\n", l->webView);
  fprintf(stderr, "script=%p\n", script);
  WebString str = toWebString(env, script);
  fprintf(stderr, "calling execute\n");
  JSValue value = l->webView->ExecuteJavascriptWithResult(str, WSLit(""));
  jobject result = fromJSValue(env, value);
  return result;
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    create_js_array
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_f3_media_web_awesomium_Browser_create_1js_1array
  (JNIEnv *, jclass)
{
  return (long) new JSArray(0);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    create_js_object
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_f3_media_web_awesomium_Browser_create_1js_1object
(JNIEnv *, jclass) {
    return (long) new JSObject();
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    destroy_js_array
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_destroy_1js_1array
  (JNIEnv *, jclass, jlong h)
{
    delete (JSArray*)h;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    destroy_js_object
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_destroy_1js_1object
(JNIEnv *, jclass, jlong h) {
    delete (JSObject*)h;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    invoke
 * Signature: (Ljava/lang/String;J)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_f3_media_web_awesomium_Browser_invoke
(JNIEnv *env, jclass, jlong h, jstring method, jlong a)
{
  JSObject *obj = (JSObject*)h;
  JSArray *arr = (JSArray*)a;
  WebString m = toWebString(env, method);
  JSValue value = obj->Invoke(m, *arr);
  jobject result = fromJSValue(env, value);
  return result;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getPropertyNames
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_f3_media_web_awesomium_Browser_getPropertyNames
  (JNIEnv *env, jclass cls, jlong h)
{
  JSObject *obj = (JSObject*)h;
  return (long)new JSArray(obj->GetPropertyNames());
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getMethodNames
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_f3_media_web_awesomium_Browser_getMethodNames
  (JNIEnv *env, jclass cls, jlong h)
{
  JSObject *obj = (JSObject*)h;
  return (long)(new JSArray(obj->GetMethodNames()));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    has
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_f3_media_web_awesomium_Browser_has
  (JNIEnv *env, jclass, jlong h, jstring index)
{
  JSObject *obj = (JSObject*)h;
  WebString str = toWebString(env, index);
  bool result = obj->HasProperty(str);
  return result;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    hasMethod
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_f3_media_web_awesomium_Browser_hasMethod
  (JNIEnv *env, jclass cls, jlong h, jstring index)
{
    JSObject *obj = (JSObject*)obj;
    WebString str = toWebString(env, index);
    bool result = obj->HasMethod(str);
    return result;
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_null
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1null
(JNIEnv *env, jclass cls, jlong h, jstring index) 
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue());
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_boolean
 * Signature: (JLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1boolean
(JNIEnv *env, jclass cls, jlong h, jstring index, jboolean value) 
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue(value));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_int
 * Signature: (JLjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1int
(JNIEnv *env, jclass cls, jlong h, jstring index, jint value) 
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue((int)value));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_double
 * Signature: (JLjava/lang/String;D)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1double
(JNIEnv *env, jclass cls, jlong h, jstring index, jdouble value) 
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue(value));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_string
 * Signature: (JLjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1string
  (JNIEnv *env, jclass cls, jlong h, jstring index, jstring value)
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue(toWebString(env, value)));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_object
 * Signature: (JLjava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1object
  (JNIEnv *env, jclass cls, jlong h, jstring index, jlong value)
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue((JSObject*)value));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_array
 * Signature: (JLjava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1array
  (JNIEnv *env, jclass cls, jlong h, jstring index, jlong value)
{
    JSObject * obj = (JSObject*)h;
    WebString str = toWebString(env, index);
    obj->SetProperty(str, JSValue(*(JSArray*)value));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    get
 * Signature: (JLjava/lang/String;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_f3_media_web_awesomium_Browser_get
  (JNIEnv *env, jclass cls, jlong h, jstring value)
{
    JSObject *obj = (JSObject*)h;
    WebString str = toWebString(env, value);
    jobject result = fromJSValue(env, obj->GetProperty(str));
    return result;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    get_element
 * Signature: (JI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_f3_media_web_awesomium_Browser_get_1element
  (JNIEnv *env, jclass cls, jlong h, jint index)
{
    JSArray *arr = (JSArray*)h;
    return fromJSValue(env, arr->At(index));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_null_element
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1null_1element
(JNIEnv *env, jclass cls, jlong h,  jint index)
{
    JSArray *arr = (JSArray*)h;
    while (arr->size() <= index) {
        arr->Push(JSValue());
    }
    (*arr)[index] = JSValue();
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_boolean_element
 * Signature: (JIZ)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1boolean_1element
  (JNIEnv *env, jclass cls, jlong h, jint index, jboolean v)
{
    JSArray *arr = (JSArray*)h;
    while (arr->size() <= index) {
        arr->Push(JSValue());
    }
    (*arr)[index] = JSValue(v);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_int_element
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1int_1element
  (JNIEnv *env, jclass cls, jlong h, jint index, jint v)
{
    JSArray *arr = (JSArray*)h;
    while (arr->size() <= index) {
        arr->Push(JSValue());
    }
    (*arr)[index] = JSValue((int)v);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_double_element
 * Signature: (JID)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1double_1element
  (JNIEnv *env, jclass cls, jlong h, jint index, jdouble v)
{
    JSArray *arr = (JSArray*)h;
    while (arr->size() <= index) {
        arr->Push(JSValue());
    }
    (*arr)[index] = JSValue(v);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_string_element
 * Signature: (JILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1string_1element
  (JNIEnv *env, jclass cls, jlong h, jint index, jstring value)
{
    JSArray *arr = (JSArray*)h;
    while (arr->size() <= index) {
        arr->Push(JSValue());
    }
    (*arr)[index] = JSValue(toWebString(env, value));
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_object_element
 * Signature: (JIJ)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1object_1element
  (JNIEnv *env, jclass cls, jlong h, jint index, jlong value)
{
    JSArray *arr = (JSArray*)h;
    while (arr->size() <= index) {
        arr->Push(JSValue());
    }
    (*arr)[index] = JSValue((JSObject*)value);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    put_array_element
 * Signature: (JIJ)V
 */
JNIEXPORT void JNICALL Java_org_f3_media_web_awesomium_Browser_put_1array_1element
  (JNIEnv *env, jclass cls, jlong h, jint index, jlong value)
{
  JSArray *arr = (JSArray*)h;
  JSArray *v = (JSArray*)value;
  while (arr->size() <= index) {
      arr->Push(JSValue());
  }
  (*arr)[index] = JSValue(*v);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_f3_media_web_awesomium_Browser_getSize
  (JNIEnv *env, jclass cls, jlong h)
{
  JSArray *arr = (JSArray*)h;
  return arr->size();
}


#endif

#endif
