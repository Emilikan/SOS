package com.example.searchandrescue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class Volonters2 extends Fragment {
    private DatabaseReference mRef;
    public String v;
    private List<String> mVolonters;
    ListView listVolonters;
    private String counter = "-1";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public String number_volonter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_volonters2, container, false);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter = dataSnapshot.child("numberOfPeople").getValue(String.class);
                if("-1".equals(counter)){
                    Fragment fragment = new Volunteer();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                    Toast.makeText(getActivity(), "Нет Волонтеров", Toast.LENGTH_SHORT).show();
                }
                else {
                    checked();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listVolonters = (ListView) rootView.findViewById(R.id.discr_for_volonters);
        return rootView;
    }
    private void checked(){
        mRef = FirebaseDatabase.getInstance().getReference();

        listVolonters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView) itemClicked;
                String strText = textView.getText().toString(); // получаем текст нажатого элемента
                //Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
                number_volonter = position+"";
                mRef = FirebaseDatabase.getInstance().getReference();
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        v = dataSnapshot.child("Ids").child(number_volonter).getValue(String.class);
                        Fragment fragment = new Profile();
                        FragmentManager fragmentManager = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                        Bundle bundle = new Bundle();
                        String valueOfReplace = number_volonter;
                        bundle.putString("ForProfile", v);
                        fragment.setArguments(bundle);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error cod" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                mVolonters = dataSnapshot.child("ratingOfVolonterNames").getValue(t);
                updateUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error cod" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateUI(){
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(Objects.requireNonNull(getActivity()).getBaseContext()),  R.layout.list_text_view, mVolonters);
            listVolonters.setAdapter(adapter);
        }
    }
}
