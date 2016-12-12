package com.nexttapp.cara.controller;


import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.nexttapp.cara.MainActivity;
import com.nexttapp.cara.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public class UserPicker extends BaseController {

    @BindView(R.id.customer_btn)
    protected TextView customerBtn;
    @BindView(R.id.driver_btn)
    protected TextView driverBtn;

    private boolean clicked = false;

    @Override
    protected int getLayoutID() {
        return R.layout.view_user_picker;
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void render() {

    }

    @OnClick(R.id.customer_btn)
    protected void onCustomerClicked() {
        if (clicked) return;
        onBtnClicked(customerBtn);
        gotoCustomer();
    }

    @OnClick(R.id.driver_btn)
    protected void onDriverClicked() {
        if (clicked) return;
        onBtnClicked(driverBtn);
        gotoDriver();
    }

    private void onBtnClicked(TextView btn) {
        clicked = true;
        btn.setBackgroundResource(R.color.text_color);
        btn.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
    }

    private void gotoCustomer() {
        MainActivity activity = (MainActivity)getActivity();
        activity.loadCustomer();
    }

    private void gotoDriver() {
        MainActivity activity = (MainActivity)getActivity();
        activity.loadDriver();
    }
}
