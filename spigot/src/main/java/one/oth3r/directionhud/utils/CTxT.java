package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import one.oth3r.directionhud.common.utils.CUtl;

import java.awt.*;
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
    private final List<TextComponent> append = new ArrayList<>();
    private Boolean rainbow = false;
    private Float start = null;
    private Float step = null;
    private static ClickEvent click(int typ, String arg) {
        if (typ == 1) return new ClickEvent(ClickEvent.Action.RUN_COMMAND,arg);
        if (typ == 2) return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,arg);
        if (typ == 3) return new ClickEvent(ClickEvent.Action.OPEN_URL,arg);
        return null;
    }
    private static HoverEvent hover(CTxT text) {
        ComponentBuilder cb = new ComponentBuilder(text.b());
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(cb.create()));
    }
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
        this.color = ChatColor.of(CUtl.color.format(color));
        return this;
    }
    public CTxT color(Character color) {
        this.color = ChatColor.getByChar(color);
        return this;
    }
    public CTxT color(ChatColor color) {
        this.color = color;
        return this;
    }
    public CTxT cEvent(int typ, String arg) {
        if (arg == null) this.clickEvent = null;
        else this.clickEvent = click(typ, arg);
        return this;
    }
    public CTxT hEvent(CTxT hEvent) {
        if (hEvent == null) this.hoverEvent = null;
        else this.hoverEvent = hover(hEvent);
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
    @Override
    public String toString() {
        return b().toPlainText();
    }
    public TextComponent b() {
        TextComponent output = new TextComponent();
        TextComponent text = this.name;
        if (this.rainbow) {
            float hue = start % 360f;
            String string = name.toPlainText();
            TextComponent rainbow = new TextComponent();
            for (int i = 0; i < string.codePointCount(0, string.length()); i++) {
                if (string.charAt(i) == ' ') {
                    rainbow.addExtra(" ");
                    continue;
                }
                Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                TextComponent add = new TextComponent(Character.toString(string.codePointAt(i)));
                add.setColor(ChatColor.of(CUtl.color.format(hexColor)));
                rainbow.addExtra(add);
                hue = ((hue % 360f)+step)%360f;
            }
            text = rainbow;
        } else text.setColor(this.color);
        text.setClickEvent(this.clickEvent);
        text.setHoverEvent(this.hoverEvent);
        text.setItalic(this.italic);
        text.setBold(this.bold);
        text.setStrikethrough(this.strikethrough);
        text.setUnderlined(this.underline);

        if (this.button) output.addExtra("[");
        output.addExtra(text);
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
