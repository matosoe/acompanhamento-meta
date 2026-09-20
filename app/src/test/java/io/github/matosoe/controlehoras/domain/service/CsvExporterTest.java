package io.github.matosoe.controlehoras.domain.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Collections;

import org.junit.Test;

import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

public class CsvExporterTest {
    @Test public void emitsBomHeadersEscapesAndOriginalOvernightRecord() {
        CsvExporter exporter = new CsvExporter();
        TimeEntryEntity entry = new TimeEntryEntity(1, 1,
                java.time.LocalDateTime.of(2026, 9, 6, 23, 30).atZone(ZoneId.of("America/Sao_Paulo")).toInstant().toEpochMilli(),
                java.time.LocalDateTime.of(2026, 9, 7, 1, 0).atZone(ZoneId.of("America/Sao_Paulo")).toInstant().toEpochMilli(),
                5400, null, 0, 0);
        CsvExporter.CsvRow row = exporter.row(entry, "Itaú; \"Estudo\"", ZoneId.of("America/Sao_Paulo"));
        byte[] bytes = exporter.export(Collections.singletonList(row), ';');
        assertArrayEquals(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}, java.util.Arrays.copyOf(bytes, 3));
        assertEquals("\uFEFFData;Hora de Início;Hora de Término;Semana;Duração Real;Projeto\r\n"
                        + "2026-09-06;23:30:00;01:00:00;253;01:30:00;\"Itaú; \"\"Estudo\"\"\"\r\n",
                new String(bytes, StandardCharsets.UTF_8));
    }

    @Test public void quotesLineBreaksAndUsesConfiguredCommaSeparator() {
        CsvExporter exporter = new CsvExporter();
        byte[] bytes = exporter.export(Collections.singletonList(new CsvExporter.CsvRow(
                "2026-09-06", "00:00:00", "00:01:00", 253, "00:01:00", "Casa\n\"teste\"")), ',');
        assertEquals("\uFEFFData,Hora de Início,Hora de Término,Semana,Duração Real,Projeto\r\n"
                        + "2026-09-06,00:00:00,00:01:00,253,00:01:00,\"Casa\n\"\"teste\"\"\"\r\n",
                new String(bytes, StandardCharsets.UTF_8));
    }
}
