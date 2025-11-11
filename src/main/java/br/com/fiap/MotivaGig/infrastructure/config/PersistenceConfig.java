package br.com.fiap.motivagig.infrastructure.config;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import br.com.fiap.motivagig.infrastructure.persistence.DatabaseConnection;
import br.com.fiap.motivagig.infrastructure.persistence.DatabaseConnectionImpl;

@ApplicationScoped
public class PersistenceConfig {

    @Produces
    public DatabaseConnection databaseConnection(AgroalDataSource ds) {
        return new DatabaseConnectionImpl(ds);
    }
}
