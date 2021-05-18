package com.cloud7831.goaltracker.Objects;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "units_table")
public class Units {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String singular;
    private String plural;
    private String reducedSingular; // Max 4 letters. Examples are "min" "hr" "ml" etc
    private String reducedPlural; // Max 4 letters. Examples are "mins" "hrs" "mls" etc
    private int isDeletable; // Units not made by the user should not be deleted.

    public Units(String sing, String plur, String rSing, String rPlur, int isDeletable){
        setIsDeletable(isDeletable);
        setSingular(sing);
        setPlural(plur);
        setReducedSingular(rSing);
        setReducedPlural(rPlur);
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getReducedSingular() {
        return reducedSingular;
    }

    public boolean setReducedSingular(String reducedSingular) {
        // Returns true if the string was set without having it's letters truncated.
        if(reducedSingular.length() <= 4){
            // The string was the proper size.
            this.reducedSingular = reducedSingular;
            return true;
        }
        else{
            this.reducedSingular = reducedSingular.substring(0, 4);
            return false;
        }
    }

    public String getReducedPlural() {
        return reducedPlural;
    }

    public boolean setReducedPlural(String reducedPlural) {
        // Returns true if the string was set without having it's letters truncated.
        if(reducedPlural.length() <= 4){
            // The string was the proper size.
            this.reducedPlural = reducedPlural;
            return true;
        }
        else{
            this.reducedPlural = reducedPlural.substring(0, 4);
            return false;
        }
    }

    public int getIsDeletable() {
        return isDeletable;
    }

    public void setIsDeletable(int isDeletable) {
        this.isDeletable = isDeletable;
    }
}
