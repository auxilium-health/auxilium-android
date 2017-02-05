package com.pluscubed.auxilium.business;

import com.pluscubed.auxilium.business.drugbank.DrugBankResponse;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

public class DrugBankApi {
    public interface DrugBankService {
        @GET("drug_names/simple")
        Observable<DrugBankResponse> getDrugBankSearchResponse(@Header("Authorization") String authorization, @Query("q") String name);

    }
}
