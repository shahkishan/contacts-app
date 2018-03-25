package com.example.kishanshah.contactsapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements SignupFragment.OnUserCreatedListener, LoginFragment.OnUserLoggedInListner{

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startMainActivity();
        }

        setContentView(R.layout.activity_login);

        fragmentManager=getSupportFragmentManager();
        viewPager=findViewById(R.id.pager);
        tabLayout=findViewById(R.id.tabs);
        adapter=new PagerAdapter(fragmentManager);
        adapter.addFragment(new LoginFragment(),"Login");
        adapter.addFragment(new SignupFragment(),"Signup");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onUserCreated(FirebaseUser user) {
        startMainActivity();
    }

    @Override
    public void onUserLoggedIn(FirebaseUser user) {
       startMainActivity();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter{
        private List<Fragment> mFragmentList=new ArrayList<>();
        private List<String> mFragmentTitleList=new ArrayList<>();

        public PagerAdapter(android.support.v4.app.FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void startMainActivity(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }


}

