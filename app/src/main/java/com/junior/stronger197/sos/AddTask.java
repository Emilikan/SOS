package com.junior.stronger197.sos;

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
import android.widget.Toast;

import com.example.searchandrescue.CircularTransformation;
import com.example.searchandrescue.CropSquareTransformation;
import com.example.searchandrescue.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    private volatile String dbCounter = "";

    private FirebaseAuth mAuth;

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

    private void saveDataToDatabase() throws InterruptedException {

        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(counterFor == 1) {
                    dbCounter = dataSnapshot.child("counter").getValue(String.class);
                    Toast.makeText(getActivity(), dbCounter, Toast.LENGTH_SHORT).show();
                    int intCounter = Integer.parseInt(dbCounter);
                    intCounter++;
                    String stringCounter = Integer.toString(intCounter);
                    // устанавливаем значение
                    mRef.child("tasks").child(stringCounter).child("number").setValue(stringCounter);
                    mRef.child("tasks").child(stringCounter).child("nameOfTask").setValue(mNameTask);
                    mRef.child("tasks").child(stringCounter).child("describing").setValue(mDescribingOfTask);
                    mRef.child("tasks").child(stringCounter).child("Coordinate1").setValue(mCoordinate1);
                    mRef.child("tasks").child(stringCounter).child("Coordinate2").setValue(mCoordinate2);
                    mRef.child("tasks").child(stringCounter).child("Equipment").setValue(mEquipment);
                    mRef.child("tasks").child(stringCounter).child("NaturalConditions").setValue(mNaturalConditions);
                    mRef.child("tasks").child(stringCounter).child("time").setValue(mTime);
                    mRef.child("tasks").child(stringCounter).child("Relevance").setValue(true);
                    mRef.child("tasks").child(stringCounter).child("Date").setValue(mDate);
                    mRef.child("counter").setValue(stringCounter);

                    mRef.child("allTasks").child(stringCounter).setValue(mNameTask);
                    counterFor = 0;
                    Toast.makeText(getActivity(), "Задача успешно создана", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        // баг с тем, что занчения в бд он отправляет раньше, чем получает dbCounter

    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        super.onActivityResult(requestCode, resultCode, resultIntent);

        ImageView img = (ImageView) root.findViewById(R.id.attachesImg);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_REQUEST:
                    Uri selectedImage = resultIntent.getData();
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

}
