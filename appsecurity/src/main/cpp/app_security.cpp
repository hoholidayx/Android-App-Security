#include "app_security.h"
#include "utils/android_log.h"
#include "utils/md5.h"
#include "AntiDebug.h"


static JavaVM *g_jvm;

static AntiDebug *g_antiDebug;


static JNIEnv *getCurrentJNIEnv() {
    JNIEnv *env = nullptr;
    g_jvm->AttachCurrentThread(&env, nullptr);
    return env;
}

static void throwJavaRuntimeException(JNIEnv *env, const char *message) {
    LOGE("throw exception to java. %s", message);
    //加载自定义异常类
    jclass appSecurityExceptionClass = env->FindClass(
            "hoholiday/app/lib/appsecurity/exception/AppSecurityException");
    env->ThrowNew(appSecurityExceptionClass, message);
    env->DeleteLocalRef(appSecurityExceptionClass);
}

static void javaRuntimeExit(int exitCode) {
    JNIEnv *env = getCurrentJNIEnv();
    jclass runtimeClass = env->FindClass("java/lang/Runtime");
    jmethodID getRuntimeMethod = env->GetStaticMethodID(runtimeClass, "getRuntime",
                                                        "()Ljava/lang/Runtime;");
    jmethodID exitMethod = env->GetMethodID(runtimeClass, "exit", "(I)V");
    jobject runtimeObj = env->CallStaticObjectMethod(runtimeClass, getRuntimeMethod);

    env->DeleteLocalRef(runtimeClass);
    //Runtime.getRuntime().exit(int);
    env->CallVoidMethod(runtimeObj, exitMethod, exitCode);
    env->DeleteLocalRef(runtimeObj);
}


/**
 * 获取 app context
 * @param env
 * @return
 */
static jobject getGlobalContext(JNIEnv *env) {
    // Gets an instance of an object Activity Thread
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread,
                                                             "currentActivityThread",
                                                             "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    // Get the Application, which is a global Context
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication",
                                                "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(at, getApplication);

    env->DeleteLocalRef(at);
    env->DeleteLocalRef(activityThread);

    return context;
}

/**
 * 获取App签名
 * @param appContext
 */
static const char *getAppSignature(JNIEnv *env, jobject appContext) {
    // context
    jclass native_context = env->GetObjectClass(appContext);

    // context.getPackageManager()
    jmethodID methodID_func = env->GetMethodID(native_context, "getPackageManager",
                                               "()Landroid/content/pm/PackageManager;");
    jobject package_manager = env->CallObjectMethod(appContext, methodID_func);
    jclass pm_clazz = env->GetObjectClass(package_manager);

    //packageManager.getPackageInfo()
    jmethodID methodId_pm = env->GetMethodID(pm_clazz,
                                             "getPackageInfo",
                                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");

    //context.getPackageName()
    jmethodID methodID_packagename = env->GetMethodID(native_context, "getPackageName",
                                                      "()Ljava/lang/String;");
    jstring name_str = static_cast<jstring>(env->CallObjectMethod(appContext,
                                                                  methodID_packagename));
    jobject package_info = env->CallObjectMethod(package_manager, methodId_pm, name_str, 64);
    jclass pi_clazz = env->GetObjectClass(package_info);

    //packageInfo.signatures
    jfieldID fieldID_signatures = env->GetFieldID(pi_clazz, "signatures",
                                                  "[Landroid/content/pm/Signature;");
    jobject signatur = env->GetObjectField(package_info, fieldID_signatures);
    jobjectArray signatures = reinterpret_cast<jobjectArray>(signatur);

    //signatures[0]
    jobject signature = env->GetObjectArrayElement(signatures, 0);
    jclass s_clazz = env->GetObjectClass(signature);

    //signatures[0].toCharString()
    jmethodID methodId_ts = env->GetMethodID(s_clazz, "toCharsString", "()Ljava/lang/String;");
    jobject ts = env->CallObjectMethod(signature, methodId_ts);

    //return signature
    jstring appSignature = reinterpret_cast<jstring>(ts);
    const char *appSignatureChars = env->GetStringUTFChars(appSignature, 0);
    env->DeleteLocalRef(appSignature);
    return appSignatureChars;
}


/**
 * AntiDebug检测回调类
 */
class DetectDebugCallbackImpl : public DetectDebugCallback {

    void onDetected(int result) override {
        LOGE("NATIVE SO IS BEING TRACED!");
        if (g_antiDebug != nullptr) {
            g_antiDebug->stop();
        }
        int exitCode = EXIT_CODE_ANTI_DEBUG;
        LOGE("安全校验失败!code=%d", exitCode);
        //直接退出
        javaRuntimeExit(exitCode);
    }
};


const char *getAppSignatureMD5() {
    JNIEnv *env = getCurrentJNIEnv();
    jobject appContext = getGlobalContext(env);
    const char *appSignatureChars = getAppSignature(env, appContext);
    MD5 md5(appSignatureChars);
    return md5.getDigest().c_str();
}

const char *md5(const char *content) {
    MD5 md5(content);
    return md5.getDigest().c_str();
}

/**
 * 初始化
 */
void nativeInit(JNIEnv *env, jobject jobj) {
    LOGI("[nativeInit]");
    //检查APP签名
    std::string appSignatureMd5 = std::string((char *) getAppSignatureMD5());
    LOGI("[nativeInit] get app signature : %s", appSignatureMd5.c_str());

    //075aa687f44a253d6abb9994d466c487,使用混淆:https://zerosum0x0.blogspot.com/2017/08/obfuscatedencrypted-cc-online-string.html
    unsigned char s[] = {
            0x1f, 0x1d, 0x15, 0x79, 0x75, 0x7, 0x37, 0x5,
            0x5b, 0xf2, 0xee, 0x59, 0xf2, 0xec, 0x11, 0x3b,
            0xda, 0x41, 0x3b, 0x37, 0x5, 0x1, 0x0, 0xba,
            0x17, 0xb2, 0xb2, 0xae, 0x39, 0xa2, 0xd6, 0xa8,
            0xbe};

    for (unsigned int m = 0; m < sizeof(s); ++m) {
        unsigned char c = s[m];
        c += m;
        c = -c;
        c += m;
        c = (c >> 0x2) | (c << 0x6);
        c = ~c;
        c += m;
        c = (c >> 0x7) | (c << 0x1);
        c += 0x29;
        c = ~c;
        c += 0xed;
        c = -c;
        c += 0xc8;
        c ^= 0x9;
        c += 0x77;
        c ^= 0xa4;
        s[m] = c;
    }
    std::string signStr = std::string((char *) s);

    if (signStr != appSignatureMd5) {
        int exitCode = EXIT_CODE_UNAUTH_SIGNATURE;
        LOGE("[nativeInit] init failed!exit:%d", exitCode);
        std::string msg = "安全校验失败!code=" + to_string(exitCode);
        throwJavaRuntimeException(env, msg.c_str());
    }

    if (ANTI_DEBUG_ENABLED) {
        LOGD("Anti debug is enabled!");
        //开启反调试
        delete g_antiDebug;
        //开始监听ptrace
        g_antiDebug = new AntiDebug();
        g_antiDebug->setDebugDetectiveCallback(new DetectDebugCallbackImpl());
        g_antiDebug->start();
    } else {
        LOGD("Anti debug is disabled!");
    }
}


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {

    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    g_jvm = vm;

    // Find your class
    jclass c = env->FindClass("hoholiday/app/lib/appsecurity/JNI");
    if (c == nullptr) return JNI_ERR;

    // Register native methods.
    static const JNINativeMethod methods[] = {
            {"nativeInit", "()V", reinterpret_cast<void *>(nativeInit)},
    };
    int rc = env->RegisterNatives(c, methods, sizeof(methods) / sizeof(JNINativeMethod));

    env->DeleteLocalRef(c);

    if (rc != JNI_OK) return rc;

    return JNI_VERSION_1_6;
}


JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    if (g_antiDebug != nullptr) {
        g_antiDebug->stop();
        delete g_antiDebug;
    }

    g_jvm = nullptr;
}