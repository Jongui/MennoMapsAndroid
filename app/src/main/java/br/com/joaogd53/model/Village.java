package br.com.joaogd53.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Model class for Villages
 */

public class Village implements ClusterItem{
    private String name;
    private String colonyGroup;
    private String district;
    private Uri uri;
    private LatLng position;
    private String description;
    private float hueColor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColonyGroup() {
        return colonyGroup;
    }

    public void setColonyGroup(String colonyGroup) {
        this.colonyGroup = colonyGroup;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public float getHueColor() {
        return hueColor;
    }

    public void setHueColor(float hueColor) {
        this.hueColor = hueColor;
    }

    @Override
    public LatLng getPosition() {
        return this.position;
    }

    @Override
    public String getTitle() {
        return this.name;
    }

    @Override
    public String getSnippet() {
        return this.description;
    }
}
