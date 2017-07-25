package com.javgon.wakeme.Fragments;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Services.AlarmService;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;

/**
 * Created by javier gonzalez on 4/27/2017.
 * How user will interact with alarm clock
 */

public class AlarmClockFragment extends BaseFragment implements View.OnClickListener, NumberPicker.OnValueChangeListener, ToggleButton.OnCheckedChangeListener{

    MyUserData mUserData;
    TextView tvHour,tvMinute, tvAmPm;
    Button btnSave, btnDelete;
    LinearLayout clockLayoutClickable,clockLayout;
    ConstraintLayout editClockLayout;
    NumberPicker npHours, npMinutes;
    ToggleButton tbMon, tbTues, tbWed, tbThur, tbFri, tbSat, tbSun, tbAmPm;
    int mAlarmHour, mAlarmMinute;
    String userId;
    int alarmId;
    boolean isAm; //is it am(true) or is it pm(false)?
    ArrayList<Integer> alarmDays;

    public static AlarmClockFragment newInstance(Alarm alarm){
        AlarmClockFragment newFrag = new AlarmClockFragment();
        Bundle args = new Bundle();
        args.putInt("alarmHour",alarm.getAlarmTimeHours());
        args.putInt("alarmMinute", alarm.getAlarmTimeMinutes());
        args.putIntegerArrayList("alarmDays", alarm.getRepeatDays());
        args.putString("userId",alarm.getUserID());
        args.putInt("alarmId",alarm.getAlarmID());
        newFrag.setArguments(args);
        return newFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserData=mUserData.getInstance(getActivity());
        mAlarmHour =getArguments().getInt("alarmHour");
        mAlarmMinute =getArguments().getInt("alarmMinute");
        alarmDays=getArguments().getIntegerArrayList("alarmDays");
        userId=mUserData.getUserID();
        alarmId=getArguments().getInt("alarmId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_alarm_clock, container, false);
        Log.d("ALARMCLOCK",""+mUserData.getAlarmSize());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //text view for alarm time display
        tvHour = (TextView) view.findViewById(R.id.tv_hours);
        tvMinute=(TextView) view.findViewById(R.id.tv_minutes);
        tvAmPm=(TextView) view.findViewById(R.id.tv_ampm);
        //the layout that is holding the alarm view
        clockLayout=(LinearLayout) view.findViewById(R.id.layout_clock);
        //toggle buttons for weekday selection
        tbMon=(ToggleButton) clockLayout.findViewById(R.id.tb_mon);
        tbTues=(ToggleButton) clockLayout.findViewById(R.id.tb_tue);
        tbWed=(ToggleButton) clockLayout.findViewById(R.id.tb_wed);
        tbThur=(ToggleButton) clockLayout.findViewById(R.id.tb_thur);
        tbFri=(ToggleButton) clockLayout.findViewById(R.id.tb_fri);
        tbSat=(ToggleButton) clockLayout.findViewById(R.id.tb_sat);
        tbSun=(ToggleButton) clockLayout.findViewById(R.id.tb_sun);
        //to change hour format
        tbAmPm=(ToggleButton)clockLayout.findViewById(R.id.tb_am_pm);
        tbAmPm.setOnCheckedChangeListener(this);
        //to save edited alarm settings
        btnSave=(Button) clockLayout.findViewById(R.id.btn_save);
        btnDelete=(Button)clockLayout.findViewById(R.id.btn_delete_alarm);
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        //clickable area of clock that expands/contracts edit view
        clockLayoutClickable= (LinearLayout)view.findViewById(R.id.layout_clock_clickable);
        //the edit part of the alarm clock
        editClockLayout=(ConstraintLayout) clockLayout.findViewById(R.id.layout_edit_clock);
        //number pickers for editing alarm time
        npHours=(NumberPicker) clockLayout.findViewById(R.id.np_hours);
        npMinutes= (NumberPicker) clockLayout.findViewById(R.id.np_minutes);

        clockLayoutClickable.setOnClickListener(this);

        if (mUserData.getAlarmSize()!=0)  //if the user has alarms set
            editClockLayout.setVisibility(View.GONE);       //do not show edit alarm view
        else editClockLayout.setVisibility(View.VISIBLE);   //else show edit alarm view as soon as user adds alarm

        formatTimeDisplay();


        tbAmPm.setChecked(isAm);
        initToggleButtons();
        initPickers();
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    private void formatTimeDisplay(){

        if (mAlarmHour ==00){
            tvHour.setText("" + (mAlarmHour + 12));
            isAm=true;
            tvAmPm.setText("AM");
        }else
        if (mAlarmHour >12) {     //for military time that is 13:00 or larger, subtract 12 hours to get standard time
            tvHour.setText("" + (mAlarmHour - 12));
            isAm=false;
            tvAmPm.setText("PM");
        }
        else {
            tvHour.setText("" + (mAlarmHour));
            isAm=true;
            tvAmPm.setText("AM");
        }

        String formatMinutes=String.format("%02d", mAlarmMinute);
        tvMinute.setText(formatMinutes);
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.layout_clock_clickable:
                if (editClockLayout.getVisibility()==View.GONE) {
                    editClockLayout.setVisibility(View.VISIBLE);
                    btnSave.getParent().requestChildFocus(btnSave,btnSave);  //focus in scrollview
                }
                else {
                    editClockLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_save:
                saveAlarm();
                break;
            case R.id.btn_delete_alarm:
                deleteAlarm();
            default:
                break;
        }

    }
    /**
     * Initialize number picker settings
     */
    private void initPickers(){
        npHours.setMinValue(1);
        npHours.setMaxValue(12);
        npHours.setValue(mAlarmHour);
        npHours.setOnValueChangedListener(this);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);
        npMinutes.setValue(mAlarmMinute);
        npMinutes.setOnValueChangedListener(this);
        npMinutes.setFormatter(new NumberPicker.Formatter() { //displays minutes 1-9 as 01-09
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
    }


    private void initToggleButtons(){

        ToggleButton [] tbList = {tbSun,tbMon, tbTues, tbWed, tbThur, tbFri, tbSat };  //to iterate through them
        tbSun.setOnCheckedChangeListener(this);
        tbMon.setOnCheckedChangeListener(this);
        tbTues.setOnCheckedChangeListener(this);
        tbWed.setOnCheckedChangeListener(this);
        tbThur.setOnCheckedChangeListener(this);
        tbFri.setOnCheckedChangeListener(this);
        tbSat.setOnCheckedChangeListener(this);

        //alarmDays contains the user-selected repeat days for the alarm
        //iterate through the alarmDays list to find selected list, and then set the apropriate toggle button if a day exists
        for (int i=0; i<alarmDays.size();  i++){
            tbList[alarmDays.get(i)].setChecked(true);
        }
    }



    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        switch(picker.getId()){
            case R.id.np_hours:
                //tvHour.setText(String.valueOf(newVal));
                break;
            case R.id.np_minutes:
                //tvMinute.setText(display);
                break;
            default:
                break;
        }

    }

    private void saveAlarm(){
        int minutes = Integer.valueOf(npMinutes.getValue());
        int hours = Integer.valueOf(npHours.getValue());



        if (hours>=1&&hours<=11 && !isAm){
            hours = Integer.valueOf(npHours.getValue() + 12);
            tvHour.setText(String.valueOf(hours-12));
        }else if(hours>11 && !isAm){
            hours = Integer.valueOf(npHours.getValue()-12);
            tvHour.setText(String.valueOf(hours+12));

        }else{
            tvHour.setText(String.valueOf(hours));
        }

        //save new alarm time to member variables of this class, in case updated alarm time needs to be used elsewhere
        mAlarmHour=hours;
        mAlarmMinute=minutes;

        Alarm alarm = new Alarm(userId,alarmId, mAlarmHour, mAlarmMinute);
        //display saved values on alarm clock
        String formatMinutes=String.format("%02d", minutes);
        tvMinute.setText(formatMinutes);

        //remove duplicates
        alarmDays=new ArrayList<>(new LinkedHashSet<>(alarmDays));
        alarm.setRepeatDays(alarmDays);

        mUserData.setAlarm(alarm, alarmId);
        DatabaseServices.getInstance(getActivity()).writeAlarm(alarm);

        editClockLayout.setVisibility(View.GONE);

        addAlarmService(alarmId);
    }

    private void deleteAlarm(){

        LinearLayout myActivityView=(LinearLayout)getActivity().findViewById(R.id.layout_alarm_clocks);
        myActivityView.removeView(myActivityView.findViewById(getId()));
        Log.d("ALARMCLOCK","before delete "+mUserData.getAlarmSize()+ " alarm num: "+alarmId);
        mUserData.deleteAlarm(alarmId);
        Log.d("ALARMCLOCK","after delete "+mUserData.getAlarmSize()+ " alarm num: "+alarmId);
        Log.d("ALARMCLOCK",mUserData.toString());
        DatabaseServices.getInstance(getActivity()).deleteAlarm(alarmId);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        MainFragment.refresh();
    }


    public void addAlarmService(int requestCode){
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, mAlarmHour);
        alarmTime.set(Calendar.MINUTE, mAlarmMinute);
        alarmTime.set(Calendar.SECOND, 00);
        AlarmService.setServiceAlarm(getActivity(),true,requestCode, alarmTime);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.tb_mon:
                if (isChecked)
                    alarmDays.add(1);
                else
                    alarmDays.removeAll(Arrays.asList(1));
                break;
            case R.id.tb_tue:
                if (isChecked)
                    alarmDays.add(2);
                else
                    alarmDays.removeAll(Arrays.asList(2));
                break;
            case R.id.tb_wed:
                if (isChecked)
                    alarmDays.add(3);
                else
                    alarmDays.removeAll(Arrays.asList(3));
                break;
            case R.id.tb_thur:
                if (isChecked)
                    alarmDays.add(4);
                else
                    alarmDays.removeAll(Arrays.asList(4));
                break;
            case R.id.tb_fri:
                if (isChecked)
                    alarmDays.add(5);
                else
                    alarmDays.removeAll(Arrays.asList(5));
                break;
            case R.id.tb_sat:
                if (isChecked)
                    alarmDays.add(6);
                else
                    alarmDays.removeAll(Arrays.asList(6));
                break;
            case R.id.tb_sun:
                if (isChecked)
                    alarmDays.add(0);
                else
                    alarmDays.removeAll(Arrays.asList(0));
                break;
            case R.id.tb_am_pm:
                if (isChecked) {
                    isAm = true;  //am
                    tvAmPm.setText("AM");
                }

                else {
                    isAm = false; //pm
                    tvAmPm.setText("PM");
                }

                break;
            default:
                break;

        }

    }
}
