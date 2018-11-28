package com.example.searchandrescue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Volunteer extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String car_name;
    private String car_reg_sign;
    private String car_seats;
    private String full_name;
    private String vol_age;
    private String car_type;
    private String equp;
    private String telephone;
    private Uri selectedImage;
    private ImageView avatar;
    String dbCounter;
    public int counterFor = 0;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    DatabaseReference mRef;
    static final int GALLERY_REQUEST = 1;

    View root;
    LayoutInflater inflate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_volunteer, container, false);
        final EditText fullName = rootView.findViewById(R.id.vol_full_name), age = rootView.findViewById(R.id.vol_age);
        final CheckBox car = rootView.findViewById(R.id.has_car);
        Button addVol =(Button) rootView.findViewById(R.id.addVol);
        ImageView avatar = rootView.findViewById(R.id.avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_REQUEST);

            }
        });

        root = rootView;
        inflate = inflater;
        car.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    ((EditText) getActivity().findViewById(R.id.car_name)).setVisibility(View.VISIBLE);
                    ((EditText) getActivity().findViewById(R.id.car_reg_sign)).setVisibility(View.VISIBLE);
                    ((EditText) getActivity().findViewById(R.id.car_seats)).setVisibility(View.VISIBLE);
                    //car_name = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_name)).getText().toString();
                    //car_reg_sign = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_reg_sign)).getText().toString();
                    //car_seats = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_seats)).getText().toString();
                    ((EditText) getActivity().findViewById(R.id.car_type)).setVisibility(View.VISIBLE);

                } else {

                    ((EditText) getActivity().findViewById(R.id.car_name)).setVisibility(View.GONE);
                    ((EditText) getActivity().findViewById(R.id.car_reg_sign)).setVisibility(View.GONE);
                    ((EditText) getActivity().findViewById(R.id.car_seats)).setVisibility(View.GONE);
                    ((EditText) getActivity().findViewById(R.id.car_type)).setVisibility(View.GONE);

                }
            }
        });



        addVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_name = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.vol_full_name)).getText().toString();
                vol_age = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.vol_age)).getText().toString();
                car_name = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_name)).getText().toString();
                car_reg_sign = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_reg_sign)).getText().toString();
                car_type = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_type)).getText().toString();
                car_seats = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.car_seats)).getText().toString();
                equp = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.equipment)).getText().toString();
                telephone = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.vol_telephone)).getText().toString();
                if(user == null){
                    Toast.makeText(getActivity(), "Пожалуйста, авторизируйтесь", Toast.LENGTH_LONG).show();
                }
                else {
                    saveDataToDatabase();
                    counterFor = 1;
                }
            }
            });
        // Inflate the layout for this fragment
        return rootView;
    }
    private void saveDataToDatabase() {

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (counterFor == 1) {
                    dbCounter = dataSnapshot.child("numberOfPeople").getValue(String.class);
                    //Toast.makeText(getActivity(), dbCounter, Toast.LENGTH_SHORT).show();
                    int intCounter = Integer.parseInt(dbCounter);
                    intCounter++;
                    String stringCounter = Integer.toString(intCounter);

                    String imagePath = "gs://forfindpeople.appspot.com/" + "volunteer/" + user.getUid(); // путь до обложки
                    // устанавливаем значение
                    mRef.child("Ids").child(stringCounter).setValue(user.getUid());
                    mRef.child("volunter").child(user.getUid()).child("full_name").setValue(full_name);
                    mRef.child("volunter").child(user.getUid()).child("car_type").setValue(car_type);
                    mRef.child("volunter").child(user.getUid()).child("car_sign_in").setValue(car_reg_sign);
                    mRef.child("volunter").child(user.getUid()).child("car_seats").setValue(car_seats);
                    mRef.child("volunter").child(user.getUid()).child("car_name").setValue(car_name);
                    mRef.child("volunter").child(user.getUid()).child("age").setValue(vol_age);
                    mRef.child("volunter").child(user.getUid()).child("equipment").setValue(equp);
                    mRef.child("volunter").child(user.getUid()).child("numberOfVolunter").setValue(stringCounter);
                    mRef.child("numberOfPeople").setValue(stringCounter);
                    mRef.child("ratingOfVolonterAchivs").child(stringCounter).setValue("0");
                    mRef.child("ratingOfVolonterNames").child(stringCounter).setValue(full_name);
                    mRef.child("volunter").child(user.getUid()).child("telephone").setValue(telephone);

                    if(selectedImage != null) {
                        uploadFile(imagePath, selectedImage);
                        mRef.child("volunter").child(user.getUid()).child("Photo").setValue(imagePath);
                    }
                    else {
                        mRef.child("volunter").child(user.getUid()).child("Photo").setValue("0");
                    }

                    mRef.child("allVolunters").child(stringCounter).setValue(user.getUid()).toString();
                    counterFor = 0;
                    Toast.makeText(getActivity(), "Волонтер успешно создан", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void setFocusChange(EditText view) {

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                EditText editText = (EditText) view;

                if (b) {
                    editText.setBackground(getResources().getDrawable(R.drawable.input_selected));
                    editText.setTextColor(Color.parseColor("#000000"));
                } else {
                    editText.setBackground(getResources().getDrawable(R.drawable.input_default));
                    editText.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        super.onActivityResult(requestCode, resultCode, resultIntent);

        avatar = (ImageView) root.findViewById(R.id.avatar);

        if (resultCode == -1) {

            switch (requestCode) {

                case GALLERY_REQUEST:
                    selectedImage = resultIntent.getData();

                    Picasso.with(getContext())
                            .load(selectedImage)
                            .transform(new CircularTransformation())
                            .into(avatar);


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
                            avatar.setImageDrawable(null);
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


