package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import one.oth3r.directionhud.common.template.ChatText;

public class CTxT extends ChatText<TextComponent, CTxT> {
    public CTxT() {
        this.text = new TextComponent("");
    }

    public CTxT(CTxT main) {
        super(main);
    }

    public CTxT(TextComponent text) {
        this.text = text.duplicate();
    }

    public CTxT(String text) {
        this.text = new TextComponent(text);
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
        this.text = old.text.duplicate();
    }

    @Override
    public CTxT clone() {
        return new CTxT(this);
    }

    @Override
    public CTxT text(String text) {
        this.text = new TextComponent(text);
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
            case 1 -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickEvent.value());
            case 2 -> new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickEvent.value());
            case 3 -> new ClickEvent(ClickEvent.Action.OPEN_URL, clickEvent.value());
            default -> null;
        };
    }
    private HoverEvent getHoverEvent() {
        if (this.hoverEvent == null) return null;
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder(this.hoverEvent.b()).create()));
    }

    @Override
    public TextComponent b() {
        TextComponent output = new TextComponent();
        if (this.rainbow.isEnabled()) {
            text = this.rainbow.colorize(text.getText(), this).b();
        } else text.setColor(ChatColor.of(this.color));
        text.setClickEvent(getClickEvent());
        text.setHoverEvent(getHoverEvent());
        text.setItalic(this.italic);
        text.setBold(this.bold);
        text.setStrikethrough(this.strikethrough);
        text.setUnderlined(this.underline);

        if (this.button) output.addExtra("[");
        output.addExtra(text);
        if (this.button) output.addExtra("]");
        output.setClickEvent(getClickEvent());
        output.setHoverEvent(getHoverEvent());
        output.setItalic(this.italic);
        output.setBold(this.bold);
        output.setStrikethrough(this.strikethrough);
        output.setUnderlined(this.underline);
        for (CTxT txt : this.append) output.addExtra(txt.b());
        return output;
    }

    @Override
    public String toString() {
        return b().toPlainText();
    }

}
