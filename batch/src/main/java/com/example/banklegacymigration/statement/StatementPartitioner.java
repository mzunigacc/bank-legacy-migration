package com.example.banklegacymigration.statement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class StatementPartitioner implements Partitioner {

    private static final Path FILE_PATH =
            Path.of("data/cuentas_anuales.csv");

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        Map<String, ExecutionContext> partitions = new HashMap<>();

        int totalRecords = countRecords();

        int baseSize = totalRecords / gridSize;
        int remainder = totalRecords % gridSize;

        int start = 0;

        for (int i = 0; i < gridSize; i++) {

            int partitionSize =
                    baseSize + (i < remainder ? 1 : 0);

            int end = start + partitionSize;

            ExecutionContext context = new ExecutionContext();

            context.putInt("start", start);
            context.putInt("end", end);

            partitions.put("partition" + i, context);

            start = end;
        }

        return partitions;
    }

    private int countRecords() {

        try (var lines = Files.lines(FILE_PATH)) {

            long totalLines = lines.count();

            return Math.max(0, (int) totalLines - 1);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No se pudo contar los registros de "
                            + FILE_PATH,
                    e
            );
        }
    }
}