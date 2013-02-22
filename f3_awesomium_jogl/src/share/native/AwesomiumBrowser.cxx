#if defined(__WIN32__) || defined(_WIN32)
#include "org_f3_media_web_awesomium_Browser.h"
#include <Awesomium/awesomium_capi.h>
#include <stdio.h>
#include <string.h>
#if defined(__WIN32__) || defined(_WIN32)
#include <windows.h>
#elif defined(__APPLE__)
#include <unistd.h>
#endif

static int initialized = 0;
static void init() {
  if (!initialized) {
    initialized = 1;
    // Create our WebCore singleton with the default options
    awe_webcore_initialize_default();
  }
}

static void cursor_change(awe_webview *webview,
                          awe_cursor_type cursor) {
}

static awe_webview *createWebView(int width, int height) {
  // Create a new WebView instance with a certain width and height, using the 
  // WebCore we just created
  init();
  awe_webview* webView = awe_webcore_create_webview(width, height, false);
  awe_webview_set_callback_change_cursor(webView, &cursor_change);
  return webView;
}  

/*
 * Class:     org_f3_media_web_Browser
 * Method:    updateAll
 * Signature: ()V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_updateAll
(JNIEnv *env, jclass cls) {
  awe_webcore_update();
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    create
 * Signature: (II)I
 */
extern "C" jlong JNICALL Java_org_f3_media_web_awesomium_Browser_create
(JNIEnv *env, jclass clazz, jint w, jint h) {

  awe_webview *p = createWebView(w, h);
  return (jlong)p;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    create
 * Signature: (II)I
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_resize
(JNIEnv *env, jclass clazz, jlong handle, jint w, jint h) {
  awe_webview * p = (awe_webview*)handle;
  awe_webview_resize(p, w, h, 0, 0);
}


/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    destroy
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_destroy
(JNIEnv *env, jclass clazz, jlong handle) {
  awe_webview * p = (awe_webview*)handle;
  awe_webview_destroy(p);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getCursor
 * Signature: (I)I
 */
extern "C" jint JNICALL Java_org_f3_media_web_awesomium_Browser_getCursor
(JNIEnv *env, jclass clazz, jlong handle) {
  awe_webview *p = (awe_webview*)handle;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    render
 * Signature: (ILjava/nio/ByteBuffer;II[I)V
 */
extern "C" jboolean JNICALL Java_org_f3_media_web_awesomium_Browser_render
(JNIEnv *env, jclass clazz, jlong handle, jobject buffer, jint rowSpan, jint depth, jintArray rectArray) {
  awe_webview *p = (awe_webview*)handle;
  awe_webcore_update();
  if (awe_webview_is_dirty(p)) {
    unsigned char *bufPtr = (unsigned char *)(env)->GetDirectBufferAddress(buffer);
    awe_rect rect = awe_webview_get_dirty_bounds(p);
    const awe_renderbuffer *renderbuf = awe_webview_render(p);
    jint arr[4];
    awe_renderbuffer_copy_to(renderbuf, bufPtr, rowSpan, depth, 1, 1);
    arr[0] = rect.x;
    arr[1] = rect.y;
    arr[2] = rect.width;
    arr[3] = rect.height;
    (env)->SetIntArrayRegion(rectArray, 0, 4, (const jint*)arr);
    return 1;
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
  awe_webview *p = (awe_webview*)handle;
  awe_mousebutton mb = button == 1 ? AWE_MB_LEFT : button == 2 ? AWE_MB_MIDDLE : AWE_MB_RIGHT;
  awe_webview_inject_mouse_down(p, mb);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectMouseUp
 * Signature: (II)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectMouseUp
(JNIEnv *, jclass, jlong handle, jint button) {
  awe_webview *p = (awe_webview*)handle;
  awe_mousebutton mb = button == 1 ? AWE_MB_LEFT : button == 2 ? AWE_MB_MIDDLE : AWE_MB_RIGHT;
  awe_webview_inject_mouse_up(p, mb);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectMouseMove
 * Signature: (III)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectMouseMove
(JNIEnv *, jclass, jlong handle, jint x, jint y) {
  awe_webview *p = (awe_webview*)handle;
  awe_webview_inject_mouse_move(p, x,  y);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectKeyDown
 * Signature: (III)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectKeyDown
(JNIEnv *, jclass, jlong handle, jint mods, jint code) {
  awe_webview *p = (awe_webview*)handle;
  awe_webkeyboardevent e;
  e.type = AWE_WKT_KEYDOWN;
  e.modifiers = mods;
  e.virtual_key_code = code;
  e.native_key_code = code;
  e.text[0] = 0;
  e.unmodified_text[0] = 0;
  e.is_system_key = 0;
  awe_webview_inject_keyboard_event(p, e);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectKeyUp
 * Signature: (III)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectKeyUp
(JNIEnv *, jclass, jlong handle, jint mods, jint code) {
  awe_webview *p = (awe_webview*)handle;
  awe_webkeyboardevent e;
  e.type = AWE_WKT_KEYUP;
  e.modifiers = mods;
  e.virtual_key_code = code;
  e.native_key_code = code;
  e.text[0] = 0;
  e.unmodified_text[0] = 0;
  e.is_system_key = 0;
  awe_webview_inject_keyboard_event(p, e);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    injectKeyInput
 * Signature: (IIILjava/lang/String;)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_injectKeyInput
(JNIEnv *env, jclass, jlong handle, jint mods, jint code, jchar ch) {
  awe_webview *p = (awe_webview*)handle;
  awe_webkeyboardevent e;
  e.type = AWE_WKT_CHAR;
  e.modifiers = mods;
  e.virtual_key_code = code;
  e.native_key_code = code;
  e.text[0] = ch;
  e.text[1] = 0;
  e.unmodified_text[0] = ch;
  e.unmodified_text[1] = 0;
  e.is_system_key = 0;
  awe_webview_inject_keyboard_event(p, e);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    setURL
 * Signature: (ILjava/lang/String;)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_setURL
(JNIEnv *env, jclass clazz, jlong handle, jstring url) {
  awe_webview *p = (awe_webview*)handle;
  jboolean iscopy;
  const char *chs = (env)->GetStringUTFChars(url, &iscopy);
  awe_string* url_str = awe_string_create_from_utf8(chs, strlen(chs));
  awe_webview_load_url(p, url_str, awe_string_empty(), 
                       awe_string_empty(), awe_string_empty());
  env->ReleaseStringChars(url, (const jchar*)chs);
  awe_string_destroy(url_str);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    getURL
 * Signature: (I)Ljava/lang/String;
 */
extern "C" jstring JNICALL Java_org_f3_media_web_awesomium_Browser_getURL
(JNIEnv *env, jclass, jlong handle) {
  awe_webview *p = (awe_webview*)handle;
  awe_string *url_str = awe_webview_get_url(p);
  int size = awe_string_to_utf8(url_str, NULL, 0);
  char *tmp = (char*)malloc(size+1);
  jstring result;
  awe_string_to_utf8(url_str, tmp, size);
  result = (env)->NewStringUTF(tmp);
  free(tmp);
  awe_string_destroy(url_str);
  return result;
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    focus
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_focus
(JNIEnv *, jclass, jlong handle) {
  awe_webview *p = (awe_webview*)handle;
  awe_webview_focus(p);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    unfocus
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_unfocus
(JNIEnv *, jclass, jlong handle) {
  awe_webview *p = (awe_webview*)handle;
  awe_webview_unfocus(p);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    zoomIn
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_setZoom
                                                                 (JNIEnv *, jclass, jlong handle, jint percent) {
  awe_webview *p = (awe_webview*)handle;
  awe_webview_set_zoom(p, percent);
}

/*
 * Class:     org_f3_media_web_awesomium_Browser
 * Method:    resetZoom
 * Signature: (I)V
 */
extern "C" void JNICALL Java_org_f3_media_web_awesomium_Browser_resetZoom
(JNIEnv *, jclass, jlong handle) {
  awe_webview *p = (awe_webview*)handle;
  awe_webview_reset_zoom(p);
}
#endif
