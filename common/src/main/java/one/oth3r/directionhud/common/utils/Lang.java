package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.CTxT;

public class Lang {
    private final String location;

    public Lang(String location) {
        this.location = location;
    }
    public CTxT get(String key, Object... args) {
        return CUtl.getLangEntry(location+key, args);
    }
    public CTxT error(String key, Object... args) {
        return CUtl.error().append(get("error."+key, args));
    }
    public CTxT btn(String key, Object... args) {
        return get("button."+key, args);
    }
    public CTxT btn() {
        return get("button");
    }
    public CTxT hover(String key, Object... args) {
        return get("hover."+key, args);
    }
    public CTxT hover() {
        return get("hover");
    }
    public CTxT ui(String key, Object... args) {
        return get("ui."+key, args);
    }
    public CTxT ui() {
        return get("ui");
    }
    public CTxT msg(String key, Object... args) {
        return get("msg."+key, args);
    }
    public CTxT desc(String key, Object... args) {
        return get("description."+key, args);
    }
}
