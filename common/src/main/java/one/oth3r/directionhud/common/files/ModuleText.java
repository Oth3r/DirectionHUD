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
        this.coordinates = new ModuleCoordinates(newFile.coordinates);
        this.destination = new ModuleDestination(newFile.destination);
        this.distance = new ModuleDistance(newFile.distance);
        this.tracking = new ModuleTracking(newFile.tracking);
        this.direction = new ModuleDirection(newFile.direction);
        this.weather = new ModuleWeather(newFile.weather);
        this.time = new ModuleTime(newFile.time);
        this.angle = new ModuleAngle(newFile.angle);
        this.speed = new ModuleSpeed(newFile.speed);
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
        @SerializedName("xz")
        private String xz = "&1XZ: &2%s %s";

        public ModuleCoordinates() {}
        public ModuleCoordinates(ModuleCoordinates coordinates) {
            this.xyz = coordinates.xyz;
            this.xz = coordinates.xz;
        }

        public String getXyz() {
            return xyz;
        }

        public String getXz() {
            return xz;
        }
    }

    public static class ModuleDestination {
        @SerializedName("xyz")
        private String xyz = "&1[&2%s %s %s&1]";
        @SerializedName("xz")
        private String xz = "&1[&2%s %s&1]";
        @SerializedName("name")
        private String name = "&2[&1%s&2]";
        @SerializedName("name_xz")
        private String name_xz = "&2[&1%s&2]";

        public ModuleDestination() {}
        public ModuleDestination(ModuleDestination destination) {
            this.xyz = destination.xyz;
            this.xz = destination.xz;
            this.name = destination.name;
            this.name_xz = destination.name_xz;
        }

        public String getXyz() {
            return xyz;
        }

        public String getXz() {
            return xz;
        }

        public String getName() {
            return name;
        }

        public String getNameXz() {
            return name_xz;
        }
    }

    public static class ModuleDistance {
        @SerializedName("number")
        private String number = "&1[&2%s&1]";

        public ModuleDistance() {}
        public ModuleDistance(ModuleDistance distance) {
            this.number = distance.number;
        }

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

            public Assets() {}
            public Assets(Assets assets) {
                this.simple = new Simple(assets.simple);
                this.compact = new Compact(assets.compact);
                this.elevation = new Elevation(assets.elevation);
            }

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

                public Simple(Simple directions) {
                    super(directions);
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

                public Compact(Compact directions) {
                    super(directions);
                }
            }

            public static class Elevation {
                @SerializedName("above")
                protected String above = arrows.north;
                @SerializedName("same")
                protected String same = "-";
                @SerializedName("below")
                protected String below = arrows.south;

                public Elevation() {}
                public Elevation(Elevation elevation) {
                    this.above = elevation.above;
                    this.same = elevation.same;
                    this.below = elevation.below;
                }

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

        public ModuleTracking() {}
        public ModuleTracking(ModuleTracking moduleTracking) {
            this.assets = new Assets(moduleTracking.assets);
            this.tracking = moduleTracking.tracking;
            this.elevationTracking = moduleTracking.elevationTracking;
        }

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

            public Assets() {}
            public Assets(Assets assets) {
                this.cardinal = new Cardinal(assets.cardinal);
            }

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

                public Cardinal(Cardinal directions) {
                    super(directions);
                }
            }
        }
        @SerializedName("assets")
        private Assets assets = new Assets();
        @SerializedName("facing")
        private String facing = "&1%s";

        public ModuleDirection() {}
        public ModuleDirection(ModuleDirection direction) {
            this.assets = new Assets(direction.assets);
            this.facing = direction.facing;
        }

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

        public ModuleWeather() {}
        public ModuleWeather(ModuleWeather weather) {
            this.weatherSingle = weather.weatherSingle;
            this.weather = weather.weather;
        }

        public String getWeatherSingle() {
            return weatherSingle;
        }

        public String getWeather() {
            return weather;
        }
    }

    public static class ModuleTime {

        @SerializedName("hour_AM")
        private String hourAM = "&2%s&1:&2%s &1AM";
        @SerializedName("hour_PM")
        private String hourPM = "&2%s&1:&2%s &1PM";
        @SerializedName("hour_24")
        private String hour24 = "&2%s&1:&2%s";

        public ModuleTime() {}
        public ModuleTime(ModuleTime time) {
            this.hourAM = time.hourAM;
            this.hourPM = time.hourPM;
            this.hour24 = time.hour24;
        }

        public String getHourAM() {
            return hourAM;
        }

        public String getHourPM() {
            return hourPM;
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

        public ModuleAngle() {}
        public ModuleAngle(ModuleAngle angle) {
            this.yaw = angle.yaw;
            this.pitch = angle.pitch;
            this.both = angle.both;
        }

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

        public ModuleSpeed() {}

        public ModuleSpeed(ModuleSpeed speed) {
            this.xzSpeed = speed.xzSpeed;
            this.xyzSpeed = speed.xyzSpeed;
        }

        public String getXzSpeed() {
            return xzSpeed;
        }

        public String getXyzSpeed() {
            return xyzSpeed;
        }
    }

    /**
     * helper class that contains all the cardinal directions
     */
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

        public Directions() {}
        public Directions(Directions directions) {
            this.northEast = directions.northEast;
            this.north = directions.north;
            this.northWest = directions.northWest;
            this.west = directions.west;
            this.southWest = directions.southWest;
            this.south = directions.south;
            this.southEast = directions.southEast;
            this.east = directions.east;
        }

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
