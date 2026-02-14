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
    private Integer year;


    @Column(name = "co2_kt", precision = 15, scale = 3)
    private BigDecimal co2Kt;


    @Column(name = "co2_kt_pending", precision = 15, scale = 3)
    private BigDecimal co2KtPending;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 20)
    private String unit;

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getCo2Kt() {
        return co2Kt;
    }

    public void setCo2Kt(BigDecimal co2Kt) {
        this.co2Kt = co2Kt;
    }

    public BigDecimal getCo2KtPending() {
        return co2KtPending;
    }

    public void setCo2KtPending(BigDecimal co2KtPending) {
        this.co2KtPending = co2KtPending;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

 
}
