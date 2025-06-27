package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.CTxT;
import java.util.HashMap;
import java.util.Map;

public record ActionResult(
    boolean success,
    CTxT message,
    Integer code,
    Map<String, String> extraSettings
) {

    /**
     * Constructor for a simple ActionResult with no extra settings or code
     * @param success if the result was a success or not
     * @param message the message to report back
     */
    public ActionResult(boolean success, CTxT message) {
        this(success, message, null, Map.of());
    }

    /**
     * Constructor for an ActionResult with a map of extra settings and code
     * @param success if the result was a success or not
     * @param message the message to report back
     * @param code optional code for identifying error/success
     * @param extraSettings a map of extra settings to include in the result
     */
    public ActionResult(boolean success, CTxT message, Integer code, Map<String, String> extraSettings) {
        if (message == null) throw new NullPointerException("message cannot be null!");
        this.success = success;
        this.message = message;
        this.code = code;
        this.extraSettings = extraSettings != null ? Map.copyOf(extraSettings) : Map.of();
    }

    /**
     * Constructor for an ActionResult with a map of extra settings, no code
     * @param success if the result was a success or not
     * @param message the message to report back
     * @param extraSettings a map of extra settings to include in the result
     */
    public ActionResult(boolean success, CTxT message, Map<String, String> extraSettings) {
        this(success, message, null, extraSettings);
    }

    /**
     * Constructor for an ActionResult with code, no extra settings
     * @param success if the result was a success or not
     * @param message the message to report back
     * @param code optional code for identifying error/success
     */
    public ActionResult(boolean success, CTxT message, Integer code) {
        this(success, message, code, Map.of());
    }

    /**
     * Constructor for an ActionResult with extra settings as individual key-value pairs, no code
     * @param success if the result was a success or not
     * @param message the message to report back
     * @param extraKeyPair alternating keys and values for settings (must be even number of arguments)
     */
    public ActionResult(boolean success, CTxT message, String... extraKeyPair) {
        this(success, message, null, convertToMap(extraKeyPair));
    }

    /**
     * Constructor for an ActionResult with code and extra settings as individual key-value pairs
     * @param success if the result was a success or not
     * @param message the message to report back
     * @param code optional code for identifying error/success
     * @param extraKeyPair alternating keys and values for settings (must be even number of arguments)
     */
    public ActionResult(boolean success, CTxT message, Integer code, String... extraKeyPair) {
        this(success, message, code, convertToMap(extraKeyPair));
    }

    /**
     * gets a message for chat, which includes a tag in front of the message and an error tag if not a success
     * @return the message to display in chat
     */
    public CTxT getChatMessage() {
        CTxT msg = CUtl.tag();
        // add ERROR: in front of the message if not success
        if (!success) msg = CUtl.error();
        // append the built message
        msg.append(message);

        return msg;
    }

    /**
     * Returns the optional code for identifying error/success.
     * @return the code, or null if not set
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Checks if a code is present.
     * @return true if code is not null, false otherwise
     */
    public boolean hasCode() {
        return code != null;
    }

    /**
     * Utility method to convert key-value pair arguments into a Map.
     *
     * @param extraKeyPair alternating keys and values for settings (must be even number of arguments)
     * @throws IllegalArgumentException if the number of arguments is odd
     */
    private static Map<String, String> convertToMap(String... extraKeyPair) {
        if (extraKeyPair == null || extraKeyPair.length == 0) {
            return Map.of();
        }
        if (extraKeyPair.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must have an even number of arguments");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < extraKeyPair.length; i += 2) {
            map.put(extraKeyPair[i], extraKeyPair[i + 1]);
        }
        return Map.copyOf(map); // Ensures immutability
    }

}
