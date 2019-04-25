package com.github.dwolverton.consoletester.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({ TestCaseHeader.class, IOTesterParameterResolver.class })
/**
 * Use this annotation to set up the defaults for a JUnit 5 test with full feedback.
 */
public @interface GradingTest {
}
