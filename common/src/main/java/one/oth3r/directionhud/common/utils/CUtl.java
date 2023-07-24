package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

public class CUtl {
    public static CTxT tag() {
        return CTxT.of("").append(CTxT.of("DirectionHUD").btn(true).color(p())).append(" ");
    }
    public static String p() {
        return Assets.mainColors.pri;
    }
    public static String s() {
        return Assets.mainColors.sec;
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
    public static CTxT TBtn(String TBtn) {
        return lang("button."+TBtn);
    }
    public static CTxT TBtn(String TBtn, Object... args) {
        return lang("button."+TBtn,args);
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
                return TBtn("dest.settings").btn(true).color(Assets.mainColors.setting).cEvent(1,"/dest settings")
                        .hEvent(CTxT.of(Assets.cmdUsage.destSettings).color(Assets.mainColors.setting).append("\n").append(TBtn("dest.settings.hover")));
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
                return CTxT.of("✕").btn(true).color(o?'c':'7').cEvent(o?1:0,"/dest clear").hEvent(
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
                return CTxT.of("✕").btn(true).color('c').cEvent(1,"/dest track .clear").hEvent(
                        CTxT.of(Assets.cmdUsage.destTrackClear).color('c').append("\n").append(TBtn("dest.track_clear.hover")));
            }
        }
        public static class hud {
            public static CTxT color() {
                return CTxT.of(Utl.color.rainbow(TBtn("hud.color").getString(),15,45)).btn(true).cEvent(1,"/hud color")
                        .hEvent(CTxT.of(Utl.color.rainbow(Assets.cmdUsage.hudColor,10f,23f)).append("\n").append(TBtn("hud.color.hover")));
            }
            public static CTxT edit() {
                return TBtn("hud.edit").btn(true).color(Assets.mainColors.edit).cEvent(1,"/hud edit").hEvent(
                        CTxT.of(Assets.cmdUsage.hudEdit).color(Assets.mainColors.edit).append("\n").append(TBtn("hud.edit.hover")));
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
}
