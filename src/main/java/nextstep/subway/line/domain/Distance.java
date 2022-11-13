package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if(!NumberUtil.isPositiveNumber(distance)) {
            throw new IllegalArgumentException(
                    SectionExceptionCode.DO_NOT_ALLOW_NEGATIVE_NUMBER_DISTANCE.getMessage());
        }
    }

    public void calculate(int distance) {
        if(this.distance <= distance) {
            throw new IllegalArgumentException(SectionExceptionCode.INVALID_DISTANCE.getMessage());
        }

        this.distance = (this.distance - distance);
    }

    public int getDistance() {
        return distance;
    }
}
