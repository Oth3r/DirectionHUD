package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper.Pair;
import one.oth3r.directionhud.common.utils.Rainbow;

import java.util.ArrayList;

public abstract class ChatText<T, C extends ChatText<T, C>> {
    protected T text;
    protected Boolean button = false;
    protected String color = "#ffffff";
    protected Pair<Integer, String> clickEvent = null;
    protected C hoverEvent = null;
    protected Boolean bold = false;
    protected Boolean italic = false;
    protected Boolean strikethrough = false;
    protected Boolean underline = false;
    protected Boolean obfuscate = false;
    protected ArrayList<C> append = new ArrayList<>();
    protected Rainbow rainbow = new Rainbow();

    public ChatText() {}

    public ChatText(C main) {
        this.text = main.text;
        this.button = main.button;
        this.color = main.color;
        this.clickEvent = main.clickEvent;
        this.hoverEvent = main.hoverEvent;
        this.bold = main.bold;
        this.italic = main.italic;
        this.strikethrough = main.strikethrough;
        this.underline = main.underline;
        this.append = main.append;
        this.rainbow = main.rainbow;
    }

    public abstract C text(String text);

    public C btn(Boolean button) {
        this.button = button;
        return self();
    }

    public C color(String color) {
        this.color = CUtl.color.format(color);
        return self();
    }

    public C color(char color) {
        this.color = CUtl.color.format(color);
        return self();
    }

    public C click(Pair<Integer, String> clickEventPair) {
        this.clickEvent = clickEventPair;
        return self();
    }

    public C click(int type, String actionString) {
        this.clickEvent = new Pair<>(type, actionString);
        return self();
    }

    public C hover(C hoverText) {
        this.hoverEvent = hoverText;
        return self();
    }

    public C bold(Boolean bold) {
        this.bold = bold;
        return self();
    }

    public C italic(Boolean italic) {
        this.italic = italic;
        return self();
    }

    public C strikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
        return self();
    }

    public C underline(Boolean underline) {
        this.underline = underline;
        return self();
    }

    public C obfuscate(Boolean obfuscate) {
        this.obfuscate = obfuscate;
        return self();
    }

    public abstract C append(String append);

    public abstract C append(T append);

    public C append(C append) {
        this.append.add(append);
        return self();
    }

    public C rainbow(Rainbow rainbow) {
        this.rainbow = rainbow;
        return self();
    }

    public abstract T b();

    @Override
    public abstract String toString();

    public Boolean isBtn() {
        return button;
    }

    public String getColor() {
        return color;
    }

    public Boolean isBold() {
        return bold;
    }

    public Boolean isItalic() {
        return italic;
    }

    public Boolean isStrikethrough() {
        return strikethrough;
    }

    public Boolean isUnderline() {
        return underline;
    }

    public Boolean isObfuscated() {
        return obfuscate;
    }

    public Pair<Integer, String> getClick() {
        return clickEvent;
    }

    public C getHover() {
        return hoverEvent;
    }

    public boolean isEmpty() {
        return this.toString().isEmpty();
    }

    // helper to return the subclass instance
    @SuppressWarnings("unchecked")
    protected C self() {
        return (C) this;
    }
}
