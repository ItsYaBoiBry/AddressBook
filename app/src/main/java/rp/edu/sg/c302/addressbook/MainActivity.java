package rp.edu.sg.c302.addressbook;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    Intent intent;
    ArrayList<Person> personList = new ArrayList<Person>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    public void onResume(){
        super.onResume();
        personList.clear();
        // Check if there is network access
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            HttpRequest request = new HttpRequest("http://10.0.2.2/p07_webService_problem/getListOfContacts.php");
            request.setMethod("GET");
            request.execute();
            try{
                String jsonString = request.getResponse();
                System.out.println(">>" + jsonString);

                JSONArray jsonArray = new JSONArray(jsonString);

                // Populate the arraylist personList
                for(int i=0 ; i < jsonArray.length(); i++){
                    JSONObject jObj = jsonArray.getJSONObject(i);
                    System.out.println(jObj.getString("firstname") + " " + jObj.getString("lastname"));
                    Person person = new Person();
                    person.setId(jObj.getInt("id"));
                    person.setFirstName(jObj.getString("firstname"));
                    person.setLastName(jObj.getString("lastname"));
                    person.setHomeNumber(jObj.getString("home"));
                    person.setMobileNumber(jObj.getString("mobile"));
                    person.setAddress(jObj.getString("address"));
                    person.setPostalCode(jObj.getString("postalcode"));
                    person.setEmail(jObj.getString("email"));
                    personList.add(person);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            PersonAdapter arrayAdapter = new PersonAdapter(this, R.layout.listview, personList);
            listView = (ListView) findViewById(R.id.listViewPersons);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int arg2, long arg3) {

                    Person person = (Person)parent.getItemAtPosition(arg2);

                    intent = new Intent(getApplicationContext(), DisplayUserInfoActivity.class);
                    intent.putExtra("com.example.MAIN_MESSAGE", Integer.toString(person.getId()));
                    startActivity(intent);
                }
            });
        } else {
            // AlertBox
            showAlert();
        }
    }

    private void showAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No network connection!")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.menu_add) {
            intent = new Intent(getApplicationContext(), AddUserInfoActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }
}