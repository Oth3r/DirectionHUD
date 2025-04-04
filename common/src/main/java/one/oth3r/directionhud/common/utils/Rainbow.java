package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.CTxT;

import java.awt.*;

public class Rainbow {
    protected boolean enabled = false;
    protected float position = 0;
    protected float stepSize = 5;
    protected float brightness = 1;
    protected float saturation = 1;

    public Rainbow(boolean enabled, float position, float stepSize, float brightness, float saturation) {
        this.enabled = enabled;
        this.position = position;
        this.stepSize = stepSize;
        this.brightness = brightness;
        this.saturation = saturation;
    }

    public Rainbow(float position, float stepSize) {
        this.enabled = true;
        this.position = position;
        this.stepSize = stepSize;
    }

    public Rainbow() {}

    public Rainbow(Rainbow rainbow) {
        this.enabled = rainbow.enabled;
        this.position = rainbow.position;
        this.stepSize = rainbow.stepSize;
        this.brightness = rainbow.brightness;
        this.saturation = rainbow.saturation;
    }

    public Rainbow(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public float getStepSize() {
        return stepSize;
    }

    public void setStepSize(float stepSize) {
        this.stepSize = stepSize;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    /**
     * makes a string ranbow using the varibles in the Rainbow class
     * @return a colorized CTxT object
     */
    public CTxT colorize(String target, CTxT settings) {
        // if not enabled, don't send a string
        if (!enabled) return CTxT.of(target);

        // get the hue as the position
        float hue = position;

        // create the TxT to add too
        CTxT rainbow = CTxT.of("");

        // loop for the text length
        for (int i = 0; i < target.codePointCount(0, target.length()); i++) {
            // if empty, skip
            if (target.charAt(i) == ' ') {
                rainbow.append(" ");
                continue;
            }

            // get the color from the hue, sat and brightness
            Color color = Color.getHSBColor(hue / 360.0f, saturation, brightness);
            // convert to hex string
            String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

            rainbow.append(CTxT.of(Character.toString(target.codePointAt(i)))
                    .color(hexColor)
                    // copy the settings of the settings CTxT EXCEPT for button
                    .bold(settings.isBold())
                    .italic(settings.isItalic())
                    .strikethrough(settings.isStrikethrough())
                    .underline(settings.isUnderline())
                    .obfuscate(settings.isObfuscated())
                    .click(settings.getClick())
                    .hover(settings.getHover()));

            // bump the hue
            hue = (hue+stepSize) % 360f;
        }
        // update the position to the new ending point
        position = hue;
        return rainbow;
    }
}
