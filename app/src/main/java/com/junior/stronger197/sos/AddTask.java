package com.junior.stronger197.sos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.searchandrescue.CircularTransformation;
import com.example.searchandrescue.CropSquareTransformation;
import com.example.searchandrescue.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class AddTask extends Fragment {

    static final int GALLERY_REQUEST = 1;
    View root;
    private String mNameTask;
    private String mDescribingOfTask;
    private String mCoordinate1;
    private String mCoordinate2 = "No coordinate 2";
    private String mEquipment;
    private String mNaturalConditions;
    private String mTime;
    private String mDate;
    private Uri selectedImage = null;
    private ImageView img;
    private volatile String dbCounter = "";



    private DatabaseReference mRef;
    public int counterFor = 0;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        root = rootView;

        Button addImage = rootView.findViewById(R.id.addImage);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);

            }
        });

        Button addTask =(Button) rootView.findViewById(R.id.addTask); // конпка добавления задачи
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameTask = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.nameTask)).getText().toString();
                mDescribingOfTask = ((EditText) getActivity().findViewById(R.id.describingOfTask)).getText().toString();
                mCoordinate1 = ((EditText) getActivity().findViewById(R.id.coordinate1)).getText().toString();
                mCoordinate2 = ((EditText) getActivity().findViewById(R.id.coordinate2)).getText().toString();
                mEquipment = ((EditText) getActivity().findViewById(R.id.equipment)).getText().toString();
                mNaturalConditions = ((EditText) getActivity().findViewById(R.id.naturalConditions)).getText().toString();
                mTime = ((EditText) getActivity().findViewById(R.id.time)).getText().toString();
                mDate = ((EditText) getActivity().findViewById(R.id.date)).getText().toString();
                if("".equals(mNameTask)|| "".equals(mDescribingOfTask)|| "".equals(mCoordinate1) || "".equals(mEquipment)
                        || "".equals(mNaturalConditions) || "".equals(mTime)) {
                    Toast.makeText(getActivity(), "Одно из полей не заполненно. Пожалуйста, заполните все поля и повторите отправку", Toast.LENGTH_LONG).show();
                }
                else if(user == null){
                    Toast.makeText(getActivity(), "Пожалуйста, авторизируйтесь", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        saveDataToDatabase();
                        counterFor = 1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return rootView;
    }

    private void saveDataToDatabase() {

        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(counterFor == 1) {
                    dbCounter = dataSnapshot.child("counter").getValue(String.class);

                    //Toast.makeText(getActivity(), dbCounter, Toast.LENGTH_SHORT).show();

                    int intCounter = Integer.parseInt(dbCounter);
                    intCounter++;
                    String stringCounter = Integer.toString(intCounter);

                    String imagePath = "gs://forfindpeople.appspot.com/" + "images/" + mNameTask + "_" + stringCounter + "_img"; // путь до обложки
                    // устанавливаем значение
                    mRef.child("tasks").child(stringCounter).child("number").setValue(stringCounter);
                    mRef.child("tasks").child(stringCounter).child("nameOfTask").setValue(mNameTask);
                    mRef.child("tasks").child(stringCounter).child("describing").setValue(mDescribingOfTask);
                    mRef.child("tasks").child(stringCounter).child("Coordinate1").setValue(mCoordinate1);
                    mRef.child("tasks").child(stringCounter).child("Coordinate2").setValue(mCoordinate2);
                    mRef.child("tasks").child(stringCounter).child("Equipment").setValue(mEquipment);
                    mRef.child("tasks").child(stringCounter).child("NaturalConditions").setValue(mNaturalConditions);
                    mRef.child("tasks").child(stringCounter).child("time").setValue(mTime);
                    mRef.child("tasks").child(stringCounter).child("Date").setValue(mDate);
                    mRef.child("tasks").child(stringCounter).child("Relevance").setValue(true);
                    mRef.child("tasks").child(stringCounter).child("someTask").child("0").setValue("Тестовая задача");
                    mRef.child("tasks").child(stringCounter).child("someTaskValue").setValue("-1");

                    if(selectedImage != null) {
                        uploadFile(imagePath, selectedImage);
                        mRef.child("tasks").child(stringCounter).child("UriForPhoto").setValue(imagePath);
                    }
                    else {
                        mRef.child("tasks").child(stringCounter).child("UriForPhoto").setValue("0");
                    }
                    mRef.child("counter").setValue(stringCounter);
                    mRef.child("allTasks").child(stringCounter).setValue(mNameTask);

                    ((EditText) getActivity().findViewById(R.id.nameTask)).setText("");
                    ((EditText) getActivity().findViewById(R.id.describingOfTask)).setText("");
                    ((EditText) getActivity().findViewById(R.id.coordinate1)).setText("");
                    ((EditText) getActivity().findViewById(R.id.coordinate2)).setText("");
                    ((EditText) getActivity().findViewById(R.id.equipment)).setText("");
                    ((EditText) getActivity().findViewById(R.id.naturalConditions)).setText("");
                    ((EditText) getActivity().findViewById(R.id.time)).setText("");
                    ((EditText) getActivity().findViewById(R.id.date)).setText("");
                    selectedImage = null;
                    counterFor = 0;
                    Toast.makeText(getActivity(), "Задача успешно создана", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        super.onActivityResult(requestCode, resultCode, resultIntent);

        img = (ImageView) root.findViewById(R.id.attachesImg);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_REQUEST:
                    selectedImage = resultIntent.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    img.setImageBitmap(bitmap);
                    img.setVisibility(View.VISIBLE);
                    img.setClipToOutline(true);

            }

        }

    }
    private void uploadFile(String path, Uri pathOfFile) {
        //if there is a file to upload
        if (pathOfFile != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference riversRef = storage.getReferenceFromUrl(path);// путь на облаке, куда загружается файл, im - название файла на облаке
            // в переменной типа Uri filePath хранится путь на устройстве до загружаемого файла
            riversRef.putFile(pathOfFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            img.setImageDrawable(null);
                            Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message

                            //* заменить на активити
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }


}
