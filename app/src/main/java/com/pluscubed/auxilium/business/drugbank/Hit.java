
package com.pluscubed.auxilium.business.drugbank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hit {

    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("value")
    @Expose
    private String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Hit{" +
                "field='" + field + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
