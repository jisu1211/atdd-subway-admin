package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.util.Assert;

@Embeddable
public class Distance {

    @Column(name = "distance", nullable = false)
    public int value;

    protected Distance() {
    }

    private Distance(int value) {
        Assert.isTrue(positive(value), String.format("distance value(%d) must be positive", value));
        this.value = value;
    }

    public static Distance from(int value) {
        return new Distance(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    private boolean positive(int value) {
        return value >= 0;
    }
}