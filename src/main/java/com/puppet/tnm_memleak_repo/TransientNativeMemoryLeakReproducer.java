package com.puppet.tnm_memleak_repo;

import jnr.ffi.provider.jffi.NativeRuntime;
import jnr.ffi.provider.jffi.TransientNativeMemory;

import java.lang.reflect.Field;
import java.util.Map;

public class TransientNativeMemoryLeakReproducer {
    public static void main(String[] args) throws Throwable {
        Field referenceSetField = TransientNativeMemory.class.getDeclaredField("referenceSet");
        referenceSetField.setAccessible(true);
        Map<Object, Boolean> referenceSet =
                (Map<Object, Boolean>) referenceSetField.get(TransientNativeMemory.class);
        final NativeRuntime runtime = NativeRuntime.getInstance();

        for (;;) {
            Thread t = new Thread() {
                public void run() {
                    TransientNativeMemory.allocate(runtime, 2, 8, true);
                }
            };
            t.start();
            t.join();
            System.out.println ("referenceSet count: " + referenceSet.size());
        }
    }
}
