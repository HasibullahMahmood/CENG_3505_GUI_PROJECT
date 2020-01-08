package org.com.example.dogal_sepeti;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignUpWithEmailActivityTest {

    @Test
    public void isEmpty() {
        SignUpWithEmailActivity obj = new SignUpWithEmailActivity();
        boolean actual = obj.isEmpty("Ahmed", "Ibrahim", "ahmed@gmail.com", "055555555", "12.12.1912", "123456", "male", "Mugla", "Kotekli");
        assertEquals(false, actual);
    }
}