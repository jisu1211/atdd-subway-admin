package nextstep.subway.section.ui;

public class SectionRequest {
    private String downStationId;
    private String upStationId;
    private int distance;

    public SectionRequest() {
    }

    private SectionRequest(String downStationId, String upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionRequest of(String downStationId, String upStationId, int distance) {
        return new SectionRequest(downStationId, upStationId, distance);
    }

    public String getDownStationId() {
        return downStationId;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
