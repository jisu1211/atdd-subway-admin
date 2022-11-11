package nextstep.subway.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@ActiveProfiles("test")
public class DatabaseCleanup implements InitializingBean {

    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String[] TABLE_TYPE = new String[]{"TABLE"};

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;
    private List<String> tableNames;


    @Override
    public void afterPropertiesSet() throws SQLException {
        List<String> tempTableNames = new ArrayList<>();
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();

        try(ResultSet resultSet = metaData.getTables(null, null, null, TABLE_TYPE)) {
            while(resultSet.next()) {
                tempTableNames.add(resultSet.getString(TABLE_NAME));
            }
        }

        tableNames = tempTableNames;
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}