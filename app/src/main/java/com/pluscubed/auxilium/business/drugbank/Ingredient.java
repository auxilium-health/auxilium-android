
package com.pluscubed.auxilium.business.drugbank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient {

    @SerializedName("drugbank_id")
    @Expose
    private String drugbankId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cas")
    @Expose
    private String cas;
    @SerializedName("strength_number")
    @Expose
    private String strengthNumber;
    @SerializedName("strength_unit")
    @Expose
    private String strengthUnit;

    public String getDrugbankId() {
        return drugbankId;
    }

    public void setDrugbankId(String drugbankId) {
        this.drugbankId = drugbankId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getStrengthNumber() {
        return strengthNumber;
    }

    public void setStrengthNumber(String strengthNumber) {
        this.strengthNumber = strengthNumber;
    }

    public String getStrengthUnit() {
        return strengthUnit;
    }

    public void setStrengthUnit(String strengthUnit) {
        this.strengthUnit = strengthUnit;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "drugbankId='" + drugbankId + '\'' +
                ", name='" + name + '\'' +
                ", cas='" + cas + '\'' +
                ", strengthNumber='" + strengthNumber + '\'' +
                ", strengthUnit='" + strengthUnit + '\'' +
                '}';
    }
}
