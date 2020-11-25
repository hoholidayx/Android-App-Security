#ifndef APP_SECURITY_ANDROID_LOG_H
#define APP_SECURITY_ANDROID_LOG_H

#include <android/log.h>

/**
 * 是否开启ANDROID LOG
 * 1=enable
 */
#define LOG_ENABLE 1

#if(LOG_ENABLE == 1)

#ifndef LOG_TAG
#define LOG_TAG "APP_SEC"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__)
#endif

#else
#define LOG_TAG "APP_SEC"
#define LOGD(...) NULL
#define LOGI(...) NULL
#define LOGW(...) NULL
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)
#define LOGF(...) NULL
#endif

#endif //APP_SECURITY_ANDROID_LOG_H
