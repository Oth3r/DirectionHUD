package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.utils.LangEntry;
import one.oth3r.otterlib.chat.LoaderText;
import one.oth3r.otterlib.registry.LanguageReg;

import java.util.ArrayList;

public class CTxT extends LoaderText<CTxT> {
    protected LangEntry lang = null;

    public CTxT() {
        this.text = new TextComponent("");
    }

    public CTxT(CTxT main) {
        super(main);
        lang = main.lang;
    }

    public CTxT(String text) {
        this.text = new TextComponent(text);
    }

    public CTxT(TextComponent text) {
        this.text = text.duplicate();
    }

    public CTxT(LangEntry lang) {
        this.lang = lang;
    }

    public CTxT append(LangEntry append) {
        return super.append(new CTxT(append));
    }

    public CTxT translatable(CTxT cTxT) {
        this.lang = cTxT.lang;
        return this;
    }

    public CTxT translatable(LangEntry lang) {
        this.lang = lang;
        return this;
    }

    @Override
    public void copyFromObject(CTxT old) {
        this.lang = old.lang;
        super.copyFromObject(old);
        this.text = old.text.duplicate();
    }

    @Override
    public CTxT clone() {
        return new CTxT(this);
    }

    @Override @SuppressWarnings("unchecked")
    public TextComponent b() {
        // if lang is not null, use the language registry to get the translated text
        if (lang != null) {
            LoaderText<?> langText = LanguageReg.getLang(Assets.MOD_ID).translatable(lang.key(), lang.args());
            this.text = langText.getText();
            ArrayList<CTxT> arrayList = (ArrayList<CTxT>) langText.getAppends();
            arrayList.addAll(this.append);
            this.append = arrayList;

            this.lang = null; // clear lang to prevent infinite recursion
        }
        return super.b();
    }

}
