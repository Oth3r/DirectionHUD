package one.oth3r.directionhud.spigot.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class CTxT {
    private TextComponent name;
    private Boolean button = false;
    private ChatColor color = null;
    private ClickEvent clickEvent = null;
    private HoverEvent hoverEvent = null;
    private Boolean bold = false;
    private Boolean italic = false;
    private Boolean strikethrough = false;
    private Boolean underline = false;
    private List<TextComponent> append = new ArrayList<>();
    private Boolean rainbow = false;
    private Float start = null;
    private Float step = null;
    private CTxT() {}
    public static CTxT of(String of) {
        CTxT instance = new CTxT();
        instance.name = new TextComponent(of);
        return instance;
    }
    public static CTxT of(TextComponent of) {
        CTxT instance = new CTxT();
        instance.name = of;
        return instance;
    }
    public static CTxT of(CTxT of) {
        CTxT instance = new CTxT();
        instance.name = of.b();
        return instance;
    }
    public CTxT btn(Boolean btn) {
        this.button = btn;
        return this;
    }
    public CTxT color(String color) {
        this.color = Utl.color.getTC(color);
        return this;
    }
    public CTxT color(Character color) {
        this.color = CUtl.TC(color);
        return this;
    }
    public CTxT color(ChatColor color) {
        this.color = color;
        return this;
    }
    public CTxT cEvent(int typ, String arg) {
        this.clickEvent = CUtl.cEvent(typ, arg);
        return this;
    }
    public CTxT hEvent(CTxT hEvent) {
        this.hoverEvent = CUtl.hEvent(hEvent);
        return this;
    }
    public CTxT bold(Boolean bold) {
        this.bold = bold;
        return this;
    }
    public CTxT italic(Boolean italic) {
        this.italic = italic;
        return this;
    }
    public CTxT strikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }
    public CTxT underline(Boolean underline) {
        this.underline = underline;
        return this;
    }
    public CTxT rainbow(Boolean rainbow, Float start, Float step) {
        this.rainbow = rainbow;
        this.start = start;
        this.step = step;
        return this;
    }
    public CTxT append(String append) {
        this.append.add(new TextComponent(append));
        return this;
    }
    public CTxT append(TextComponent append) {
        this.append.add(append);
        return this;
    }
    public CTxT append(CTxT append) {
        this.append.add(append.b());
        return this;
    }
    public String getString() {
        return b().toPlainText();
    }
    public TextComponent b() {
        TextComponent output = new TextComponent();
        TextComponent text = this.name;
        text.setColor(this.color);

        if (this.button) output.addExtra("[");
        if (this.rainbow) output.addExtra(Utl.color.rainbow(this.name.toPlainText(),this.start,this.step));
        else output.addExtra(text);
        if (this.button) output.addExtra("]");
        output.setClickEvent(this.clickEvent);
        output.setHoverEvent(this.hoverEvent);
        output.setItalic(this.italic);
        output.setBold(this.bold);
        output.setStrikethrough(this.strikethrough);
        output.setUnderlined(this.underline);
        for (TextComponent textComponent : this.append) output.addExtra(textComponent);
        return output;
    }
}
