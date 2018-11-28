package com.example.searchandrescue;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.junior.stronger197.sos.NewTaskAfterAdd;

public class Profile extends Fragment {
    private TextView mNameTask;
    private TextView mTelephone;
    private TextView mSex;
    private TextView mCarTupe;
    private TextView mCarName;
    private TextView mCarSign;
    private TextView mCarSeats;
    private TextView mEquip;
    private TextView mAge;

    private DatabaseReference mRef;

    public String idUser = "0";
    public String valueAfterBlog1;
    public String valueAfterBlog2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            idUser = bundle.getString("ForProfile", "0");
            valueAfterBlog1 = bundle.getString("ForProfileValue1", "0");
            valueAfterBlog2 = bundle.getString("ForProfileValue2", "0");
        }
        mNameTask = (TextView) rootView.findViewById(R.id.prof_name);
        mTelephone = (TextView) rootView.findViewById(R.id.prof_telephone);
        mSex = (TextView) rootView.findViewById(R.id.prof_sex);
        mCarTupe = (TextView) rootView.findViewById(R.id.prof_car_type);
        mCarName = (TextView) rootView.findViewById(R.id.prof_car_name);
        mCarSign = (TextView) rootView.findViewById(R.id.prof_car_sign);
        mCarSeats = (TextView) rootView.findViewById(R.id.prof_car_seats);
        mEquip = (TextView) rootView.findViewById(R.id.prof_equip);
        mAge = (TextView) rootView.findViewById(R.id.prof_age);

        changeText();

        ImageView back = (ImageView) rootView.findViewById(R.id.back6);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NewTaskAfterAdd();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                Bundle bundle = new Bundle();
                bundle.putString("ValueForNewTaskAfterAdd", valueAfterBlog1);
                bundle.putString("ValueOfStartFragment", valueAfterBlog2);
                fragment.setArguments(bundle);
            }
        });

        return rootView;
    }

    public void changeText() {

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNameTask.setText("ФИО: " + dataSnapshot.child("volunter").child(idUser).child("full_name").getValue(String.class));
                mTelephone.setText("Телефон: " + dataSnapshot.child("volunter").child(idUser).child("telephone").getValue(String.class));
                mSex.setText("Пол: " + dataSnapshot.child("volunter").child(idUser).child("coordinate").getValue(String.class));
                mCarTupe.setText(dataSnapshot.child("volunter").child(idUser).child("car_type").getValue(String.class));
                mCarName.setText(dataSnapshot.child("volunter").child(idUser).child("car_name").getValue(String.class));
                mCarSign.setText(dataSnapshot.child("volunter").child(idUser).child("car_sign_in").getValue(String.class));
                mCarSeats.setText(dataSnapshot.child("volunter").child(idUser).child("car_seats").getValue(String.class));
                mEquip.setText("Снаряжение: " + dataSnapshot.child("volunter").child(idUser).child("equipment").getValue(String.class));
                mAge.setText("Возраст: " + dataSnapshot.child("volunter").child(idUser).child("age").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });



    }

}
