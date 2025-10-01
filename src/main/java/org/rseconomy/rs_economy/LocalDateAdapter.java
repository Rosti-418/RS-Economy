/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Gson TypeAdapter for serializing and deserializing LocalDate objects to/from JSON.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    /**
     * Writes a LocalDate to JSON as a string.
     *
     * @param out   The JsonWriter to write to.
     * @param value The LocalDate to serialize.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value.toString());
    }

    /**
     * Reads a LocalDate from JSON as a string.
     *
     * @param in The JsonReader to read from.
     * @return The deserialized LocalDate.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return LocalDate.parse(in.nextString());
    }
}