package hoholiday.app.lib.appsecurity.exception;

import androidx.annotation.Keep;

@Keep
public class AppSecurityException extends java.lang.SecurityException {

    public AppSecurityException() {
    }

    public AppSecurityException(String s) {
        super(s);
    }

    public AppSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppSecurityException(Throwable cause) {
        super(cause);
    }
}
