package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Edges")
public class Edge {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private int type_id;
    @DatabaseField(uniqueCombo = true)
    private int source_id;
    @DatabaseField(uniqueCombo = true)
    private int target_id;
    @DatabaseField
    private Date modified = null;

    public Edge(int id, int type_id, int source_id, int target_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.type_id = type_id;
        this.source_id = source_id;
        this.target_id = target_id;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Edge(int id, int type_id, int source_id, int target_id, Date modified) {
        this.id = id;
        this.type_id = type_id;
        this.source_id = source_id;
        this.target_id = target_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Edge() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public int getType_id() {
        return type_id;
    }

    public int getSource_id() {
        return source_id;
    }

    public int getTarget_id() {
        return target_id;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }
}
