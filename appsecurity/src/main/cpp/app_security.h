#ifndef HOHOLIDAY_APP_SECURITY_H
#define HOHOLIDAY_APP_SECURITY_H

#include <jni.h>
#include <string.h>


/**
 * 是否开启反调试开关，开发阶段默认关闭
 */
#define ANTI_DEBUG_ENABLED false


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
