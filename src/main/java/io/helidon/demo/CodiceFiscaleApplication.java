package io.helidon.demo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Simple Application that produces an Italian tax code.
 */
@ApplicationScoped
@ApplicationPath("/")
public class CodiceFiscaleApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add(CodiceFiscaleResource.class);
        return Collections.unmodifiableSet(set);
    }
}
