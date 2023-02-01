package net.woorisys.lighting.control3.admin.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.common.AbsractCommonAdapter;
import net.woorisys.lighting.control3.admin.databinding.ContentSearchBinding;
import net.woorisys.lighting.control3.admin.databinding.SearchListviewItemBinding;
import net.woorisys.lighting.control3.admin.domain.Temp;
import net.woorisys.lighting.control3.admin.sjp.RememberData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    private final static String TAG="SJP_SearchActivity_TAG";
    private File file;

    @BindView(R.id.page_title)
    TextView pageTitle;

    @BindView(R.id.search_edit)
    AutoCompleteTextView search_edit;

    @BindView(R.id.btn_refresh_list)
    Button btn_refresh;

    ContentSearchBinding binding;

    AbsractCommonAdapter<Temp> tempAdapter;

    List<Temp> list ;
    List<String> searchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search);
        ButterKnife.bind(this);
        binding = DataBindingUtil.setContentView(this, R.layout.content_search);
        binding.setActivity(this);

        pageTitle.setText("CSV 목록");

        file=new File(Environment.getExternalStorageDirectory(),"Area_Group");

        if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SearchActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        else
        {
            CreateDirectory();
        }

        btn_refresh=findViewById(R.id.btn_refresh_list);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetListVIew();
                ReadDirectory();
                Additem();
            }
        });

    }

    // 해당 파일 안에 있는 .xml 파일을 불러온다.
    private void Additem()
    {
        if(list==null)
            return;

        tempAdapter = new AbsractCommonAdapter<Temp>(SearchActivity.this, list) {

            SearchListviewItemBinding adapterBinding;

            @Override
            protected View getUserEditView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = tempAdapter.inflater.inflate(R.layout.search_listview_item, null);
                    adapterBinding = DataBindingUtil.bind(convertView);
                    adapterBinding.setDomain(tempAdapter.data.get(position));
                    convertView.setTag(adapterBinding);
                } else {
                    adapterBinding = (SearchListviewItemBinding) convertView.getTag();
                    adapterBinding.setDomain(tempAdapter.data.get(position));
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File combinefile=new File(file,list.get(position).getText());
                        Log.d(TAG,"SELECT : " +combinefile);
                        RememberData.getInstance().setSavefilepath(combinefile);

                        Intent intent=new Intent();
                        setResult(RESULT_OK,intent);

                        finish();
                    }
                });
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });
                return adapterBinding.getRoot();
            }
        };

        binding.searchResultListview.setAdapter(tempAdapter);
    }

    // 해당 파일을 생성
    private void CreateDirectory()
    {
        if(!DirectoryCheck())
            file.mkdir();

        ReadDirectory();
        Additem();
        setAutoSearch();
    }

    private boolean DirectoryCheck()
    {
        if(file.exists())
        {
            return true;
        }
        else
            return false;
    }

    private void ReadDirectory()
    {
        File[] files=file.listFiles();
        list = new ArrayList<Temp>();
        searchlist=new ArrayList<>();

        for(File file : files)
        {
            if(file.getName().toLowerCase().endsWith(".csv"))
            {
                Log.d(TAG,"FILE NAME : "+file.getName());
                list.add(new Temp(file.getName()));
                searchlist.add(file.getName());
            }
        }
    }

    private void ResetListVIew()
    {
        list.clear();
        tempAdapter.data.clear();
        binding.searchResultListview.setAdapter(tempAdapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"PERMISSION CODE : "+requestCode+" / "+permissions);

        if(requestCode==1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                CreateDirectory();

            } else {
                this.finish();
            }
        }
    }

    private void setAutoSearch()
    {
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_dropdown_item_1line,searchlist);
        search_edit=findViewById(R.id.search_edit);
        search_edit.setThreshold(1);
        search_edit.setAdapter(adapter);
        search_edit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResetListVIew();
                list = new ArrayList<Temp>();
                list.add(new Temp(parent.getItemAtPosition(position).toString()));
                Additem();
            }
        });
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString()==null || s.toString().equals(null) || s.toString()=="" || s.toString().equals(""))
                {
                    ResetListVIew();
                    ReadDirectory();
                    Additem();
                }
            }
        });
    }
}
