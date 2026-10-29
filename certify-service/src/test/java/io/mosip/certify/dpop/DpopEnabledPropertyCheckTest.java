package io.mosip.certify.dpop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DpopEnabledPropertyCheckTest {

    // No profile is active, as under the local profile: the check must not depend on one.
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(DpopEnabledPropertyCheck.class);

    @ParameterizedTest
    @ValueSource(strings = {"yes", "on", "1", "ture", "", "true "})
    void should_failStartup_when_dpopEnabledIsNeitherTrueNorFalse(String value) {
        // Each of these would silently disable DPoP through the validator's string match.
        withDpopEnabled(value)
                .run(context -> assertThat(context).hasFailed()
                        .getFailure()
                        .rootCause()
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("mosip.certify.dpop.enabled must be true or false, but was '" + value + "'"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "TRUE", "false", "False"})
    void should_start_when_dpopEnabledIsTrueOrFalseInAnyCase(String value) {
        // @ConditionalOnProperty ignores case too, so these all pick the mode they name.
        withDpopEnabled(value)
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void should_start_when_dpopEnabledIsUnset() {
        contextRunner.run(context -> assertThat(context).hasNotFailed());
    }

    /**
     * Sets the value verbatim, as a properties file does. withPropertyValues trims it, which
     * would turn "true " into "true", while Spring Boot keeps the trailing space from a file.
     */
    private ApplicationContextRunner withDpopEnabled(String value) {
        return contextRunner.withInitializer(context -> context.getEnvironment().getPropertySources()
                .addFirst(new MapPropertySource("test", Map.of("mosip.certify.dpop.enabled", value))));
    }
}
