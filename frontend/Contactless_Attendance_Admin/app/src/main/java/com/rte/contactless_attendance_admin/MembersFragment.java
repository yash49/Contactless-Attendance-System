package com.rte.contactless_attendance_admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class MembersFragment extends Fragment {
    EditText nameBox, emailBox;
    Button imageSelector, saveBtn;
    ImageView preview_img;
    File byteData;
    RecyclerView membersContainer;
    MembersAdapter adapter;
    DatabaseReference database;
    ArrayList<HashMap<String,String>> data = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.members_fragment, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       nameBox = view.findViewById(R.id.user_name);
       emailBox = view.findViewById(R.id.user_email);
       imageSelector = view.findViewById(R.id.photo_btn);
       saveBtn = view.findViewById(R.id.save_btn);
       preview_img = view.findViewById(R.id.preview_img);
       membersContainer = view.findViewById(R.id.members_container);
       adapter = new MembersAdapter(getContext(),data);

       imageSelector.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showFileChooser();
           }
       });
       saveBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(byteData == null) Log.e("ERROR:","NULL BYTES");
               if(byteData != null)sendRequest(byteData);
           }
       });

       membersContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
       membersContainer.setAdapter(adapter);

       database = FirebaseDatabase.getInstance().getReference();
       database.child("users").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               for(DataSnapshot snapshot1 : snapshot.getChildren()){
                   if(snapshot1.getKey().equals("GS2"))continue;

                   HashMap<String,String> temp = (HashMap<String, String>) snapshot1.getValue();
                   if(data.contains(temp))continue;
                   data.add(temp);
                   adapter.notifyDataSetChanged();
                }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a photo of member to upload"),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
         }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        Log.e("ERROR1:","path:"+new File(uri.getPath()).getAbsolutePath());
                        Bitmap bitmap = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            try {
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContext().getContentResolver(), uri));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        else {
                            try {
                                bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        preview_img.setImageBitmap(bitmap);
                        File dir = new File(getActivity().getFilesDir(),"FaceData");
                        dir.mkdir();
                        byteData = new File(dir,"upload"+System.currentTimeMillis()+".jpeg");
                        try {
                            byteData.createNewFile();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            FileOutputStream fos = new FileOutputStream(byteData);
                            fos.write(outputStream.toByteArray());
                            fos.flush();fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
    }

    void sendRequest(File bytesData){
        MemberPayload service = RetrofitClientInstance.createService(MemberPayload.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),bytesData);
        MultipartBody.Part profilePhoto = MultipartBody.Part.createFormData("image", bytesData.getName(), requestFile);

        final String name = nameBox.getText().toString().trim();
        String email = emailBox.getText().toString().trim();

        if(name.length() == 0 || email.length() == 0 ){
            Toast.makeText(getActivity(),"Please fill out all the details properly!",Toast.LENGTH_LONG).show();
            return;
        }
        if(name.equals(email)){
            Toast.makeText(getActivity(),"Name email could not be same!",Toast.LENGTH_LONG).show();
            return;
        }

        RequestBody nameLoad = RequestBody.create(okhttp3.MultipartBody.FORM, name);
        RequestBody emailLoad = RequestBody.create(okhttp3.MultipartBody.FORM, email);

        Call<ResponseBody> call = service.insertdata(nameLoad, emailLoad, profilePhoto);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try{
                    JSONObject res = new JSONObject();
                    res = new Gson().fromJson(response.body().string(),JSONObject.class);
                    Toast.makeText(getActivity(),response.body().string(),Toast.LENGTH_LONG).show();
                    emailBox.setText("");
                    nameBox.setText("");
                    preview_img.setImageDrawable(getResources().getDrawable(R.drawable.scanner_ic));
                }
                catch(Exception e){
                    //Toast.makeText(getActivity(),"RESPONSE ERROR:"+e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("HERE:","ERROR:"+t.getMessage());
                Toast.makeText(getActivity(),"Upload error:"+t.getMessage(),Toast.LENGTH_LONG);
            }
        });

    }
}
