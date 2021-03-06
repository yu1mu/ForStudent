package com.CAMPS.camps;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.CAMPS.camps.BoxClass.Assignment_Model;
import com.CAMPS.camps.BoxClass.Grade_Model;
import com.CAMPS.camps.BoxClass.Schedule_Model;
import com.CAMPS.camps.BoxClass.TestSub_Model;
import com.CAMPS.camps.BoxClass.Timetable_Model;
import com.CAMPS.camps.BoxHelperClass.AssignmentHelper;
import com.CAMPS.camps.BoxHelperClass.GradeHelper;
import com.CAMPS.camps.BoxHelperClass.ScheduleHelper;
import com.CAMPS.camps.BoxHelperClass.TestSubHelper;
import com.CAMPS.camps.DataClass.Assignment;
import com.CAMPS.camps.DataClass.Grade;
import com.CAMPS.camps.DataClass.Schedule;
import com.CAMPS.camps.DataClass.TestSub;
import com.CAMPS.camps.DataClass.Timetable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.objectbox.Box;

public class MainActivity<notesBox> extends AppCompatActivity {
    private static MainActivity instance;

    private TextView mTextMessage;
    public FragmentManager fragmentManager = getSupportFragmentManager();



    /*****objectbox*****/
    //박스 선언은 여기에서 함. 유저정보, 시간표정보 등등 필요한 구성에 따라 나눌 예정
    private Box<UserData> userDataBox;
    private Box<Assignment_Model> assignmentBox;
    private Box<TestSub_Model> testBox;
    private Box<Schedule_Model> scheduleBox;
    private Box<Grade_Model> gradeBox;
    private Box<Timetable_Model> timetableBox;

    //박스에 들어갈 객체 선언
    private UserData user;
    private Assignment_Model assignment_model;
    private TestSub_Model testSub_model;
    private Schedule_Model schedule_model;
    private Grade_Model grade_model;


    //for storage
    ArrayList<Assignment> assignment = new ArrayList<>();
    ArrayList<Assignment> important = new ArrayList<>();
    public static ArrayList<TestSub> testSub = new ArrayList<>();
    ArrayList<Schedule> schedules = new ArrayList<>();
    ArrayList<Grade> grades = new ArrayList<>();
    ArrayList<Timetable> timeTables = new ArrayList<>();
    ArrayList<Timetable_Model> timetable_models = new ArrayList<>();    // for merge models
    ArrayList<com.github.tlaabs.timetableview.Schedule> stickers = new ArrayList<com.github.tlaabs.timetableview.Schedule>();
    //store things
    //dday count
    int year;
    int month;
    int day;
    Date lastDay;

    /*****Fragment*****/
    public HomeFragment homeFragment= new HomeFragment();
    public TimetableFragment timetableFragment= new TimetableFragment();
    public CalendarFragment calendarFragment= new CalendarFragment();
    public TodoFragment todoFragment= new TodoFragment();
    public ExamFragment examFragment= new ExamFragment();
    public addNewExamSub addnewExamsub = new addNewExamSub();
    public AddNewAssignment addNewAssignment = new AddNewAssignment();
    public HomeFragmentSetup homeFragmentSetup = new HomeFragmentSetup();
    public SchoolMap schoolMap = new SchoolMap();
    public Fragment fragment;



    /*****toolbar, navigation *****/

    public ActionBar actionBar;
    public Menu menu;
    public Toolbar toolbar;
    public BottomNavigationView navBar;
    public String schoolName;

    TextView centerToolbarTitle;
    // 각 fragment 마다 툴바의 버튼 상태를 저장
    ArrayList<String> toolbarButtonState;


    /*****backpressed*****/
    InputMethodManager keypad;
    private long time=0;
    public  boolean BACK_STACK = false;

    public interface OnBackPressedListener{
        public void OnBack();
    }
    public OnBackPressedListener mBackListener;
    public void setOnBackPressedListener(OnBackPressedListener listener){
        mBackListener = listener;
    }



    NotificationManager notificationManager;
    PendingIntent intent;

    Intent pushIntent;

    /**** FINAL VALUES ****/
    final int SCHEDULE_ALARM_BASE   = 20000000;
    final int ASSIGNMENT_ALARM_BASE = 40000000;
    final int TEST_ALARM_BASE       = 60000000;
    final static int STORAGE_PERMISSION_CODE =1;

    public static Uri timetableUri;


    final long id=77;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Tutorial();
        //toolbarButtonState.add("SETTING_INVISIBLE");

/*

        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class),

                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = null;
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "default_channel_id";
            String channelDescription = "Default Channel";
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(this, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        Notification.Builder builder = new Notification.Builder(this)

                .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘 설정하지 않으면 오류남

                .setDefaults(Notification.DEFAULT_ALL)

                .setContentTitle("알림 제목") // 제목 설정

                .setContentText("알림 내용") // 내용 설정

                .setTicker("한줄 출력") // 상태바에 표시될 한줄 출력

                .setAutoCancel(true)

                .setContentIntent(intent);

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
*/

/*
        Toast.makeText(getApplicationContext(),"Service 시작",Toast.LENGTH_SHORT).show();
        pushIntent = new Intent(MainActivity.this,PushService.class);
        pushIntent.putExtra("messageTitle","TITLECHANKG");
        pushIntent.putExtra("messageText","TEXTCHANFKGE");
        startService(pushIntent);
*/

        /*
        Toast.makeText(getApplicationContext(),"Service 끝",Toast.LENGTH_SHORT).show();
        Intent intentl = new Intent(MainActivity.this,PushService.class);
        stopService(intent);*/




        /**********toolbar*********/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navBar = (BottomNavigationView)findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        actionBar= getSupportActionBar();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        //actionBar.setHomeAsUpIndicator(R.drawable.button_back); //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요

        toolbar.bringToFront();
        centerToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        instance = this;
        ObjectBox.init(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        //mTextMessage = findViewById(R.id.message);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();
        //navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        /********objectbox*********/
        //박스를 가져오는 작업
        userDataBox = ObjectBox.get().boxFor(UserData.class);
        assignmentBox = ObjectBox.get().boxFor(Assignment_Model.class);
        testBox = ObjectBox.get().boxFor(TestSub_Model.class);
        scheduleBox = ObjectBox.get().boxFor(Schedule_Model.class);
        gradeBox = ObjectBox.get().boxFor(Grade_Model.class);
        timetableBox = ObjectBox.get().boxFor(Timetable_Model.class);

        //test 유저 처음 저장 작업 (처음실행용)
        if(userDataBox.isEmpty()) {

            //user 생성
            user = new UserData(id, "DEFAULT", new Date(), 99, true, true,  true,  true, R.id.viewAllAssign, R.id.cal45, true, true, true);
            //박스에 user 객체 저장
            userDataBox.put(user);

            System.out.println("Initialize user "+id);
        }
        else{
            user = userDataBox.get(id);
            System.out.println("DAY "+user.lastDay);
        }

        /*
        if(userDataBox.get(id).getName().equals("DEFAULT")){
            System.out.println("Change Name");
            //디폴트인 경우 이름 바꿈, 텍스트박스안 텍스트를 받아오거나 하는 경우로 사용하면됨.
            user.setName("JIYOUNG");
            userDataBox.put(user);

        }*/

        this.loadData(user);
        //schedule data load
        for(long i=1; i<=scheduleBox.count(); i++){
            schedules.add(ScheduleHelper.getSchedule(i));
        }

        //assignment data load
        for(long i=1; i<=assignmentBox.count(); i++){
            assignment.add(AssignmentHelper.getAssignment(i));
        }

        //Exam data load
        testSub = new ArrayList<>();
        for(long i=1; i<=testBox.count(); i++){
            testSub.add(TestSubHelper.getTestSub(i));
        }

        //Grade data load
        for(long i=1; i<=gradeBox.count(); i++){
            grades.add(GradeHelper.getGrade(i));
        }
        System.out.println("IN");
        //Timetable data load
        for(long i=1; i<=timetableBox.count(); i++){
            timetable_models = new ArrayList<Timetable_Model>(timetableBox.getAll());
            //timetable_models.add(TimetableHelper.getLecture(this,i));
        }
        String classTitle = "DEFAULT";
        String professor = "DEFAULT";
        ArrayList<Calendar> startTimes=null;
        ArrayList<Calendar> endTimes=null;
        ArrayList<String> lectureRooms=null;
        if(!getTimetableBox().isEmpty()) {
            for (int i = 0; i < timetable_models.size(); i++) {
                Timetable_Model model = timetable_models.get(i);
                if(model == null) continue;
                if (!model.getSubject().equals(classTitle)) {
                    if (i != 0) {
                        Timetable timetable = new Timetable(classTitle, professor, startTimes, endTimes, lectureRooms);
                        timeTables.add(timetable);
                    }
                    classTitle = model.getSubject();
                    professor = model.getProfessor();
                    startTimes = new ArrayList<>();
                    endTimes = new ArrayList<>();
                    lectureRooms = new ArrayList<>();
                }
                startTimes.add(makeCalendar(model.getStart_hour(), model.getStart_minute(), model.getDayOfWeek()));
                endTimes.add(makeCalendar(model.getEnd_hour(), model.getEnd_minute(), model.getDayOfWeek()));
                lectureRooms.add(model.getLectureRoom());
                if (i == timetable_models.size()-1) {
                    Timetable timetable = new Timetable(classTitle, professor, startTimes, endTimes, lectureRooms);
                    timeTables.add(timetable);
                }
            }

        }


        /**********bottom navigation*********/
        //private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
          //      = new BottomNavigationView.OnNavigationItemSelectedListener() {
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.replace_in,R.anim.replace_out,R.anim.replace_in,R.anim.replace_out);
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        transaction.replace(R.id.frame_layout, homeFragment,"Home Fragment").commitAllowingStateLoss();
                        break;
                    case R.id.navigation_timetable:
                        transaction.replace(R.id.frame_layout, timetableFragment, "Time Table Fragment").commitAllowingStateLoss();
                        break;
                    case R.id.navigation_calendar:
                        transaction.replace(R.id.frame_layout, calendarFragment, "Calendar Fragment").commitAllowingStateLoss();
                        break;
                    case R.id.navigation_todo:
                        transaction.replace(R.id.frame_layout, todoFragment, "Todo Fragment").commitAllowingStateLoss();
                        break;
                    case R.id.navigation_exam:
                        transaction.replace(R.id.frame_layout, examFragment, "ExamFragment").commitAllowingStateLoss();
                        break;

            }
                return true;
            }

        });


        keypad = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);




    }//onCreate


    public void onBackPressed(){
            if(BACK_STACK){
                FragmentRemove(fragment);
                BACK_STACK=false;
            }
            else{
                if(System.currentTimeMillis()-time>=1000){
                    time=System.currentTimeMillis();
                    return;
                }
                else{
                    finish();
                }
            }

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectBox.get().close();

    }


    //나중에 objectbox 구현하면 삭제해야할 부분
    protected void loadData(UserData user){

        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(user.lastDay);

        year = lastDay.get(Calendar.YEAR);
        month = lastDay.get(Calendar.MONTH);
        day = lastDay.get(Calendar.DAY_OF_MONTH);
        /*
        SharedPreferences store = PreferenceManager.getDefaultSharedPreferences(this);
        year = store.getInt("D-dayYear", 2000);
        month = store.getInt("D-dayMonth", 1);
        day = store.getInt("D-dayDay", 1);*/
    }



    /**********Fragment***********/
    public void FragmentAdd(Fragment fragment){
        this.fragment = fragment;
        BACK_STACK=true;

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(R.anim.popup,R.anim.slide_down,0,R.anim.slide_down);
        transaction.replace(R.id.frame_layout,fragment);
        transaction.addToBackStack(null);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        navBar.setVisibility(View.GONE);
        transaction.commit();
    }

    public void internalFragmentRemove(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        fragmentManager.popBackStack();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navBar.setVisibility(View.VISIBLE);
        transaction.commit();
    }
    public void FragmentRemove(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        fragmentManager.popBackStack();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        navBar.setVisibility(View.VISIBLE);
        transaction.commit();

    }


    /***********objectbox************/


    /****user****/
    public Box getUserDataBox(){
        return userDataBox;
    }

    public void setLastDay(Date lastDay){
        this.lastDay = lastDay;
    }

    public UserData getUser(){
        return user;
    }


    /****schedule****/
    public Box getScheduleBox(){return scheduleBox;}


    /****assignment****/
    public Box<Assignment_Model> getAssignmentBox() {
        return assignmentBox;
    }

    public Assignment_Model getAssignment() {
        return assignment_model;
    }


    /****testsub****/
    public Box<TestSub_Model> getTestSubBox(){
        return testBox;
    }

    public TestSub_Model getTestSub(){
        return testSub_model;
    }

    /****grade****/
    public Box<Grade_Model> getGradeBox() {
        return gradeBox;
    }

    public Grade_Model getGrade(){
        return grade_model;
    }

    /*** timetable ***/
    public Box<Timetable_Model> getTimetableBox(){ return timetableBox;}


    /***********toolbar************/
    private void setActionbarTextColor(Toolbar actBar, int color) {

        String title = actBar.getTitle().toString();
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);

    }
    public void setActionBarTitle(String title) {
        Drawable backGround = getDrawable(R.drawable.actionbar_background);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.BLACK);
    }

    public void hideActionBar(){
        actionBar.hide();
    }

    public void showActionBar(){
        actionBar.show();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    /**** capture ****/

    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (view instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) view;
            surfaceView.setZOrderOnTop(true);
            surfaceView.draw(canvas);
            surfaceView.setZOrderOnTop(false);
            return bitmap;
        } else {
            //For ViewGroup & View
            view.draw(canvas);
            return bitmap;
        }
    }
    //캡쳐버튼클릭
    public void mOnCaptureClick(View v){
        //전체화면
        //View rootView = getWindow().getDecorView();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("권한이 없습니다.");
            dialog.setMessage("설정에서 권한을 설정해 주세요.");
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
            return;
        }



        File screenShot = ScreenShot(v);
        if(screenShot!=null){
            //갤러리에 추가
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
            System.out.println("HERE1"+screenShot.exists());
            Toast.makeText(getApplicationContext(), "시간표 저장 완료", Toast.LENGTH_SHORT).show();
        }
    }

    //화면 캡쳐하기
    public File ScreenShot(View view){
        String folder = "Pictures";
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        Date time = new Date();

        String ca = "camps_photo_";
        String datetext = format1.format(time);
        String j = ".jpg";

        String result1 = ca.concat(datetext);

        String filename = result1.concat(j);

        File sdCardPath = Environment.getExternalStorageDirectory();
        File dirs = new File(Environment.getExternalStorageDirectory(), folder);

        if (!dirs.exists()) { // 원하는 경로에 폴더가 있는지 확인
            dirs.mkdirs(); // Test 폴더 생성
             }
        String save = sdCardPath.getPath() + "/" + folder + "/" + filename;

        File file = new File(dirs, filename);//Pictures폴더 screenshot.jpg 파일
        FileOutputStream os = null;
        try{
            //filePath.mkdir();
            os = new FileOutputStream(save);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        setTimeTableUri(file);
        view.setDrawingCacheEnabled(false);
        return file;
    }

    private void setTimeTableUri(File file) {
        if(file.exists()){
            timetableUri = Uri.fromFile(file);
        }
    }


    /**** alarm ****/
    public Calendar calcHourBefore(Calendar calendar,int amount){
        calendar.set(Calendar.SECOND,0);

        if(24<=amount) {
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.HOUR,0);
            calendar.set(Calendar.HOUR_OF_DAY,0);
        }

        long destDateInMillis = calendar.getTimeInMillis()-3600000*amount;

        Calendar destDate = Calendar.getInstance();
        destDate.setTimeInMillis(destDateInMillis);
        return destDate;
    }

    public void alarmDelete(Object src){
        String className = src.getClass().getSimpleName();
        Calendar ringDate = null;
        Calendar alarmDate = Calendar.getInstance();

        Assignment assignment;
        Schedule schedule;
        TestSub testSub;

        String title=null;
        String memo=null;

        int timeBefore=0;
        int id=-1;

        switch (className){
            case "Assignment":
                timeBefore = 24;
                assignment = (Assignment) src;
                alarmDate.set(assignment.Period.get(Calendar.YEAR),assignment.Period.get(Calendar.MONTH), assignment.Period.get(Calendar.DAY_OF_MONTH), assignment.Period.get(Calendar.HOUR_OF_DAY), assignment.Period.get(Calendar.MINUTE));
                title = "[과제]";
                memo = "내일 까지 "+assignment.Name+"가 있습니다.";
                id = ASSIGNMENT_ALARM_BASE+todoFragment.AssList.indexOf(assignment)+1;
                break;
            case "Schedule":
                timeBefore = 24;
                schedule = (Schedule) src;
                //alarmDate = schedule.getDate();
                alarmDate.set(schedule.getDate().get(Calendar.YEAR),schedule.getDate().get(Calendar.MONTH), schedule.getDate().get(Calendar.DAY_OF_MONTH), schedule.getDate().get(Calendar.HOUR_OF_DAY), schedule.getDate().get(Calendar.MINUTE));
                title = "[일정]";
                memo = "1시간 후에 "+schedule.getTitle()+"가 있습니다.";
                id = SCHEDULE_ALARM_BASE+calendarFragment.schedules.indexOf(schedule)+1;
                break;
            case "TestSub":
                timeBefore = 24;
                testSub = (TestSub) src;
                alarmDate.set(testSub.TestDate.get(Calendar.YEAR),testSub.TestDate.get(Calendar.MONTH), testSub.TestDate.get(Calendar.DAY_OF_MONTH), testSub.TestDate.get(Calendar.HOUR_OF_DAY), testSub.TestDate.get(Calendar.MINUTE));
                title = "[시험]";
                memo = "내일 "+testSub.Name+"시험이 있습니다.";
                id = TEST_ALARM_BASE+examFragment.ExamList.indexOf(testSub)+1;
                break;


        }
        ringDate = calcHourBefore(alarmDate,timeBefore);

        if(ringDate.compareTo(Calendar.getInstance())==-1){
            return;
        }
        Intent alarmIntent = new Intent("com.example.ForStudent.ALARM_START");

        alarmIntent.putExtra("year",ringDate.get(Calendar.YEAR));
        alarmIntent.putExtra("month",ringDate.get(Calendar.MONTH));
        alarmIntent.putExtra("day",ringDate.get(Calendar.DAY_OF_MONTH));
        alarmIntent.putExtra("hour",ringDate.get(Calendar.HOUR));
        alarmIntent.putExtra("minute",ringDate.get(Calendar.MINUTE));
        alarmIntent.putExtra("title",title);
        alarmIntent.putExtra("memo",memo);

        alarmIntent.addCategory("android.intent.category.DEFAULT");
        alarmIntent.setClass(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,(int)id,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

    }

    public void alarmSet(Object src){
        String className = src.getClass().getSimpleName();
        Calendar ringDate = null;
        Calendar alarmDate = Calendar.getInstance();

        Assignment assignment;
        Schedule schedule;
        TestSub testSub;

        String title=null;
        String memo=null;

        int timeBefore=0;
        int id=-1;

        switch (className){
            case "Assignment":
                timeBefore = 24;
                assignment = (Assignment) src;
                alarmDate.set(assignment.Period.get(Calendar.YEAR),assignment.Period.get(Calendar.MONTH), assignment.Period.get(Calendar.DAY_OF_MONTH), assignment.Period.get(Calendar.HOUR_OF_DAY), assignment.Period.get(Calendar.MINUTE));
                title = "[과제]";
                memo = "내일 까지 "+assignment.Name+"가 있습니다.";
                id = ASSIGNMENT_ALARM_BASE+todoFragment.AssList.indexOf(assignment)+1;
                break;
            case "Schedule":
                timeBefore = 24;
                schedule = (Schedule) src;
                alarmDate.set(schedule.getDate().get(Calendar.YEAR),schedule.getDate().get(Calendar.MONTH), schedule.getDate().get(Calendar.DAY_OF_MONTH), schedule.getDate().get(Calendar.HOUR_OF_DAY), schedule.getDate().get(Calendar.MINUTE));

                title = "[일정]";
                memo = "1시간 후에 "+schedule.getTitle()+"가 있습니다.";
                id = SCHEDULE_ALARM_BASE+calendarFragment.schedules.indexOf(schedule)+1;
                break;
            case "TestSub":
                timeBefore = 24;
                testSub = (TestSub) src;
                alarmDate.set(testSub.TestDate.get(Calendar.YEAR),testSub.TestDate.get(Calendar.MONTH), testSub.TestDate.get(Calendar.DAY_OF_MONTH), testSub.TestDate.get(Calendar.HOUR_OF_DAY), testSub.TestDate.get(Calendar.MINUTE));
                title = "[시험]";
                memo = "내일 "+testSub.Name+"시험이 있습니다.";
                id = TEST_ALARM_BASE+examFragment.ExamList.indexOf(testSub)+1;
                break;


        }
        ringDate = calcHourBefore(alarmDate,timeBefore);
        if(ringDate.compareTo(Calendar.getInstance())==-1){
            return;
        }
        Intent alarmIntent = new Intent("com.example.ForStudent.ALARM_START");
        alarmIntent.putExtra("year",ringDate.get(Calendar.YEAR));
        alarmIntent.putExtra("month",ringDate.get(Calendar.MONTH));
        alarmIntent.putExtra("day",ringDate.get(Calendar.DAY_OF_MONTH));
        alarmIntent.putExtra("hour",ringDate.get(Calendar.HOUR));
        alarmIntent.putExtra("minute",ringDate.get(Calendar.MINUTE));
        alarmIntent.putExtra("title",title);
        alarmIntent.putExtra("memo",memo);
        alarmIntent.addCategory("android.intent.category.DEFAULT");
        alarmIntent.setClass(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,(int)id,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        long time = ringDate.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,pendingIntent);
    }
    public Calendar makeCalendar(int hour, int minute, int dayOfWeek){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,dayOfWeek);
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,minute);
        return cal;
    }

    private void Tutorial(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("저장소 권한 필요");
                dialog.setMessage("권한이 필요합니다.");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder notice = new AlertDialog.Builder(MainActivity.this);
                        notice.setTitle("권한을 부여하지 않습니다.");
                        notice.setMessage("나중에 설정에서 권한을 설정 할 수 있습니다.");
                        notice.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.dismiss();
                    }
                }).create().show();
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }

    }



}

/*
ObjectBox 사용 안내


저장 하고자 하는 객체를 생성함 ( UserData 참고 : id 부분은 그대로 복사해서 사용하고 나머지 요소들은 원하는대로 추가)         {UserData}
**모든 요소에 대해서 생성자가 있어야함**
그 뒤 build를 한번 실행 ( Build -> Rebuild 해야 되는거 같음 ) ( 모델 파일을 생성하는 과정 )
MainActivity 최상단에 박스 선언 하는 부분에 방금 생성했던 객체를 제네릭으로 Box 멤버를 하나 생성함.                     {private Box<UserData> userDataBox;}
그 바로 아래 방금 작성한 객체를 담을 수 있는 변수를 하나 추가함.                                                   {private UserData user;}
추가한 두가지 멤버에 대해 MainActivity 최하단에 getter를 생성함.                                               {getUserDataBox(),getUser()}
onCreate 부분에 박스를 가져오는 과정 부분에 젤 위에서 만든 Box멤버에 ObjectBox.get().boxFor(객체.class);를 할당해줌.  {userDataBox = ObjectBox.get().boxFor(UserData.class);}
해당 멤버를 isEmpty를 통해 첫 실행의 경우 디폴트값을 설정하는 과정을 거쳐야함.                                       {if(userDataBox.isEmpty())}
윗 부분에서 생성함 객체를 담을 수 있는 변수를 instantiate해서 하나 생성해줌.                                       {user = new UserData(id, "DEFAULT", new Date(), 99);}
그리고 가져온 박스 멤버에 .put(id)를 하면 박스에 객체가 저장됨. (id는 고정된 final 멤버로 통일할 것임.)                 {userDataBox.put(user);}
만약 객체를 업데이트 하고 싶다면, 아까 생성한 객체 담는 멤버에 setter로 값을 바꿔주고 다시 .put(id)하면 됨.               {user.setName("JIYOUNG");userDataBox.put(user);}
객체를 상자에서 받아오고 싶다면 Box변수.get(id)하면 됨.                                                        {user = userDataBox.get(id);}
fragment에서 사용하고 싶은경우,((MainActivity)getActivity())를 통해 mainactivity를 받아온 뒤, 위에 생성한 박스    {user = (UserData) ((MainActivity)getActivity()).getUserDataBox().get(id);}
getter를 통해, 박스를 가져온 뒤, 위와 동일하게 진행하면 됨.
*/
