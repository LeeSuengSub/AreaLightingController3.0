package net.woorisys.lighting.control3.admin.search;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.common.AbsractCommonAdapter;
import net.woorisys.lighting.control3.admin.databinding.ContentSearchBinding;
import net.woorisys.lighting.control3.admin.databinding.SearchListviewItemBinding;
import net.woorisys.lighting.control3.admin.domain.Temp;
import net.woorisys.lighting.control3.admin.sjp.RememberData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private final static String TAG="SJP_SearchActivity_TAG";
    private static final int REQUEST_CODE_OPEN_DOWNLOAD_FOLDER = 100;
    private File file;

    TextView pageTitle;
    AutoCompleteTextView search_edit;
    Button btn_refresh;

    ContentSearchBinding binding;

    AbsractCommonAdapter<Temp> tempAdapter;

    List<Temp> list ;
    List<String> searchlist;

    Uri treeUri;

    public static Uri DefaultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search);
//        ButterKnife.bind(this);
        binding = DataBindingUtil.setContentView(this, R.layout.content_search);
        binding.setActivity(this);

        pageTitle=findViewById(R.id.page_title);
        search_edit=findViewById(R.id.search_edit);
        btn_refresh=findViewById(R.id.btn_refresh_list);

        pageTitle.setText("CSV 목록");

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOWNLOAD_FOLDER);



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOWNLOAD_FOLDER && resultCode == RESULT_OK) {
            treeUri = data.getData();

            if(treeUri == null) {
                Log.e(TAG, "트리 URI가 null입니다.");
                return;
            }

            if (!hasUriPermission(this, treeUri)) {
                getContentResolver().takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
                Log.d("SAF", "새로운 권한 획득: " + treeUri.toString());
            } else {
                Log.d("SAF", "이미 권한 보유: " + treeUri.toString());
            }

            // 폴더 내부 CSV 파일 중 첫 번째 URI를 DefaultUri로 저장
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            if (pickedDir != null && pickedDir.isDirectory()) {
                for (DocumentFile file : pickedDir.listFiles()) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                        DefaultUri = file.getUri(); // ✅ 여기에 저장
                        Log.d(TAG, "DefaultUri 설정됨: " + DefaultUri);
                        Log.d(TAG, "DefaultUri set to file: " + file.getName() + " | URI: " + DefaultUri);
                        break;
                    }
                }
            }

            CreateDirectory();
        }
    }

    private boolean hasUriPermission(Context context, Uri treeUri) {
        List<UriPermission> permissions = context.getContentResolver().getPersistedUriPermissions();
        for (UriPermission permission : permissions) {
            if (permission.getUri().equals(treeUri) && permission.isReadPermission() && permission.isWritePermission()) {
                return true;
            }
        }
        return false;
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
//                        File combinefile=new File(file,list.get(position).getText());
//                        Log.d(TAG,"SELECT : " +combinefile);
//                        RememberData.getInstance().setSavefilepath(combinefile);
//
//                        Intent intent=new Intent();
//                        setResult(RESULT_OK,intent);
//
//                        finish();

                        // 여기에 선택한 파일 이름으로 DefaultUri 업데이트
                        DocumentFile pickedDir = DocumentFile.fromTreeUri(SearchActivity.this, treeUri);
                        if (pickedDir != null && pickedDir.isDirectory()) {
                            for (DocumentFile file : pickedDir.listFiles()) {
                                if (file.isFile() && file.getName().equals(list.get(position).getText())) {
                                    DefaultUri = file.getUri();
                                    Log.d(TAG, "선택된 파일 URI로 DefaultUri 설정: " + DefaultUri);
                                    break;
                                }
                            }
                        }

                        // 이후 처리
                        File combinefile = new File(file, list.get(position).getText());
                        RememberData.getInstance().setSavefilepath(combinefile);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
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
        list = new ArrayList<Temp>();
        searchlist=new ArrayList<>();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

        if (pickedDir != null && pickedDir.isDirectory()) {
            DocumentFile[] files = pickedDir.listFiles();

            for (DocumentFile file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();  // ✅ 파일 이름 가져오기
                    Log.d("FileList", "파일 이름: " + fileName);
                    if(file.getName().toLowerCase().endsWith(".csv"))
                    {
                        Log.d(TAG,"FILE NAME : "+file.getName());
                        list.add(new Temp(file.getName()));
                        searchlist.add(file.getName());
                    }
                }
            }
        } else {
            Log.e("FileList", "유효한 폴더가 아닙니다.");
        }

    }

    private void ResetListVIew()
    {
        list.clear();
        tempAdapter.data.clear();
        binding.searchResultListview.setAdapter(tempAdapter);
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
