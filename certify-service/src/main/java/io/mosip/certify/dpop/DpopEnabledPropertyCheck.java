package io.mosip.certify.dpop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Refuses to start unless {@code mosip.certify.dpop.enabled} is absent, {@code true} or
 * {@code false}, ignoring case.
 *
 * <p>{@link DpopProofValidator}'s {@code @ConditionalOnProperty} is a string match, so
 * {@code yes}, {@code on}, {@code 1} or a typo would quietly turn DPoP off. The check lives
 * here rather than in a filter because the filters are profile-scoped, and a bad value must
 * fail startup under every profile.
 */
@Component
public class DpopEnabledPropertyCheck {

    public DpopEnabledPropertyCheck(@Value("${mosip.certify.dpop.enabled:true}") String dpopEnabled) {
        if (!"true".equalsIgnoreCase(dpopEnabled) && !"false".equalsIgnoreCase(dpopEnabled)) {
            throw new IllegalStateException(
                    "mosip.certify.dpop.enabled must be true or false, but was '" + dpopEnabled + "'");
        }
    }
}
