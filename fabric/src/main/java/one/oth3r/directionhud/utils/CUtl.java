package one.oth3r.directionhud.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.LangReader;

public class CUtl {
    public static CTxT tag() {
        return CTxT.of("").append(CTxT.of("DirectionHUD").btn(true).color(p())).append(" ");
    }
    public static String p() {
        return c.pri;
    }
    public static String s() {
        return c.sec;
    }
    public static CTxT error(CTxT s) {
        return tag().append(lang("error").color("#FF4646")).append(" ").append(s);
    }
    public static CTxT usage(String s) {
        return tag().append(lang("usage").color("#FF4646")).append(" ").append(s);
    }
    public static CTxT lang(String key) {
        if (DirectionHUD.isClient) {
            return CTxT.of(Text.translatable("key.directionhud."+key));
        } else {
            return LangReader.of("key.directionhud."+key).getTxT();
        }
    }
    public static MutableText tLang(String key) {
        if (DirectionHUD.isClient) {
            return Text.translatable("key.directionhud."+key);
        } else {
            return LangReader.of("key.directionhud."+key).getTxT().b();
        }
    }
    public static CTxT lang(String key, Object... args) {
        if (DirectionHUD.isClient) {
            Object[] fixedArgs = new Object[args.length];
            for (var i = 0;i < args.length;i++) {
                if (args[i] instanceof CTxT) fixedArgs[i] = ((CTxT) args[i]).b();
                else fixedArgs[i] = args[i];
            }
            return CTxT.of(Text.translatable("key.directionhud."+key, fixedArgs));
        } else {
            return LangReader.of("key.directionhud."+key, args).getTxT();
        }
    }
    public static CTxT TBtn(String TBtn) {
        return lang("button."+TBtn);
    }
    public static CTxT TBtn(String TBtn, Object... args) {
        return lang("button."+TBtn,args);
    }
    public static class c {
        public static String convert = "#ffa93f";
        public static String set = "#fff540";
        public static String saved = "#1ee16f";
        public static String add = "#36ff89";
        public static String setting = "#e9e9e9";
        public static String lastdeath = "#ac4dff";
        public static String send = "#52e1ff";
        public static String track = "#ff6426";
        public static String edit = "#5070ff";
        public static String dest = "#29a2ff";
        public static String hud = "#29ff69";
        public static String defaults = "#ff6629";
        public static String reload = "#69ff29";
        public static String back = "#ff9500";
        public static String sec = "#ffee35";
        public static String pri = "#2993ff";
    }
    public static class CButton {
        public static CTxT back(String cmd) {
            return TBtn("back").btn(true).color(c.back).cEvent(1,cmd).hEvent(CTxT.of(cmd).color(c.back).append("\n").append(TBtn("back.hover")));
        }
        public static class dest {
            public static CTxT convert(String cmd) {
                return TBtn("dest.convert").btn(true).color(c.convert).cEvent(1,cmd).hEvent(
                        CTxT.of(cmd).color(c.convert).append("\n").append(TBtn("dest.convert.hover")));
            }
            public static CTxT set(String cmd) {
                return TBtn("dest.set").btn(true).color(c.set).cEvent(1,cmd).hEvent(
                        CTxT.of(cmd).color(c.set).append("\n").append(TBtn("dest.set.hover")));
            }
            public static CTxT edit(int t, String cmd) {
                return CTxT.of(symbols.pencil()).btn(true).color(c.edit).cEvent(t,cmd).hEvent(TBtn("dest.edit.hover").color(c.edit)).color(c.edit);
            }
            public static CTxT settings() {
                return TBtn("dest.settings").btn(true).color(c.setting).cEvent(1,"/dest settings")
                        .hEvent(CTxT.of(cmdUsage.destSettings()).color(c.setting).append("\n").append(TBtn("dest.settings.hover")));
            }
            public static CTxT saved() {
                return TBtn("dest.saved").btn(true).color(c.saved).cEvent(1,"/dest saved").hEvent(
                        CTxT.of(cmdUsage.destSaved()).color(c.saved).append("\n").append(TBtn("dest.saved.hover")));
            }
            public static CTxT add() {
                return CTxT.of("+").btn(true).color(c.add).cEvent(2,"/dest add ").hEvent(
                        CTxT.of(cmdUsage.destAdd()).color(c.add).append("\n").append(TBtn("dest.add.hover",TBtn("dest.add.hover_2").color(c.add))));
            }
            public static CTxT add(String cmd) {
                return CUtl.TBtn("dest.add").btn(true).color(c.add).cEvent(2,cmd).hEvent(
                        CTxT.of(cmdUsage.destAdd()).color(c.add).append("\n").append(TBtn("dest.add.hover",TBtn("dest.add.hover_2").color(c.add))));
            }
            public static CTxT set() {
                return TBtn("dest.set").btn(true).color(c.set).cEvent(2,"/dest set ").hEvent(
                        CTxT.of(cmdUsage.destSet()).color(c.set).append("\n").append(TBtn("dest.set.hover_info")));
            }
            public static CTxT clear(Player player) {
                boolean o = Destination.get(player).hasXYZ();
                return CTxT.of("✕").btn(true).color(o?'c':'7').cEvent(o?1:0,"/dest clear").hEvent(
                        CTxT.of(cmdUsage.destClear()).color(o?'c':'7').append("\n").append(TBtn("dest.clear.hover")));
            }
            public static CTxT clear() {
                return TBtn("clear").btn(true).color('c').cEvent(1,"/dest track .clear").hEvent(
                        CTxT.of(cmdUsage.destTrackClear()).color('c').append("\n").append(TBtn("dest.track_clear.hover")));
            }
            public static CTxT lastdeath() {
                return TBtn("dest.lastdeath").btn(true).color(c.lastdeath).cEvent(1,"/dest lastdeath").hEvent(
                        CTxT.of(cmdUsage.destLastdeath()).color(c.lastdeath).append("\n").append(TBtn("dest.lastdeath.hover")));
            }
            public static CTxT send() {
                return TBtn("dest.send").btn(true).color(c.send).cEvent(2,"/dest send ").hEvent(
                        CTxT.of(cmdUsage.destSend()).color(c.send).append("\n").append(TBtn("dest.send.hover")));
            }
            public static CTxT track() {
                return TBtn("dest.track").btn(true).color(c.track).cEvent(2,"/dest track ").hEvent(
                        CTxT.of(cmdUsage.destTrack()).color(c.track).append("\n").append(TBtn("dest.track.hover")));
            }
            public static CTxT trackX() {
                return CTxT.of("✕").btn(true).color('c').cEvent(1,"/dest track .clear").hEvent(
                        CTxT.of(cmdUsage.destTrackClear()).color('c').append("\n").append(TBtn("dest.track_clear.hover")));
            }
        }
        public static class hud {
            public static CTxT color() {
                return CTxT.of(Utl.color.rainbow(TBtn("hud.color").getString(),15,45)).btn(true).cEvent(1,"/hud color")
                        .hEvent(CTxT.of(Utl.color.rainbow(cmdUsage.hudColor(),10f,23f)).append("\n").append(TBtn("hud.color.hover")));
            }
            public static CTxT edit() {
                return TBtn("hud.edit").btn(true).color(c.edit).cEvent(1,"/hud edit").hEvent(
                        CTxT.of(cmdUsage.hudEdit()).color(c.edit).append("\n").append(TBtn("hud.edit.hover")));
            }
            public static CTxT toggle(Character color, String type) {
                return TBtn("hud.toggle").btn(true).color(color).cEvent(1,"/hud toggle "+type).hEvent(
                        CTxT.of(cmdUsage.hudToggle()).color(color).append("\n").append(TBtn("hud.toggle.hover")));
            }
        }
        public static class dirHUD {
            public static CTxT hud() {
                return TBtn("dirhud.hud").btn(true).color(c.hud).cEvent(1,"/hud").hEvent(
                        CTxT.of(cmdUsage.hud()).color(c.hud).append("\n").append(TBtn("dirhud.hud.hover")));
            }
            public static CTxT dest() {
                return TBtn("dirhud.dest").btn(true).color(c.dest).cEvent(1,"/dest").hEvent(
                        CTxT.of(cmdUsage.dest()).color(c.dest).append("\n").append(TBtn("dirhud.dest.hover")));
            }
            public static CTxT defaults() {
                return TBtn("dirhud.defaults").btn(true).color(c.defaults).cEvent(1,"/dirhud defaults").hEvent(
                        CTxT.of(cmdUsage.defaults()).color(c.defaults).append("\n").append(TBtn("dirhud.defaults.hover")));
            }
            public static CTxT reload() {
                return TBtn("dirhud.reload").btn(true).color(c.reload).cEvent(1,"/dirhud reload").hEvent(
                        CTxT.of(cmdUsage.reload()).color(c.reload).append("\n").append(TBtn("dirhud.reload.hover")));
            }
        }
    }
    public static class cmdUsage {
        public static String hud() {return "/hud";}
        public static String hudToggle() {return "/hud toggle";}
        public static String hudColor() {return "/hud color";}
        public static String hudEdit() {return "/hud edit";}
        public static String dest() {return "/dest | /destination";}
        public static String destAdd() {return "/dest (saved) add <name> <x> (y) <z> (dimension) (color)";}
        public static String destSet() {return "/dest set <x> (y) <z> (dimension) (convert) | /dest set saved <name> (convert)";}
        public static String destLastdeath() {return "/dest lastdeath";}
        public static String destClear() {return "/dest clear";}
        public static String destSaved() {return "/dest saved";}
        public static String destSettings() {return "/dest settings";}
        public static String destSend() {return "/dest send <IGN> saved <name> | /dest send <IGN> (name) <x> (y) <z> (dimension)";}
        public static String destTrack() {return "/dest track <IGN> | /dest track .clear";}
        public static String destTrackClear() {return "/dest track .clear";}
        public static String defaults() {return "/dirhud defaults";}
        public static String reload() {return "/dirhud reload";}
    }
    public static class symbols {
        public static String up() {
            return "\u25b2";
        }
        public static String down() {
            return "\u25bc";
        }
        public static String left() {
            return "\u25c0";
        }
        public static String right() {
            return "\u25b6";
        }
        public static String x() {
            return "\u2715";
        }
        public static String pencil() {
            return "\u270e";
        }
        public static String sun() {
            return "\u2600";
        }
        public static String moon() {
            return "\u263d";
        }
        public static String rain() {
            return "\ud83c\udf27";
        }
        public static String thunder() {
            return "\u26c8";
        }
        public static String link() {
            return "\u29c9";
        }
    }
}
