package com.example.cropsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
	private EditText getEditTextEmail;
	private EditText editTextEmail;
	private EditText editTextPassword;

	private Button buttonLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Initialize your UI elements

		editTextEmail = findViewById(R.id.editTextEmail);
		editTextPassword = findViewById(R.id.enterPassword);

		buttonLogin = findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				performLogin();
			}
		});

	}

	private void performLogin() {
		String useremail=editTextEmail.getText().toString();
		String userpassword=editTextPassword.getText().toString();
		String url = "http://172.17.18.189/cropsafe/login.php"; // Replace with your actual server URL

		// Create a StringRequest to make a POST request
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Handle the response from the server
						try {
							JSONObject jsonResponse = new JSONObject(response);
							boolean error = jsonResponse.getBoolean("error");
							String message = jsonResponse.getString("message");

							if (!error) {
								Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

								// Redirect to MainActivity
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);

								// Finish LoginActivity to prevent going back to it on pressing back button
								finish();
							} else {
								// Login failed
								Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(LoginActivity.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Handle errors
						Toast.makeText(LoginActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Set the parameters to be sent to the server
				Map<String, String> params = new HashMap<>();
				params.put("email", useremail);
				params.put("password", userpassword);
				return params;
			}
		};
		int timeout = 30000; // 30 seconds
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				timeout,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
		));

		// Add the request to the Volley request queue
		Volley.newRequestQueue(this).add(stringRequest);
	}
}