package my.awesome.rootdownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import my.awesome.rootdownloader.R;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater= this.getMenuInflater();
        menuInflater.inflate(R.menu.menus,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.exit:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeMenu);
        loadFragment(new HomeFragment());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
            switch (menuItem.getItemId()){
                case R.id.homeMenu:
                    fragment=new HomeFragment();
                    break;
                case R.id.historyMenu:
                    fragment=new HistoryFragment();
                    break;
                default:
                    return false;
            }
           return loadFragment(fragment);
    }

    public boolean loadFragment(Fragment fragment){
        if (fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment).commit();
            return true;
        }
        return false;
    }

}