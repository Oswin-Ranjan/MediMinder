package com.example.mediminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    EditText nameInput, ageInput;
    RadioGroup genderGroup;
    Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user info is already saved
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        boolean isUserInfoSaved = prefs.getBoolean("user_info_saved", false);

        if (isUserInfoSaved) {
            // Skip welcome screen and go directly to MainActivity
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
            return;
        }

        // First-time launch, show welcome screen
        setContentView(R.layout.activity_welcome);

        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        continueBtn = findViewById(R.id.continueButton);

        continueBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String age = ageInput.getText().toString().trim();
            int genderId = genderGroup.getCheckedRadioButtonId();

            if (name.isEmpty() || age.isEmpty() || genderId == -1) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = findViewById(genderId);
            String gender = selectedGender.getText().toString();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", name);
            editor.putString("age", age);
            editor.putString("gender", gender);
            editor.putBoolean("user_info_saved", true);
            editor.apply();

            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
});
    }
}