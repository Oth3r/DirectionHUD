package one.oth3r.directionhud.common.files.dimension;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.utils.Helper.*;

public class RatioEntry {

    @SerializedName("dimension-1")
    private Pair<String,Double> dimension1;

    @SerializedName("dimension-2")
    private Pair<String,Double> dimension2;

    public RatioEntry() {}

    public RatioEntry(Pair<String, Double> dimension1, Pair<String, Double> dimension2) {
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }

    public RatioEntry(RatioEntry ratioEntry) {
        this.dimension1 = ratioEntry.dimension1;
        this.dimension2 = ratioEntry.dimension2;
    }

    public Pair<String, Double> getDimension1() {
        return dimension1;
    }

    public void setDimension1(Pair<String, Double> dimension1) {
        this.dimension1 = dimension1;
    }

    public Pair<String, Double> getDimension2() {
        return dimension2;
    }

    public void setDimension2(Pair<String, Double> dimension2) {
        this.dimension2 = dimension2;
    }

    public Pair<String,String> getDimensionPair() {
        return new Pair<>(dimension1.key(), dimension2.key());
    }

    public Double getRatio() {
        return dimension1.value()/dimension2.value();
    }
}
