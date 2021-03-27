package com.example.contactless_attendance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CameraSource cameraSource = null;

    private CameraPreview preview;
    TextView status;
    boolean allowToSend = true;
    // permission of play service/// khabar nahi kem but keep in mind
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preview = (CameraPreview) findViewById(R.id.preview_holder);
        status = (TextView) findViewById(R.id.status);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
        else{
            initCamera();
        }


    }

    private void initCamera() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w("MAIN ACTIVITY", "Face detector dependencies are not yet available.");
        }

        cameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(720, 720).setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                preview.start(cameraSource);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 100 && requestCode != 101) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initCamera();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contactless Attendance")
                .setMessage("We need camera permission! please grant from settings.")
                .setPositiveButton("OK", listener)
                .show();
    }


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new FaceTracker();
        }
    }

    private class FaceTracker extends Tracker<Face>{
        @Override
        public void onNewItem(int i, Face face) {
            int[] pixels = new int[480*480];
            cameraSource.takePicture(new CameraSource.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(@NonNull byte[] bytes) {
                    if(allowToSend){
                        try{
                            File dir = new File(MainActivity.this.getFilesDir(),"FaceData");
                            dir.mkdir();
                            File bytesData = new File(dir,"temp"+".jpeg");
                            boolean isCreate = bytesData.createNewFile();
                            FileOutputStream fos=new FileOutputStream(bytesData.getPath());
                            fos.write(bytes);
                            fos.close();
                            allowToSend = false;
                            uploadRequest(bytesData);

                        }
                        catch (Exception e){
                            Toast.makeText(MainActivity.this,"FILE ERROR:"+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            super.onNewItem(i, face);
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    private void uploadRequest(File bytesData) {
        Log.e("IN REQ","REQ"+bytesData.getAbsolutePath());
        Payload service = RetrofitClientInstance.createService(Payload.class);

        RequestBody requestFile =
            RequestBody.create(MediaType.parse("image/jpeg"),bytesData);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", bytesData.getName(), requestFile);
        String text = "req test";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, text);

        Call<ResponseBody> call = service.upload(description, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,Response<ResponseBody> response) {
                allowToSend = true;
                try{
                    status.setText("Detected:"+response.body().string());
                }
                catch(Exception e){
                    Toast.makeText(MainActivity.this,"RESPONSE ERROR:"+e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                allowToSend = true;
                Toast.makeText(MainActivity.this,"Upload error:"+t.getMessage(),Toast.LENGTH_LONG);
            }
        });

    }
}
