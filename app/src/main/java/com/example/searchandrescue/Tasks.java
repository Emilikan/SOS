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
import com.junior.stronger197.sos.AddTask;

import java.util.List;
import java.util.Objects;

public class Tasks extends Fragment {

    private DatabaseReference mRef;
    private List<String> mTasks;

    private ListView listTasks;
    private String counter = "-1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter = dataSnapshot.child("counter").getValue(String.class);
                if("-1".equals(counter)){
                    Fragment fragment = new AddTask();
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                    Toast.makeText(getActivity(), "Нет задач", Toast.LENGTH_SHORT).show();
                }
                else {
                    checked();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listTasks = (ListView) rootView.findViewById(R.id.discr_for_task);



        return rootView;

    }

    private void checked(){
        mRef = FirebaseDatabase.getInstance().getReference();

        listTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView) itemClicked;
                String strText = textView.getText().toString(); // получаем текст нажатого элемента
                //Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
                Fragment fragment = new Task();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                Bundle bundle = new Bundle();
                String valueOfReplace = position+"";
                bundle.putString("Value", valueOfReplace);
                fragment.setArguments(bundle);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                mTasks = dataSnapshot.child("allTasks").getValue(t);
                updateUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "Error cod" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUI(){


        if(getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_text_view, mTasks);
            listTasks.setAdapter(adapter);
        }


    }



}
