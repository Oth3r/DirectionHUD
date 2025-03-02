package one.oth3r.directionhud.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.BaseModuleAdapter;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import org.apache.commons.text.similarity.FuzzyScore;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Helper {
    public static final int MAX_NAME = 16;

    public static class Enums {

        /**
         * converts an arraylist of enums to an arraylist of strings of the enum names using {@link Enum#name()}
         * @param enumList the arraylist of Enums
         * @return the converted arraylist of strings
         */
        public static <T extends Enum<T>> ArrayList<String> toStringList(ArrayList<T> enumList) {
            return (ArrayList<String>) enumList.stream().map(Enum::name).collect(Collectors.toList());
        }

        /**
         * method overload of {@link #toStringList(ArrayList)}
         * @param enumArray an array of custom enum
         */
        public static <T extends Enum<T>> ArrayList<String> toStringList(T[] enumArray) {
            return toStringList(new ArrayList<>(List.of(enumArray)));
        }

        /**
         * turns an {@link ArrayList} of enum strings into an ArrayList of {@link  Enum} <br>
         * if the enum cannot be found for one of the strings in the list, it will be skipped
         * @param stringList the list of enum strings
         * @param enumType the class of the enum
         * @return the converted {@link ArrayList} of enums
         */
        public static <T extends Enum<T>> ArrayList<T> toEnumList(ArrayList<String> stringList, Class<T> enumType) {
            return toEnumList(Enum::valueOf, stringList, enumType);
        }
        public static <T extends Enum<T>> ArrayList<T> toEnumList(BiFunction<Class<T>,String, T> function, ArrayList<String> stringList, Class<T> enumType) {
            ArrayList<T> moduleList = new ArrayList<>();

            // for each string in the enum string list
            for (String module : stringList) {
                // try to get the Enum from the string, if it throws an Exception, ignore it (invalid strings get ignored)
                try {
                    T enumValue = function.apply(enumType, module);
                    moduleList.add(enumValue);
                } catch (IllegalArgumentException ignored) {}
            }

            // return the new list
            return moduleList;
        }

        /**
         * finds the next enum in the enum array, wrapping around if the enum is at the end of the array
         * @param current the current enum
         * @param enumType the class type of the enum
         * @param exclude the list of enums to 'skip' over when finding the next enum
         * @return the next enum in the array
         */
        @SafeVarargs
        public static <T extends Enum<T>> T next(T current, Class<T> enumType, T... exclude) {
            T[] values = enumType.getEnumConstants();
            // get the next enum in the values list, wrapping around if necessary
            T next = values[(current.ordinal()+1)%values.length];

            // if the exclude list isn't empty
            if (exclude != null) {
                // if the next enum is in the exclude list, find the next one after the current next one
                if (Arrays.asList(exclude).contains(next)) return next(next, enumType, exclude);
            }

            // return the next Enum
            return next;
        }

        /**
         * gets an enum from a string without returning null
         * @param enumString the string of the enum
         * @param enumType the class of enums
         * @return an enum, if there isn't a match, it returns the first enum
         */
        public static <T extends Enum<T>> T get(Object enumString, Class<T> enumType) {
            T[] values = enumType.getEnumConstants();
            // if there's no match return the first entry
            return search(enumString,enumType).orElse(values[0]);
        }

        /**
         * searches for an enum constant by its name in the specified enum type class
         *
         * @param <T> the type of the enum
         * @return an {@code Optional} containing the matching enum constant if found, otherwise an empty {@code Optional}
         */
        public static <T extends Enum<T>> Optional<T> search(Object enumString, Class<T> enumType) {
            T[] values = enumType.getEnumConstants();
            return Arrays.stream(values).filter(entry -> entry.name().equals(enumString.toString())).findFirst();
        }

        /**
         * checks if an Enum string is valid in the enum type
         */
        public static <T extends Enum<T>> boolean contains(Object enumString, Class<T> enumType) {
            return search(enumString,enumType).isPresent();
        }
    }

    public static class Num {

        public static boolean isInt(String string) {
            try {
                Integer.parseInt(string);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        }

        public static Integer toInt(String s) {
            // return an int no matter what
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                try {
                    return (int) Double.parseDouble(s);
                } catch (NumberFormatException e2) {
                    return 0;
                }
            }
        }

        public static boolean isNum(String s) {
            // checks if int or a double
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e1) {
                try {
                    Double.parseDouble(s);
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            }
        }

        public static boolean inBetween(double num, double min, double max) {
            // if min is greater than max, flip
            if (min > max) return num >= min || num <= max;
            return num >= min && num <= max;
        }

        public static double wSubtract(double num, double sub, double max) {
            // wrapped subtract
            double output = num - sub;
            if (output < 0) output = max - (output*-1);
            return output;
        }

        public static double wAdd(double num, double add, double max) {
            // wrapped add
            return (num+add)%max;
        }

        /**
         * formats a given number to have a specific number of digits (good for clocks etc.) <br>
         * the method adds leading 0's as necessary, and if the original number is too big, it will be cut off
         *
         * @param number the number to be formatted
         * @param digits the total number of digits the output string should have
         * @return a formatted string of the number with the specified number of digits
         */
        public static String formatToXDigits(int number, int digits) {
            // put digit -1 amount of zeros in front of the original number
            String result = "0".repeat(Math.max(0,digits-1)) + number;
            // return the string shortened to the right size
            return result.substring(result.length() - digits);
        }

        public static String formatToTwoDigits(int number) {
            return formatToXDigits(number,2);
        }
    }

    public static class Command {

        public static class Suggester {

            /**
             * wraps a list of things in quotes
             * @param list the list of things
             * @return a list of strings with quotes around items
             */
            public static <T>ArrayList<String> wrapQuotes(List<T> list) {
                ArrayList<String> output = new ArrayList<>();
                for (T entry:list) {
                    output.add(wrapQuotes(entry));
                }
                return output;
            }

            /**
             * wraps an object with quotes
             * @param obj object to wrap
             * @return wrapped string
             */
            public static String wrapQuotes(Object obj) {
                return "\""+obj+"\"";
            }

            /**
             * gets the current typed string from the player
             * @param args command args
             * @param pos typing pos
             */
            public static String getCurrent(String[] args, int pos) {
                // if the length is the same as the pos, there's nothing currently being typed at that pos
                // get the current typed message
                if (args.length == pos+1) return args[pos];
                return "";
            }

            /**
             * suggests the coordinates of the player
             * @param current current typed string
             * @param type amount of coordinates, 3 xyz, 2 z|yz, 1 z
             */
            public static ArrayList<String> xyz(Player player, String current, int type) {
                // type = 3: all 3, ect
                ArrayList<String> list = new ArrayList<>();
                if (type == 3) {
                    list.add(String.valueOf(player.getBlockX()));
                    list.add(player.getBlockX()+" "+player.getBlockZ());
                    list.add(player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
                } else if (type == 2) {
                    list.add(String.valueOf(player.getBlockY()));
                    list.add(player.getBlockY()+" "+player.getBlockZ());
                } else if (type == 1) {
                    list.add(String.valueOf(player.getBlockZ()));
                }
                // don't suggest if typing letters or different coordinates
                if (current.isEmpty() || list.get(0).startsWith(current)) return list;
                return new ArrayList<>();
            }

            /**
             * suggests player strings excluding the inputted player
             * @param player player to exclude
             */
            public static ArrayList<String> players(Player player) {

                ArrayList<String> list = new ArrayList<>();
                for (Player entry: Utl.getPlayers())
                    if (!entry.equals(player)) list.add(entry.getName());
                return list;
            }

            /**
             * suggests a list of dimensions
             * @param displayEmpty if the list should show up if current is empty
             */
            public static ArrayList<String> dims(String current, boolean displayEmpty) {
                ArrayList<String> list = new ArrayList<>(), dimList = wrapQuotes(Dimension.getAllIDs());
                if (!current.isEmpty() || displayEmpty) {
                    if (current.isEmpty()) return dimList;
                    for (String dim : dimList) {
                        if (dim.contains(current)) list.add(dim);
                    }
                }
                return list;
            }

            /**
             * suggests a list of colors and player presets
             * @param displayEmpty if the list displays when the current is empty or not
             */
            public static ArrayList<String> colors(Player player, String current, boolean displayEmpty) {
                ArrayList<String> list = new ArrayList<>();
                ArrayList<String> presets = new ArrayList<>();
                // add all presets to a list
                for (String name : DHud.preset.custom.getNames(player.getPData().getColorPresets()))
                    presets.add(String.format("\"preset-%s\"",name));
                // displaying logic, if not empty and not display empty or empty and display empty
                if (!current.isEmpty() || displayEmpty) {
                    list.add("ffffff");
                    if (!presets.isEmpty() && checkEnabled(player).customPresets()) {
                        list.add("preset");
                        if (current.startsWith("pre")) {
                            list.addAll(presets);
                            return list;
                        }
                    }
                    list.add("red");
                    list.add("orange");
                    list.add("yellow");
                    list.add("green");
                    list.add("blue");
                    list.add("purple");
                    list.add("gray");
                }
                return list;
            }

            /**
             * filters the suggestions based on similarity to the current typed out command
             */
            public static ArrayList<String> filter(ArrayList<String> suggestions, String current) {
                // if the current typed command is empty, don't filter
                if (current.equalsIgnoreCase("")) return suggestions;
                if (current.isEmpty()) return suggestions;

                // the list of filtered suggestions only
                ArrayList<String> filtered = new ArrayList<>();

                // todo have a personal fallback, as everyone doesn't speak english 🦅
                if (FileData.getConfig().getLang().equals("en_us")) {
                    FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);

                    double minimumScore = current.length() * 1.5;
                    if (current.length() == 1) minimumScore = 1;

                    // score each suggestion and retrieve the suitable option
                    for (String s : suggestions) {
                        int score = fuzzyScore.fuzzyScore(s.toLowerCase(), current.toLowerCase());

                        // if the score is greater or equal than the minimum, add to the filtered list
                        if (score >= minimumScore) filtered.add(s);
                    }
                } else {
                    // if the language isn't english, use the JaroWinklerSimilarity
                    JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
                    float minSimilarity = 0.65f;

                    for (String s : suggestions) {
                        // add the suggestion to the return list if above the min similarity threshold
                        if (similarity.apply(current, s) > minSimilarity) filtered.add(s);
                    }
                }

                return filtered;
            }
        }

        /**
         * removes all entries until the target entry is found
         * @return the edited string array, removing the keyword as well
         */
        public static String[] removeTo(String[] target, String... keywords) {
            // the args variable to return
            String[] args = target;

            String targetString = String.join(" ", target);
            // set the index to -1 as not finding a match as default
            int index = -1;

            // finds the index for the keywords
            for (String keyword : keywords) {
                index = targetString.indexOf(keyword);

                // if a match is found, break out of the loop
                if (index != -1) break;
            }

            //trims the words before the text
            if (index != -1) {
                targetString = targetString.substring(index).trim();
                args = targetString.split(" ");
                // remove the keyword as well
                args = trimStart(args, 1);
            }

            return args;
        }

        public static String[] quoteHandler(String[] args) {
            // put quoted items all in one arg
            boolean quote = false;
            ArrayList<String> output = new ArrayList<>();
            StringBuilder addBuffer = new StringBuilder();
            for (String s : args) {
                if (s.contains("\"")) {
                    int count = s.length() - s.replaceAll("\"","").length(); // count how many quotes there are
                    s = s.replace("\"",""); // remove the quote
                    if (quote) { // end of quote, don't skip the loop to add to the arraylist
                        quote = false;
                        addBuffer.append(" ").append(s);
                    } else if (count == 1) { // start of the quote, skip the rest of the loop to fill the buffer
                        quote = true;
                        addBuffer.append(s);
                        continue;
                    } else addBuffer.append(s); // all in one quote, add to buffer directly
                } else if (quote) { // middle of the quote, add a space and skip the rest of the loop
                    addBuffer.append(" ").append(s);
                    continue;
                } else addBuffer.append(s); // no loop just add to buffer
                output.add(addBuffer.toString()); // dump the buffer
                addBuffer.setLength(0); // clear the builder
            }
            // make sure if the buffer still has something then dump it to the output array
            if (!addBuffer.isEmpty()) output.add(addBuffer.toString());
            String[] arr = new String[output.size()];
            return output.toArray(arr);
        }
    }

    public static Utl.CheckEnabled checkEnabled(Player player) {
        return new Utl.CheckEnabled(player);
    }

    /**
     * creates a random ID
     */
    public static String createID() {
        //spigot not having apache smh my head
        String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            char randomChar = CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * trims a String array
     * @param numToRemove number to remove from the start
     */
    public static String[] trimStart(String[] arr, int numToRemove) {
        if (numToRemove > arr.length) return new String[0];
        String[] result = new String[arr.length - numToRemove];
        System.arraycopy(arr, numToRemove, result, 0, result.length);
        return result;
    }

    public record Pair<A, B>(A key, B value) {

        public String toString() {
                return "(" + this.key + ", " + this.value + ")";
            }

        public Pair<B, A> getFlipped() {
                return new Pair<>(value, key);
            }
    }

    public record ColorPreset(String name, String color) {

        public String toString() {
            return "(" + this.name + ": " + this.color + ")";
        }
    }

    /**
     * helps separate lists into page sized chunks
     * @param <T> the type of list
     */
    public static class ListPage<T> {
        private final ArrayList<T> list;
        private final int perPage;

        public ListPage(ArrayList<T> list, int perPage) {
            this.list = list;
            this.perPage = perPage;
        }
        public int getTotalPages() {
            // get max pages, min = 1
            return Math.max(1,(int) Math.ceil((double) list.size() / perPage));
        }
        public ArrayList<T> getList() {
            return list;
        }

        public int getPageOf(T item) {
            // get the quotient of the index and the amount of items per page rounded to the next integer to get page of the current item
            return getPageOf(getIndexOf(item));
        }

        public int getPageOf(int index) {
            // get the quotient of the index and the amount of items per page rounded to the next integer to get page of the current item
            return (int) Math.ceil((double) (index + 1) / perPage);
        }

        public int getIndexOf(T item) {
            return list.indexOf(item);
        }

        public ArrayList<T> getPage(int page) {
            //return a list with the entries in the page given
            int max = getTotalPages();
            if (max < page) page = max;
            if (page <= 0) page = 1;
            ArrayList<T> pageList = new ArrayList<>();
            // loop for amount per page
            for (int i = 0;i < perPage;i++) {
                // get the current index, (page-1) * amt per page + current page index
                int index = (page-1)*perPage+i;
                if (list.size() > index) pageList.add(list.get(index));
            }
            return pageList;
        }

        public CTxT getNavButtons(int page, String command) {
            // return the buttons to change page
            int max = getTotalPages();
            if (page > max) page = max;
            if (page < 2) page = 1;
            CTxT left = CTxT.of("");
            CTxT right = CTxT.of("");
            // if at the start left is gray else not
            if (page==1) left.append(CTxT.of("<<").btn(true).color('7'));
            else left.append(CTxT.of("<<").btn(true).color(CUtl.s()).click(1,command+(page-1)));
            // if at the end right is gray else not
            if (page==max) right.append(CTxT.of(">>").btn(true).color('7'));
            else right.append(CTxT.of(">>").btn(true).color(CUtl.s()).click(1,command+(page+1)));
            // build and return
            return CTxT.of("")
                    .append(left).append(" ")
                    .append(CTxT.of(String.valueOf(page)).btn(true).color(CUtl.p()).click(2,command).hover(CUtl.LANG.hover("page_set").color(CUtl.p())))
                    .append(" ").append(right);
        }
    }

    /**
     * removes duplicate classes seen in an array <br>
     * perfect for an array for an abstract class, and making sure each item in the array is a unique subclass
     * @param list the list to check
     * @param <T> the list object
     */
    public static <T> void removeDuplicateSubclasses(ArrayList<T> list) {
        Set<Class<?>> seenClasses = new HashSet<>();
        list.removeIf(item -> !seenClasses.add(item.getClass()));
    }

    /**
     * move an item in an arraylist to a new index
     * @param list the list to modify
     * @param currentIndex the index of the item to move
     * @param targetIndex the new index of the item
     * @param <T> the list object
     */
    public static <T> void moveTo(ArrayList<T> list, int currentIndex, int targetIndex) {
        if (currentIndex < 0 || currentIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Current index is out of bounds.");
        }

        // Remove the element and save it
        T element = list.remove(currentIndex);

        // Adjust target index if it's out of bounds
        if (targetIndex >= list.size()) {
            list.add(element); // Add to the end if too high
        } else {
            list.add(targetIndex, element); // Insert at the specified position
        }
    }

    /**
     * gets a Gson with the LenientTypeAdapter
     */
    public static Gson getGson() {

        return new GsonBuilder()
                .registerTypeAdapter(BaseModule.class, new BaseModuleAdapter())
                .registerTypeAdapterFactory(new LenientTypeAdapterFactory())
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
    }

    /**
     * the LenientTypeAdapter, doesn't throw anything when reading a weird JSON entry, good for human entered JSONs
     */
    @SuppressWarnings("unchecked")
    public static class LenientTypeAdapterFactory implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

            return new TypeAdapter<>() {
                // normal writer
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }
                // custom reader
                public T read(JsonReader in) throws IOException {
                    try {
                        //Try to read value using default TypeAdapter
                        return delegate.read(in);
                    } catch (Exception e) {
                        // skip the invalid json value
                        in.skipValue();

                        // try to provide a default instance for common types
                        Class<? super T> rawType = type.getRawType();

                        if (List.class.isAssignableFrom(rawType)) {
                            return (T) new ArrayList<>();
                        } else if (Map.class.isAssignableFrom(rawType)) {
                            return (T) new HashMap<>();
                        }

                        // attempt to create a new instance using a no-arg constructor
                        try {
                            Constructor<? super T> ctor = rawType.getDeclaredConstructor();
                            ctor.setAccessible(true);
                            return (T) ctor.newInstance();
                        } catch (Exception ex) {
                            // no default instance is available, fallback to null
                            return null;
                        }
                    }
                }
            };
        }
    }
}
