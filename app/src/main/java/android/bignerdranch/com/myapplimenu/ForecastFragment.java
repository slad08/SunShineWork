package android.bignerdranch.com.myapplimenu;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.text.format.Time;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Denis on 19.03.2016.
 */
public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> adapter;

    public ForecastFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setHasOptionsMenu(true);
    }
    @Override
    public  void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.act_refre){
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] sunList={
                "Today-Sunny-80/65",
                "Tomorrow-Foggy-70/45",
                "Weds-Cloudly-72/66",
                "Thrus-Rainy-64/52",
                "Fri-Foggy-60/44",
                "Sat-Sunny-77/65"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(sunList));

        View rootView =inflater.inflate(R.layout.fragm_with_menu,container,false);

        TextView textvv = (TextView)rootView.findViewById(R.id.list_item_forecast_textview);

//        ImageView imgview =(ImageView)rootView.findViewById(R.id.imageView);

        adapter = new ArrayAdapter<String>(getActivity()
                ,R.layout.list_item_forecast
                ,R.id.list_item_forecast_textview
                ,weekForecast);


        ListView list =(ListView)rootView.findViewById(R.id.list_view_for_cast);
        list.setAdapter(adapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private final String Log_Tag_Too = "JSON_RESULT";
        private final String Log_Tag_JSON = "RESULT_PARSE_JSON";

        /*Код конвертации даты/времени
        * */

        private  String getReadableDateString(long time){
            //т.к. API возвращает ЮНИКС временную метку в секундах, это должно быть конвертировано в милиссекунды
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }
        //Подготавливаетм погоду Высок/Низкую для представления (Округляем температуру)
        private String formatHighLows(double hight, double low){
            //Для представления , предполагаем пользователь не беспокоится о десятых градуса
            long roundedHigh = Math.round(hight);
            long roundedLow= Math.round(low);
            String highLowStr = roundedHigh + "/" +roundedHigh;
            return highLowStr;
        }
        // Возвращаем прогноз JSON строку в массив прогнозов
        private  String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException{
            //Там имена JSON объектов, которые нужно извлечь
            final String OWN_LIST = "list";
            final String OWN_WEATHER = "weather";
            final  String OWN_TEMPERATURE = "temp";
            final String OWN_MAX = "max";
            final String OWN_MIN = "min";
            final String OWN_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weaatherArray = forecastJson.getJSONArray(OWN_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();
            
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for (int i=0; i<weaatherArray.length();i++){
                // Сейчас, используем формат "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                //Get the JSON object representing the day
                JSONObject dayForecast = weaatherArray.getJSONObject(i);
                //Date/Time возвращ-ся в формте long/. Дам нужно конвертировать это в
                //человеко читаемый формат, чтобы человек мог прочитать "1400356800" как "this saturday"
                long dateTime;
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWN_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWN_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWN_TEMPERATURE);
                double high = temperatureObject.getDouble(OWN_MAX);
                double low = temperatureObject.getDouble(OWN_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - "+description + " - " + highAndLow;
            }
            for (String s:resultStrs){
                Log.v(Log_Tag_JSON,"Forecast entry: "+ s);
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length ==0){
              return null;
        }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;
            try {

              //  final String FORECAST_BASE_URL =
               //         "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&units=metric&cnt=7&APPID=fc98f6f48ac0446ca20c391650c87479";
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?APPID=fc98f6f48ac0446ca20c391650c87479";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM ="cnt";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG,"Built URI "+ builtUri.toString());

                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buff = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine())!=null){
                    buff.append(line + "\n");
                }
                if (buff.length()==0){
                    return null;
                }

                forecastJsonStr = buff.toString();
                Log.i(Log_Tag_Too,"Результат запроса  +" + forecastJsonStr);
            }
            catch (IOException e) {
                Log.e(LOG_TAG,"Error ", e);
                return null;
            }finally {
                if (urlConnection !=null) {
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                    reader.close();
                }catch (final IOException e){
                        Log.e(LOG_TAG,"Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] resul) {
            if (resul !=null){
                adapter.clear();
                for (String dayForecastStr : resul){
                    adapter.add(dayForecastStr);
                }
            }//Новые данные возвращаются с сервера
        }
    }

}

