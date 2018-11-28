package com.junior.stronger197.sos;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.searchandrescue.Blog;
import com.example.searchandrescue.R;
import com.example.searchandrescue.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class AddNewTask extends Fragment {
    public String conterOfFragment = "0";

    private String mNameTask;
    private String mDescribingOfTask;
    private String mCoordinate1;
    private String mTime;

    public int counterFor = 0;
    private String dbCounter;

    private DatabaseReference mRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_task, container, false);
        Bundle bundle = getArguments();
        conterOfFragment = "0";
        if(bundle != null){
            conterOfFragment = bundle.getString("ValueToNewTask", "0");
        }


        final Button addNewTask = (Button) rootView.findViewById(R.id.addNewTask); // кнопка добавления задачи
        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameTask = ((EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.nameTaskNewTask)).getText().toString();
                mDescribingOfTask = ((EditText) getActivity().findViewById(R.id.describingOfTaskNewTask)).getText().toString();
                mCoordinate1 = ((EditText) getActivity().findViewById(R.id.timeNewTask)).getText().toString();
                mTime = ((EditText) getActivity().findViewById(R.id.coordinate1NewTask)).getText().toString();
                if("".equals(mNameTask)|| "".equals(mDescribingOfTask)|| "".equals(mCoordinate1) || "".equals(mTime)) {
                    Toast.makeText(getActivity(), "Одно из полей не заполненно. Пожалуйста, заполните все поля и повторите отправку", Toast.LENGTH_LONG).show();
                }
                else if(user == null){
                    Toast.makeText(getActivity(), "Пожалуйста, авторизируйтесь", Toast.LENGTH_LONG).show();
                }
                else{
                    addNewTask();
                    counterFor = 1;
                }

            }
        });

        Button back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
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
        return rootView;
    }

    private void addNewTask(){
        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(counterFor == 1) {
                    dbCounter = dataSnapshot.child("tasks").child(conterOfFragment).child("someTaskValue").getValue(String.class);
                    Toast.makeText(getActivity(), dbCounter, Toast.LENGTH_SHORT).show();
                    int intCounter = Integer.parseInt(dbCounter);
                    intCounter++;
                    String stringCounter = Integer.toString(intCounter);
                    // устанавливаем значение
                    mRef.child("tasks").child(conterOfFragment).child("tasksNew").child(stringCounter).child("nameOfTask").setValue(mNameTask);
                    mRef.child("tasks").child(conterOfFragment).child("tasksNew").child(stringCounter).child("describing").setValue(mDescribingOfTask);
                    mRef.child("tasks").child(conterOfFragment).child("tasksNew").child(stringCounter).child("coordinate").setValue(mCoordinate1);
                    mRef.child("tasks").child(conterOfFragment).child("tasksNew").child(stringCounter).child("time").setValue(mTime);
                    mRef.child("tasks").child(conterOfFragment).child("tasksNew").child(stringCounter).child("user").setValue(user.getUid());

                    mRef.child("tasks").child(conterOfFragment).child("someTask").child(stringCounter).setValue(mNameTask);
                    mRef.child("tasks").child(conterOfFragment).child("someTaskValue").setValue(stringCounter);

                    ((EditText) getActivity().findViewById(R.id.nameTaskNewTask)).setText("");
                    ((EditText) getActivity().findViewById(R.id.describingOfTaskNewTask)).setText("");
                    ((EditText) getActivity().findViewById(R.id.timeNewTask)).setText("");
                    ((EditText) getActivity().findViewById(R.id.coordinate1NewTask)).setText("");
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


}
