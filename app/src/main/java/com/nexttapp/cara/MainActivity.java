package com.nexttapp.cara;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.karumi.dexter.Dexter;
import com.nexttapp.cara.api.APIManager;
import com.nexttapp.cara.controller.BaseController;
import com.nexttapp.cara.controller.Customer;
import com.nexttapp.cara.controller.Driver;
import com.nexttapp.cara.controller.UserPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.activity_main)
//    protected View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this, this);

        Dexter.initialize(this);

        if (Config.api == null) Config.api = new APIManager();

        switch (Config.curView) {
            case 1:
                loadCustomer();
                break;
            case 2:
                loadDriver();
                break;
            default:
                loadPicker();
                break;
        }

    }


    public void loadCustomer() {
        Config.curView = 1;
        loadController(new Customer(), "customer");
    }

    public void loadDriver() {
        Config.curView = 2;
        loadController(new Driver(), "driver");
    }

    private void loadPicker() {
        Config.curView = 0;
        loadController(new UserPicker(), "userpicker");
    }

    private void loadController(BaseController controller, String id) {
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
//        if (dir == LoadDirection.UP) {
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_out_down);
//        } else {
//            transaction.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
//        }
        transaction.add(R.id.activity_main, controller, id);
//        transaction.addToBackStack(id);
        transaction.commit();
//        controller.onActiveView();
    }

}
