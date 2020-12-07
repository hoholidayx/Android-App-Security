#ifndef HOHOLIDAY_APP_SECURITY_H
#define HOHOLIDAY_APP_SECURITY_H

#include <jni.h>
#include <string.h>

/**
 * 是否开发模式
 */
#define APP_DEBUG false

/**
 * 是否开启反调试开关
 */
#define ANTI_DEBUG_ENABLED true


/**
 * 退出码-不合法的签名
 */
#define EXIT_CODE_UNAUTH_SIGNATURE -101

/**
 * 退出码-反调试
 */
#define EXIT_CODE_ANTI_DEBUG -102


extern "C" {

const char *getAppSignatureMD5();

const char *md5(const char *content);
}


#endif //HOHOLIDAY_APP_SECURITY_H
