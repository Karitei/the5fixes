package com.the5fixes.audit;

import com.the5fixes.common.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlNormalizerTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost:3000",
            "http://127.0.0.1",
            "http://[::1]/",
            "http://169.254.169.254/latest/meta-data/",
            "http://10.0.0.5/admin",
            "http://192.168.1.1",
            "http://172.16.0.1",
            "http://localtest.me",
            "file:///C:/Windows/win.ini",
            "ftp://exemple.com",
            "http://user:pass@exemple.com"
    })
    void shouldRejectInvalidUrls(String url) {
        assertThrows(
                BadRequestException.class,
                () -> UrlNormalizer.normalize(url)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.mozilla.org, https://www.mozilla.org",
            "http://www.mozilla.org, http://www.mozilla.org",
            "mozilla.org , mozilla.org",
            "https://www.mozilla.org/, https://www.mozilla.org",
            "HTTPS://WWW.MOZILLA.ORG , https://www.mozilla.org",
            "https://mozilla.org/en-US/ , https://mozilla.org/en-US"

    })
    void souldNormlize(String input, String expected){
        String actual = UrlNormalizer.normalize(input);
        //Check
        assertEquals(expected, actual);
    }
    @Test
    void shouldAcceptUrl(){
        String input = "mozilla.org";
        String expected = "mozilla.org";

        String actual = UrlNormalizer.normalize(input);
        //Check
        assertEquals(expected, actual);
    }

    @Test
    void shouldRefuseUrl(){
        String input = "http://169.254.169.254/latest/meta-data/";

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> UrlNormalizer.normalize("http://169.254.169.254/latest/meta-data/")
        );
        assertEquals("Local or private addresses are not allowed", ex.getMessage());
    }
    @Test
    void shouldRejectEmptyUrl() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> UrlNormalizer.normalize("")
        );
        assertEquals("Malformed URL", ex.getMessage());
    }

}
