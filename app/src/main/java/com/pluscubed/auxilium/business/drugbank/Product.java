
package com.pluscubed.auxilium.business.drugbank;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    @SerializedName("hits")
    @Expose
    private List<Hit> hits = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("brands")
    @Expose
    private List<String> brands = null;
    @SerializedName("ndc_product_codes")
    @Expose
    private List<String> ndcProductCodes = null;
    @SerializedName("dosage_forms")
    @Expose
    private List<String> dosageForms = null;
    @SerializedName("strength_number")
    @Expose
    private String strengthNumber;
    @SerializedName("strength_unit")
    @Expose
    private String strengthUnit;
    @SerializedName("route")
    @Expose
    private String route;
    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients = null;

    public Product() {
    }

    protected Product(Parcel in) {
        this.hits = new ArrayList<Hit>();
        in.readList(this.hits, Hit.class.getClassLoader());
        this.name = in.readString();
        this.brands = in.createStringArrayList();
        this.ndcProductCodes = in.createStringArrayList();
        this.dosageForms = in.createStringArrayList();
        this.strengthNumber = in.readString();
        this.strengthUnit = in.readString();
        this.route = in.readString();
        this.ingredients = new ArrayList<>();
        in.readList(this.ingredients, Ingredient.class.getClassLoader());
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public List<String> getNdcProductCodes() {
        return ndcProductCodes;
    }

    public void setNdcProductCodes(List<String> ndcProductCodes) {
        this.ndcProductCodes = ndcProductCodes;
    }

    public List<String> getDosageForms() {
        return dosageForms;
    }

    public void setDosageForms(List<String> dosageForms) {
        this.dosageForms = dosageForms;
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Product{" +
                "hits=" + hits +
                ", name='" + name + '\'' +
                ", brands=" + brands +
                ", ndcProductCodes=" + ndcProductCodes +
                ", dosageForms=" + dosageForms +
                ", strengthNumber='" + strengthNumber + '\'' +
                ", strengthUnit='" + strengthUnit + '\'' +
                ", route='" + route + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (hits != null ? !hits.equals(product.hits) : product.hits != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (brands != null ? !brands.equals(product.brands) : product.brands != null) return false;
        if (ndcProductCodes != null ? !ndcProductCodes.equals(product.ndcProductCodes) : product.ndcProductCodes != null)
            return false;
        if (dosageForms != null ? !dosageForms.equals(product.dosageForms) : product.dosageForms != null)
            return false;
        if (strengthNumber != null ? !strengthNumber.equals(product.strengthNumber) : product.strengthNumber != null)
            return false;
        if (strengthUnit != null ? !strengthUnit.equals(product.strengthUnit) : product.strengthUnit != null)
            return false;
        if (route != null ? !route.equals(product.route) : product.route != null) return false;
        return ingredients != null ? ingredients.equals(product.ingredients) : product.ingredients == null;

    }

    @Override
    public int hashCode() {
        int result = hits != null ? hits.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (brands != null ? brands.hashCode() : 0);
        result = 31 * result + (ndcProductCodes != null ? ndcProductCodes.hashCode() : 0);
        result = 31 * result + (dosageForms != null ? dosageForms.hashCode() : 0);
        result = 31 * result + (strengthNumber != null ? strengthNumber.hashCode() : 0);
        result = 31 * result + (strengthUnit != null ? strengthUnit.hashCode() : 0);
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (ingredients != null ? ingredients.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.hits);
        dest.writeString(this.name);
        dest.writeStringList(this.brands);
        dest.writeStringList(this.ndcProductCodes);
        dest.writeStringList(this.dosageForms);
        dest.writeString(this.strengthNumber);
        dest.writeString(this.strengthUnit);
        dest.writeString(this.route);
        dest.writeList(this.ingredients);
    }
}
