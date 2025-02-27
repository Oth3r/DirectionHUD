package one.oth3r.directionhud.common.files;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets.symbols.arrows;
import one.oth3r.directionhud.common.template.CustomFile;
import org.jetbrains.annotations.NotNull;

public class ModuleText implements CustomFile<ModuleText> {
    @SerializedName("version")
    private Double version = 1.0;

    @SerializedName("coordinates")
    private ModuleCoordinates coordinates = new ModuleCoordinates();
    @SerializedName("destination")
    private ModuleDestination destination = new ModuleDestination();
    @SerializedName("distance")
    private ModuleDistance distance = new ModuleDistance();
    @SerializedName("tracking")
    private ModuleTracking tracking = new ModuleTracking();
    @SerializedName("direction")
    private ModuleDirection direction = new ModuleDirection();
    @SerializedName("weather")
    private ModuleWeather weather = new ModuleWeather();
    @SerializedName("time")
    private ModuleTime time = new ModuleTime();
    @SerializedName("angle")
    private ModuleAngle angle = new ModuleAngle();
    @SerializedName("speed")
    private ModuleSpeed speed = new ModuleSpeed();

    public ModuleCoordinates getCoordinates() {
        return coordinates;
    }

    public ModuleDestination getDestination() {
        return destination;
    }

    public ModuleDistance getDistance() {
        return distance;
    }

    public ModuleTracking getTracking() {
        return tracking;
    }

    public ModuleDirection getDirection() {
        return direction;
    }

    public ModuleWeather getWeather() {
        return weather;
    }

    public ModuleTime getTime() {
        return time;
    }

    public ModuleAngle getAngle() {
        return angle;
    }

    public ModuleSpeed getSpeed() {
        return speed;
    }

    @Override
    public void reset() {
        coordinates = new ModuleCoordinates();
        destination = new ModuleDestination();
        distance = new ModuleDistance();
        tracking = new ModuleTracking();
        direction = new ModuleDirection();
        weather = new ModuleWeather();
        time = new ModuleTime();
        angle = new ModuleAngle();
        speed = new ModuleSpeed();
    }

    @Override
    public @NotNull Class<ModuleText> getFileClass() {
        return ModuleText.class;
    }

    @Override
    public void copyFileData(ModuleText newFile) {
        this.coordinates = newFile.coordinates;
        this.destination = newFile.destination;
        this.distance = newFile.distance;
        this.tracking = newFile.tracking;
        this.direction = newFile.direction;
        this.weather = newFile.weather;
        this.time = newFile.time;
        this.angle = newFile.angle;
        this.speed = newFile.speed;
    }

    @Override
    public void update(JsonElement json) {

    }

    @Override
    public String getFileName() {
        return "module-text.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.getData().getConfigDirectory();
    }

    public static class ModuleCoordinates {
        @SerializedName("xyz")
        private String xyz = "&1XYZ: &2%s %s %s";
        @SerializedName("xy")
        private String xy = "&1XY: &2%s %s";

        public String getXyz() {
            return xyz;
        }

        public String getXy() {
            return xy;
        }
    }

    public static class ModuleDestination {
        @SerializedName("xyz")
        private String xyz = "&1[&2%s %s %s&1]";
        @SerializedName("xz")
        private String xz = "&1[&2%s %s&1]";
        @SerializedName("name")
        private String name = "&2[&s%s&1]";

        public String getXyz() {
            return xyz;
        }

        public String getXz() {
            return xz;
        }

        public String getName() {
            return name;
        }
    }

    public static class ModuleDistance {
        @SerializedName("number")
        private String number = "&1[&2%s&1]";

        public String getNumber() {
            return number;
        }
    }

    public static class ModuleTracking {
        public static class Assets {
            @SerializedName("simple")
            private Simple simple = new Simple();
            @SerializedName("compact")
            private Compact compact = new Compact();
            @SerializedName("elevation")
            private Elevation elevation = new Elevation();

            public Simple getSimple() {
                return simple;
            }

            public Compact getCompact() {
                return compact;
            }

            public Elevation getElevation() {
                return elevation;
            }

            public static class Simple extends Directions {

                public Simple() {
                    this.northEast = "-" + arrows.up + arrows.right;
                    this.north = "-" + arrows.up + "-";
                    this.northWest = arrows.left + arrows.up + "-";
                    this.west = arrows.left + "--";
                    this.southWest = arrows.left + arrows.down + "-";
                    this.south = "-" + arrows.down + "-";
                    this.southEast = "-" + arrows.down + arrows.right;
                    this.east = "--" + arrows.right;
                }
            }

            public static class Compact extends Directions {
                public Compact() {
                    this.northEast = arrows.north_east;
                    this.north = arrows.north;
                    this.northWest = arrows.north_west;
                    this.west = arrows.west;
                    this.southWest = arrows.south_west;
                    this.south = arrows.south;
                    this.southEast = arrows.south_east;
                    this.east = arrows.east;
                }

            }

            public static class Elevation {
                @SerializedName("above")
                protected String above = arrows.north;
                @SerializedName("same")
                protected String same = "-";
                @SerializedName("below")
                protected String below = arrows.south;

                public String getAbove() {
                    return above;
                }

                public String getSame() {
                    return same;
                }

                public String getBelow() {
                    return below;
                }
            }
        }
        @SerializedName("assets")
        private Assets assets = new Assets();
        @SerializedName("tracking")
        private String tracking = "&1&s[&r&2%s&1&s]";
        @SerializedName("elevation_tracking")
        private String elevationTracking = "&1&s[&r&2%s&1|&2%s&1&s]";

        public Assets getAssets() {
            return assets;
        }

        public String getTracking() {
            return tracking;
        }

        public String getElevationTracking() {
            return elevationTracking;
        }
    }

    public static class ModuleDirection {
        public static class Assets {
            @SerializedName("cardinal")
            private Cardinal cardinal = new Cardinal();

            public Cardinal getCardinal() {
                return cardinal;
            }

            public static class Cardinal extends Directions {
                public Cardinal() {
                    this.northEast = "NE";
                    this.north = "N";
                    this.northWest = "NW";
                    this.west = "W";
                    this.southWest = "SW";
                    this.south = "S";
                    this.southEast = "SE";
                    this.east = "E";
                }
            }
        }
        @SerializedName("assets")
        private Assets assets = new Assets();
        @SerializedName("facing")
        private String facing = "&1%s";

        public Assets getAssets() {
            return assets;
        }

        public String getFacing() {
            return facing;
        }

    }

    public static class ModuleWeather {
        @SerializedName("weather_single")
        private String weatherSingle = "&1%s";
        @SerializedName("weather")
        private String weather = "&1%s%s";

        public String getWeatherSingle() {
            return weatherSingle;
        }

        public String getWeather() {
            return weather;
        }
    }

    public static class ModuleTime {
        public static class Assets {
            @SerializedName("am")
            private String am = "AM";
            @SerializedName("PM")
            private String pm = "PM";
            @SerializedName("time_separator")
            private String timeSeparator = ":";

            public String getAM() {
                return am;
            }

            public String getPM() {
                return pm;
            }

            public String getTimeSeparator() {
                return timeSeparator;
            }
        }

        @SerializedName("assets")
        private Assets assets = new Assets();
        @SerializedName("hour_12")
        private String hour12 = "&2%s &1%s";
        @SerializedName("hour_24")
        private String hour24 = "&2%s";

        public Assets getAssets() {
            return assets;
        }

        public String getHour12() {
            return hour12;
        }

        public String getHour24() {
            return hour24;
        }
    }

    public static class ModuleAngle {
        @SerializedName("yaw")
        private String yaw = "&2%s";
        @SerializedName("pitch")
        private String pitch = "&2%s";
        @SerializedName("both")
        private String both = "&2%s&1/&2%s";

        public String getYaw() {
            return yaw;
        }

        public String getPitch() {
            return pitch;
        }

        public String getBoth() {
            return both;
        }
    }

    public static class ModuleSpeed {
        @SerializedName("xz_speed")
        private String xzSpeed = "&2%s &1B/S";
        @SerializedName("xyz_speed")
        private String xyzSpeed = "&2%s &1B/S";

        public String getXzSpeed() {
            return xzSpeed;
        }

        public String getXyzSpeed() {
            return xyzSpeed;
        }
    }

    private abstract static class Directions {
        @SerializedName("north_east")
        protected String northEast;
        @SerializedName("north")
        protected String north;
        @SerializedName("north_west")
        protected String northWest;
        @SerializedName("west")
        protected String west;
        @SerializedName("south_west")
        protected String southWest;
        @SerializedName("south")
        protected String south;
        @SerializedName("south_east")
        protected String southEast;
        @SerializedName("east")
        protected String east;

        public String getNorthEast() {
            return northEast;
        }

        public String getNorth() {
            return north;
        }

        public String getNorthWest() {
            return northWest;
        }

        public String getWest() {
            return west;
        }

        public String getSouthWest() {
            return southWest;
        }

        public String getSouth() {
            return south;
        }

        public String getSouthEast() {
            return southEast;
        }

        public String getEast() {
            return east;
        }
    }
}
