package dev.app.shriram.miniproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pshri on 4/23/2016.
 */
public class Signin extends AppCompatActivity {
    private EditText inputName, inputEmail, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btnSignUp, btnRegister;
    private CheckBox ch1, ch2;
    private String urlJsonObj = "http://trialnew.16mb.com/select-db.php";
    private static String TAG = "Check";
    private ProgressDialog pDialog;
    int donororreceivercheck=0;
    String z="0";
    int dr = 0;
    private String jsonResponse;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputName = (EditText) findViewById(R.id.input_name);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnRegister = (Button) findViewById(R.id.btn_register);
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();
                ch1 = (CheckBox) findViewById(R.id.Check1);
                ch2 = (CheckBox) findViewById(R.id.Check2);
                if (ch1.isChecked() && ch2.isChecked()) {
                    dr = 3;
                } else if (ch1.isChecked()) {
                    donororreceivercheck=1;
                    editor.putInt("DRCheck", donororreceivercheck);
                    editor.putInt("Login_Check", 1);

                    dr = 1;
                } else if (ch2.isChecked()) {
                    editor.putInt("DRCheck", donororreceivercheck);
                    editor.putInt("Login_Check", 2);
                    donororreceivercheck=2;
                    dr = 2;
                } else {
                    dr = 0;
                }
                String a = inputName.getText().toString();
                String b = inputPassword.getText().toString();
                editor.apply();
                registerUser(a,b);
               // getData();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signin.this, Register.class);
                startActivity(intent);
            }
        });
    }
    /*Retriving from database*/
    private void showJSON(String response){
        Log.v("Response",response);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        String name="";
        String email="";
        int id = 0;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject collegeData = result.getJSONObject(0);
            name = collegeData.getString("name");
            Log.v("Working",name);
            email = collegeData.getString("email");
            String ad1=collegeData.getString("addrl1");
            String ad2=collegeData.getString("addrl2");
            String phn=collegeData.getString("phone");
            editor.putString("Addrl1",ad1);
            editor.putString("Addrl2",ad2);
            editor.putString("Phone",phn);
            id=collegeData.getInt("id");
            editor.putInt("id",id);
            editor.putString("name", name);
            editor.putString("email", email);
            Log.v("Working",name);
            editor.apply();
            z="1";
            Log.v("Z=",z);
            Log.v("Z1=",z);
            if (z=="1") {
                submitForm(dr);
            } else {
                Toast.makeText(getApplicationContext(), "Retry", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            z="0";
            Log.v("Working","notworking");
        }

    }

    private void registerUser(final String name, final String password){
        final String username = name.trim();
        final String pass = password.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,urlJsonObj,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Signin.this,response,Toast.LENGTH_LONG).show();
                        showJSON(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Signin.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",username);
                params.put("password",pass);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void submitForm(int dr) {

        if (dr == 1) {
            Intent intent = new Intent(Signin.this, MainActivity.class);
            Toast.makeText(Signin.this, "Thanks", Toast.LENGTH_LONG).show();

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        } else if (dr == 2) {
            Intent intent = new Intent(Signin.this, MainActivityR.class);
            Toast.makeText(Signin.this, "Thanks", Toast.LENGTH_LONG).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (dr == 0) {
            Toast.makeText(Signin.this, "Check only one", Toast.LENGTH_LONG).show();
        } else if (dr == 3) {
            Toast.makeText(Signin.this, "Check only one", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
