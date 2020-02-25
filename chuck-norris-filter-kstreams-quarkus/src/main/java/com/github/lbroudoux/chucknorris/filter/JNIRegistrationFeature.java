package com.github.lbroudoux.chucknorris.filter;

import org.graalvm.nativeimage.hosted.Feature;
import org.rocksdb.RocksDBException;
import org.rocksdb.Status;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jni.JNIRuntimeAccess;

/**
 * Workaround for having Kafka streams run in native-mode.
 * See https://github.com/quarkusio/quarkus/issues/7066
 * @author laurent
 */
@AutomaticFeature
class JNIRegistrationFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        JNIRuntimeAccess.register(RocksDBException.class);
        JNIRuntimeAccess.register(RocksDBException.class.getConstructors());
        JNIRuntimeAccess.register(Status.class);
        JNIRuntimeAccess.register(Status.class.getDeclaredConstructors());
    }
}