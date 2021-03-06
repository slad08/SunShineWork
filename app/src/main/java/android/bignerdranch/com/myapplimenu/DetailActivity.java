package android.bignerdranch.com.myapplimenu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Denis on 11.04.2016.
 */
public class DetailActivity extends ActionBarActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState ==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detal,new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail,menu);
        return true;
    }
    @Override
        public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id ==R.id.action_setting){
            return true;
        }
        return super.onOptionsItemSelected(item);
}
    public static class PlaceholderFragment extends Fragment {
        public  PlaceholderFragment(){

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_deatil,container,false);

        Intent intent = getActivity().getIntent();
            if (intent !=null && intent.hasExtra(Intent.EXTRA_TEXT)){
                String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView)rootView.findViewById(R.id.text_deatil))
                .setText(forecastStr);
            }

            return rootView;
        }

    }
}
