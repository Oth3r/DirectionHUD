package one.oth3r.directionhud.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.io.IOException;
import java.util.*;

public class Helper {
    public static final int MAX_NAME = 16;

    public static class Enums {
        public static <T extends Enum<T>> ArrayList<T> toArrayList(T[] array) {
            return new ArrayList<>(Arrays.asList(array));
        }
        public static <T extends Enum<T>> ArrayList<String> toStringList(ArrayList<T> enumList) {
            ArrayList<String> stringList = new ArrayList<>();
            for (T entry:enumList) stringList.add(entry.toString());
            return stringList;
        }
        public static <T extends Enum<T>> ArrayList<T> toEnumList(ArrayList<String> stringList, Class<T> enumType, boolean... setting) {
            boolean settingMode = setting != null;
            ArrayList<T> moduleList = new ArrayList<>();
            for (String module:stringList) {
                try {
                    T enumValue = Enum.valueOf(enumType, module);
                    if (settingMode) enumValue = Enum.valueOf(enumType, module.replace(".","__"));
                    moduleList.add(enumValue);
                } catch (IllegalArgumentException ignored) {}
            }
            return moduleList;
        }
        @SafeVarargs
        public static <T extends Enum<T>> T next(T current, Class<T> enumType, T... exclude) {
            T[] values = enumType.getEnumConstants();
            T next = values[(current.ordinal()+1)%values.length];
            if (exclude != null) {
                for (T item:exclude)
                    if (item.equals(next))
                        return next(next,enumType,exclude);
            }
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
            for (T all : values) {
                // check if there is a match for any of the enum names
                if (enumString.toString().equals(all.name())) return all;
            }
            // if there's no match return the first entry
            return values[0];
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

    public static class ListPage<T> {
        // helps separate lists into page sized chunks
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
            if (list.contains(item)) return (int) Math.ceil((double) (list.indexOf(item) + 1) / perPage);
            else return 1;
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
            else left.append(CTxT.of("<<").btn(true).color(CUtl.s()).cEvent(1,command+(page-1)));
            // if at the end right is gray else not
            if (page==max) right.append(CTxT.of(">>").btn(true).color('7'));
            else right.append(CTxT.of(">>").btn(true).color(CUtl.s()).cEvent(1,command+(page+1)));
            // build and return
            return CTxT.of("")
                    .append(left).append(" ")
                    .append(CTxT.of(String.valueOf(page)).btn(true).color(CUtl.p()).cEvent(2,command).hEvent(CUtl.LANG.hover("page_set").color(CUtl.p())))
                    .append(" ").append(right);
        }
    }

    /**
     * gets a Gson with the LenientTypeAdapter
     */
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new LenientTypeAdapterFactory())
                .create();
    }

    /**
     * the LenientTypeAdapter, doesn't throw anything when reading a weird JSON entry, good for human entered JSONs
     */
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
                    } catch (JsonSyntaxException | MalformedJsonException e) {
                        // don't throw anything if there's a weird JSON, just return null
                        in.skipValue();
                        return null;
                    }
                }
            };
        }
    }
}
