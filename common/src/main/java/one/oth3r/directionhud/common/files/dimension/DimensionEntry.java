package one.oth3r.directionhud.common.files.dimension;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.Assets;

public class DimensionEntry {

    @SerializedName("id")
    private String id = "";

    @SerializedName("name")
    private String name = "";

    @SerializedName("color")
    private String color = "";

    @SerializedName("time")
    private Time time = new Time();

    public DimensionEntry() {}

    public DimensionEntry(String id, String name, String color, Time time) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.time = time;
    }

    public DimensionEntry(DimensionEntry dimensionEntry) {
        this.id = dimensionEntry.id;
        this.name = dimensionEntry.name;
        this.color = dimensionEntry.color;
        this.time = new Time(dimensionEntry.time);
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static class Time {

        @SerializedName("enabled")
        private Boolean enabled = false;

        @SerializedName("weather")
        private Weather weather = new Weather();

        public Time() {}

        public Time(Boolean enabled) {
            this.enabled = enabled;
        }

        public Time(Weather weather) {
            this.enabled = true;
            this.weather = weather;
        }

        public Time(Time time) {
            this.enabled = time.enabled;
            this.weather = new Weather(time.weather);
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public Weather getWeather() {
            return weather;
        }

        public static class Weather {
            @SerializedName("enabled")
            private Boolean enabled = false;

            @SerializedName("night-ticks")
            private NightTicks nightTicks;

            @SerializedName("icons")
            private Icons icons;

            public Weather() {}

            public Weather(NightTicks nightTicks, Icons icons) {
                this.icons = icons;
                this.nightTicks = nightTicks;
                this.enabled = true;
            }

            public Weather(Weather weather) {
                this.enabled = weather.enabled;
                this.nightTicks = weather.nightTicks;
                this.icons = weather.icons;
            }

            public Boolean getEnabled() {
                return enabled;
            }

            public NightTicks getNightTicks() {
                return nightTicks;
            }

            public Icons getIcons() {
                return icons;
            }

            public record NightTicks(
                    TimePair normal, TimePair storm, TimePair thunderstorm) {

                @Override
                public TimePair normal() {
                    return normal;
                }

                @Override
                public TimePair storm() {
                    return storm;
                }

                @Override
                public TimePair thunderstorm() {
                    return thunderstorm;
                }
            }

            public record Icons(
                    String day, String night, String storm, String thunderstorm) {
                public static Icons defaultIcons() {
                    return new Icons(Assets.symbols.sun,Assets.symbols.moon,Assets.symbols.rain,Assets.symbols.lighting_bolt);
                }

                @Override
                public String day() {
                    return day;
                }

                @Override
                public String night() {
                    return night;
                }

                @Override
                public String storm() {
                    return storm;
                }

                @Override
                public String thunderstorm() {
                    return thunderstorm;
                }
            }
        }

        public record TimePair(Integer startTick, Integer endTick) {
            public String toString() {
                return "(Start tick: " + this.startTick + ", End tick: " + this.endTick + ")";
            }
        }
    }
}
