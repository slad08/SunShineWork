package android.bignerdranch.com.myapplimenu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragmenPlaceHold = new ForecastFragment();
        FragmentTransaction fragmTrans = getFragmentManager().beginTransaction()
                .add(R.id.container_activity_frag, fragmenPlaceHold);
        fragmTrans.commit();

    }

}
