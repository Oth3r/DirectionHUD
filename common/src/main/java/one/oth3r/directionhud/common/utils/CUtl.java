package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CUtl {
    public static CTxT LARGE = CTxT.of("\n                                             ").strikethrough(true);
    public static CTxT LINE_35 = CTxT.of("\n                                   ").strikethrough(true);
    public static CTxT tag() {
        return CTxT.of("").append(CTxT.of("DirectionHUD").btn(true).color(p())).append(" ");
    }
    public static String p() {
        return DirectionHUD.PRIMARY;
    }
    public static String s() {
        return DirectionHUD.SECONDARY;
    }
    public static CTxT error(CTxT s) {
        return tag().append(lang("error").color(Assets.mainColors.error)).append(" ").append(s);
    }
    public static CTxT usage(String s) {
        return tag().append(lang("usage").color(Assets.mainColors.usage)).append(" ").append(s);
    }
    public static CTxT lang(String key, Object... args) {
        if (DirectionHUD.isClient) {
            Object[] fixedArgs = new Object[args.length];
            for (var i = 0;i < args.length;i++) {
                if (args[i] instanceof CTxT) fixedArgs[i] = ((CTxT) args[i]).b();
                else fixedArgs[i] = args[i];
            }
            return Utl.getTranslation("key.directionhud."+key,fixedArgs);
        } else {
            return LangReader.of("key.directionhud."+key, args).getTxT();
        }
    }
    public static CTxT toggleBtn(boolean button, String cmd) {
        return CUtl.TBtn(button?"on":"off").btn(true).color(button?'a':'c').hEvent(CUtl.TBtn("state.hover",
                CUtl.TBtn(button?"off":"on").color(button?'c':'a'))).cEvent(1,cmd+(button?"off":"on"));
    }
    public static String formatCMD(String cmd) {
        return cmd.substring(1).replace(" ", "-");
    }
    public static String unFormatCMD(String cmd) {
        return "/"+cmd.replace("-"," ");
    }
    public static CTxT TBtn(String key, Object... args) {
        return lang("button."+key,args);
    }
    public static class PageHelper<T> {
        //helper for things that have pages??
        private ArrayList<T> list;
        private int perPage;
        public PageHelper(ArrayList<T> list, int perPage) {
            this.list = list;
            this.perPage = perPage;
        }
        private int getMax() {
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
        public ArrayList<T> getPage(int page) {
            //return a list with the entries in the page given
            int max = getMax();
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
            int max = getMax();
            if (page > max) page = max;
            if (page < 2) page = 1;
            CTxT left = CTxT.of("");
            CTxT right = CTxT.of("");
            // if at the start left is gray else not
            if (page==1) left.append(CTxT.of("<<").btn(true).color('7'));
            else left.append(CTxT.of("<<").btn(true).color(s()).cEvent(1,command+(page-1)));
            // if at the end right is gray else not
            if (page==max) right.append(CTxT.of(">>").btn(true).color('7'));
            else right.append(CTxT.of(">>").btn(true).color(s()).cEvent(1,command+(page+1)));
            // build and return
            return CTxT.of("")
                    .append(left).append(" ")
                    .append(CTxT.of(String.valueOf(page)).btn(true).color(p()).cEvent(2,command).hEvent(TBtn("page.set").color(p())))
                    .append(" ").append(right);
        }
    }
    public static class CButton {
        public static CTxT back(String cmd) {
            return TBtn("back").btn(true).color(Assets.mainColors.back).cEvent(1,cmd).hEvent(CTxT.of(cmd).color(Assets.mainColors.back).append("\n").append(TBtn("back.hover")));
        }
        public static class dest {
            public static CTxT convert(String cmd) {
                return TBtn("dest.convert").btn(true).color(Assets.mainColors.convert).cEvent(1,cmd).hEvent(
                        CTxT.of(cmd).color(Assets.mainColors.convert).append("\n").append(TBtn("dest.convert.hover")));
            }
            public static CTxT set(String cmd) {
                return TBtn("dest.set").btn(true).color(Assets.mainColors.set).cEvent(1,cmd).hEvent(
                        CTxT.of(cmd).color(Assets.mainColors.set).append("\n").append(TBtn("dest.set.hover")));
            }
            public static CTxT edit(int t, String cmd) {
                return CTxT.of(Assets.symbols.pencil).btn(true).color(Assets.mainColors.edit).cEvent(t,cmd).hEvent(TBtn("dest.edit.hover").color(Assets.mainColors.edit)).color(Assets.mainColors.edit);
            }
            public static CTxT settings() {
                return TBtn("settings").btn(true).color(Assets.mainColors.setting).cEvent(1,"/dest settings")
                        .hEvent(CTxT.of(Assets.cmdUsage.destSettings).color(Assets.mainColors.setting).append("\n").append(TBtn("settings.hover",lang("hud.module.destination"))));
            }
            public static CTxT saved() {
                return TBtn("dest.saved").btn(true).color(Assets.mainColors.saved).cEvent(1,"/dest saved").hEvent(
                        CTxT.of(Assets.cmdUsage.destSaved).color(Assets.mainColors.saved).append("\n").append(TBtn("dest.saved.hover")));
            }
            public static CTxT add() {
                return CTxT.of("+").btn(true).color(Assets.mainColors.add).cEvent(2,"/dest add ").hEvent(
                        CTxT.of(Assets.cmdUsage.destAdd).color(Assets.mainColors.add).append("\n").append(TBtn("dest.add.hover",TBtn("dest.add.hover_2").color(Assets.mainColors.add))));
            }
            public static CTxT add(String cmd) {
                return CUtl.TBtn("dest.add").btn(true).color(Assets.mainColors.add).cEvent(2,cmd).hEvent(
                        CTxT.of(Assets.cmdUsage.destAdd).color(Assets.mainColors.add).append("\n").append(TBtn("dest.add.hover",TBtn("dest.add.hover_2").color(Assets.mainColors.add))));
            }
            public static CTxT set() {
                return TBtn("dest.set").btn(true).color(Assets.mainColors.set).cEvent(2,"/dest set ").hEvent(
                        CTxT.of(Assets.cmdUsage.destSet).color(Assets.mainColors.set).append("\n").append(TBtn("dest.set.hover_info")));
            }
            public static CTxT clear(Player player) {
                boolean o = Destination.get(player).hasXYZ();
                return CTxT.of(Assets.symbols.x).btn(true).color(o?'c':'7').cEvent(o?1:0,"/dest clear").hEvent(
                        CTxT.of(Assets.cmdUsage.destClear).color(o?'c':'7').append("\n").append(TBtn("dest.clear.hover")));
            }
            public static CTxT clear() {
                return TBtn("clear").btn(true).color('c').cEvent(1,"/dest track .clear").hEvent(
                        CTxT.of(Assets.cmdUsage.destTrackClear).color('c').append("\n").append(TBtn("dest.track_clear.hover")));
            }
            public static CTxT lastdeath() {
                return TBtn("dest.lastdeath").btn(true).color(Assets.mainColors.lastdeath).cEvent(1,"/dest lastdeath").hEvent(
                        CTxT.of(Assets.cmdUsage.destLastdeath).color(Assets.mainColors.lastdeath).append("\n").append(TBtn("dest.lastdeath.hover")));
            }
            public static CTxT send() {
                return TBtn("dest.send").btn(true).color(Assets.mainColors.send).cEvent(2,"/dest send ").hEvent(
                        CTxT.of(Assets.cmdUsage.destSend).color(Assets.mainColors.send).append("\n").append(TBtn("dest.send.hover")));
            }
            public static CTxT track() {
                return TBtn("dest.track").btn(true).color(Assets.mainColors.track).cEvent(2,"/dest track ").hEvent(
                        CTxT.of(Assets.cmdUsage.destTrack).color(Assets.mainColors.track).append("\n").append(TBtn("dest.track.hover")));
            }
            public static CTxT trackX() {
                return CTxT.of(Assets.symbols.x).btn(true).color('c').cEvent(1,"/dest track .clear").hEvent(
                        CTxT.of(Assets.cmdUsage.destTrackClear).color('c').append("\n").append(TBtn("dest.track_clear.hover")));
            }
        }
        public static class hud {
            public static CTxT color() {
                return CTxT.of(CUtl.color.rainbow(TBtn("hud.color").getString(),15,45)).btn(true).cEvent(1,"/hud color")
                        .hEvent(CTxT.of(CUtl.color.rainbow(Assets.cmdUsage.hudColor,10f,23f)).append("\n").append(TBtn("hud.color.hover")));
            }
            public static CTxT modules() {
                return TBtn("hud.modules").btn(true).color(Assets.mainColors.edit).cEvent(1,"/hud modules").hEvent(
                        CTxT.of(Assets.cmdUsage.hudModules).color(Assets.mainColors.edit).append("\n").append(TBtn("hud.modules.hover")));
            }
            public static CTxT settings() {
                return TBtn("settings").btn(true).color(Assets.mainColors.setting).cEvent(1,"/hud settings").hEvent(
                        CTxT.of(Assets.cmdUsage.hudSettings).color(Assets.mainColors.setting).append("\n").append(TBtn("settings.hover",lang("hud"))));
            }
            public static CTxT toggle(Character color, String type) {
                return TBtn("hud.toggle").btn(true).color(color).cEvent(1,"/hud toggle "+type).hEvent(
                        CTxT.of(Assets.cmdUsage.hudToggle).color(color).append("\n").append(TBtn("hud.toggle.hover")));
            }
        }
        public static class dirHUD {
            public static CTxT hud() {
                return TBtn("dirhud.hud").btn(true).color(Assets.mainColors.hud).cEvent(1,"/hud").hEvent(
                        CTxT.of(Assets.cmdUsage.hud).color(Assets.mainColors.hud).append("\n").append(TBtn("dirhud.hud.hover")));
            }
            public static CTxT dest() {
                return TBtn("dirhud.dest").btn(true).color(Assets.mainColors.dest).cEvent(1,"/dest").hEvent(
                        CTxT.of(Assets.cmdUsage.dest).color(Assets.mainColors.dest).append("\n").append(TBtn("dirhud.dest.hover")));
            }
            public static CTxT defaults() {
                return TBtn("dirhud.defaults").btn(true).color(Assets.mainColors.defaults).cEvent(1,"/dirhud defaults").hEvent(
                        CTxT.of(Assets.cmdUsage.defaults).color(Assets.mainColors.defaults).append("\n").append(TBtn("dirhud.defaults.hover")));
            }
            public static CTxT reload() {
                return TBtn("dirhud.reload").btn(true).color(Assets.mainColors.reload).cEvent(1,"/dirhud reload").hEvent(
                        CTxT.of(Assets.cmdUsage.reload).color(Assets.mainColors.reload).append("\n").append(TBtn("dirhud.reload.hover")));
            }
        }
    }
    public static class color {
        public static String updateOld(String string,String defaultColor) {
            if (string.equals("red")) return "#FF5555";
            if (string.equals("dark_red")) return "#AA0000";
            if (string.equals("gold")) return "#FFAA00";
            if (string.equals("yellow")) return "#FFFF55";
            if (string.equals("green")) return "#55FF55";
            if (string.equals("dark_green")) return "#00AA00";
            if (string.equals("aqua")) return "#55FFFF";
            if (string.equals("dark_aqua")) return "#00AAAA";
            if (string.equals("blue")) return "#5555FF";
            if (string.equals("dark_blue")) return "#0000AA";
            if (string.equals("pink")) return "#FF55FF";
            if (string.equals("purple")) return "#AA00AA";
            if (string.equals("white")) return "#FFFFFF";
            if (string.equals("gray")) return "#AAAAAA";
            if (string.equals("dark_gray")) return "#555555";
            if (string.equals("black")) return "#000000";
            if (string.charAt(0)=='#') return format(string);
            return format(defaultColor);
        }
        public static boolean checkValid(String color, String current) {
            //checks the validity of the color by seeing if it resets.
            //if color isn't current color, test if its valid
            if (!CUtl.color.format(color).equals(current)) {
                //format the color and set default to current
                color = CUtl.color.format(color, current);
                //if color is current (it reset), it's not valid
                return !color.equals(current);
            }
            return true;
        }
        public static ArrayList<String> presetsSuggester(Player player) {
            //for every preset that isn't white, add it to the suggester
            // format: preset-#
            ArrayList<String> list = new ArrayList<>();
            int i = 0;
            for (String s : PlayerData.get.colorPresets(player)) {
                if (!s.equals("#ffffff")) list.add("preset-"+(i+1));
                i++;
            }
            return list;
        }
        public static String format(String hex, String defaultColor) {
            if (hex == null) return format(defaultColor);
            if (hex.length() == 6) hex = "#"+hex;
            if (hex.length() == 7) {
                String regex = "^#([A-Fa-f0-9]{6})$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(hex);
                if (matcher.matches()) return hex.toLowerCase();
            }
            return format(defaultColor);
        }
        public static String format(String hex) {
            return format(hex,"#ffffff");
        }
        public static CTxT getBadge(String hex) {
            return CTxT.of(Assets.symbols.square+" "+format(hex).toUpperCase()).color(hex);
        }
        public static float[] HSB(String hex) {
            Color color = Color.decode(format(hex));
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            float[] hsb = new float[3];
            Color.RGBtoHSB(r, g, b, hsb);
            return hsb;
        }
        public static int[] RGB(String hex) {
            Color color = Color.decode(format(hex));
            int[] i = new int[3];
            i[0] = color.getRed();
            i[1] = color.getGreen();
            i[2] = color.getBlue();
            return i;
        }
        public static String HSBtoHEX(float[] hsb) {
            Color color = new Color(Color.HSBtoRGB(hsb[0],hsb[1],hsb[2]));
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }
        public static String editHSB(int type, String hex, float change) {
            float[] hsb = HSB(hex);
            hsb[type] = Math.max(Math.min(hsb[type]+change,1),0);
            return HSBtoHEX(hsb);
        }
        public static void presetUI(Player player, String type, String setCMD, String backCMD) {
            String formattedReturnCMDArgs = formatCMD(setCMD)+" "+formatCMD(backCMD);
            CTxT defaultBtn = TBtn("color.presets.default").color(CUtl.s()).cEvent(1,"/dirhud presets default "+formattedReturnCMDArgs).btn(true);
            CTxT minecraftBtn = TBtn("color.presets.minecraft").color(CUtl.s()).cEvent(1,"/dirhud presets minecraft "+formattedReturnCMDArgs).btn(true);
            CTxT customBtn = TBtn("color.custom").color(CUtl.s()).cEvent(1,"/dirhud presets custom "+formattedReturnCMDArgs).btn(true);
            List<String> colorStrings;
            List<String> colors;
            int rowAmt;
            if (type.equals("default")) {
                defaultBtn.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                colorStrings = List.of("red","orange","yellow","green","blue","purple","gray");
                colors = List.of("#ff5757","#d40000","#900000",
                        "#ffa562","#ff9834","#e77400",
                        "#ffff86","#ffff5b","#f9c517",
                        "#9aff9a","#5dc836","#396a30",
                        "#8ddfff","#0099ff","#004995",
                        "#a38cff","#8c04dd","#5c00a7",
                        "#d9d9d9","#808080","#404040");
                rowAmt = 3;
            } else {
                minecraftBtn.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                colorStrings = List.of("red","yellow","green","aqua","blue","purple","gray");
                colors = List.of("#FF5555","#AA0000",
                        "#FFFF55","#FFAA00",
                        "#55FF55","#00AA00",
                        "#55FFFF","#00AAAA",
                        "#5555FF","#0000AA",
                        "#FF55FF","#AA00AA",
                        "#AAAAAA","#555555");
                rowAmt = 2;
            }
            CTxT list = CTxT.of("");
            int colorIndex = 0;
            for (String s:colorStrings) {
                list.append("\n ");
                for (int i = 0; i < rowAmt;i++) {
                    String color = colors.get(colorIndex);
                    list.append(CTxT.of(Assets.symbols.square).btn(true).color(color).cEvent(1,setCMD+color.substring(1))
                            .hEvent(TBtn("color.hover",getBadge(color))));
                    colorIndex++;
                }
                list.append(" ").append(lang("color.presets."+s));
            }
            CTxT msg = CTxT.of(" ").append(lang("color.presets.ui").color(Assets.mainColors.presets))
                    .append(CTxT.of("\n                               \n").strikethrough(true))
                    .append(" ").append(defaultBtn).append(" ").append(minecraftBtn).append("\n").append(list)
                    .append("\n\n    ").append(customBtn).append("  ").append(CButton.back(backCMD))
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void customUI(Player player, String setCMD, String backCMD) {
            String formattedReturnCMDArgs = formatCMD(setCMD)+" "+formatCMD(backCMD);
            CTxT defaultBtn = TBtn("color.presets.default").color(CUtl.s()).cEvent(1, "/dirhud presets default " + formattedReturnCMDArgs).btn(true);
            CTxT minecraftBtn = TBtn("color.presets.minecraft").color(CUtl.s()).cEvent(1, "/dirhud presets minecraft " + formattedReturnCMDArgs).btn(true);
            CTxT customBtn = TBtn("color.custom").btn(true).color('7');
            CTxT list = CTxT.of("\n   ");
            int i = 0;
            for (String s : PlayerData.get.colorPresets(player)) {
                boolean x = !s.equals("#ffffff");
                CTxT xBtn = CTxT.of(Assets.symbols.x).btn(true).color(x ? 'c' : '7').cEvent(x ? 1 : 0, "/dirhud presets custom reset " + i + " " + formattedReturnCMDArgs);
                CTxT squareBtn = CTxT.of(Assets.symbols.square).color(s).btn(true).cEvent(1, setCMD + s.substring(1))
                        .hEvent(TBtn("color.hover", getBadge(s)));
                if (i % 2 == 0) list.append(xBtn).append(" ").append(squareBtn).append(" -=- ");
                else list.append(squareBtn).append(" ").append(xBtn).append("\n   ");
                i++;
            }
            CTxT msg = CTxT.of(" ").append(lang("color.presets.ui").color(Assets.mainColors.presets))
                    .append(CTxT.of("\n                               \n").strikethrough(true))
                    .append(" ").append(defaultBtn).append(" ").append(minecraftBtn).append("\n").append(list)
                    .append("\n    ").append(customBtn).append("  ").append(CButton.back(backCMD))
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void customReset(Player player, int preset, String setCMD, String backCMD) {
            ArrayList<String> presets = PlayerData.get.colorPresets(player);
            presets.set(preset, config.colorPresets.get(preset));
            PlayerData.set.colorPresets(player,presets);
            player.sendMessage(tag().append(lang("color.presets.reset",CTxT.of("#"+(preset+1) ).color(s()))));
            customUI(player,setCMD,backCMD);
        }
        public static void customSet(Player player, int preset, String color, String returnCMD) {
            ArrayList<String> presets = PlayerData.get.colorPresets(player);
            presets.set(preset,CUtl.color.format(color));
            PlayerData.set.colorPresets(player,presets);
            player.sendMessage(tag().append(lang("color.presets.set",CTxT.of("#"+(preset+1)).color(s()),getBadge(color))));
            player.performCommand(returnCMD.substring(1));
        }
        public static void customAddUI(Player player, String color, String returnCMD) {
            String formattedReturnCMD = formatCMD(returnCMD);
            CTxT list = CTxT.of("   ");
            int i = 0;
            for (String s: PlayerData.get.colorPresets(player)) {
                CTxT square = CTxT.of(Assets.symbols.square).color(s).btn(true).hEvent(getBadge(s));
                CTxT plusBtn = CTxT.of("+").btn(true).color('a').cEvent(1,"/dirhud presets custom add "+i+" "+color.substring(1)+" "+formattedReturnCMD)
                        .hEvent(TBtn("color.presets.plus.hover",CTxT.of("#"+(i+1)).color(s()),getBadge(color)));
                if (i%2==0) list.append(plusBtn).append(" ").append(square).append(" -=- ");
                else list.append(square).append(" ").append(plusBtn).append("\n   ");
                i++;
            }
            CTxT msg = CTxT.of(" ").append(lang("color.presets.ui").color(Assets.mainColors.presets))
                    .append(CTxT.of("\n                               \n").strikethrough(true)).append("  ")
                    .append(lang("color.presets.add",CTxT.of("+").color('a'))).append("\n\n")
                    .append(list).append("\n ").append(CButton.back(returnCMD))
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static CTxT colorEditor(String color,String step,String setCMD,String stepBigCMD) {
            CTxT defaultSquare = CTxT.of(Assets.symbols.square).color(color).hEvent(getBadge(color));
            CTxT smallButton = CUtl.TBtn("color.size.small").color(CUtl.s()).cEvent(1,stepBigCMD.replace("big","small"))
                    .hEvent(CUtl.TBtn("color.size.hover",CUtl.TBtn("color.size.small").color(CUtl.s()))).btn(true);
            CTxT normalButton = CUtl.TBtn("color.size.normal").color(CUtl.s()).cEvent(1,stepBigCMD.replace("big","normal"))
                    .hEvent(CUtl.TBtn("color.size.hover",CUtl.TBtn("color.size.normal").color(CUtl.s()))).btn(true);
            CTxT bigButton = CUtl.TBtn("color.size.big").color(CUtl.s()).cEvent(1,stepBigCMD)
                    .hEvent(CUtl.TBtn("color.size.hover",CUtl.TBtn("color.size.big").color(CUtl.s()))).btn(true);
            float[] changeAmounts = new float[3];
            if (step == null || step.equals("normal")) {
                normalButton.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                changeAmounts[0] = 0.02f;
                changeAmounts[1] = 0.05f;
                changeAmounts[2] = 0.1f;
            } else if (step.equals("small")) {
                smallButton.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                changeAmounts[0] = 0.005f;
                changeAmounts[1] = 0.0125f;
                changeAmounts[2] = 0.025f;
            } else if (step.equals("big")) {
                bigButton.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                changeAmounts[0] = 0.04f;
                changeAmounts[1] = 0.1f;
                changeAmounts[2] = 0.2f;
            }
            ArrayList<CTxT> hsbList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                hsbList.add(CTxT.of("-").btn(true));
                hsbList.add(CTxT.of("+").btn(true));
            }
            int i = 0;
            for (int changeAmt = 0; changeAmt < 3;changeAmt++) {
                for (int plus = i;plus < i+2;plus++) {
                    String editedColor = CUtl.color.editHSB(changeAmt,color,(plus%2==0)?changeAmounts[changeAmt]*-1:(changeAmounts[changeAmt]));
                    hsbList.get(plus).color(editedColor.equals(color)?Assets.mainColors.gray:editedColor);
                    if (!editedColor.equals(color)) {
                        hsbList.get(plus).hEvent(CUtl.TBtn("color.hover",getBadge(editedColor)));
                        hsbList.get(plus).cEvent(1,setCMD+editedColor.substring(1));
                    }
                }
                i = i+2;
            }
            return CTxT.of("  ")
                    .append(hsbList.get(0)).append(" ").append(defaultSquare).append(" ").append(hsbList.get(1)).append(" ").append(lang("color.hue")).append("\n  ")
                    .append(hsbList.get(2)).append(" ").append(defaultSquare).append(" ").append(hsbList.get(3)).append(" ").append(lang("color.saturation")).append("\n  ")
                    .append(hsbList.get(4)).append(" ").append(defaultSquare).append(" ").append(hsbList.get(5)).append(" ").append(lang("color.brightness")).append("\n\n ")
                    .append(smallButton).append(" ").append(normalButton).append(" ").append(bigButton);
        }
        public static CTxT rainbow(String string, float start, float step) {
            float hue = start % 360f;
            CTxT text = CTxT.of("");
            for (int i = 0; i < string.codePointCount(0, string.length()); i++) {
                if (string.charAt(i) == ' ') {
                    text.append(" ");
                    continue;
                }
                Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                text.append(CTxT.of(Character.toString(string.codePointAt(i))).color(hexColor));
                hue = ((hue % 360f)+step)%360f;
            }
            return text;
        }
    }
}
