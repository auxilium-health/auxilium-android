package com.pluscubed.auxilium.choose;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pluscubed.auxilium.AddMedicationController;
import com.pluscubed.auxilium.BundleBuilder;
import com.pluscubed.auxilium.R;
import com.pluscubed.auxilium.base.RefWatchingController;
import com.pluscubed.auxilium.business.DrugBankApi;
import com.pluscubed.auxilium.business.drugbank.Product;

import java.util.List;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ChooseController extends RefWatchingController {

    public static final String ARG_SEARCH = "products";

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.search)
    EditText editText;
    @BindView(R.id.searchIcon)
    View search;

    private ChooseAdapter adapter;
    private DrugBankApi.DrugBankService service;
    private String initialSearch;

    public ChooseController() {
        super();
    }

    protected ChooseController(Bundle args) {
        super(args);
    }

    public ChooseController(String search) {
        this(new BundleBuilder(new Bundle())
                .putString(ARG_SEARCH, search)
                .build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.view_choose_drug, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.drugbankplus.com/v1/us/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(DrugBankApi.DrugBankService.class);

        //resultTextView.setText(TextUtils.join("\n", products));

        initialSearch = getArgs().getString(ARG_SEARCH);
        editText.setText(initialSearch);

        adapter = new ChooseAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        /*RxTextView.textChanges(editText)
                .filter(s -> s.length() > 2)
                .debounce(5000, TimeUnit.MILLISECONDS)
                .compose(new SearchTransformer())
                .subscribe(new SearchSubscriber());*/

        search.setOnClickListener(v -> Observable.just(editText.getText().toString())
                .compose(new SearchTransformer())
                .subscribe(new SearchSubscriber()));

        Observable.just(initialSearch)
                .compose(new SearchTransformer())
                .subscribe(new SearchSubscriber());
    }

    public void onClick(Product product) {
        ((AddMedicationController) getTargetController()).setMedication(product);
        getRouter().popCurrentController();
    }

    private class SearchSubscriber extends Subscriber<List<Product>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e);
        }

        @Override
        public void onNext(List<Product> products) {
            adapter.setItems(products);
        }
    }

    private class SearchTransformer implements Observable.Transformer<CharSequence, List<Product>> {
        @Override
        public Observable<List<Product>> call(Observable<CharSequence> obs) {
            return obs.subscribeOn(Schedulers.io())
                    .switchMap(charSequence -> service.getDrugBankSearchResponse(ChooseController.this.getActivity().getString(R.string.drugbank_api), charSequence.toString()))
                    .map((drugBankResponse) -> drugBankResponse.getProducts())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }
}
