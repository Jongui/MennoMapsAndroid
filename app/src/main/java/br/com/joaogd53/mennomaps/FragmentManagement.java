package br.com.joaogd53.mennomaps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentManagement {

    public static final int MAPS_FRAGMENT = 0;
    public static final int VILLAGE_FRAGMENT = 1;

    private static FragmentManagement instance;

    public static FragmentManagement getInstance(){
        if(instance == null)
            instance = new FragmentManagement();
        return instance;
    }

    private FragmentManagement(){

    }

    public void callFragment(int fragment, Bundle bundle, FragmentManager fm){
        Fragment f = new Fragment();
        FragmentTransaction ft = fm.beginTransaction();
        String tag = "";
        switch (fragment){
            case MAPS_FRAGMENT:
                f = new MapsFragment();
                fm.popBackStack("control", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                tag = "MAPS_FRAGMENT";
                break;
            case VILLAGE_FRAGMENT:
                f = new VillageFragment();
                //fm.popBackStack("control", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                tag = "VILLAGE_FRAGMENT";
                break;
        }
        f.setArguments(bundle);
        ft.replace(R.id.container, f, tag).addToBackStack(null).commit();
    }


}
