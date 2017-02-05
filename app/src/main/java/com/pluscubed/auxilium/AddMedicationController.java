package com.pluscubed.auxilium;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.pluscubed.auxilium.base.RefWatchingController;
import com.pluscubed.auxilium.business.CloudVisionApi;
import com.pluscubed.auxilium.business.GetTokenTask;
import com.pluscubed.auxilium.business.drugbank.Product;
import com.pluscubed.auxilium.choose.ChooseController;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AddMedicationController extends RefWatchingController {

    public static final int RC_FIREBASE_SIGN_IN = 32;
    public static final int REQUEST_GET_AUTH_TOKEN = 45;
    public static final int REQUEST_PERMISSIONS = 34;
    public static final int REQUEST_GALLERY_IMAGE = 37;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.start)
    Button start;
    @BindView(R.id.empty)
    View emptyView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.loaded)
    View loadedView;
    @BindView(R.id.medication)
    TextView medicationText;
    @BindView(R.id.done)
    ImageView done;
    @BindView(R.id.frequencies)
    LinearLayout frequencies;
    @BindView(R.id.interval)
    TextView interval;
    @BindView(R.id.length)
    TextView length;
    private Account account;
    private String accessToken;
    private File imageFile;
    private Product medication;


    private List<Integer> times = new ArrayList<>();
    private String medicine = "";

    private void launchImagePicker() {
        /*Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), REQUEST_GALLERY_IMAGE);*/

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            for (File old : storageDir.listFiles()) {
                old.delete();
            }
            imageFile = null;
            try {
                imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
            } catch (IOException e) {
                e.printStackTrace();
            }


            Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.pluscubed.auxilium.fileprovider", imageFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, REQUEST_GALLERY_IMAGE);
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.view_scan, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        start.setOnClickListener(v -> {
            int check = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);
            if (check == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_PERMISSIONS);
            } else {
                onPermissionsGranted();
            }
        });

        progressBar.setVisibility(View.GONE);

        toolbar.setTitle(R.string.add_medication);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRouter().popCurrentController();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReminderController) getTargetController()).addMedication(times.get(0));
                getRouter().popCurrentController();
            }
        });

        if (medication == null) {
            image.setImageDrawable(null);
            emptyView.setVisibility(View.VISIBLE);
            loadedView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            loadedView.setVisibility(View.VISIBLE);
            interval.setText("Interval: Daily");
            length.setText("Length: Indefinite");
            showSnackbar("Instructions: " + medicine.toUpperCase());
            inflateTimes();
        }

        medicationText.setOnClickListener(v -> openChooseController(medication.getName()));

        if (medication != null) {
            medicationText.setText(medication.getName());
        }

        if (imageFile != null) {
            image.setImageURI(Uri.fromFile(imageFile));
        }
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

    }

    private void onPermissionsGranted() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                .setPermissions(Arrays.asList(Scopes.EMAIL, Scopes.PROFILE, "https://www.googleapis.com/auth/cloud-platform"))
                .build();


        if (auth.getCurrentUser() != null) {
            onFirebaseSignedIn();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(googleIdp))
                            .build(), RC_FIREBASE_SIGN_IN);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void showSnackbar(String string) {
        if (getView() != null)
            Snackbar.make(getView(), string, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_FIREBASE_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                onFirebaseSignedIn();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar("sign_in_cancelled");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar("R.string.no_internet_connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar("R.string.unknown_error");
                    return;
                }
                Timber.e(response.getErrorCode() + "");
            }
        }


        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            progressBar.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            image.setImageURI(Uri.fromFile(imageFile));

            CloudVisionApi.get(getActivity())
                    .callTextDetection(bitmap, accessToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            showSnackbar(e.toString());
                        }

                        @Override
                        public void onNext(List<String> s) {
                            processTexts(s);
                        }
                    });
        }

        if (requestCode == REQUEST_GET_AUTH_TOKEN) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extra = data.getExtras();
                onTokenReceived(extra.getString("authtoken"));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Authorization Failed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void processTexts(List<String> s) {
        medicine = s.get(0).toLowerCase();

        String[] lines = medicine.split("\n");

        int index = medicine.indexOf("once");
        int parsedFrequency = 1;
        if (index == -1) {
            index = medicine.indexOf("twice");
            parsedFrequency = 2;
        }
        if (index == -1) {
            index = medicine.indexOf("thrice");
            parsedFrequency = 3;
        }
        if (index == -1) {
            index = medicine.indexOf("times");
            parsedFrequency = Integer.parseInt(medicine.substring(index - 2, index - 1));
        }
        if (index == -1) {
            parsedFrequency = 1;
        }

        int minutes = 28800;
        for (int i = 0; i < parsedFrequency; i++) {
            times.add(minutes);
            minutes += 14400;
        }

        inflateTimes();


        index = medicine.indexOf("daily");


        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.toLowerCase().contains("ben")) {
                openChooseController(lines[i + 1]);
                break;
            }
        }

        index = medicine.indexOf("take");
        if (index == -1) {
            index = medicine.indexOf("apply");
        }
        if (index == -1) {
            index = medicine.indexOf("use");
        }
        if (index == -1) {
            index = medicine.indexOf("inject");
        }
        if (index == -1) {
            index = 0;
        }
        medicine = medicine.substring(index);
        index = medicine.indexOf("terry");
        if (index == -1) {
            index = medicine.indexOf("rx");
        }
        if (index == -1) {
            index = medicine.indexOf("3");
        }
        if (index == -1) {
            index = medicine.length();
        }
        medicine = medicine.substring(0, index);
    }

    private void inflateTimes() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        simpleDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+00"));
        for (int i = 0; i < times.size(); i++) {
            int time = times.get(i);
            TextView text = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_time, frequencies, false);

            text.setText("Time: " + simpleDateFormat.format(new Date(time * 1000L)));
            int finalI1 = i;
            text.setOnClickListener(v -> {
                int finalI = finalI1;
                TimePickerDialog dpd = TimePickerDialog.newInstance(
                        (view, hourOfDay, minute, second) -> {
                            text.setText("Time: " + simpleDateFormat.format(new Date(hourOfDay * 60 * 60L * 1000 + minute * 60 * 1000L)));
                            times.set(finalI, hourOfDay * 60 * 60 + minute * 60);
                        },
                        time / 60 / 60,
                        time % 60,
                        true
                );
                dpd.show(getActivity().getFragmentManager(), "time-picker");
            });

            frequencies.addView(text);
        }
    }

    private void openChooseController(String string) {
        ChooseController chooseController = new ChooseController(string);
        chooseController.setTargetController(this);
        getRouter().pushController(RouterTransaction.with(chooseController).pushChangeHandler(new FadeChangeHandler()));
    }

    private void onFirebaseSignedIn() {
        tryGetAuthToken();
    }

    private void tryGetAuthToken() {
        String SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        if (account == null) {
            AccountManager am = AccountManager.get(getActivity());
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            for (Account account : accounts) {
                String firebaseEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                if (account.name.equals(firebaseEmail)) {
                    this.account = account;
                    break;
                }
            }
            if (account == null) {
                account = accounts[0];
            }
        }

        new GetTokenTask(this, account, SCOPE, REQUEST_GET_AUTH_TOKEN).execute();

    }

    public void onTokenReceived(String token) {
        accessToken = token;
        launchImagePicker();
    }

    public void setMedication(Product medication) {
        this.medication = medication;
    }
}
