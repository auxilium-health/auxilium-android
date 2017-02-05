package com.pluscubed.auxilium.business;

import android.accounts.Account;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.pluscubed.auxilium.AddMedicationController;

import java.io.IOException;

public class GetTokenTask extends AsyncTask<Void, Void, String> {
    private final AddMedicationController detectorController;
    String mScope;
    Account mAccount;
    int mRequestCode;

    public GetTokenTask(AddMedicationController activity, Account account, String scope, int requestCode) {
        detectorController = activity;
        this.mScope = scope;
        this.mAccount = account;
        this.mRequestCode = requestCode;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String token = fetchToken();
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String token) {
        super.onPostExecute(token);
        if (token != null) {
            detectorController.onTokenReceived(token);
        }
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        String accessToken;
        try {
            accessToken = GoogleAuthUtil.getToken(detectorController.getActivity(), mAccount, mScope);
            GoogleAuthUtil.clearToken(detectorController.getActivity(), accessToken); // used to remove stale tokens.
            accessToken = GoogleAuthUtil.getToken(detectorController.getActivity(), mAccount, mScope);
            return accessToken;
        } catch (UserRecoverableAuthException userRecoverableException) {
            detectorController.getActivity().startActivityForResult(userRecoverableException.getIntent(), mRequestCode);
        } catch (GoogleAuthException fatalException) {
            fatalException.printStackTrace();
        }
        return null;
    }
}