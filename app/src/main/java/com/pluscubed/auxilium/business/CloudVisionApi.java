package com.pluscubed.auxilium.business;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.pluscubed.auxilium.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Emitter;
import rx.Observable;
import timber.log.Timber;

public class CloudVisionApi {

    private static CloudVisionApi INSTANCE;
    private Context context;

    public CloudVisionApi(Context context) {
        this.context = context;
    }

    public static CloudVisionApi get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CloudVisionApi(context);
        }
        return INSTANCE;
    }

    /*public Observable<String> getTextDetection(File uri, String token) {
        if (uri != null) {
            return callTextDetection(uri, token);
        } else {
           return Observable.just("Null image was returned.");
        }
    }*/

    public Observable<List<String>> callTextDetection(Bitmap bitmap, String accessToken) {
        return Observable.fromEmitter(stringEmitter -> {
            try {
                Bitmap newBitmap = Utils.resizeBitmap(bitmap);

                GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, credential);
                Vision vision = builder.build();

                List<Feature> featureList = new ArrayList<>();

                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(50);
                featureList.add(textDetection);

                List<AnnotateImageRequest> imageList = new ArrayList<>();
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                Image base64EncodedImage = getBase64EncodedJpeg(newBitmap);
                annotateImageRequest.setImage(base64EncodedImage);
                annotateImageRequest.setFeatures(featureList);
                imageList.add(annotateImageRequest);

                BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                        new BatchAnnotateImagesRequest();
                batchAnnotateImagesRequest.setRequests(imageList);

                Vision.Images.Annotate annotateRequest =
                        vision.images().annotate(batchAnnotateImagesRequest);
                // Due to a bug: requests to Vision API containing large images fail when GZipped.
                annotateRequest.setDisableGZipContent(true);
                Timber.d("sending request");

                BatchAnnotateImagesResponse response = annotateRequest.execute();
                stringEmitter.onNext(convertResponseToString(response));

            } catch (IOException e) {
                stringEmitter.onError(e);
            }

        }, Emitter.BackpressureMode.BUFFER);
    }

    private List<String> convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("Results:\n\n");

        message.append("Texts:\n");
        List<String> strings = new ArrayList<>();
        List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();
        /*if (texts != null) {
            texts.remove(0);
            for (EntityAnnotation text : texts) {
                //message.append(text.getDescription());
               // message.append("\n=========\n");
                strings.add(text.getDescription());
            }
        } else {
            message.append("nothing\n");
        }*/
        strings.add(texts.get(0).getDescription());

        return strings;
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }
}
