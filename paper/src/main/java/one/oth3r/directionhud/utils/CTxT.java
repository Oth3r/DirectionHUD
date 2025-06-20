package one.oth3r.directionhud.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import one.oth3r.directionhud.common.template.ChatText;

public class CTxT extends ChatText<TextComponent, CTxT> {
    public CTxT() {
        this.text = Component.empty();
    }

    public CTxT(CTxT main) {
        super(main);
    }

    public CTxT(TextComponent text) {
        // i think this copies?
        this.text = text.toBuilder().build();
    }

    public CTxT(String text) {
        this.text = Component.text(text);
    }

    public static CTxT of(String of) {
        return new CTxT(of);
    }

    public static CTxT of(TextComponent of) {
        return new CTxT(of);
    }

    public static CTxT of(CTxT of) {
        return new CTxT(of);
    }

    @Override
    public void copyFromObject(CTxT old) {
        super.copyFromObject(old);
        this.text = old.text.toBuilder().build();
    }

    @Override
    public CTxT clone() {
        return new CTxT(this);
    }

    @Override
    public CTxT text(String text) {
        this.text = Component.text(text);
        return this;
    }

    public CTxT text(CTxT text) {
        this.text = text.text.toBuilder().build();
        return this;
    }

    public CTxT text(TextComponent text) {
        this.text = text.toBuilder().build();
        return this;
    }
    
    @Override
    public CTxT append(String append) {
        this.append(new CTxT(append));
        return this;
    }

    @Override
    public CTxT append(TextComponent append) {
        this.append(new CTxT(append));
        return this;
    }

    private ClickEvent getClickEvent() {
        if (this.clickEvent == null || this.clickEvent.value() == null) return null;
        return switch (this.clickEvent.key()) {
            case 1 -> ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, clickEvent.value());
            case 2 -> ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickEvent.value());
            case 3 -> ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, clickEvent.value());
            default -> null;
        };
    }
    private HoverEvent<Component> getHoverEvent() {
        if (this.hoverEvent == null) return null;
        return HoverEvent.showText(this.hoverEvent.b());
    }

    @Override
    public TextComponent b() {
        TextComponent.Builder output = Component.text();
        if (this.rainbow.isEnabled()) {
            //todo the rainbow in the HUD doesnt work for some reason
            text = this.rainbow.colorize(PlainTextComponentSerializer.plainText().serialize(text), this).b();
        } else text = text.toBuilder().color(TextColor.fromHexString(this.color)).build();

        text.clickEvent(getClickEvent());
        text.hoverEvent(getHoverEvent());
        if (this.italic) text.decorate(TextDecoration.ITALIC);
        if (this.bold) text.decorate(TextDecoration.BOLD);
        if (this.strikethrough) text.decorate(TextDecoration.STRIKETHROUGH);
        if (this.underline) text.decorate(TextDecoration.UNDERLINED);

        if (this.button) output.append(Component.text("["));
        output.append(text);
        if (this.button) output.append(Component.text("]"));
        output.clickEvent(getClickEvent());
        output.hoverEvent(getHoverEvent());
        if (this.italic) output.decorate(TextDecoration.ITALIC);
        if (this.bold) output.decorate(TextDecoration.BOLD);
        if (this.strikethrough) output.decorate(TextDecoration.STRIKETHROUGH);
        if (this.underline) output.decorate(TextDecoration.UNDERLINED);
        for (CTxT txt : this.append) output.append(txt.b());
        return output.build();
    }

    @Override
    public String toString() {
        return PlainTextComponentSerializer.plainText().serialize(b());
    }

}
