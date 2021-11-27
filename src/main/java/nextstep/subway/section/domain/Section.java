package nextstep.subway.section.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

    public static final Section DUMMY_SECTION = new Section();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_of_section"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_up_station_of_section"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_down_station_of_section"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.from(distance);
    }

    public static Section of(Line line, Long upStationId, Long downStationId, Integer distance) {
        return new Section(line, Station.of(upStationId), Station.of(downStationId), distance);
    }

    public void connectByUpStation(Section section) {
        this.upStation = section.downStation;
        this.distance = this.distance.subtract(section.distance);
    }

    public void connectByDownStation(Section section) {
        this.downStation = section.upStation;
        this.distance = this.distance.subtract(section.distance);
    }

    public void updateForDelete(Section section) {
        this.downStation = section.downStation;
        this.distance = this.distance.add(section.getDistance());
    }

    public boolean isLessThanOrEqualDistance(Section other) {
        return this.distance.lessThanOrEqual(other.distance);
    }

    public boolean isEqualToDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isEqualToUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    Distance getDistance() {
        return distance;
    }

    public boolean isDummy() {
        return this == DUMMY_SECTION;
    }
}
