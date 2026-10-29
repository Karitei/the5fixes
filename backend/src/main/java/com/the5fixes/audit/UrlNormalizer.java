package com.the5fixes.audit;

import com.the5fixes.common.BadRequestException;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Locale;

public final class UrlNormalizer {

    public UrlNormalizer() {
        // Utility class
    }

    public static URI validateUrl(String url) {

        if (url == null || url.isBlank()) {
            throw new BadRequestException("A URL is required.");
        }

        url = url.trim();

        if (url.length() > 500) {
            throw new BadRequestException("URL is too long.");
        }

        try {
            // Reject explicitly supplied protocols other than HTTP/HTTPS
            if (url.matches("(?i)^[a-z][a-z0-9+.-]*:.*")
                    && !url.matches("(?i)^https?://.*")) {
                throw new BadRequestException(
                        "Only HTTP and HTTPS URLs are allowed"
                );
            }

            URI uri = new URI(url);

            String scheme = uri.getScheme();

            if (scheme!=null && (!(scheme.equalsIgnoreCase("http")
                            || scheme.equalsIgnoreCase("https")))) {
                throw new BadRequestException(
                        "Only HTTP and HTTPS URLs are allowed"
                );
            }

            // Validate host before DNS lookup
            String host = uri.getHost();

            if (host == null || host.isBlank()) {
                throw new BadRequestException(
                        "Not a valid URL: " + url
                );
            }

            // Reject URLs containing credentials
            if (uri.getRawUserInfo() != null) {
                throw new BadRequestException(
                        "URLs must not contain credentials"
                );
            }

            // Validate port
            int port = uri.getPort();

            if (port != -1 && (port < 1 || port > 65535)) {
                throw new BadRequestException("Invalid port.");
            }

            // Check resolved IP addresses
            try {
                InetAddress[] addresses = InetAddress.getAllByName(host);

                for (InetAddress addr : addresses) {
                    if (addr.isLoopbackAddress()
                            || addr.isSiteLocalAddress()
                            || addr.isAnyLocalAddress()
                            || addr.isLinkLocalAddress()
                            || addr.isMulticastAddress()) {
                        throw new BadRequestException(
                                "Local or private addresses are not allowed"
                        );
                    }
                }
            } catch (UnknownHostException e) {
                throw new BadRequestException("Invalid host");
            }

            return uri;

        } catch (URISyntaxException e) {
            throw new BadRequestException("Malformed URL");
        }
    }

    public static String normalize(String input) throws BadRequestException {

        boolean hasScheme = input.matches("(?i)^https?://.*");

        if (!hasScheme) {
            input = "https://" + input;
        }

        URI uri = validateUrl(input);

        String host = uri.getHost().toLowerCase(Locale.ROOT);
        String path = uri.getPath() == null ? "" : uri.getPath();
        int port = uri.getPort();

        if(path.endsWith("/")){
            path = path.substring(0,path.length()-1);
        }

        String authority = host;
        if (port != -1 && port != 443 && port != 80) {
            throw new BadRequestException("Port not authorised");
        }

        if(hasScheme){
            String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
            return scheme+"://" + authority +path;
        } else{
            return  authority +path;
        }


    }
}
