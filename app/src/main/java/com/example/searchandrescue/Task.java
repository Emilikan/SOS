package com.example.searchandrescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Task extends Fragment {

    private TextView mNameTask;
    private TextView mDescrbingOfTask;
    private TextView mCoordiinate1;
    private TextView mCoordinate2;
    private TextView mEquipment;
    private TextView mNaturalConditions;
    private TextView mTime;
    private TextView mDate;
    public String conterOfFragment = "0";

    private String stringCounter;
    private FirebaseAuth mAuth;
    private FirebaseUser user = mAuth.getInstance().getCurrentUser();
    private String nowNumber;


    private DatabaseReference mRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        Bundle bundle = getArguments();
        conterOfFragment = "0";
        if(bundle != null){
            conterOfFragment = bundle.getString("Value", "0");
        }

        mNameTask = (TextView) rootView.findViewById(R.id.nameFromDatabase);
        mDescrbingOfTask = (TextView) rootView.findViewById(R.id.descridingFromDatabase);
        mCoordiinate1 = (TextView) rootView.findViewById(R.id.coordinate1From);
        mCoordinate2 = (TextView) rootView.findViewById(R.id.coordinate2From);
        mEquipment = (TextView) rootView.findViewById(R.id.equipmentFromDatabase);
        mNaturalConditions = (TextView) rootView.findViewById(R.id.naturalConditionsFrom);
        mTime = (TextView) rootView.findViewById(R.id.timeFrom);
        mDate = (TextView) rootView.findViewById(R.id.dateFrom);

        Button pin = rootView.findViewById(R.id.cektedTask), base = rootView.findViewById(R.id.wentToBase), home = rootView.findViewById(R.id.returnToHome), chat = rootView.findViewById(R.id.chat);


        //Toast.makeText(getActivity(), conterOfFragment, Toast.LENGTH_SHORT).show();
        changeText();

        Button cektedReturnHome = (Button) rootView.findViewById(R.id.returnToHome); // кнопка о возвращении домой
        cektedReturnHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int intCounter = Integer.parseInt(nowNumber);
                intCounter++;
                String stringCounterTru = Integer.toString(intCounter);
                mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("ratingOfVolunteerCounter").child(stringCounter).setValue(stringCounterTru);
                mRef.child("tasks").child(conterOfFragment).child("whoAddTask").child(user.getUid()).child("returnToHome").setValue("true");
                Toast.makeText(getActivity(), "Вы отметились, что возвратились домой", Toast.LENGTH_SHORT).show();
            }
        });

        Button cektedTask = (Button) rootView.findViewById(R.id.cektedTask); // кнопка о проикреплении к задачи
        cektedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("tasks").child(conterOfFragment).child("whoAddTask").child(user.getUid()).child("name");
                mRef.child("tasks").child(conterOfFragment).child("whoAddTask").child(user.getUid()).child("wentToBase").setValue("false");
                mRef.child("tasks").child(conterOfFragment).child("whoAddTask").child(user.getUid()).child("returnToHome").setValue("false");
                Toast.makeText(getActivity(), "закрепление прошло удачно", Toast.LENGTH_SHORT).show();

            }
        });

        Button wentToBase = (Button) rootView.findViewById(R.id.wentToBase); // кнопка о приезде на базу
        wentToBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child("tasks").child(conterOfFragment).child("whoAddTask").child(user.getUid()).child("wentToBase").setValue("true");
                Toast.makeText(getActivity(), "Вы отметились, что приехали на базу", Toast.LENGTH_SHORT).show();
            }
        });

        Button chats = (Button) rootView.findViewById(R.id.chat); // кнопка перехода в чат
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Blog();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                Bundle bundle = new Bundle();
                String valueOfReplace = conterOfFragment;
                bundle.putString("ValueOFPositionParentTask", valueOfReplace);
                fragment.setArguments(bundle);
            }
        });

        ImageView back = (ImageView) rootView.findViewById(R.id.back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Tasks();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        return rootView;
    }

    @SuppressLint("WrongViewCast")
    public void changeText() {

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //dbCounter = dataSnapshot.child("counter").getValue(String.class);
                    //Toast.makeText(getActivity(), dbCounter, Toast.LENGTH_SHORT).show();
                    mNameTask.setText(dataSnapshot.child("tasks").child(conterOfFragment).child("nameOfTask").getValue(String.class));
                    mDescrbingOfTask.setText(dataSnapshot.child("tasks").child(conterOfFragment).child("describing").getValue(String.class));
                    mCoordiinate1.setText("Координата 1: " +dataSnapshot.child("tasks").child(conterOfFragment).child("Coordinate1").getValue(String.class));
                    mCoordinate2.setText("Координата 2: " + dataSnapshot.child("tasks").child(conterOfFragment).child("Coordinate2").getValue(String.class));
                    mEquipment.setText("Снаряжение: " + dataSnapshot.child("tasks").child(conterOfFragment).child("Equipment").getValue(String.class));
                    mNaturalConditions.setText("Природные условия: " + dataSnapshot.child("tasks").child(conterOfFragment).child("NaturalConditions").getValue(String.class));
                    mTime.setText("Время сбора: " + dataSnapshot.child("tasks").child(conterOfFragment).child("time").getValue(String.class));
                    mDate.setText("Дата сбора: " + dataSnapshot.child("tasks").child(conterOfFragment).child("Date").getValue(String.class));
                    stringCounter = dataSnapshot.child("volunter").child(user.getUid()).child("numberOfVolunter").getValue(String.class);
                    nowNumber = dataSnapshot.child("ratingOfVolonterAchivs").child(stringCounter).getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });



    }

}
