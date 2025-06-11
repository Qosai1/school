package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnhancedAdsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EnhancedAdsAdapter adapter;
    private List<EnhancedAdvertisement> advertisementList;
    private List<EnhancedAdvertisement> filteredList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private Spinner categorySpinner;
    private SearchView searchView;
    private String currentCategory = "الكل";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_ads);


        Toolbar toolbar = findViewById(R.id.adsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("إعلانات المدرسة");
        }

        recyclerView = findViewById(R.id.recyclerViewAds);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        categorySpinner = findViewById(R.id.categorySpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        advertisementList = new ArrayList<>();
        filteredList = new ArrayList<>();

        setupCategorySpinner();

        adapter = new EnhancedAdsAdapter(this, filteredList);
        adapter.setOnAdClickListener(new EnhancedAdsAdapter.OnAdClickListener() {
            @Override
            public void onAdClick(EnhancedAdvertisement advertisement, int position) {
                Toast.makeText(EnhancedAdsActivity.this,
                        "تم النقر على: " + advertisement.getTitle(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShareClick(EnhancedAdvertisement advertisement) {
            }

            @Override
            public void onSaveClick(EnhancedAdvertisement advertisement, int position) {
                advertisement.setSaved(!advertisement.isSaved());
                adapter.notifyItemChanged(position);

                if (advertisement.isSaved()) {
                    Toast.makeText(EnhancedAdsActivity.this,
                            "تم حفظ الإعلان",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EnhancedAdsActivity.this,
                            "تم إلغاء حفظ الإعلان",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshAds);

        loadAds();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"الكل", "عروض", "أخبار", "فعاليات"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = parent.getItemAtPosition(position).toString();
                filterAds();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadAds() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Simulate  delay with  background thread
        new Thread(() -> {
            try {
                Thread.sleep(1500);

                List<EnhancedAdvertisement> ads = createSampleAds();

                runOnUiThread(() -> {
                    advertisementList.clear();
                    advertisementList.addAll(ads);
                    filterAds();

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void refreshAds() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());

                    EnhancedAdvertisement newAd = new EnhancedAdvertisement(
                            "إعلان جديد",
                            "تم إضافة هذا الإعلان للتو عند تحديث الصفحة",
                            R.drawable.default_ad_image,
                            "أخبار",
                            cal.getTime());

                    advertisementList.add(0, newAd);
                    filterAds();

                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(EnhancedAdsActivity.this,
                            "تم تحديث الإعلانات",
                            Toast.LENGTH_SHORT).show();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }
        }).start();
    }

    private List<EnhancedAdvertisement> createSampleAds() {
        List<EnhancedAdvertisement> ads = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -2);
        Date twoDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -3);
        Date fiveDaysAgo = cal.getTime();

        EnhancedAdvertisement ad1 = new EnhancedAdvertisement(
                "عروض على الكتب",
                "خصم 50% على جميع الكتب المدرسية",
                R.drawable.book_discount_image,
                "عروض",
                today);

        EnhancedAdvertisement ad2 = new EnhancedAdvertisement(
                "عروض نهاية الفصل",
                "خصم 30% على جميع المستلزمات",
                R.drawable.sale_image,
                "عروض",
                twoDaysAgo);

        EnhancedAdvertisement ad3 = new EnhancedAdvertisement(
                "حفل نهاية العام",
                "دعوة لحضور حفل نهاية العام الدراسي",
                R.drawable.default_ad_image,
                "فعاليات",
                fiveDaysAgo);

        EnhancedAdvertisement ad4 = new EnhancedAdvertisement(
                "نتائج الامتحانات",
                "تم إعلان نتائج امتحانات الفصل الأول",
                R.drawable.default_ad_image,
                "أخبار",
                today);

        ads.add(ad1);
        ads.add(ad2);
        ads.add(ad3);
        ads.add(ad4);

        return ads;
    }

    private void filterAds() {
        filteredList.clear();

        for (EnhancedAdvertisement ad : advertisementList) {

            boolean categoryMatch = currentCategory.equals("الكل") ||
                    ad.getCategory().equals(currentCategory);


            boolean searchMatch = currentQuery.isEmpty() ||
                    ad.getTitle().toLowerCase().contains(currentQuery.toLowerCase()) ||
                    ad.getDescription().toLowerCase().contains(currentQuery.toLowerCase());

            if (categoryMatch && searchMatch) {
                filteredList.add(ad);
            }
        }

        Collections.sort(filteredList, (ad1, ad2) ->
                ad2.getPublishDate().compareTo(ad1.getPublishDate()));

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ads_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("بحث في الإعلانات...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    currentQuery = query;
                    filterAds();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = newText;
                    filterAds();
                    return true;
                }
            });
        } else {
            Log.e("EnhancedAdsActivity", "MenuItem for search not found.");
        }

        return true;
    }
}