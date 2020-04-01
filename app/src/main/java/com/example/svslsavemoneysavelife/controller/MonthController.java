package com.example.svslsavemoneysavelife.controller;

import com.example.svslsavemoneysavelife.callback.MonthCallback;
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
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

public class MonthController {
    private String node = "Users";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(node);
    private ArrayList<Month> months = new ArrayList<>();

    public void save(final User user, ArrayList<Month> months, final MonthCallback callback) {

        myRef = database.getReference(node+"/"+ user.getKey() + "/" + "months");
        myRef.setValue(months)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(months);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFail(e.toString());
                    }
                });
    }

    public void getMonths(final User user, final MonthCallback callback){
        if(user.getMonths() != null) {
            if(user.getMonths().size() > 0) {
                callback.onSuccess(user.getMonths());
            }else {
                callback.onFail("No month added yet");
            }
        }else {
            callback.onFail("No month added yet");
        }
    }


    public int getCurrentMonthIndex() {
        Date current = Calendar.getInstance().getTime();
        int index = -1;
        if(SharedData.currentUser.getMonths() != null) {
            for (int i = 0; i < SharedData.currentUser.getMonths().size(); i++) {
                if (checkDateInMonth(i, current)) {
                    index = i;
                }
            }
            if(index > -1) {
                return index;
            }else {
                addMonth();
                return getCurrentMonthIndex();
            }
        }else {
            addMonth();
            return getCurrentMonthIndex();
        }
    }

    private void addMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));

        Date monthStart = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        Date monthEnd = cal.getTime();

        final Month month = new Month(getNewKey(), 0.0, 0.0, monthStart, monthEnd, new ArrayList<>());
        if(SharedData.currentUser.getMonths() != null) {
            SharedData.currentUser.getMonths().add(month);
        }else {
            ArrayList<Month> months = new ArrayList<>();
            months.add(month);
            SharedData.currentUser.setMonths(months);
        }
    }

    private String getNewKey() {
        int newKey = 1;
        if(SharedData.currentUser.getMonths() != null) {
            newKey = SharedData.currentUser.getMonths().size() + 1;
        }
        return String.valueOf(newKey);
    }

    private boolean checkDateInMonth(int monthIndex, Date current) {
        if (SharedData.currentUser.getMonths().size() > monthIndex) {
            Date start = SharedData.currentUser.getMonths().get(monthIndex).getMonthStart();
            Date end = SharedData.currentUser.getMonths().get(monthIndex).getMonthEnd();
            return current.after(start) && current.before(end);
        }else {
            return false;
        }
    }
}
