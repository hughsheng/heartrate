package com.guyuan.heartrate.ui;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.guyuan.heartrate.R;
import com.guyuan.heartrate.base.BaseActivity;
import com.guyuan.heartrate.databinding.ActivityMainBinding;
import com.guyuan.heartrate.ui.earphone.EarphoneFragment;
import com.guyuan.heartrate.ui.sport.SportFragment;
import com.guyuan.heartrate.util.ActivityUtils;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private Fragment currentFragment;
    private SportFragment sportFragment;
    private EarphoneFragment earphoneFragment;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setNav();
        initFragment();
    }

    private void initFragment() {
        sportFragment = SportFragment.newInstance();
        earphoneFragment = EarphoneFragment.newInstance();
        currentFragment = sportFragment;//设置运动为初始界面
        ActivityUtils.addFragmentToActivity(fragmentManager, currentFragment,
                R.id.fragment_container, SportFragment.TAG);
    }


    private void setNav() {
        RadioGroup rp = findViewById(R.id.rd_group);
        rp.setOnCheckedChangeListener(this);
    }


    //切换fragment
    private void switchContent(Fragment from, Fragment to, String tag) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_container, to, tag).commitAllowingStateLoss(); //
                // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.home_sport:
                switchContent(currentFragment,sportFragment, SportFragment.TAG);
                break;

            case R.id.home_earphone:
                switchContent(currentFragment,earphoneFragment, EarphoneFragment.TAG);
                break;

            default:

                break;
        }
    }
}