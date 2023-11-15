package com.example.virtualcampus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class About extends AppCompatActivity {

    Toolbar tb;
    TextView tbtitle;
    ImageView createnew,hamicon,back;
    String Name,projectName,Email,About,Version,Releasedate,Institution,Dept;

    TextView about,projectname,version,releasedate,name,institution,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//ID FINDS
        tb=findViewById(R.id.tbar);
        tbtitle=tb.findViewById(R.id.toolbartitle);
        back=tb.findViewById(R.id.backbtn2);
        createnew=tb.findViewById(R.id.Createnew);
        hamicon=tb.findViewById(R.id.hamburgericon);
        about=findViewById(R.id.abouttxt);
        projectname=findViewById(R.id.projectname);
        version=findViewById(R.id.version);
        releasedate=findViewById(R.id.releasedate);
        name=findViewById(R.id.name);
        institution=findViewById(R.id.institution);
        email=findViewById(R.id.email);


//TOOLBAR SETUP
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(null);
        tbtitle.setText("About");
        createnew.setVisibility(View.GONE);
        hamicon.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

//LISTENER FOR BACK BUTTON
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(About.this,MainActivity.class));
            }
        });
//VOLLEY INITIALIZE
        RequestQueue reqQ= Volley.newRequestQueue(About.this);
        String url="https://api.myjson.online/v1/records/f7614d36-02f3-436b-a9b0-d58534a633dc";
        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parsejson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(About.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });

        reqQ.add(request);

    }
    public void parsejson(String response)
    {
        try {
            JSONObject data=new JSONObject(response);
            JSONObject dataobj=data.getJSONObject("data");
            About=dataobj.getString("About");
            projectName=dataobj.getString("Project Name");
            Version=dataobj.getString("Version");
            Releasedate=dataobj.getString("Released On");
            Name=dataobj.getString("Name");
            Institution=dataobj.getString("Institution");
            Dept=dataobj.getString("Dept");
            Email=dataobj.getString("Email");

            about.setText(About);
            projectname.setText(projectName);
            version.setText(Version);
            releasedate.setText(Releasedate);
            name.setText(Name);
            institution.setText(Institution);
            email.setText(Email);



        }
        catch (JSONException e)
        {
            throw new RuntimeException();
        }
    }
}