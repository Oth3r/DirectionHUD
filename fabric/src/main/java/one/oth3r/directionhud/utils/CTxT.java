package one.oth3r.directionhud.utils;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import one.oth3r.directionhud.common.utils.CUtl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CTxT {
    private MutableText name;
    private Boolean button = false;
    private TextColor color = null;
    private ClickEvent clickEvent = null;
    private HoverEvent hoverEvent = null;
    private Boolean bold = false;
    private Boolean italic = false;
    private Boolean strikethrough = false;
    private Boolean underline = false;
    private final List<MutableText> append = new ArrayList<>();
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
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.b());
    }
    private CTxT() {}
    public static CTxT of(String of) {
        CTxT instance = new CTxT();
        instance.name = Text.literal(of);
        return instance;
    }
    public static CTxT of(MutableText of) {
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
        this.color = TextColor.parse(CUtl.color.format(color)).result().orElse(TextColor.fromFormatting(Formatting.BLACK));
        return this;
    }
    public CTxT color(Character color) {
        this.color = TextColor.fromFormatting(Formatting.byCode(color));
        return this;
    }
    public CTxT color(TextColor color) {
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
        this.append.add(Text.literal(append));
        return this;
    }
    public CTxT append(MutableText append) {
        this.append.add(append);
        return this;
    }
    public CTxT append(CTxT append) {
        this.append.add(append.b());
        return this;
    }
    public CTxT append(Text append) {
        this.append.add((MutableText) append);
        return this;
    }
    @Override
    public String toString() {
        return b().getString();
    }
    public MutableText b() {
        MutableText output = Text.literal("");
        if (this.rainbow) {
            float hue = start % 360f;
            String string = name.getString();
            MutableText rainbow = Text.empty();
            for (int i = 0; i < string.codePointCount(0, string.length()); i++) {
                if (string.charAt(i) == ' ') {
                    rainbow.append(" ");
                    continue;
                }
                Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                rainbow.append(Text.literal(Character.toString(string.codePointAt(i))).styled(style ->
                        style.withColor(TextColor.parse(CUtl.color.format(hexColor)).result().orElse(TextColor.fromFormatting(Formatting.BLACK)))));
                hue = ((hue % 360f)+step)%360f;
            }
            this.name = rainbow;
        }
        if (this.button) output.append("[").setStyle(Style.EMPTY.withColor(Formatting.byCode('f')));
        output.append(this.name.styled(style -> style.withColor(this.color)
                .withClickEvent(this.clickEvent)
                .withHoverEvent(this.hoverEvent)
                .withItalic(this.italic)
                .withBold(this.bold)
                .withStrikethrough(this.strikethrough)
                .withUnderline(this.underline)));
        if (this.button) output.append("]").setStyle(Style.EMPTY.withColor(Formatting.byCode('f')));
        output.styled(style -> style
                .withClickEvent(this.clickEvent)
                .withHoverEvent(this.hoverEvent)
                .withItalic(this.italic)
                .withBold(this.bold)
                .withStrikethrough(this.strikethrough)
                .withUnderline(this.underline));
        for (MutableText mutableText : this.append) output.append(mutableText);
        return output;
    }
}
