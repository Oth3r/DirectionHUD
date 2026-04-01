package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.CTxT;

public record LangEntry(String key, Object... args) {
    public CTxT asCTxT() {
        return new CTxT(this);
    }
}
