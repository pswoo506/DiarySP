package com.example.scit.diarysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static MediaPlayer mp;

    //메인 엑티비티 전역변수
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ArrayList<Diary> data;
    public static DiaryDBHelper dbHelper;
    public static DiaryPassDBHelper pdbHelper;
    public static SQLiteDatabase db;


    //Tab_Setting 전역변수
    public static Switch secretSwitch;
    public static EditText secretEditText;
    public static Button secretSaveBtn;

    public static EditText checkPass;
    public static int flag = 0;

    //Tab_Write 전역변수
    public static EditText edit_title, edit_contents;
    public static Button save_btn;


    //Tab_List 전역변수
    public static Button refresh_btn;
    public static ListView list_diary;
    public static DiaryList listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //배경음악 무한루프
        mp=MediaPlayer.create(this,R.raw.bgm);
        mp.setLooping(true);
        mp.start();

        //툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //탭레이아웃
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_action_settings));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_action_create));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_action_visibility));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //디비헬퍼
        dbHelper = new DiaryDBHelper(this);
        pdbHelper = new DiaryPassDBHelper(this);
        data = new ArrayList<>();

        //뷰페이지 연결
        viewPager = findViewById(R.id.pager);

        //페이지 어답터 연결
        TabPager pagerAdapter = new TabPager(getSupportFragmentManager(), tabLayout.getTabCount());

        //뷰 페이지 어답터 설정
        viewPager.setAdapter(pagerAdapter);

        //페이지에 어댑터 변경
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //탭 이벤트
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public static class Diary_Write extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_write, container, false);
            edit_title = view.findViewById(R.id.edit_title_write);
            edit_contents = view.findViewById(R.id.edit_contents_write);
            save_btn =  view.findViewById(R.id.save_btn_write);
            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InsertDB();
                }
            });
            return view;
        }
    }

    //다이어리 목록
    public static class Diary_List extends Fragment{

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.tab_list, container, false);
            flag = 1;
            refresh_btn = view.findViewById(R.id.refresh_btn_list);
            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(secretSwitch.isChecked()){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setMessage("비밀번호를 입력하세요");
                        View dialogview = inflater.inflate(R.layout.passwd, container, false);
                        alertDialog.setView(dialogview);
                        checkPass = dialogview.findViewById(R.id.check_pass);

                        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cp = checkPass();
                                if(checkPass.getText().toString().equals(cp)){
                                    list_diary = view.findViewById(R.id.list_diary);
                                    data = showDB();
                                    listAdapter = new DiaryList(getContext(), R.layout.list_layout, data);
                                    list_diary.setAdapter(listAdapter);
                                    list_diary.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(getContext(), DiaryUpdate.class);
                                            int code = data.get(position).getCode();
                                            intent.putExtra("code", code);
                                            startActivity(intent);
                                        }
                                    });
                                    list_diary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                            alertDialog.setMessage(data.get(position).getTitle()+"을(를) 삭제하시겠습니까?");
                                            alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    int code = data.get(position).getCode();
                                                    deleteDB(code);
                                                    showDB();
                                                    listAdapter.notifyDataSetChanged();
                                                }
                                            });
                                            alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            alertDialog.show();
                                            return false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    list_diary = view.findViewById(R.id.list_diary);
                                    data = showDB();
                                    listAdapter = null;
                                    list_diary.setAdapter(listAdapter);
                                }
                            }
                        });
                        alertDialog.show();
                    }else{
                        list_diary = view.findViewById(R.id.list_diary);
                        data = showDB();
                        listAdapter = new DiaryList(getContext(), R.layout.list_layout, data);
                        list_diary.setAdapter(listAdapter);
                        list_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), DiaryUpdate.class);
                                int code = data.get(position).getCode();
                                intent.putExtra("code", code);
                                startActivity(intent);
                            }
                        });
                        list_diary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                alertDialog.setMessage(data.get(position).getTitle()+"을(를) 삭제하시겠습니까?");
                                alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int code = data.get(position).getCode();
                                        deleteDB(code);
                                        showDB();
                                        listAdapter.notifyDataSetChanged();
                                    }
                                });
                                alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialog.show();
                                return false;
                            }
                        });
                    }
                }
            });
            if(flag == 1){
                return view;
            }else{
                return null;
            }
        }
    }

    //비밀번호 환경설정
    public static class Diary_Setting extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_setting, container, false);
            secretSwitch = view.findViewById(R.id.switch1);
            secretEditText = view.findViewById(R.id.secret_editText);
            secretSaveBtn = view.findViewById(R.id.secret_save_btn);
            secretSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        //On
                        secretEditText.setVisibility(View.VISIBLE);

                    }else{
                        //Off
                        secretEditText.setVisibility(View.INVISIBLE);


                    }
                }
            });
            secretSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = secretEditText.getText().toString();
                    passDB(str);
                }
            });
            return view;
        }
    }

    //DB메소드-----------------------------------------------------------------------

    //일기 쓰기 메소드
    public static void InsertDB(){
        db = dbHelper.getWritableDatabase();
        String sql = "insert into diary ('title', 'date', 'contents') values(?,?,?)";
        SQLiteStatement st = db.compileStatement(sql);
        st.bindString(1,edit_title.getText().toString());
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분 ss초");
        String writeTime = sdf.format(date);
        st.bindString(2,writeTime);
        st.bindString(3,edit_contents.getText().toString());
        st.execute();
        db.close();
        edit_title.setText("");
        edit_contents.setText("");
    }

    //일기 목록 보기 메소드
    public static ArrayList<Diary> showDB(){
        data.clear();
        db = dbHelper.getReadableDatabase();
        String sql = "select * from diary";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Diary diary = new Diary();
            diary.setCode(cursor.getInt(0));
            diary.setTitle(cursor.getString(1));
            diary.setDate(cursor.getString(2));
            diary.setContents(cursor.getString(3));
            data.add(diary);
        }
        cursor.close();
        db.close();
        return data;
    }

    //일기 삭제 메소드
    public static void deleteDB(int code){
        db = dbHelper.getReadableDatabase();
        String sql = "delete from diary where code="+code;
        db.execSQL(sql);
        db.close();
    }

    //비밀번호 입력 메소드
    public static void passDB(String str){
        db = pdbHelper.getWritableDatabase();
        String sql = "insert into passwd values('"+str+"')";
        db.execSQL(sql);
        db.close();
        secretEditText.setText("");
    }
    //비밀번호 확인 메소드
    public static String checkPass(){
        db = pdbHelper.getReadableDatabase();
        String sql = "select * from passwd";
        Cursor cursor = db.rawQuery(sql, null);
        String pass="";
        while(cursor.moveToNext()){
            pass = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pass;
    }

}





