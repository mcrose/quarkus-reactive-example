package py.com.icarusdb.reactiveexample;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import py.com.icarusdb.reactiveexample.exception.CustomPersistanceException;
import py.com.icarusdb.reactiveexample.exception.RequiredParameterException;

/**
 * @author Betto McRose [icarus] / icarusdb@gmail.com
 */
@ApplicationScoped
public class CountryRepository implements PanacheRepositoryBase<Country, Long> {

    private static final int DEFAULT_MAX_TIME_QUERY = 5000;

    /**
     * @param country
     * @return created entity in a lazy asynchronous action
     */
    public Uni<Country> create(Country country) {
        return Panache.withTransaction(country::persist).replaceWith(country)
                .ifNoItem().after(Duration.ofMillis(1000)).fail()
                .onFailure().transform(CustomPersistanceException::new);
    }

    /**
     * @param country
     * @return updated entity in a lazy asynchronous action
     */
    public Uni<Country> update(Country country) {
        return Panache.withTransaction(
                () -> findById(country.getId())
                        .onItem().ifNotNull().transform(entity -> {
                            entity.updateWith(country);
                            return entity;
                        })
                        .onFailure().transform(CustomPersistanceException::new));
    }

    /**
     * @param country
     * @return <code>true</code> of <code>false</code> of delete operation
     */
    public Uni<Boolean> delete(Long id) {
        return Panache.withTransaction(() -> deleteById(id));
    }

    /**
     * @return entity list in a lazy asynchronous action
     */
    public Uni<List<Country>> all() {
        return find("select c from Country c").list()
                .ifNoItem().after(Duration.ofMillis(DEFAULT_MAX_TIME_QUERY)).fail()
                .onFailure().recoverWithUni(emptyCollection());
    }

    /**
     * @return entity list in a lazy asynchronous action
     */
    public Uni<List<Country>> findAllByName(final String name) {
        if (isBlank(name)) {
            return Uni.createFrom().failure(new RequiredParameterException("Name is required"));
        }

        final Parameters params = Parameters.with("name", '%' + name + '%');
        return find("#Country.findAllByName", params).list()
                .ifNoItem().after(Duration.ofMillis(DEFAULT_MAX_TIME_QUERY)).fail()
                .onFailure().recoverWithUni(emptyCollection());
    }

    private Uni<List<Country>> emptyCollection() {
        return Uni.createFrom().<List<Country>> item(Collections.emptyList());
    }

}
