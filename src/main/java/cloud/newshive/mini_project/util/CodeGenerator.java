package cloud.newshive.mini_project.util;

import java.security.SecureRandom;

public class CodeGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateCode() {
        int code = 100_000 + random.nextInt(900_000);
        return String.valueOf(code);
    }
}
