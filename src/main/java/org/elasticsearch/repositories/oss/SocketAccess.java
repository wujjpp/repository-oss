package org.elasticsearch.repositories.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;

import org.elasticsearch.SpecialPermission;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public final class SocketAccess {

    private SocketAccess() {}

    public static <T> T doPrivilegedIOException(PrivilegedExceptionAction<T> operation) throws IOException {
        SpecialPermission.check();
        try {
            return AccessController.doPrivileged(operation);
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static <T> T doPrivilegedException(PrivilegedExceptionAction<T> operation) throws OSSException {
        SpecialPermission.check();
        try {
            return AccessController.doPrivileged(operation);
        } catch (PrivilegedActionException e) {
            throw (OSSException) e.getCause();
        }
    }

    public static void doPrivilegedVoidException(StorageRunnable action) throws OSSException, ClientException {
        SpecialPermission.check();
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
                action.executeCouldThrow();
                return null;
            });
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof OSSException) {
                throw (OSSException) cause;
            } else {
                throw (ClientException) cause;
            }
        }
    }

    @FunctionalInterface
    public interface StorageRunnable {
        void executeCouldThrow() throws OSSException, URISyntaxException, IOException;
    }

}