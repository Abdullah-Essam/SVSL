package com.example.svslsavemoneysavelife.controller;

import com.example.svslsavemoneysavelife.callback.BooleanCallback;
import com.example.svslsavemoneysavelife.callback.UserCallback;
import com.example.svslsavemoneysavelife.models.Month;
import com.example.svslsavemoneysavelife.models.User;
import com.example.svslsavemoneysavelife.utils.SharedData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class UserController {
    private String node = "Users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<User> users = new ArrayList<>();

    public void save(final User user, final UserCallback callback) {
        if(user.getKey() == null){
            user.setKey(myRef.push().getKey());
        }else if(user.getKey().equals("")){
            user.setKey(myRef.push().getKey());
        }

        myRef = database.getReference(node+"/"+user.getKey());
        myRef.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        users = new ArrayList<>();
                        users.add(user);
                        callback.onSuccess(users);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFail(e.toString());
                    }
                });
    }

    public void getUserByPhone(String phone, final UserCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user1 = snapshot.getValue(User.class);
                    assert user1 != null;
                    if(user1.getPhone().equals(phone)) {
                        users.add(user1);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void checkLogin(final String phone, final String pass, final UserCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user1 = snapshot.getValue(User.class);
                    assert user1 != null;
                    //check if we have the phone and password in the database
                     if (user1.getPhone().equals(phone) && user1.getPass().equals(pass)) {
                        users.add(user1);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void checkPhone(final String phone, final BooleanCallback callback){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = true;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user1 = snapshot.getValue(User.class);
                    assert user1 != null;
                    if(user1.getPhone().equals(phone))
                        check = false;

                }
                callback.onSuccess(check);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(databaseError.getMessage());
            }
        });
    }

    public void newUser(User user, final UserCallback userCallback) {
        if(user.validate()) {
            checkPhone(user.getPhone(), new BooleanCallback() {
                @Override
                public void onSuccess(boolean bool) {
                    if(bool) {
                        save(user, new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<User> users) {
                                SharedData.currentUser = users.get(0);
                                new MonthController().getCurrentMonthIndex();
                                save(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<User> users) {
                                        SharedData.currentUser = users.get(0);
                                        SharedData.currentMonthIndex = new MonthController().getCurrentMonthIndex();
                                        userCallback.onSuccess(users);
                                    }
                                    @Override
                                    public void onFail(String error) {
                                        userCallback.onFail(error);
                                    }
                                });
                            }
                            @Override
                            public void onFail(String error) {
                                userCallback.onFail(error);
                            }
                        });
                    }else {
                        userCallback.onFail("Phone is used before!");
                    }
                }
                @Override
                public void onFail(String error) {
                    userCallback.onFail(error);
                }
            });
        }else {
            userCallback.onFail("Not Valid Data!");
        }
    }
}
