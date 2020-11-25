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
    //6c08fe5d22aa9801fdd2168895afc35f,使用混淆:https://zerosum0x0.blogspot.com/2017/08/obfuscatedencrypted-cc-online-string.html
    unsigned char s[] = {
            0xdf, 0x41, 0xcf, 0x39, 0x37, 0x8e, 0x3, 0x92,
            0x4f, 0x6d, 0xb8, 0x4, 0x5b, 0x65, 0xd4, 0xe6,
            0xc1, 0xf7, 0xe1, 0x2b, 0x2d, 0x27, 0x86, 0x60,
            0xb6, 0x88, 0x9b, 0x61, 0x35, 0x8, 0x22, 0xba,
            0x7a};

    for (unsigned int m = 0; m < sizeof(s); ++m) {
        unsigned char c = s[m];
        c = ~c;
        c ^= m;
        c += 0xe5;
        c ^= m;
        c -= 0x66;
        c = (c >> 0x3) | (c << 0x5);
        c += m;
        c ^= 0x1b;
        c -= m;
        c ^= 0x2b;
        c = -c;
        c = (c >> 0x7) | (c << 0x1);
        c = ~c;
        c -= 0x4f;
        c ^= m;
        s[m] = c;
    }
    std::string signStr = std::string((char *) s);
    if (signStr != appSignatureMd5) {
        int exitCode = EXIT_CODE_UNAUTH_SIGNATURE;
        LOGE("[nativeInit] init failed!exit:%d", exitCode);
        std::string msg = "安全校验失败!code=" + to_string(exitCode);
        throwJavaRuntimeException(env, msg.c_str());
    }

    delete g_antiDebug;
    //开始监听ptrace
    g_antiDebug = new AntiDebug();
    g_antiDebug->setDebugDetectiveCallback(new DetectDebugCallbackImpl());
    g_antiDebug->start();
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