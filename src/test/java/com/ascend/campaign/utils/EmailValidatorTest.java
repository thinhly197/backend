package com.ascend.campaign.utils;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmailValidatorTest {

    private EmailValidator validator;

    @Before
    public void setUp() {
        validator = new EmailValidator();
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValid() {
        assertThat(validator.isValid("a@test.com"), is(true));
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValidNameWithUnderscore() {
        assertThat(validator.isValid("a_b@test.com"), is(true));
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValidNameWithDot() {
        assertThat(validator.isValid("a.b@test.com"), is(true));
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValidNameWithDash() {
        assertThat(validator.isValid("a_b@test.com"), is(true));
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValidNameWithNumber() {
        assertThat(validator.isValid("a1@test.com"), is(true));
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValidDomainWithNumber() {
        assertThat(validator.isValid("a@1test.com"), is(true));
    }

    @Test
    public void shouldReturnTrueWhenEmailIsValidDomainWithDash() {
        assertThat(validator.isValid("a@test-abc.com"), is(true));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidWithNotContainAtSign() {
        assertThat(validator.isValid("acom"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidNameStartWithDot() {
        assertThat(validator.isValid(".a@test.com"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidWithNotName() {
        assertThat(validator.isValid("@a.com"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidDomainStartWithDot() {
        assertThat(validator.isValid("a@.com"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidWithLastDomainMustContainAtLeastTwoCharacters() {
        assertThat(validator.isValid("a@test.c"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidWithNameAllowCharDigitUnderscoreDash() {
        assertThat(validator.isValid("a()@test.com"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidWithDomainAllowCharDigitUnderscoreDash() {
        assertThat(validator.isValid("a@test().como"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidNameWithDoubleDot() {
        assertThat(validator.isValid("a..b@test.com"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidDomainWithDoubleDot() {
        assertThat(validator.isValid("a@test..com"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmailInvalidDomainWithDoubleAtSign() {
        assertThat(validator.isValid("a@test@mail.com"), is(false));
    }
}
