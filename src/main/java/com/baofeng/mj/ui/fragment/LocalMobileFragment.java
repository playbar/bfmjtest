package com.baofeng.mj.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baofeng.mj.R;

/**
 * 本地手机Fragment
 * Created by muyu on 2016/4/6.
 */
public class LocalMobileFragment extends BaseFragment{
    private LocalVideoFragment localVideoFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_local_mobile, null);
        replaceLocalVideoFragment();
        return rootView;
    }

    public void replaceLocalVideoFragment() {
        if(localVideoFragment == null){
            localVideoFragment = new LocalVideoFragment();
        }
        transFrag(localVideoFragment);
    }

    private void transFrag(BaseViewPagerFragment fragment){
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.local_mobile_framelayout, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        Log.i("ChoicenessFragment", "onResume   LocalMobileFragment");
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.i("ChoicenessFragment", "isVisible   LocalMobileFragment");
        }
    }
}
