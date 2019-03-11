package com.example.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titletext;
    private Button button;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private  Province selectedProvince;
    private  City selectedCity;
    private  County selectedCounty;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titletext = (TextView) view.findViewById(R.id.title_text);
        button = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.listview);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY)
                    queryProvinces();
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        titletext.setText("中国");
        button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0)
        {
            datalist.clear();
            for(Province province:provinceList)
            {
                datalist.add(province.getProvincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    private void queryCities()
    {
        titletext.setText(selectedProvince.getProvincename());
        button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            datalist.clear();
            for(City city:cityList){
                datalist.add(city.getCityname());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = LEVEL_CITY;
    }else {
            int provinceid = selectedProvince.getProvincecode();
            String address = "http://guolin.tech/api/china/" + provinceid;
            queryFromServer(address,"city");
    }
 }
 private void  queryCounty(){
        titletext.setText(selectedCity.getCityname());
        button.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0) {
            datalist.clear();
            for (County county : countyList) {
                datalist.add(county.getCountyname());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else
        {
            int provincecode=selectedProvince.getProvincecode();
            int citycode = selectedCity.getCitycode();
            String address = "http://guolin.tech/api/china/ "+provincecode+"/"+citycode;
            queryFromServer(address,"county");
        }
      }
      private void queryFromServer(String address,final String type){
        showProgressDialog();
         HttpUtil.sendOkhttpRequest(address, new Callback() {
              @Override
              public void onFailure(Call call, IOException e) {
                  getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          closeProgressDialog();
                          Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                      }
                  });
              }

              @Override
              public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().toString();
                    boolean result = false;
                    if("province".equals(type))
                    {
                        result = Utility.handleProvinceResponse(responseText);
                    }else if("city".equals(type))
                    {
                        result = Utility.handlecityresponse(responseText,selectedProvince.getId());
                    }else if("county".equals(type))
                    {
                        result = Utility.handlecountyresponse(responseText,selectedCity.getId());
                    }
                    if(result){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if("province".equals(type))
                                {
                                    queryProvinces();
                                }else if("city".equals(type))
                                {
                                    queryCities();
                                }else if("county".equals(type)){
                                    queryCounty();
                                }
                            }
                        });
                    }
              }

          });
      }
      private void showProgressDialog()
      {
          if(progressDialog==null)
          {
              progressDialog = new ProgressDialog(getActivity());
              progressDialog.setMessage("正在加载");
              progressDialog.setCancelable(false);
          }
          progressDialog.show();
      }
      private void closeProgressDialog()
      {
          if(progressDialog!=null)
          progressDialog.dismiss();

      }
}
