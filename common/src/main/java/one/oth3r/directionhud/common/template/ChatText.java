package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper.Pair;
import one.oth3r.directionhud.common.utils.Rainbow;
import one.oth3r.directionhud.utils.CTxT;

import java.util.ArrayList;

public abstract class ChatText {
    private String text;
    private Boolean button = false;
    private String color = null;
    private Pair<Integer, String> clickEvent = null;
    private CTxT hoverEvent = null;
    private Boolean bold = false;
    private Boolean italic = false;
    private Boolean strikethrough = false;
    private Boolean underline = false;
    private ArrayList<ChatText> append = new ArrayList<>();
    private Rainbow rainbow = new Rainbow();

    public ChatText() {}

    public ChatText(String text) {
        this.text = text;
    }

    public ChatText(ChatText main) {
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

    public ChatText text(String text) {
        this.text = text;
        return this;
    }

    public ChatText btn(Boolean button) {
        this.button = button;
        return this;
    }

    public ChatText color(String color) {
        this.color = CUtl.color.format(color);
        return this;
    }

    public ChatText click(int type, String actionString) {
        this.clickEvent = new Pair<>(type, actionString);
        return this;
    }

    public ChatText hover(CTxT hoverText) {
        this.hoverEvent = hoverText;
        return this;
    }

    public ChatText bold(Boolean bold) {
        this.bold = bold;
        return this;
    }

    public ChatText italic(Boolean italic) {
        this.italic = italic;
        return this;
    }

    public ChatText strikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public ChatText underline(Boolean underline) {
        this.underline = underline;
        return this;
    }

    public abstract ChatText append(String append);

    public ChatText append(ChatText append) {
        this.append.add(append);
        return this;
    }

    public ChatText rainbow(Rainbow rainbow) {
        this.rainbow = rainbow;
        return this;
    }
}
