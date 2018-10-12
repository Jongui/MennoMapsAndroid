package br.com.joaogd53.mennomaps;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import br.com.joaogd53.dao.VillageFirebaseDAO;
import br.com.joaogd53.model.Village;
import br.com.joaogd53.utils.NetworkUtils;

public class VillageFragment extends Fragment {

    private Village mVillage;

    public VillageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View returnView = inflater.inflate(R.layout.fragment_village,
                container, false);

        Bundle bundle = this.getArguments();
        int idVillage = bundle.getInt("idVillage");
        if (NetworkUtils.networkIsConnected(this.getActivity())) {
            this.mVillage = VillageFirebaseDAO.getInstance().findByFirebaseKey(idVillage);
        } else {
            this.mVillage = Village.getVillageAtIndex(idVillage);
        }
        this.loadComponents(returnView);
        return returnView;
    }

    private void loadComponents(View returnView) {
        TextView txtName = returnView.findViewById(R.id.txtName);
        txtName.setText(this.mVillage.getName());
        float textColor = this.mVillage.getHueColor() * 4 / 2;
        if(textColor > 360)
            textColor -= 360;
        txtName.setTextColor(Color.HSVToColor(new float[]{textColor, 0.5f, 0.5f}));

        TextView txtCountry = returnView.findViewById(R.id.txtCountry);
        Locale l = new Locale("", this.mVillage.getCountry());
        txtCountry.setText(l.getDisplayCountry());
        txtCountry.setTextColor(Color.HSVToColor(new float[]{textColor, 0.5f, 0.5f}));

        RelativeLayout villageHeader = returnView.findViewById(R.id.village_header);
        villageHeader.setBackgroundColor(Color.HSVToColor(new float[]{this.mVillage.getHueColor(), 1.0f, 1.0f}));

        TextView txtColonyGroup = returnView.findViewById(R.id.txtColonyGroup);
        txtColonyGroup.setText(this.mVillage.getColonyGroup());

        TextView txtLatitude = returnView.findViewById(R.id.txtLatitude);
        txtLatitude.setText(String.valueOf(this.mVillage.getLatitude()));

        TextView txtLongitude = returnView.findViewById(R.id.txtLongitude);
        txtLongitude.setText(String.valueOf(this.mVillage.getLongitude()));

        TextView txtDescription = returnView.findViewById(R.id.txtDescription);
        txtDescription.setText(this.mVillage.getDescription());

        Context context = this.getActivity();
        String flagName = this.mVillage.getCountry().toLowerCase();
        ImageView imgFlag = returnView.findViewById(R.id.imgFlag);
        imgFlag.setImageResource(context.getResources().getIdentifier("drawable/" + flagName, null, context.getPackageName()));

    }

}
