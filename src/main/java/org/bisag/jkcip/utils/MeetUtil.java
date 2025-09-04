package org.bisag.jkcip.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class MeetUtil {

    private static final SecureRandom rnd = new SecureRandom();

    public static String generateMeetLink() {
        // Generate a URL-safe random token and inject into a lookup-style Google Meet
        byte[] bytes = new byte[8];
        rnd.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        // A lookup link format; note: not guaranteed to create a real meeting
        return "https://meet.google.com/lookup/" + token;
    }
}
