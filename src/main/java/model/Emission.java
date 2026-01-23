package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "emission")
public class Emission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false)
    private int year;

    @Column(name = "co2_kt", nullable = false, precision = 15, scale = 3)
    private BigDecimal co2Kt;

    @Column(nullable = false, length = 20)
    private String unit;

    // Getter / Setter

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getCo2Kt() {
        return co2Kt;
    }

    public void setCo2Kt(BigDecimal co2Kt) {
        this.co2Kt = co2Kt;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
