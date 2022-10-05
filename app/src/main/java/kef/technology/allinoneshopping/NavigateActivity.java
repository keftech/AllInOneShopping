package kef.technology.allinoneshopping;

import static kef.technology.allinoneshopping.MainActivity.PREFS_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kef.technology.allinoneshopping.databinding.ActivityNavigateBinding;

public class NavigateActivity extends AppCompatActivity {

    protected static final String ITEM_KEY = "ItemKey";
    private AdView adView;
    private FrameLayout adContainer;
    private SharedPreferences prefs;
    private MenuItem searchItem;
    private ActivityNavigateBinding binding;
    private int position;
    private GridItemAdapter allItemsAdapter, favItemsdapter;
    private List<ListItem> allGridItems;

    private static final String JUMIA_URL = "https://kol.jumia.com/api/click/custom/81038060-a4ea-4994-9818-dcf8cd71ec72/306ad549-764c-349e-a497-cdd2d98c349a?r=https%3A%2F%2Fwww.jumia.com.ng%2F&s1=KefTech&s2=BBB&s3=AllInOne&s4=Nigeria",
            KONGA_URL = "https://www.konga.com/",
            ALIEXPRESS_URL = "https://s.click.aliexpress.com/e/_97nvx5",
            AMAZON_URL = "https://www.amazon.com/",
            JIJI_URL = "https://www.jiji.ng/",
            KARA_URL = "http://www.kara.com.ng/",
            GEARBEST_URL = "https://www.gearbest.com/",
            SPAR_NIGERIA_URL = "https://www.sparnigeria.com/",
            DELUXE_URL = "https://www.deluxe.com.ng/",
            SUPERMART_URL = "https://www.supermart.ng/",
            SLOT_URL = "https://www.slot.ng/",
            _3CHUB_URL = "https://www.3chub.com/",
            KUSNAP_URL = "https://www.kusnap.com/",
            AJEBO_MARKET_URL = "https://www.ajebomarket.com/",
            OBIWEZY_URL = "https://www.obiwezy.com/",
            PRINTIVO_URL = "https://www.printivo.com/",
            VCONNECT_URL = "https://www.vconnect.com/",
            PAYPORTE_URL = "https://www.payporte.com/",
            WEBMALLNG_URL = "http://www.webmallng.com/",
            KILIMALL_URL = "https://www.kilimall.ng/",
            JAMARAHOME_URL = "https://www.jamarahome.com/",
            APP_LINK = "https://play.google.com/store/apps/details?id=kef.technology.shoppingnigeria";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        adContainer = binding.adContainer;
        //adContainer.post(() -> loadBannerAd(binding.getRoot().getWidth()));

        init();

        binding.gridFavourite.listTitle.setText(getString(R.string.favorite_sites));
        favItemsdapter = new GridItemAdapter(this, new ArrayList<>(), this::openPage);
        binding.gridFavourite.gridList.setAdapter(favItemsdapter);

        allItemsAdapter = new GridItemAdapter(this, allGridItems, this::openPage);
        binding.gridAllLay.gridList.setAdapter(allItemsAdapter);

        refreshList();
        openPage(getIntent().getData());
    }

    private void init(){
        ListItem jumiaItem = new ListItem(getString(R.string.jumia), R.drawable.jumia, JUMIA_URL);
        ListItem aliItem = new ListItem(getString(R.string.aliexpress), R.drawable.aliexpress, ALIEXPRESS_URL);
        ListItem amazonItem = new ListItem(getString(R.string.amazon), R.drawable.amazon, AMAZON_URL);
        ListItem jijiItem = new ListItem(getString(R.string.jiji), R.drawable.jiji, JIJI_URL);
        ListItem kongaItem = new ListItem(getString(R.string.konga), R.drawable.konga, KONGA_URL);
        ListItem jamaraItem = new ListItem(getString(R.string.jamarahome), R.drawable.jamara, JAMARAHOME_URL);
        ListItem gearbestItem = new ListItem(getString(R.string.gearbest), R.drawable.gearbest, GEARBEST_URL);
        ListItem karaItem = new ListItem(getString(R.string.kara), R.drawable.kara, KARA_URL);
        ListItem deluxeItem = new ListItem(getString(R.string.deluxe), R.drawable.deluxe, DELUXE_URL);
        ListItem slotItem = new ListItem(getString(R.string.slot), R.drawable.slot, SLOT_URL);
        ListItem ajeboItem = new ListItem(getString(R.string.ajebo_market), R.drawable.ajebo, AJEBO_MARKET_URL);
        ListItem vconnectItem = new ListItem(getString(R.string.vconnect), R.drawable.vconnect, VCONNECT_URL);
        ListItem payporteItem = new ListItem(getString(R.string.payporte), R.drawable.payporte, PAYPORTE_URL);
        ListItem sparItem = new ListItem(getString(R.string.spar), R.drawable.spar_logo, SPAR_NIGERIA_URL);
        ListItem supermartItem = new ListItem(getString(R.string.supermart), R.drawable.supermart, SUPERMART_URL);
        ListItem _3chubItem = new ListItem(getString(R.string._3chub), R.drawable._3chub, _3CHUB_URL);
        ListItem kusnapItem = new ListItem(getString(R.string.kusnap), R.drawable.kusnap, KUSNAP_URL);
        ListItem obiwezyItem = new ListItem(getString(R.string.obiwezy), R.drawable.obiwezy, OBIWEZY_URL);
        ListItem webmallItem = new ListItem(getString(R.string.webmallng), R.drawable.webmall, WEBMALLNG_URL);
        ListItem kilimallItem = new ListItem(getString(R.string.kilimall), R.drawable.kilimall, KILIMALL_URL);
        ListItem printivoItem = new ListItem(getString(R.string.printivo), R.drawable.printivo, PRINTIVO_URL);

        jumiaItem.setNum_freq(prefs.getInt(jumiaItem.getTitle(), 0));
        aliItem.setNum_freq(prefs.getInt(aliItem.getTitle(), 0));
        amazonItem.setNum_freq(prefs.getInt(amazonItem.getTitle(), 0));
        jijiItem.setNum_freq(prefs.getInt(jijiItem.getTitle(), 0));
        kongaItem.setNum_freq(prefs.getInt(kongaItem.getTitle(), 0));
        jamaraItem.setNum_freq(prefs.getInt(jamaraItem.getTitle(), 0));
        gearbestItem.setNum_freq(prefs.getInt(gearbestItem.getTitle(), 0));
        karaItem.setNum_freq(prefs.getInt(karaItem.getTitle(), 0));
        deluxeItem.setNum_freq(prefs.getInt(deluxeItem.getTitle(), 0));
        slotItem.setNum_freq(prefs.getInt(slotItem.getTitle(), 0));
        ajeboItem.setNum_freq(prefs.getInt(ajeboItem.getTitle(), 0));
        vconnectItem.setNum_freq(prefs.getInt(vconnectItem.getTitle(), 0));
        payporteItem.setNum_freq(prefs.getInt(payporteItem.getTitle(), 0));
        sparItem.setNum_freq(prefs.getInt(sparItem.getTitle(), 0));
        supermartItem.setNum_freq(prefs.getInt(supermartItem.getTitle(), 0));
        _3chubItem.setNum_freq(prefs.getInt(_3chubItem.getTitle(), 0));
        kusnapItem.setNum_freq(prefs.getInt(kusnapItem.getTitle(), 0));
        obiwezyItem.setNum_freq(prefs.getInt(obiwezyItem.getTitle(), 0));
        webmallItem.setNum_freq(prefs.getInt(webmallItem.getTitle(), 0));
        kilimallItem.setNum_freq(prefs.getInt(kilimallItem.getTitle(), 0));
        printivoItem.setNum_freq(prefs.getInt(printivoItem.getTitle(), 0));

        allGridItems = new ArrayList<>();
        allGridItems.add(jumiaItem);
        allGridItems.add(aliItem);
        allGridItems.add(amazonItem);
        allGridItems.add(jijiItem);
        allGridItems.add(kongaItem);
        allGridItems.add(jamaraItem);
        allGridItems.add(gearbestItem);
        allGridItems.add(karaItem);
        allGridItems.add(slotItem);
        allGridItems.add(deluxeItem);
        allGridItems.add(ajeboItem);
        allGridItems.add(vconnectItem);
        allGridItems.add(payporteItem);
        allGridItems.add(sparItem);
        allGridItems.add(supermartItem);
        allGridItems.add(_3chubItem);
        allGridItems.add(kusnapItem);
        allGridItems.add(obiwezyItem);
        allGridItems.add(webmallItem);
        allGridItems.add(kilimallItem);
        allGridItems.add(printivoItem);
    }

    private void refreshList(){
        List<ListItem> favouriteItems = new ArrayList<>();
        List<ListItem> allRemainItems = new ArrayList<>(allGridItems);
        Collections.sort(allRemainItems, (item1, item2) -> Integer.compare(item1.getNum_freq(), item2.getNum_freq()));
        Collections.reverse(allRemainItems);
        int numItem = 0;
        for(ListItem item : allRemainItems){
            if(numItem == 3)
                break;
            if(item.getNum_freq() > 0) {
                favouriteItems.add(item);
                numItem++;
            }
        }
        if(!favouriteItems.isEmpty()){
            binding.gridFavourite.getRoot().setVisibility(View.VISIBLE);
            updateList(favItemsdapter, favouriteItems);
            for(ListItem item: favouriteItems){
                allRemainItems.remove(item);
            }
        }
        Collections.sort(allRemainItems, (item1, item2) -> item1.getTitle().compareTo(item2.getTitle()));
        updateList(allItemsAdapter, allRemainItems);
        allItemsAdapter.notifyItemRangeChanged(0, allItemsAdapter.getItemCount());
    }

    private void updateList(GridItemAdapter adapter, List<ListItem> itemsList){
        List<ListItem> newList = new ArrayList<>(itemsList);
        adapter.copiedList.clear();
        adapter.copiedList.addAll(newList);
        adapter.updateList(newList);
    }

    private void openPage(Uri uri){
        if(uri != null){
            Intent pageIntent = new Intent(this, MainActivity.class);
            pageIntent.setData(uri);
            startActivity(pageIntent);
        }
    }

    private void openPage(ListItem item){
        if(item != null){
            if(!((SearchView)searchItem.getActionView()).isIconified())
                searchItem.collapseActionView();
            position = allGridItems.indexOf(item);
            Intent pageIntent = new Intent(this, MainActivity.class);
            pageIntent.putExtra(ITEM_KEY, item);
            activityResultLauncher.launch(pageIntent);
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().hasExtra(ITEM_KEY)){
            ListItem item = (ListItem) result.getData().getSerializableExtra(ITEM_KEY);
            allGridItems.remove(position);
            allGridItems.add(position, item);
            refreshList();
        }
    });

    private void loadBannerAd(int width){
        adView = new AdView(this);
        adView.setAdSize(getAdsize(width, this));
        adView.setAdUnitId("ca-app-pub-8327472816877927/7232639795");//"ca-app-pub-3940256099942544/6300978111");
        adContainer.removeAllViews();
        adContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    protected static AdSize getAdsize(int width, Context context){
        width /= context.getResources().getDisplayMetrics().density;
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width);
    }

    private void share(String textToShare){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(intent,getString(R.string.share_using)));
    }

    private void loadExternal(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.share_btn)
            share(getString(R.string.download_our_app)+"\n"+APP_LINK);
        else if(item.getItemId() == R.id.more_apps)
            loadExternal("https://play.google.com/store/apps/dev?id=6543429764882580007");
        else if(item.getItemId() == R.id.rate_us)
            loadExternal(APP_LINK);
        else if(item.getItemId() == R.id.privacy_btn)
            new BottomSheet(this, "https://www.kefblog.com.ng/p/jumia-app-privacy-policy.html").show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.navigate, menu);
        searchItem = menu.findItem(R.id.menu_search);
        SearchView menuSearchview = (SearchView) searchItem.getActionView();
        menuSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return filterList(query);}

            @Override
            public boolean onQueryTextChange(String newText) {return filterList(newText);}
        });
        return true;
    }

    private boolean filterList(String newText){
        favItemsdapter.getFilter().filter(newText);
        allItemsAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    protected void onPause() {
        if(adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    protected void onDestroy() {
        if(adView != null)
            adView.destroy();
        super.onDestroy();
    }
}
