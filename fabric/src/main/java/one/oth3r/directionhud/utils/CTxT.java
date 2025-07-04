package one.oth3r.directionhud.utils;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import one.oth3r.directionhud.common.template.ChatText;

import java.net.URI;

public class CTxT extends ChatText<MutableText, CTxT> {
    public CTxT() {
        this.text = Text.literal("");
    }

    public CTxT(CTxT main) {
        super(main);
    }

    public CTxT(MutableText text) {
        // create a copy of the mutable
        this.text = text.copy();
    }

    public CTxT(String text) {
        this.text = Text.literal(text);
    }

    public static CTxT of(String of) {
        return new CTxT(of);
    }

    public static CTxT of(MutableText of) {
        return new CTxT(of);
    }

    public static CTxT of(CTxT of) {
        return new CTxT(of);
    }

    @Override
    public void copyFromObject(CTxT old) {
        super.copyFromObject(old);
        this.text = old.text.copy();
    }

    @Override
    public CTxT clone() {
        return new CTxT(this);
    }

    @Override
    public CTxT text(String text) {
        this.text = Text.literal(text);
        return this;
    }

    public CTxT text(MutableText text) {
        this.text = text.copy();
        return this;
    }

    public CTxT text(CTxT text) {
        copyFromObject(text);
        return this;
    }

    @Override
    public CTxT append(String append) {
        this.append.add(new CTxT(append));
        return this;
    }

    @Override
    public CTxT append(MutableText append) {
        this.append.add(new CTxT(append));
        return this;
    }

    private ClickEvent getClickEvent() {
        if (this.clickEvent == null || this.clickEvent.value() == null) return null;
        return switch (this.clickEvent.key()) {
            case 1 -> new ClickEvent.RunCommand(clickEvent.value());
            case 2 -> new ClickEvent.SuggestCommand(clickEvent.value());
            case 3 -> new ClickEvent.OpenUrl(URI.create(clickEvent.value()));
            default -> null;
        };
    }

    private HoverEvent getHoverEvent() {
        if (this.hoverEvent == null) return null;
        return new HoverEvent.ShowText(this.hoverEvent.b());
    }

    @Override
    public MutableText b() {

        MutableText output = Text.literal("");
        if (this.rainbow.isEnabled()) {
            this.text = this.rainbow.colorize(text.getString(), this).b();
        }

        if (this.button) output.append("[").setStyle(Style.EMPTY.withColor(Formatting.byCode('f')));

        output.append(this.text.styled(style -> style
                .withColor(TextColor.parse(color).result().orElse(TextColor.fromFormatting(Formatting.BLACK)))
                .withClickEvent(getClickEvent())
                .withHoverEvent(getHoverEvent())
                .withItalic(this.italic)
                .withBold(this.bold)
                .withStrikethrough(this.strikethrough)
                .withUnderline(this.underline)
                .withObfuscated(this.obfuscate)));
        if (this.button) output.append("]").setStyle(Style.EMPTY.withColor(Formatting.byCode('f')));

        // make sure everything including the button pieces are styled ?
        output.styled(style -> style
                .withClickEvent(getClickEvent())
                .withHoverEvent(getHoverEvent())
                .withItalic(this.italic)
                .withBold(this.bold)
                .withStrikethrough(this.strikethrough)
                .withUnderline(this.underline)
                .withObfuscated(this.obfuscate));

        for (CTxT txt : this.append) output.append(txt.b());

        return output.copy();
    }

    @Override
    public String toString() {
        return b().getString();
    }
}
