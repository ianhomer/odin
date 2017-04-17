package com.purplepip.odin.common;

import com.purplepip.odin.midi.MidiSystemHelper;
import com.sun.media.sound.MidiOutDeviceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple bean utilities.
 */
public class BeanUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSystemHelper.class);

    public void dumpStaticMethodResponse(Class clazz, String methodName) {
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            LOG.info("{}.{} {} ", clazz.getSimpleName(), methodName, method.invoke(null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Cannot invoke " + methodName + " on " + clazz.getName(), e);
        } catch (NoSuchMethodException e) {
            LOG.debug("No such method {} on {}", methodName, clazz.getName());
        }
    }
}
