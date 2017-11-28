package br.com.joaogd53.mennomaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.ColorUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;

/**
 * Village cluster marker
 */

public class VillageIconRendered extends DefaultClusterRenderer<Village> {

    private final IconGenerator mClusterIconGenerator;
    private Context mContext;

    public VillageIconRendered(Context context, GoogleMap map, ClusterManager<Village> clusterManager) {
        super(context, map, clusterManager);
        mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
        mContext = context;

    }

    @Override
    protected void onBeforeClusterItemRendered(Village item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(item.getHueColor()));
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Village> cluster,
                                           MarkerOptions markerOptions) {
        List<String> countryNames = this.findCountriesNames(cluster);
        StringBuilder sb = new StringBuilder();
        for (String country : countryNames){
            sb.append(country);
            sb.append(", ");
            //title += country + "\t";
        }
        String title = sb.toString();

        markerOptions.title(title);

        List<String> coloniesNames = this.findColoniesNames(cluster);
        sb = new StringBuilder();
        sb.append("Colonies: ");
        for(String colonyName : coloniesNames){
            sb.append(colonyName);
            sb.append(", ");
        }
        String snippet = sb.toString();
        markerOptions.snippet(snippet);
        float avg, total = 0f;

        for (Village v : cluster.getItems()) {
            total += v.getHueColor();
        }

        avg = total / cluster.getItems().size();

        GradientDrawable d = (GradientDrawable)  mContext.getResources().getDrawable(R.drawable.background_circle);
        d.setColor(ColorUtils.HSLToColor(new float[]{avg, 0.5f, 0.5f}));
        mClusterIconGenerator.setBackground(d);
        try {
            mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
        } catch (Exception ex){
            ex.getStackTrace();
        }
        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

    }

    private List<String> findCountriesNames(Cluster<Village> cluster) {
        List<String> ret = new ArrayList<>();
        HashMap<String, String> countriesHash = new HashMap<>();
        for (Village v : cluster.getItems()) {
            try {
                String dummy = countriesHash.get(v.getCountry());
                if (dummy == null) {
                    countriesHash.put(v.getCountry(), v.getName());
                    Locale locale = new Locale("", v.getCountry());
                    ret.add(locale.getDisplayCountry());
                }
            } catch (NullPointerException ex){
                ex.getStackTrace();
            }
        }

        return ret;
    }

    private List<String> findColoniesNames(Cluster<Village> cluster) {
        List<String> ret = new ArrayList<>();
        HashMap<String, String> colonyHash = new HashMap<>();
        for(Village v : cluster.getItems()){
            String colonyGroup = colonyHash.get(v.getColonyGroup());
            if (colonyGroup == null){
                colonyHash.put(v.getColonyGroup(), v.getName());
                ret.add(v.getColonyGroup());
            }
        }
        return ret;
    }

}
