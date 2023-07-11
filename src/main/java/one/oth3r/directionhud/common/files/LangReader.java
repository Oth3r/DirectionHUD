package one.oth3r.directionhud.common.files;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Utl;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String regex = "%\\d*\\$?[dfs]";
            Matcher anyMatch = Pattern.compile(regex).matcher(translated);
            Matcher endMatch = Pattern.compile(regex+"$").matcher(translated);
            //Arraylist with all the %(#$)[dfs]
            ArrayList<String> matches = new ArrayList<>();
            while (anyMatch.find()) {
                String match = anyMatch.group();
                matches.add(match);
            }
            //SPLITS the text at each regex and remove the regex
            String[] parts = translated.split(regex);
            //if the last element of the array ends with regex, remove it and add an empty string to the end of the array
            if (endMatch.find()) {
                String[] newParts = Arrays.copyOf(parts, parts.length + 1);
                newParts[parts.length] = "";
                parts = newParts;
            }
            //if there are placeholders specified, and the split is more than 1, it will replace %(dfs) with the placeholder objects
            if (parts.length > 1) {
                CTxT txt = CTxT.of("");
                int i = 0;
                for (String match : matches) {
                    int get = i;
                    //if the match is numbered, change GET to the number it wants
                    if (match.contains("$")) {
                        match = match.substring(1,match.indexOf('$'));
                        get = Integer.parseInt(match)-1;
                    }
                    if (parts.length != i) txt.append(parts[i]);
                    //convert the obj into txt
                    txt.append(Utl.getTxTFromObj(placeholders[get]));
                    i++;
                }
                if (parts.length != i) txt.append(parts[i]);
                return CTxT.of(txt);
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