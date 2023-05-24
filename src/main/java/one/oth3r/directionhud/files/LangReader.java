package one.oth3r.directionhud.files;

import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.utils.CTxT;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LangReader {
    private static final Map<String, String> languageMap = new HashMap<>();
    private final String translationKey;
    private final Object[] placeholders;

    public LangReader(String translationKey, Object... placeholders) {
        this.translationKey = translationKey;
        this.placeholders = placeholders;
    }

    public CTxT getTxT() {
        String translated = getLanguageValue(translationKey);
        if (placeholders != null && placeholders.length > 0) {
            //removed all double \\ and replaces with \
            translated = translated.replaceAll("\\\\\\\\", "\\\\");
            //SPLITS the text at each %(dfs) and removes the %(dfs)
            String[] parts = translated.split("%[dfs]");
            // calculates the total amount of %(dfs) in the translated object
            int max = (translated.length() - String.join("", parts).length())/2;
            // If the last element of the array ends with %(dfs), remove it and add an empty string to the end of the array
            if (translated.matches(".*%[dfs]$")) {
                String[] newParts = Arrays.copyOf(parts, parts.length + 1);
                newParts[parts.length] = "";
                parts = newParts;
            }
            //if there are placeholders specified, and the split is more than 1, it will replace %(dfs) with the placeholder objects
            if (parts.length > 1) {
                TextComponent mutableText = new TextComponent();
                int i = 0;
                for (Object placeholder : placeholders) {
                    // if it keeps looping after the max, stop the loop
                    if (i == max) break;
                    if (parts.length != i) mutableText.addExtra(parts[i]);
                    //if the placeholder object is a text, it will append the text
                    //otherwise it will try to turn the object into a string and append that
                    if (placeholder instanceof CTxT) {
                        mutableText.addExtra(((CTxT) placeholder).b());
                    } else if (placeholder instanceof TextComponent) {
                        mutableText.addExtra((TextComponent) placeholder);
                    } else {
                        mutableText.addExtra(String.valueOf(placeholder));
                    }
                    i++;
                }
                if (parts.length != i) mutableText.addExtra(parts[i]);
                return CTxT.of(mutableText);
            }
        }
        return CTxT.of(translated);
    }

    public static LangReader of(String translationKey, Object... placeholders) {
        return new LangReader(translationKey, placeholders);
    }

    public static void loadLanguageFile() {
        try {
            ClassLoader classLoader = DirectionHUD.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("assets/directionhud/lang/"+ config.lang+".json");
            if (inputStream == null) {
                inputStream = classLoader.getResourceAsStream("assets/directionhud/lang/"+config.defaults.lang+".json");
                config.lang = config.defaults.lang;
            }
            if (inputStream == null) throw new IllegalArgumentException("CANT LOAD THE LANGUAGE FILE. DIRECTIONHUD WILL BREAK.");
            Scanner scanner = new Scanner(inputStream);
            String currentLine;
            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine().trim();
                if (currentLine.startsWith("{") || currentLine.startsWith("}")) {
                    continue;
                }
                String[] keyValue = currentLine.split(":", 2);
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                if (value.endsWith(",")) value = value.substring(0, value.length() - 1);
                languageMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLanguageValue(String key) {
        return languageMap.getOrDefault(key, key);
    }
}