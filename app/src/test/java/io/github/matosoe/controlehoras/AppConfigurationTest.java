package io.github.matosoe.controlehoras;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AppConfigurationTest {
    @Test
    public void usesDefinedApplicationId() {
        assertEquals("io.github.matosoe.controlehoras", BuildConfig.APPLICATION_ID);
    }
}
