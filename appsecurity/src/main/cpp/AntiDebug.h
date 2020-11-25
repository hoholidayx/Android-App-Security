//
// SO反调试
//

#ifndef APP_SECURITY_ANTIDEBUG_H
#define APP_SECURITY_ANTIDEBUG_H

#define DETECTED_RESULT_PTRACE 1

class DetectDebugCallback {
public:
    virtual void onDetected(int result) = 0;
};


class AntiDebug {
public:

    AntiDebug();

    virtual ~AntiDebug();

    /**
     * 启动线程循环检查ptrace
     */
    void start();

    /**
     * 设置检测回调
     * @param callback
     */
    void setDebugDetectiveCallback(DetectDebugCallback *callback);

    /**
     * 结束循环
     */
    void stop();

private:
    DetectDebugCallback *detectDebugCallback = nullptr;
};


#endif //APP_SECURITY_ANTIDEBUG_H
