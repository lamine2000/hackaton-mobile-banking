package sn.finedev.java.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static String ADMIN_NETWORK = "ROLE_ADMIN_NETWORK";

    public static String INTERMEDIATE_AGENT = "ROLE_INTERMEDIATE_AGENT";

    public static String AGENT = "ROLE_AGENT";

    private AuthoritiesConstants() {}
}
