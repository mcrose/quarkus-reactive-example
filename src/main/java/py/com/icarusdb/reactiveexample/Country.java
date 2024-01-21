package py.com.icarusdb.reactiveexample;

import java.util.Objects;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Betto McRose [icarus] / icarusdb@gmail.com
 */
@JsonbNillable
@Entity
@Cacheable
@Table(name = "COUNTRY", schema = "PUBLIC")
@NamedQuery(name = "Country.findAllByName",
            query = "select c from Country c where c.name like :name")
public class Country extends PanacheEntityBase {

    @Id
    @SequenceGenerator(name = "COUNTRY_ID_GENERATOR",
                       schema = "PUBLIC",
                       sequenceName = "COUNTRY_SEQ",
                       allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "COUNTRY_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String name;

    @Column(name = "ISO_CODE_3166_1_ALPHA2", length = 2)
    @Size(max = 2)
    private String isoCodeAlpha2;

    public Country() {
        super();
    }

    public Country(Long id,
            @Size(max = 60) @NotNull String name,
            @Size(max = 2) String isoCodeAlpha2) {
        super();
        this.id = id;
        this.name = name;
        this.isoCodeAlpha2 = isoCodeAlpha2;
    }

    @PrePersist
    public void prePersist() {
        this.isoCodeAlpha2 = (this.isoCodeAlpha2 == null) ? null : this.isoCodeAlpha2.toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCodeAlpha2() {
        return isoCodeAlpha2;
    }

    public void setIsoCodeAlpha2(String isoCodeAlpha2) {
        this.isoCodeAlpha2 = isoCodeAlpha2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isoCodeAlpha2, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Country)) {
            return false;
        }
        Country other = (Country) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(isoCodeAlpha2, other.isoCodeAlpha2)
                && Objects.equals(name, other.name);
    }

    /**
     * updates all current properties with the given parameter except:
     */
    public void updateWith(Country other) {
        this.id = other.id;
        this.name = other.name;
        this.isoCodeAlpha2 = other.isoCodeAlpha2;
    }

}
