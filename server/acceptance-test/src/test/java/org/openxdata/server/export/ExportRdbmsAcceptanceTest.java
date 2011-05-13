package org.openxdata.server.export;

import java.util.List;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.openxdata.server.export.rdbms.engine.RdmsEngine;
import org.openxdata.server.export.rdbms.engine.TableQuery;

/**
 *
 * @author Jonny Heggheim
 */
@RunWith(ConcordionRunner.class)
public class ExportRdbmsAcceptanceTest {

    public String generateTables(String definition) throws Exception {
        StringBuilder result = new StringBuilder();
        List<TableQuery> queries = RdmsEngine.getStructureSql(definition);
        for (TableQuery tableQuery : queries) {
            String sql = tableQuery.getSql();
            result.append(sql);
            result.append("\n");
        }

        return result.toString();
    }

}
