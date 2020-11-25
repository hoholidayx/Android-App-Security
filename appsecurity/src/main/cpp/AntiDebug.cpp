//.
//

#include "AntiDebug.h"
#include "utils/android_log.h"
#include <pthread.h>
#include <unistd.h>
#include <cstdio>
#include <cstdlib>
#include <sys/ptrace.h>
#include <string>

static DetectDebugCallback *g_detectDebugCallback = nullptr;

static bool g_stopLoop = false;

/**
 * 获取TracePid
 * @param str
 * @return
 */
static int get_number_for_str(char *str) {
    if (str == nullptr) {
        return -1;
    }
    char result[20];
    int count = 0;
    while (*str != '\0') {
        if (*str >= 48 && *str <= 57) {
            result[count] = *str;
            count++;
        }
        str++;
    }
    int val = atoi(result);
    return val;
}

/**
 * 开启循环轮训检查TracePid字段
 * @param argv
 * @return
 */

static void *thread_function(void *argv) {
    int pid = getpid();
    char file_name[20] = {'\0'};
    sprintf(file_name, "/proc/%d/status", pid);
    char linestr[256];
    int i = 0, traceid;
    FILE *fp;
    while (!g_stopLoop) {
        i = 0;
        fp = fopen(file_name, "r");
        if (fp == nullptr) {
            break;
        }
        while (!feof(fp)) {
            fgets(linestr, 256, fp);
            if (i == 5) {
                traceid = get_number_for_str(linestr);
                if (traceid > 1000 && g_detectDebugCallback != nullptr) {
                    //华为P9会主动给app附加一个进程，暂且认为小于1000的是系统的
                    LOGI("Detect traceId = %d", traceid);
                    g_detectDebugCallback->onDetected(DETECTED_RESULT_PTRACE);
                }
                break;
            }
            i++;
        }
        fclose(fp);
        sleep(5);
    }
    return ((void *) 0);
}

static void create_thread_check_traceid() {
    pthread_t t_id;
    int err = pthread_create(&t_id, nullptr, thread_function, nullptr);
    if (err != 0) {
        LOGE("create thread fail: %s", strerror(err));
    }
}


void AntiDebug::start() {
    LOGI("begin tracing traceId...");
    g_stopLoop = false;
    create_thread_check_traceid();
}

void AntiDebug::setDebugDetectiveCallback(DetectDebugCallback *callback) {
    if (detectDebugCallback != nullptr) {
        delete detectDebugCallback;
    }
    detectDebugCallback = callback;
    g_detectDebugCallback = detectDebugCallback;
}

void AntiDebug::stop() {
    g_stopLoop = true;
}

AntiDebug::AntiDebug() {

}

AntiDebug::~AntiDebug() {
    if (detectDebugCallback != nullptr) {
        delete detectDebugCallback;
        detectDebugCallback = nullptr;
        g_detectDebugCallback = nullptr;
    }
    stop();
}

